
package com.cp.monsterMod.ui.fragments;

import static com.cp.monsterMod.Constants.SIZE_NORMAL;
import static com.cp.monsterMod.Constants.SRC_FIRST_AVAILABLE;
import static com.cp.monsterMod.Constants.TYPE_ALBUM;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cp.monsterMod.R;
import com.cp.monsterMod.cache.ImageInfo;
import com.cp.monsterMod.cache.ImageProvider;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.helpers.utils.VisualizerUtils;
import com.cp.monsterMod.ui.widgets.VisualizerView;
import com.example.ex.ToastUtils;
/**
 * 上拉后中间界面
 * @author cp
 *
 */
public class AlbumArtFragment extends Fragment implements OnClickListener {
	public ImageView albumArt;
	String TAG = "AlbumArtFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View root = inflater.inflate(R.layout.nowplaying_album_art, null);
    	Log.e(TAG, "ONCREATEVIEW");
      WeakReference<VisualizerView> mView = new WeakReference<VisualizerView>((VisualizerView)root.findViewById(R.id.visualizerView));
      VisualizerUtils.updateVisualizerView(mView);
        albumArt = (ImageView)root.findViewById(R.id.audio_player_album_art);
        
        loadImage();
//        if(mInfo!=null){
//        	ImageProvider.getInstance( getActivity() ).loadImage(albumArt, mInfo );
//        }
        return root;
    }

	public void loadImage() {
		Log.e(TAG, "loadImage");
		Log.e(TAG, albumArt+"]");
		if(albumArt==null){
			return;
		}
		albumArt.setImageResource(R.drawable.no_art_normal);
		String artistName = MusicUtils.getArtistName();
        String albumName = MusicUtils.getAlbumName();
        String albumId = String.valueOf(MusicUtils.getCurrentAlbumId());
        if(albumName==null){
        	new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					loadImage();
				}
			}, 500);
        	return;
        }
        ImageInfo mInfo = new ImageInfo();
        mInfo.type = TYPE_ALBUM;
        mInfo.size = SIZE_NORMAL;
        mInfo.source = SRC_FIRST_AVAILABLE;
        mInfo.data = new String[]{ albumId , artistName, albumName };
        Log.e("data", albumId+","+artistName+","+albumName);
        ImageProvider.getInstance( getActivity() ).loadImage(albumArt, mInfo );
	}

	@Override
	public void onClick(View v) {
		ToastUtils.showToast(getActivity(), "点击!!");
		ToastUtils.showToast(getActivity(), "正在获取lrc");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.e("耗时计算", System.currentTimeMillis()+"ms");
				List<String> allLrc = MusicUtils.getAllLrc(Environment.getExternalStorageDirectory()+"");
				Log.e("SD卡所有歌词,allLrc", allLrc.toString());
				Log.e("耗时计算", System.currentTimeMillis()+"ms");
				MusicUtils.getlocal_lrc("", "", allLrc);
			}
		}).start();
	}
	private ImageInfo mInfo;
	public void setImageInfo(ImageInfo mInfo) {
		// TODO Auto-generated method stub
		this.mInfo = mInfo;
//		ImageProvider.getInstance( getActivity() ).loadImage( cur.albumArt, mInfo );
		
	}

}
