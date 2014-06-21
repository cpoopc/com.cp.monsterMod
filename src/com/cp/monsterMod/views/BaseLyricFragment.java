package com.cp.monsterMod.views;

import com.cp.monsterMod.ui.fragments.AlbumArtFragment;


public abstract class BaseLyricFragment extends AlbumArtFragment{
	/**
	 * 拖动完毕更新播放器()ACTION_UP
	 */
	public abstract void updateplayer();
	/**
	 * 确定拖动前增加位移
	 * 确定拖动后判断是否有效拖动
	 * @param dx-x轴偏移量
	 * @param dy-y轴偏移量
	 * @param toggle-是否可拖动
	 * @return
	 */
	public abstract boolean updatelab(float dx,float dy,boolean toggle);
	/**
	 * 确定有效拖动开始,取消自动滚动任务
	 */
	public abstract void slidestart();
	/**
	 * 拖动时更新lyricview
	 * @param f
	 * @param g
	 */
	public abstract  void updateprogress(float f, float g);
	
}
