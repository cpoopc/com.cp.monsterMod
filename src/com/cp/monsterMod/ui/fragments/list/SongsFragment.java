
package com.cp.monsterMod.ui.fragments.list;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.cp.monsterMod.R;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.ui.adapters.list.SonglistAdapter;
import com.cp.monsterMod.ui.fragments.base.ListViewFragment;
import static com.cp.monsterMod.Constants.TYPE_SONG;

public class SongsFragment extends ListViewFragment{

    public void setupFragmentData(){
        mAdapter = new SonglistAdapter(getActivity(), R.layout.listview_items, null,
                										new String[] {}, new int[] {}, 0);
    	mProjection = new String[] {
                BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST
        };
        StringBuilder where = new StringBuilder();
        where.append(AudioColumns.IS_MUSIC + "=1").append(" AND " + MediaColumns.TITLE + " != ''");
        mWhere = where.toString();
        mSortOrder = Audio.Media.DEFAULT_SORT_ORDER;
        mUri = Audio.Media.EXTERNAL_CONTENT_URI;
        mFragmentGroupId = 3;
        mType = TYPE_SONG;
        mTitleColumn = MediaColumns.TITLE;         
        View shuffle_temp = View.inflate(getActivity(), R.layout.shuffle_all, null);
        mListView.addHeaderView(shuffle_temp);        
    	RelativeLayout  shuffle = (RelativeLayout)shuffle_temp.findViewById(R.id.shuffle_wrapper);
    	shuffle.setVisibility(View.VISIBLE);
    	shuffle.setOnClickListener(new RelativeLayout.OnClickListener() {  
            public void onClick(View v)
            {
                MusicUtils.suffle(getActivity());
            }

         });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        MusicUtils.playAll(getActivity(), mCursor, position-1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if( mFragmentGroupId != 0 ){
	    	menu.add(mFragmentGroupId, PLAY_SELECTION, 0, getResources().getString(R.string.play_all));
	        menu.add(mFragmentGroupId, ADD_TO_PLAYLIST, 0, getResources().getString(R.string.add_to_playlist));
	        menu.add(mFragmentGroupId, USE_AS_RINGTONE, 0, getResources().getString(R.string.use_as_ringtone));
	        menu.add(mFragmentGroupId, SEARCH, 0, getResources().getString(R.string.search));
	        AdapterContextMenuInfo mi = (AdapterContextMenuInfo)menuInfo;
	        mSelectedPosition = mi.position-1;
	        mCursor.moveToPosition(mSelectedPosition);
	        mCurrentId = mCursor.getString(mCursor.getColumnIndexOrThrow(BaseColumns._ID));
	        try {
	            mSelectedId = Long.parseLong(mCurrentId);
	        } catch (IllegalArgumentException ex) {
	            mSelectedId = mi.id;
	        }
	        String title = mCursor.getString(mCursor.getColumnIndexOrThrow(mTitleColumn));
	        menu.setHeaderTitle(title);
        }
    }

}
