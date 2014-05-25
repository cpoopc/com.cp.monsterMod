/**
 * 
 */

package com.cp.monsterMod.ui.fragments.base;


import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.cp.monsterMod.R;
import com.cp.monsterMod.helpers.RefreshableFragment;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.service.ApolloService;
import com.cp.monsterMod.ui.adapters.base.DragSortListViewAdapter;
import com.cp.monsterMod.ui.widgets.KenBurnsView;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import static com.cp.monsterMod.Constants.INTENT_ADD_TO_PLAYLIST;
import static com.cp.monsterMod.Constants.INTENT_PLAYLIST_LIST;
//noboring
//可拖拽
public abstract class CpDragSortListViewFragment extends RefreshableFragment implements LoaderCallbacks<Cursor>,
        OnItemClickListener {

    // Adapter
    protected DragSortListViewAdapter mAdapter;

    // ListView
    protected DragSortListView mListView;

    // Cursor
    protected Cursor mCursor;

    // Selected position
    protected int mSelectedPosition;

    // Used to set ringtone
    protected long mSelectedId;

    // Options
    protected final int PLAY_SELECTION = 0;

    protected final int USE_AS_RINGTONE = 1;

    protected final int ADD_TO_PLAYLIST = 2;

    protected final int SEARCH = 3;

    protected final int REMOVE = 4;
    
    protected int mFragmentGroupId = 0;

    protected String mSortOrder = null, mWhere = null,
    				 mType = null, mMediaIdColumn = null;
    
    protected String[] mProjection = null;
    
    protected Uri mUri = null;
    
    
    //noboring
    private int mActionBarHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private KenBurnsView mHeaderPicture;
    private View mHeader;
    private View mPlaceHolderView;
    private TypedValue mTypedValue = new TypedValue();
    // Bundle
    public CpDragSortListViewFragment() {
    }

    public CpDragSortListViewFragment(Bundle args) {
        setArguments(args);
    }

    /*
     * To be overrode in child classes to setup fragment data
     */
    public abstract void setupFragmentData();
    /*
     * To be overrode in child classes to remove item from list
     */
    public abstract void removePlaylistItem(int which);
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupFragmentData();
        mListView.setOnCreateContextMenuListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        DragSortController controller = new DragSortController(mListView);
        //设置拖拽控件
        controller.setDragHandleId(R.id.listview_drag_handle);
        controller.setRemoveEnabled(true);
        controller.setRemoveMode(1);
        mListView.setFloatViewManager(controller);
        mListView.setOnTouchListener(controller);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void refresh() {
        if( mListView != null ) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cp_dragsort_listview, container, false);
        mListView = (DragSortListView)root.findViewById(R.id.list_view);
        //noboring
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();
        mHeader = root.findViewById(R.id.header);
        mHeaderPicture = (KenBurnsView) root.findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.picture0, R.drawable.picture1);
        setupListView();
        return root;
    }
    //noboring
    private void setupListView() {
        mPlaceHolderView = getActivity().getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(mPlaceHolderView);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
            }
        });
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    public int getScrollY() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }


    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
        return mActionBarHeight;
    }
    
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	return new CursorLoader(getActivity(), mUri, mProjection, mWhere, null, mSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Check for database errors
        if (data == null) {
            return;
        }
        mAdapter.reset();
        mAdapter.changeCursor(data);
        mListView.invalidateViews();
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null)
            mAdapter.changeCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putAll(getArguments() != null ? getArguments() : new Bundle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if( mFragmentGroupId != 0 ){
	    	menu.add(mFragmentGroupId, PLAY_SELECTION, 0, getResources().getString(R.string.play_all));
	        menu.add(mFragmentGroupId, ADD_TO_PLAYLIST, 0, getResources().getString(R.string.add_to_playlist));
	        menu.add(mFragmentGroupId, USE_AS_RINGTONE, 0, getResources().getString(R.string.use_as_ringtone));
	        menu.add(mFragmentGroupId, REMOVE, 0, R.string.remove);
	        menu.add(mFragmentGroupId, SEARCH, 0, getResources().getString(R.string.search));
	        AdapterContextMenuInfo mi = (AdapterContextMenuInfo)menuInfo;
	        mSelectedPosition = mi.position;
	        mCursor.moveToPosition(mSelectedPosition);
	        try {
	            mSelectedId = mCursor.getLong(mCursor.getColumnIndexOrThrow(mMediaIdColumn));
	        } catch (IllegalArgumentException ex) {
	            mSelectedId = mi.id;
	        }
	        title = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaColumns.TITLE));
	        menu.setHeaderTitle(title);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	if( item.getGroupId() == mFragmentGroupId ){
	        switch (item.getItemId()) {
	            case PLAY_SELECTION:
	                int position = mSelectedPosition;
	                MusicUtils.playAll(getActivity(), mCursor, position);
	                break;
	            case ADD_TO_PLAYLIST: {
	                Intent intent = new Intent(INTENT_ADD_TO_PLAYLIST);
	                long[] list = new long[] {
	                    mSelectedId
	                };
	                intent.putExtra(INTENT_PLAYLIST_LIST, list);
	                getActivity().startActivity(intent);
	                break;
	            }
	            case USE_AS_RINGTONE:
	                MusicUtils.setRingtone(getActivity(), mSelectedId);
	                break;
	            case SEARCH: {
	                MusicUtils.doSearch(getActivity(), mCursor, mType);
	                break;
	            }
	            case REMOVE: {
	                removePlaylistItem(mSelectedPosition);
	                break;
	            }
	            default:
	                break;
	        }
	        return true;
    	}
        return super.onContextItemSelected(item);
    }
   
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//    	ToastUtils.showToast(getActivity(), ""+position);
    	if(position==0){
    		//headview
    		
    	}
        MusicUtils.playAll(getActivity(), mCursor, position-1);
    }

    /**
     * Update the list as needed
     */
    private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListView != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

	private String title;

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApolloService.META_CHANGED);
        filter.addAction(ApolloService.PLAYSTATE_CHANGED);
        getActivity().registerReceiver(mMediaStatusReceiver, filter);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mMediaStatusReceiver);
        super.onStop();
    }
}
