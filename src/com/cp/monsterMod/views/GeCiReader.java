package com.cp.monsterMod.views;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
 * 根据传来的路径，把歌词解析到一个List里面，
 */
public class GeCiReader {
	private List<GeCiBean> list=new ArrayList<GeCiBean>();

	public List<GeCiBean> getList() {
		return list;
	}
	
	/*
	 * 根据传来的路径解析歌词，加入到List里面
	 */
	public void read(String path) throws IOException{
		list.clear();
		FileInputStream fis=new FileInputStream(path);
		InputStreamReader isr=new InputStreamReader(fis);
		BufferedReader br=new BufferedReader(isr);
		String line=null;//根据路径得到字符串
		while ((line=br.readLine())!=null) {
			line=line.trim();
			line=line.replace("[", "");
			line=line.replace("]", "@");
			String[] strs=line.split("@");
			if(strs.length==2){
				GeCiBean gc=new GeCiBean();
				gc.setTime(getTime(strs[0]));
				gc.setSentence(strs[1]);
				list.add(gc);
			}	
		}
	}
	/*
	 * 把时间转化成微秒
	 */
	private int getTime(String time){
		int times=0;
		time=time.replace(":", "@");
		time=time.replace(".", "@");
		String[] ss=time.split("@");
		if(ss.length==2){
			int minute=Integer.parseInt(ss[0]);
			int second=Integer.parseInt(ss[1]);
			times=1000*(minute*60+second);
		}
		if(ss.length==3){
			int minute=Integer.parseInt(ss[0]);
			int second=Integer.parseInt(ss[1]);
			int weisecond=Integer.parseInt(ss[2]);
			times=1000*(minute*60+second)+weisecond*10;
		}
		return times;
	}
}
