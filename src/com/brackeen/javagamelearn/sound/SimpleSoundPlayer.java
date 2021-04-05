package com.brackeen.javagamelearn.sound;

import com.brackeen.javagamelearn.util.LoopingByteInputStream;

import java.io.*;

import javax.sound.sampled.*;


public class SimpleSoundPlayer {

	public static void main(String [] args){
		//jlap j=new jlap();
		//try{
			//j.play("sounds/fly-bzz.wav");}catch(IOException | JavaLayerException  e){}
		SimpleSoundPlayer player=new SimpleSoundPlayer("sounds/fly-bzz.wav");
		InputStream stream=new LoopingByteInputStream(player.getSamples());
		player.play(stream,false);
		System.exit(0);
	}
	
	private AudioFormat format;
	private AudioFormat stereoFormat;
	private byte [] samples;
	
	public SimpleSoundPlayer(String file){
		try{
			AudioInputStream stream=AudioSystem.getAudioInputStream(new File(file));
			format= stream.getFormat();
			samples=getSamples(stream);
			System.out.println(format.toString());
			stereoFormat=new AudioFormat(44100, 16, 2, true, false);
		}catch(UnsupportedAudioFileException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public byte[] getSamples(){
		return samples;
	}
	
	private byte[] getSamples(AudioInputStream input){
		int length= (int)(input.getFrameLength()*format.getFrameSize());
		
		byte [] samples=new byte [length];
		DataInputStream is=new DataInputStream(input);
		try{
			is.readFully(samples);
		}catch (IOException e){
			e.printStackTrace();
		}
		return samples;
	}
	
	public void play(InputStream source, boolean stereo){
		AudioFormat finalFormat;
		finalFormat= stereo?stereoFormat: format;
		
		int bufferSize= finalFormat.getFrameSize()*Math.round(finalFormat.getFrameRate()/10);
		byte [] buffer=new byte [bufferSize];
		SourceDataLine line;
		
		try{
			DataLine.Info info=new DataLine.Info(SourceDataLine.class, finalFormat);
			line=(SourceDataLine)AudioSystem.getLine(info);
			line.open(finalFormat, bufferSize);
		}catch (LineUnavailableException e){
			e.printStackTrace();
			return;
		}
		
		line.start();
		try{
			int numBytesRead=0;
			while(numBytesRead!=-1){
				numBytesRead=source.read(buffer, 0, buffer.length);
				if (numBytesRead!=-1){
					line.write(buffer, 0, numBytesRead);
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		line.drain();
		line.close();
	}
}
