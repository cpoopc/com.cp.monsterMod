/**
 * 
 */

package com.cp.monsterMod.ui.widgets;

import static com.cp.monsterMod.Constants.SIZE_THUMB;
import static com.cp.monsterMod.Constants.SRC_FIRST_AVAILABLE;
import static com.cp.monsterMod.Constants.TYPE_ALBUM;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp.monsterMod.R;
import com.cp.monsterMod.activities.QuickQueue;
import com.cp.monsterMod.cache.ImageInfo;
import com.cp.monsterMod.cache.ImageProvider;
import com.cp.monsterMod.helpers.utils.MusicUtils;

/**
 * @author Andrew Neal
 */
public class BottomActionBar extends LinearLayout implements OnLongClickListener {
	 
    public BottomActionBar(Context context) {
        super(context);
    }

    public BottomActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnLongClickListener(this);
    }

    public BottomActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Updates the bottom ActionBar's info
     * 
     * @param activity
     * @throws RemoteException
     */
    public void updateBottomActionBar(Activity activity) {
        View bottomActionBar = activity.findViewById(R.id.bottom_action_bar);
        if (bottomActionBar == null) {
            return;
        }

        if (MusicUtils.mService != null && MusicUtils.getCurrentAudioId() != -1) {

            // Track name
            TextView mTrackName = (TextView)bottomActionBar
                    .findViewById(R.id.bottom_action_bar_track_name);
            mTrackName.setText(MusicUtils.getTrackName());

            // Artist name
            TextView mArtistName = (TextView)bottomActionBar
                    .findViewById(R.id.bottom_action_bar_artist_name);
            mArtistName.setText(MusicUtils.getArtistName());

            // Album art
            ImageView mAlbumArt = (ImageView)bottomActionBar
                    .findViewById(R.id.bottom_action_bar_album_art);
            

            ImageInfo mInfo = new ImageInfo();
            mInfo.type = TYPE_ALBUM;
            mInfo.size = SIZE_THUMB;
            mInfo.source = SRC_FIRST_AVAILABLE;
            mInfo.data = new String[]{ String.valueOf(MusicUtils.getCurrentAlbumId()) , MusicUtils.getArtistName(), MusicUtils.getAlbumName() };
            
            ImageProvider.getInstance( activity ).loadImage( mAlbumArt , mInfo );
            
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Context context = v.getContext();
        context.startActivity(new Intent(context, QuickQueue.class));
        return true;
    }
    
}
