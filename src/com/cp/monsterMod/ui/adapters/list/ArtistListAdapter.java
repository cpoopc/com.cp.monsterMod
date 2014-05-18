package com.cp.monsterMod.ui.adapters.list;

import static com.cp.monsterMod.Constants.TYPE_ARTIST;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.ui.adapters.base.ListViewAdapter;

public class ArtistListAdapter extends ListViewAdapter {

	public ArtistListAdapter(Context context, int layout, Cursor c,	String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public void setupViewData(Cursor mCursor) {
    	mLineOneText = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaColumns.TITLE));    	
    	mLineTwoText = mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ALBUM));    	
        mImageData = new String[]{ mLineTwoText };        
        mPlayingId = MusicUtils.getCurrentAudioId();
        mCurrentId = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID));
        mListType = TYPE_ARTIST;
	}
}
