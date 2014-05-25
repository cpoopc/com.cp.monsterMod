
package com.cp.monsterMod.ui.fragments.list;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.MediaColumns;
import com.cp.monsterMod.R;
import com.cp.monsterMod.ui.adapters.list.GenreListAdapter;
import com.cp.monsterMod.ui.fragments.base.CpListViewFragment;
import com.cp.monsterMod.ui.fragments.base.ListViewFragment;
import static com.cp.monsterMod.Constants.EXTERNAL;
import static com.cp.monsterMod.Constants.TYPE_GENRE;

@SuppressLint("ValidFragment")
public class GenreListFragment extends CpListViewFragment{
	
    public GenreListFragment(Bundle args) {
        setArguments(args);
    }    

    public void setupFragmentData(){
        mAdapter = new GenreListAdapter(getActivity(), R.layout.listview_items, null,
                								new String[] {}, new int[] {}, 0);
    	mProjection = new String[] {
                BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM,
                AudioColumns.ARTIST
        };
        StringBuilder where = new StringBuilder();
        where.append(AudioColumns.IS_MUSIC + "=1").append(
                				" AND " + MediaColumns.TITLE + " != ''");
        mWhere = where.toString();        
        mSortOrder = Genres.Members.DEFAULT_SORT_ORDER;
        mUri = Genres.Members.getContentUri(EXTERNAL, getArguments().getLong(BaseColumns._ID));
        mFragmentGroupId = 3;
        mType = TYPE_GENRE;
        mTitleColumn = MediaColumns.TITLE; 
    }
}
