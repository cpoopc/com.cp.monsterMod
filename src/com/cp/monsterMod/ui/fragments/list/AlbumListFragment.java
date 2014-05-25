package com.cp.monsterMod.ui.fragments.list;

import static com.cp.monsterMod.Constants.TYPE_ALBUM;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;

import com.cp.monsterMod.R;
import com.cp.monsterMod.ui.adapters.list.AlbumListAdapter;
import com.cp.monsterMod.ui.fragments.base.CpListViewFragment;
import com.cp.monsterMod.ui.fragments.base.ListViewFragment;

@SuppressLint("ValidFragment")
public class AlbumListFragment extends CpListViewFragment {

	public AlbumListFragment(Bundle args) {    
		setArguments(args);    
	}

	@Override
	public void setupFragmentData() {
        mAdapter = new AlbumListAdapter(getActivity(), R.layout.listview_items, null,
                								new String[] {}, new int[] {}, 0);
    	mProjection = new String[] {
                BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST
        };
        StringBuilder where = new StringBuilder();
        where.append(AudioColumns.IS_MUSIC + "=1")
        					.append(" AND " + MediaColumns.TITLE + " != ''");
        long albumId = getArguments().getLong(BaseColumns._ID);
        where.append(" AND " + AudioColumns.ALBUM_ID + "=" + albumId);
        mWhere = where.toString();        
        mSortOrder = Audio.Media.TRACK + ", " + Audio.Media.DEFAULT_SORT_ORDER;
        mUri = Audio.Media.EXTERNAL_CONTENT_URI;
        mFragmentGroupId = 89;
        mType = TYPE_ALBUM;
        mTitleColumn = MediaColumns.TITLE; 
	}

}
