package com.cp.monsterMod.views;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class LyricView extends TextView{
private Paint mPaint;
private float mX;
private Paint mPathPaint;
public int index = 0;
public float mTouchHistoryY;
private int mY;
private int currentDuringTime;//当前歌词持续时间
private float middleY;//Y轴中间
private final int DY = 40;//每一行的间隔
public float driftx;//x偏移量
public float drifty;//y偏移量
private float drift_r;
public boolean showprogress;//滑动时显示进度
public int temp = 0;//CP ?
private List<GeCiBean> list;//每首歌的歌词数据
	public LyricView(Context context) {
		super(context);
		init();
	}
	public LyricView(Context context,AttributeSet attr){
		super(context,attr);
		init();
	}
	public LyricView(Context context,AttributeSet attr,int i){
		super(context,attr,i);
		init();
	}
	private void init(){
		setFocusable(true);
		//非高亮部分
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(18);
		mPaint.setColor(Color.WHITE);
		mPaint.setTypeface(Typeface.SERIF);
		//高亮部分 当前歌词
		mPathPaint = new Paint();
		mPathPaint.setAntiAlias(true);
		mPathPaint.setTextSize(21);
		mPathPaint.setColor(Color.GREEN);
		mPathPaint.setTypeface(Typeface.SANS_SERIF);
	}
	//设置歌词显示数据
		public void setList(List<GeCiBean> list) {
			this.list = list;
			
		}
	protected void onDraw(Canvas canvas){
		
		super.onDraw(canvas);
		if(canvas == null){
			return;
		}
		if(list == null || list.size()==0){
			return;
		}
		//滑动相关
		//显示进度相关
		int j =(int) (-drifty/40);
		if(temp<j){
			temp++;
		}
		else if(temp>j){
			temp--;
		}
		
		if(index+temp>=0&&index+temp<list.size()-1)drift_r = drifty;
		
		canvas.drawColor(0xEFeffff);
		Paint p = mPaint;
		Paint p2 = mPathPaint;
		p.setTextAlign(Paint.Align.CENTER);
		
		if(index == -1)return;
		p2.setTextAlign(Paint.Align.CENTER);
		//先画当前行，之后再画他的前面和后面，这样就保持了当前行在中间的位置
		canvas.drawText(list.get(index).getSentence(), mX, middleY + drift_r, p2);
//		Log.e("list.get(index).getSentence()", list.get(index).getSentence());
		if(showprogress&&index+temp<list.size()-1){
			p2.setTextAlign(Paint.Align.LEFT);
			if(index+temp>=0){
				canvas.drawText(TimeParseTool.fomatTime(list.get(index+temp).getTime()), 0, middleY , p2);
			}
			else canvas.drawText("00:00", 0, middleY , p2);
			canvas.drawLine(0, middleY+1, mX*2, middleY+1, p2);
		}
		float tempY = middleY + drift_r;
		//画出本句之前的句子
		for(int i = index-1;i >= 0;i --){
			//向上推移
			tempY = tempY - DY;
			if(tempY < 0){
				break;
			}
			canvas.drawText(list.get(i).getSentence(), mX, tempY, p);
		}
		tempY = middleY + drift_r;
		//画出本句之后的句子
		for(int i = index+1;i < list.size();i ++){
			//向下推移
			tempY = tempY + DY;
			if(tempY > mY){
				break;
			}
			canvas.drawText(list.get(i).getSentence(), mX, tempY, p);
		}
		
		
		
	}
	protected void onSizeChanged(int w,int h,int ow,int oh){
		super.onSizeChanged(w, h, ow, oh);
		mX = w*0.5f;//屏幕中心坐标(转换为float?)
		mY = h;
		middleY = h*0.5f;
	}
	/**
	 * @author younger
	 * @param CurrentPosition 
	 * 当前歌词的时间轴
	 * @return drift
	 * 可以返回数据（已经废弃）
	 */
	public void updateindex(int CurrentPosition){
		if(list==null || list.size()==0) return;
		for (int i = 1; i < list.size(); i++) {
			if(CurrentPosition<list.get(i).getTime()){
				index = i-1;
//				Log.e("index", index+"");
				break;
			}else{
				if(i==list.size()-1){
					index = i;
				}
			}
		}
		if(index >= list.size()-1){
//			drifty = -CurrentPosition + middleY;
//			drift_r = CurrentPosition - middleY;
			drift_r = DY*(list.get(index).getTime() - CurrentPosition)/(3000);
		}else{
			drifty = DY*(list.get(index).getTime() - CurrentPosition)/(list.get(index+1).getTime()-list.get(index).getTime());
		}
	}
	public boolean repair(){
		if(index<=0){
			index=0;
			return false;
		}
		if(index>list.size()-1)index=list.size()-1;
		return true;
	}
	public int getCurrentTime(){
		return list.get(index).getTime();
	}
}
