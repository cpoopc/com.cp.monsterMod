package com.cp.monsterMod.views;

public class TimeParseTool {
	/**
	 * 
	 * @param Mesc
	 * @return time
	 */
	public static String MsecParseTime(String Mesc){
		int mescint =  Integer.parseInt(Mesc)/1000;
		String ss = String.valueOf(mescint%60);
		String mm = String.valueOf(mescint/60);
		if(ss.length()==1)ss = "0" + ss;
		if(mm.length()==1)mm = "0" + mm;
		String time = mm + ":" + ss;
		return time;
	}
	public String TimeParseMesc(String Time){
		
		return Time;
	}
	public static String fomatTime(int i){
		int time = i / 1000;
		int minute = time / 60;
		int second = time % 60;
		StringBuffer stringBuffer = new StringBuffer();
		if(minute<10){
			stringBuffer.append("0");
		}
		stringBuffer.append(minute).append(":");
		if(second<10){
			stringBuffer.append("0");
		}
		stringBuffer.append(second);
		return stringBuffer.toString();
	}
}
