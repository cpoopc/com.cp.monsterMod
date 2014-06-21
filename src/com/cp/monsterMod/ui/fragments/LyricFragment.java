
package com.cp.monsterMod.ui.fragments;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp.monsterMod.R;
import com.cp.monsterMod.helpers.utils.MusicUtils;
import com.cp.monsterMod.helpers.utils.VisualizerUtils;
import com.cp.monsterMod.service.ApolloService;
import com.cp.monsterMod.ui.widgets.VisualizerView;
import com.cp.monsterMod.views.BaseLyricFragment;
import com.cp.monsterMod.views.GeCiReader;
import com.cp.monsterMod.views.LyricGesture;
import com.cp.monsterMod.views.LyricView;
import com.example.ex.FileUtil;
import com.example.ex.ToastUtils;
/**
 * 上拉后中间界面;歌词
 * @author cp
 *
 */
public class LyricFragment extends BaseLyricFragment implements OnClickListener {
	public ImageView albumArt;
	public TextView tv_warrning;
	private Handler handler;
	private String NOLYRIC = "没有找到歌词";
	private String FINDLYRIC = "正在为您努力加载歌词..";
	private String defaultPath = "/MonsterMod/lrc/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View root = inflater.inflate(R.layout.nowplaying_lyric, null);
    	tv_warrning = (TextView) root.findViewById(R.id.tv_warrning);
        albumArt = (ImageView)root.findViewById(R.id.audio_player_album_art);
        albumArt.setOnClickListener(this);
        initLyricView(root);
        return root;
    }
    /**
     * 初始化歌词:
     * 根据歌名->搜索歌词->显示
     * 进度,与父Fragment的seekbar同步;
     * 需要获得mediaplayer当前时间,设置 或者 获得父seekbar的进入,以及设置进度
     */
	private void initLyricView(View root) {
		lyricView = (LyricView) root.findViewById(R.id.lyricView);
		lyricView.setLongClickable(true);
		lyricView.setOnTouchListener(new LyricGesture(this));
		lyricView.post(new Runnable() {
			
			@Override
			public void run() {
				trackName = MusicUtils.getTrackName();
				artistName = MusicUtils.getArtistName();
				if(trackName==null){
					lyricView.postDelayed(this, 500);
				}else{
					loadLyric();
				}
			}
		});
		lyricView.invalidate();
	}
	private void loadLyric() {
//		if(loadlyricThread!=null){
//		Log.e("thread isalive", loadlyricThread.isAlive()+"");}
		createThreadTime = System.currentTimeMillis();
		loadlyricThread = new Thread(new LyricRunnable(createThreadTime));//防止多个线程同时更新lyricview
		loadlyricThread.start();
	}
	Thread loadlyricThread;
	boolean searchSdCard = false;
	/**
	 * 先扫描默认路径,再扫描全局
	 * @author Administrator
	 *
	 */
	private long createThreadTime;
	class LyricRunnable implements Runnable{
		private long createTime;
		
		public LyricRunnable(long createTime) {
			super();
			this.createTime = createTime;
		}
		GeCiReader geCiReader = new GeCiReader();
		@Override
		public void run() {

			String path = Environment.getExternalStorageDirectory()+defaultPath;
			allLrc = MusicUtils.getAllLrc(path);
			trackName = MusicUtils.getTrackName();
			artistName = MusicUtils.getArtistName();
			String lrcPath = MusicUtils.getlocal_lrc(trackName, artistName, allLrc);
			if(searchSdCard==true&&(lrcPath==null||lrcPath.equals(""))){
				//已经搜索过全盘了,但是没有
				Log.e("LyricFragment", "已经搜索过全盘了,但是没有");
				setLyric(null,createTime);
				return;
			}
			//默认路径没有,搜索全盘
			if(allLrc==null||allLrc.size()==0||lrcPath==null||lrcPath.equals("")){
				searchSdCard = true;
				Log.e("LyricFragment", "默认路径莫有找到,搜索sd卡");
			allLrc = MusicUtils.getAllLrc(Environment.getExternalStorageDirectory()+"");
			if(allLrc!=null&&allLrc.size()>0){
				//存入默认路径,以后就不用全盘搜索 了;
				for (int i = 0; i < allLrc.size(); i++) {
					String fromFile = allLrc.get(i);
					File file = new File(fromFile);
					int parentLength = file.getParent().length();
					String toPath = path+fromFile.substring(parentLength+1);
//					Log.e("路径", ":::"+toPath);
					if(!toPath.equals(fromFile)){
						FileUtil.CopySdcardFile(fromFile, path+fromFile.substring(parentLength));
					}
				}
			 } 
			}
			//搜索完毕,设置歌词
			if(lrcPath==null||lrcPath.equals("")){
				lrcPath = MusicUtils.getlocal_lrc(trackName, artistName, allLrc);
			}
			if(lrcPath==null||lrcPath.equals("")){
					//搜了全盘了还是没有
					setLyric(null,createTime);
					return;
			}else{
				try {
					geCiReader.read(lrcPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setLyric(geCiReader,createTime);
			}
		
		}}
	/**
	 * 把得到的歌词传给lyr
	 * @param geCiReader
	 */
	private void setLyric(final GeCiReader geCiReader,long time) {
		if(time!=createThreadTime||lyricView==null)return;
		tv_warrning.post(new Runnable() {
			
			@Override
			public void run() {
				if(geCiReader==null){
					tv_warrning.setText(NOLYRIC);
					tv_warrning.setVisibility(View.VISIBLE);
					lyricView.setList(null);
				}else{
					tv_warrning.setVisibility(View.INVISIBLE);
					lyricView.setList(geCiReader.getList());
				}
				
			}
		});
	}
	private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	//CPTODO
//        	Log.e("歌词fragment", "收到通知:歌曲切换!");
        	if(lyricView!=null){
        		loadLyric();//renew
        	}
        }
    };
    
	@Override
	public void onStart() {
		handler = new Handler();
		handler.postDelayed(lrcScroll,500);
		IntentFilter filter = new IntentFilter();
	    filter.addAction(ApolloService.META_CHANGED);
	    filter.addAction(ApolloService.PLAYSTATE_CHANGED);
	    getActivity().registerReceiver(mMediaStatusReceiver, filter);
		super.onStart();
		
	}
	@Override
	public void onStop() {
		handler.removeCallbacks(lrcScroll);
		getActivity().unregisterReceiver(mMediaStatusReceiver);
		super.onStop();
	}
	/**
	 * 歌词view
	 */
	private boolean tracking;
	private LyricView lyricView;
	private boolean stopautoscroll;
	private float ldriftx;
	private float ldrifty;
	Runnable lrcScroll = new Runnable() {
		public void run() {

//			Log.e("MusicUtils.getArtistName()", MusicUtils.getArtistName()+", "+MusicUtils.getAlbumName()+", "+MusicUtils.getTrackName());
			//停止自动滚动
			if(lyricView!=null){
				if(!stopautoscroll){
					try {
						lyricView.updateindex((int)MusicUtils.mService.position());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					lyricView.invalidate();
				}
			}
			handler.postDelayed(lrcScroll, 300);
		};
	};
	private List<String> allLrc;
	private String trackName;
	private String artistName;
	@Override
	public void updateplayer() {
		lyricView.showprogress = false;
		lyricView.index=lyricView.index+lyricView.temp;
		lyricView.driftx = 0;
		lyricView.drifty = 0;
		try {
		if(lyricView.repair()){
//		mediaPlayer.seekTo(lyricView.getCurrentTime());
				MusicUtils.mService.seek(lyricView.getCurrentTime());
		}
//		else mediaPlayer.seekTo(0);
		else MusicUtils.mService.seek(0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		stopautoscroll = false;
	}
	@Override
	public boolean updatelab(float dx, float dy, boolean toggle) {
		if(toggle){
			ldriftx = dx + ldriftx;
			ldrifty = dy + ldrifty;
			}
			else{
			if(Math.abs(ldriftx)<Math.abs(ldrifty)){
				return true;
			}
			ldriftx=0;
			ldrifty=0;
			}	
			return false;
	}
	@Override
	public void slidestart() {
		stopautoscroll = true;
		lyricView.showprogress = true;				
	}
	@Override
	public void updateprogress(float dx, float dy) {
		lyricView.driftx = dx + lyricView.driftx;
		lyricView.drifty = dy + lyricView.drifty;
		lyricView.invalidate();//更新视图		
	}

}
