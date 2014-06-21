package com.cp.monsterMod.views;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;

public class LyricGesture implements OnTouchListener,OnGestureListener{
	private BaseLyricFragment context;
	private GestureDetector mGestureDetector;
	private int way;
	private boolean starttoggle;
	private boolean updatetoggle;
	public LyricGesture(BaseLyricFragment context){
		this.context = context;
		mGestureDetector = new GestureDetector(context.getActivity(), this);
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		if(arg1.getAction() == MotionEvent.ACTION_UP)
		{
			way = 0;
			if(updatetoggle){
			context.updateplayer();
			updatetoggle = false;
			}
		}
		return mGestureDetector.onTouchEvent(arg1);
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
//		starttoggle = true;
		return true;
	}
	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		
					
		
		return false;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
	}
	/**
	 * @Parameters:
		arg0 The first down motion event that started the scrolling.
		arg1 The move motion event that triggered the current onScroll.
		arg2 The distance along the X axis that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2.
		arg3 The distance along the Y axis that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2.
		@Returns:
		true if the event is consumed, else false
	 */
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		Log.e("onScroll", "arg2:"+arg2+","+"arg3:"+arg3);//"arg0:"+arg0+","+"arg1:"+arg1+","+
		//前3次用作starttoggle触发条件
		if(way<3){
			context.updatelab(-arg2, -arg3, true);
			way++;
		}else{
			//y轴位移大于x轴返回true
		starttoggle = context.updatelab(-arg2, -arg3, false);
		if(starttoggle){
		updatetoggle = true;
		context.slidestart();
		starttoggle = false;
		}
		if(updatetoggle){
		context.updateprogress(-arg2,-arg3);
		}
		}
		Log.e("way", "way:"+way);
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

}
