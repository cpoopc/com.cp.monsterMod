package com.cp.monsterMod;

import static com.cp.monsterMod.Constants.SIZE_THUMB;
import static com.cp.monsterMod.Constants.SRC_FIRST_AVAILABLE;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cp.monsterMod.cache.ImageInfo;
import com.cp.monsterMod.cache.ImageProvider;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.views.ViewHolderList;
import com.example.ex.HTTPUtils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class OnlineMusicFragment extends Fragment implements OnItemClickListener{
	private ArrayList<MusicInfo> musicList = new ArrayList<MusicInfo>();
	private OnlineMusicAdapter musicAdapter;
	private ListView mListView;
	// Options
    protected final int PLAY_SELECTION = 0;

    protected final int USE_AS_RINGTONE = 1;

    protected final int ADD_TO_PLAYLIST = 2;

    protected final int SEARCH = 3;
    
    protected int mFragmentGroupId = 0;
	public OnlineMusicFragment() {
		// Required empty public constructor
	}
	class MusicInfo{
		String TITLE;
		String ARTIST;
		public MusicInfo(String tITLE, String aRTIST) {
			super();
			TITLE = tITLE;
			ARTIST = aRTIST;
		}
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("onCreateView", "onCreateView");
		View layout = inflater.inflate(R.layout.listview, container,false);
		initOnlineList();
		mListView = (ListView) layout.findViewById(android.R.id.list);
		musicAdapter = new OnlineMusicAdapter();
		mListView.setAdapter(musicAdapter);
		return layout;
	}

	private void initOnlineList() {
		//在线音乐json
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String httpGet = HTTPUtils.HTTPGet(Constants.URL_ONLINE);
//					Log.e("httpGet", httpGet);
					if(httpGet!=null){
						JSONArray jsonArray = new JSONArray(httpGet);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							String TITLE = jsonObject.getString("TITLE");
							String ARTIST = jsonObject.getString("ARTIST");
							musicList.add(new MusicInfo(TITLE, ARTIST));
						}
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								musicAdapter.notifyDataSetChanged();
							}
						});
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	class OnlineMusicAdapter extends BaseAdapter{
		private WeakReference<ViewHolderList> holderReference;
		
		private AnimationDrawable mPeakOneAnimation, mPeakTwoAnimation;
	    
	    protected Context mContext;
	    
	    private int left, top;    
	    
	    public String mListType = null,  mLineOneText = null, mLineTwoText = null;
	    
	    public String[] mImageData = null;
	    
	    public long mPlayingId = 0, mCurrentId = 0;
	    
	    public boolean showContextEnabled = true;
	    
	    private ImageProvider mImageProvider;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return musicList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemlayout = getActivity().getLayoutInflater().inflate(R.layout.listview_items, null);
	        ImageView mViewHolderImage = (ImageView)itemlayout.findViewById(R.id.listview_item_image);
	        TextView mViewHolderLineOne = (TextView)itemlayout.findViewById(R.id.listview_item_line_one);
	        TextView mViewHolderLineTwo = (TextView)itemlayout.findViewById(R.id.listview_item_line_two);
	        FrameLayout mQuickContext = (FrameLayout)itemlayout.findViewById(R.id.track_list_context_frame);
	        ImageView mPeakOne = (ImageView)itemlayout.findViewById(R.id.peak_one);
	        ImageView mPeakTwo = (ImageView)itemlayout.findViewById(R.id.peak_two);
	        ImageView mQuickContextDivider = (ImageView)itemlayout.findViewById(R.id.quick_context_line);
	        ImageView mQuickContextTip = (ImageView)itemlayout.findViewById(R.id.quick_context_tip);
	        mQuickContext.setOnClickListener(showContextMenu); 
	        MusicInfo musicInfo = musicList.get(position);
	        mViewHolderLineOne.setText(musicInfo.TITLE);
	        mViewHolderLineTwo.setText(musicInfo.ARTIST);
			return itemlayout;
		}
		private final View.OnClickListener showContextMenu = new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            v.showContextMenu();
	        }
	    };	
	}
	//TODO
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if( mFragmentGroupId != 0 ){
	    	menu.add(mFragmentGroupId, PLAY_SELECTION, 0, getResources().getString(R.string.play_all));
	        menu.add(mFragmentGroupId, ADD_TO_PLAYLIST, 0, getResources().getString(R.string.add_to_playlist));
	        menu.add(mFragmentGroupId, USE_AS_RINGTONE, 0, getResources().getString(R.string.use_as_ringtone));
	        menu.add(mFragmentGroupId, SEARCH, 0, getResources().getString(R.string.search));
	        AdapterContextMenuInfo mi = (AdapterContextMenuInfo)menuInfo;
//	        mSelectedPosition = mi.position;
//	        mCursor.moveToPosition(mSelectedPosition);
//	        mCurrentId = mCursor.getString(mCursor.getColumnIndexOrThrow(BaseColumns._ID));
//	        try {
//	            mSelectedId = Long.parseLong(mCurrentId);
//	        } catch (IllegalArgumentException ex) {
//	            mSelectedId = mi.id;
//	        }
//	        String title = mCursor.getString(mCursor.getColumnIndexOrThrow(mTitleColumn));
//	        menu.setHeaderTitle(title);
        }
    }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
}
