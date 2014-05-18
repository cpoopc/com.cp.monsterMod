
package com.cp.monsterMod.ui.fragments.list;

import android.support.v4.content.Loader;
import android.view.View;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import com.cp.monsterMod.R;
import com.cp.monsterMod.helpers.AddIdCursorLoader;
import com.cp.monsterMod.helpers.OnlineListCursorLoader;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.ui.adapters.list.OnlineListAdapter;
import com.cp.monsterMod.ui.adapters.list.RecentlyAddedAdapter;
import com.cp.monsterMod.ui.fragments.base.ListViewFragment;
import static com.cp.monsterMod.Constants.NUMWEEKS;
/**
 * 
 * @author Administrator
 *	在线音乐
 */
public class OnlineListFragment extends ListViewFragment{
	//接口:准备数据
    public void setupFragmentData(){
        mAdapter = new OnlineListAdapter(getActivity(), R.layout.listview_items,
                null, new String[] {}, new int[] {}, 0);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {    
//        return new AddIdCursorLoader(getActivity(), mUri, mProjection, mWhere, null, mSortOrder);
    	return new OnlineListCursorLoader(getActivity());
    }
}
