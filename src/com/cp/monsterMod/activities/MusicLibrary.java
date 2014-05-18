/**
 * 
 */

package com.cp.monsterMod.activities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.SearchView;
import android.widget.Toast;

import com.cp.monsterMod.IApolloService;
import com.cp.monsterMod.OnlineMusicFragment;
import com.cp.monsterMod.R;
import com.cp.monsterMod.ui.adapters.PagerAdapter;
import com.cp.monsterMod.ui.adapters.ScrollingTabsAdapter;
import com.cp.monsterMod.ui.fragments.BottomActionBarFragment;
import com.cp.monsterMod.ui.fragments.grid.AlbumsFragment;
import com.cp.monsterMod.ui.fragments.grid.ArtistsFragment;
import com.cp.monsterMod.ui.fragments.list.GenresFragment;
import com.cp.monsterMod.ui.fragments.list.PlaylistsFragment;
import com.cp.monsterMod.ui.fragments.list.RecentlyAddedFragment;
import com.cp.monsterMod.ui.fragments.list.SongsFragment;
import com.cp.monsterMod.helpers.utils.ApolloUtils;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.preferences.SettingsHolder;
import com.cp.monsterMod.service.ApolloService;
import com.cp.monsterMod.service.ServiceToken;
import com.cp.monsterMod.ui.widgets.ScrollableTabView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import static com.cp.monsterMod.Constants.TABS_ENABLED;

/**
 * @author Andrew Neal
 * @Note This is the "holder" for all of the tabs
 */
public class MusicLibrary extends FragmentActivity implements ServiceConnection {
	
	private SlidingUpPanelLayout mPanel;
    
	private ServiceToken mToken;
    
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
    
	BottomActionBarFragment mBActionbar;
    
	private boolean isAlreadyStarted = false;	
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestFeature();
        setContentView(R.layout.library_browser);
        initBottomPanel();
        initActionBar();
        //TODO 音乐播放
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initPager();  
    }
    //slidinguppanel
	private void initBottomPanel() {
		mBActionbar =(BottomActionBarFragment) getSupportFragmentManager().findFragmentById(R.id.bottomactionbar_new);
  
        mBActionbar.setUpQueueSwitch(this);
        
        mPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mPanel.setAnchorPoint(0);
        
        mPanel.setDragView(findViewById(R.id.bottom_action_bar_dragview));
        //新版panel没有setShadowDrawable方法,注释掉
//        mPanel.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        mPanel.setAnchorPoint(0.0f);
        mPanel.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset < 0.2) {
                	//隐藏上一首播放下一首,显示三道杠
                    mBActionbar.onExpanded();
                    if (getActionBar().isShowing()) {
                        getActionBar().hide();
                    }
                } else {
                	//显示上一首播放下一首,隐藏三道杠
                    mBActionbar.onCollapsed();
                    if (!getActionBar().isShowing()) {
                        getActionBar().show();
                    }
                }
            }
            @Override
            public void onPanelExpanded(View panel) {
            }
            @Override
            public void onPanelCollapsed(View panel) {
            }
            @Override
            public void onPanelAnchored(View panel) {
            }
        });
        
        String startedFrom = getIntent().getStringExtra("started_from");
        if(startedFrom!=null){
        	ViewTreeObserver vto = mPanel.getViewTreeObserver();
        	vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        	    @Override
        	    public void onGlobalLayout() {
        	    	if(!isAlreadyStarted){
            	        mPanel.expandPane();
            	        isAlreadyStarted=true;
        	    	}
        	    }
        	});
        }
	}
	private boolean hasPress;
	//2秒内按两次退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(hasPress){
				finish();
				return true;
			}
			hasPress = true;
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					hasPress = false;
				}
			}, 2000);
			return true;
		}
		return false;
	}
	private void requestFeature() {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        // Landscape mode on phone isn't ready
        if (!ApolloUtils.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Scan for music
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);       
       // getSupportFragmentManager().beginTransaction().add(R.id.bottomactionbar_new, new BottomActionBarFragment(), "bottomactionbar_new").commit();
	}
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    	if(mPanel.isExpanded()){
            mPanel.collapsePane();
    	}
    	else{
    		super.onBackPressed();
    	}
    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder obj) {
        MusicUtils.mService = IApolloService.Stub.asInterface(obj);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        MusicUtils.mService = null;
    }

    @Override
    protected void onStart() {

        // Bind to Service
        mToken = MusicUtils.bindToService(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ApolloService.META_CHANGED);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Unbind
        if (MusicUtils.mService != null)
            MusicUtils.unbindFromService(mToken);

        //TODO: clear image cache

        super.onStop();
    }

    /**
     * Initiate ViewPager and PagerAdapter
     */
    public void initPager() {
        // viewpager:fragment的适配器(滑动fragment适配器)
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        //从配置中获取设置为可见的页面
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaults = new HashSet<String>(Arrays.asList(
        		getResources().getStringArray(R.array.tab_titles)
        	));
        Set<String> tabs_set = sp.getStringSet(TABS_ENABLED,defaults);
        //if its empty fill reset it to full defaults
        	//stops app from crashing when no tabs are shown
        	//TODO:rewrite activity to not crash when no tabs are chosen to show
        if(tabs_set.size()==0){
        	tabs_set = defaults;
        }
        //Only show tabs that were set in preferences
        // Recently added tracks
        if(tabs_set.contains(getResources().getString(R.string.tab_recent)))
        	mPagerAdapter.addFragment(new RecentlyAddedFragment());
        // Artists
        if(tabs_set.contains(getResources().getString(R.string.tab_artists)))
        	mPagerAdapter.addFragment(new ArtistsFragment());
        // Albums
        if(tabs_set.contains(getResources().getString(R.string.tab_albums)))
        	mPagerAdapter.addFragment(new AlbumsFragment());
        // // Tracks
        if(tabs_set.contains(getResources().getString(R.string.tab_songs)))
        	mPagerAdapter.addFragment(new SongsFragment());
        // // Playlists
        if(tabs_set.contains(getResources().getString(R.string.tab_playlists)))
        	mPagerAdapter.addFragment(new PlaylistsFragment());
        // // Genres
        if(tabs_set.contains(getResources().getString(R.string.tab_genres)))
        	mPagerAdapter.addFragment(new GenresFragment());
        //在线音乐
        if(tabs_set.contains(getResources().getString(R.string.tab_online)))
        	mPagerAdapter.addFragment(new OnlineMusicFragment());

        // Initiate ViewPager
        ViewPager mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setPageMargin(getResources().getInteger(R.integer.viewpager_margin_width));
        mViewPager.setPageMarginDrawable(R.drawable.viewpager_margin);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mViewPager.setAdapter(mPagerAdapter);
        //mViewPager.setCurrentItem(0);

        // Tabs初始化
        initScrollableTabs(mViewPager);
    }

    /**
     * Initiate the tabs
     */
    public void initScrollableTabs(ViewPager mViewPager) {
        ScrollableTabView mScrollingTabs = (ScrollableTabView)findViewById(R.id.scrollingTabs);
        //设置数据
        ScrollingTabsAdapter mScrollingTabsAdapter = new ScrollingTabsAdapter(this);
        //设置tab显示数据
        mScrollingTabs.setAdapter(mScrollingTabsAdapter);
        //关联viewpager
        mScrollingTabs.setViewPager(mViewPager);
    }
    
    /**
     * For the theme chooser
     */
    private void initActionBar() {
    	ActionBar actBar = getActionBar();
//    	actBar.setLogo(R.drawable.ic_monster);
//    	actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
//    	actBar.setTitle(R.string.app_name);
    	actBar.setDisplayUseLogoEnabled(true);
        actBar.setDisplayShowTitleEnabled(true);
    }
    
    /**
     * Respond to clicks on actionbar options
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//	        case R.id.action_search:
//	            onSearchRequested();
//	            break;

	        case R.id.action_settings:
	        	startActivityForResult(new Intent(this, SettingsHolder.class),0);
	            break;

	        case R.id.action_eqalizer:
//	    	    final Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
//	            if (getPackageManager().resolveActivity(intent, 0) == null) {
		        	startActivity(new Intent(this, SimpleEq.class));
//	        	}
//	        	else{
//	        		intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicUtils.getCurrentAudioId());
//	        		startActivity(intent);
//	        	}
	            break;

	        case R.id.action_shuffle_all:
	        	MusicUtils.suffle(this);
	            break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	//重新运行程序
    	Intent i = getBaseContext().getPackageManager()
	             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
    }
    
    /**
     * Initiate the Top Actionbar
     */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actionbar_top, menu);
	    SearchView searchView = new SearchView(this);
	    menu.add("sousuo")
	    .setIcon(R.drawable.apollo_holo_dark_search)
	    .setActionView(searchView)
	    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//	    MenuItem searchItem = menu.findItem(R.id.action_search);
//	    SearchView searchView = (SearchView) searchItem.getActionView();
//	    searchView.setBackgroundColor(Color.WHITE);
//	    searchView.setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
	    return true;
	}

}
