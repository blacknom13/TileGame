package com.brackeen.javagamelearn.sound;

public abstract class SoundFilterX {

	public void reset(){
		
	}
	
	public int getRemainingSize(){
		return 0;
	}
	
	public void filter(byte [] buf){
		filter(buf,0,buf.length);
	}
	
	public abstract void filter (byte [] buf, int offset, int length);
	
	public static short getSamples(byte [] buf, int position){
		return (short)(((buf[position+1] & 0xff) <<8)|(buf[position] & 0xff));
	}
	
	public static void setSamples(byte [] buf, int position, short sample){
		buf[position]=(byte)(sample & 0xff);
		buf[position +1]=(byte)((sample>>8) & 0xff);
	}
	
	public static byte [] convertToStereo(byte [] sound, boolean left,boolean right){
		byte [] stereo=new byte[sound.length*2];
		for (int i=0;i<sound.length;i+=2){
			stereo[i*2]=left?sound[i]:0;
			stereo[i*2+1]=left?sound[i+1]:0;
			stereo[i*2+2]=right?sound[i]:0;
			stereo[i*2+3]=right?sound[i+1]:0;
		}
		return stereo;
	}
	
	public static byte[] mergeLeftRight(byte [] left, byte [] right){
		byte [] result=new byte [left.length];
		for (int i=0; i<left.length; i++)
			result[i]= (byte) (left[i]|right[i]);
		return result;
	}
}

