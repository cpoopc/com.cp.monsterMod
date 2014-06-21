package com.cp.monsterMod.views;

public class GeCiBean {
	private int time;
	private String sentence;
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	@Override
	public String toString() {
		return "GeCiBean [time=" + time + ", sentence=" + sentence + "]";
	}
	
}
