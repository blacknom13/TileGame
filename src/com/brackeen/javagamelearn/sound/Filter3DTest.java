package com.brackeen.javagamelearn.sound;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

import com.brackeen.javagamelearn.graphics.Animation;
import com.brackeen.javagamelearn.graphics.Sprite;
import com.brackeen.javagamelearn.input.GameAction;
import com.brackeen.javagamelearn.input.InputManager;
import com.brackeen.javagamelearn.test.GameCore;
import com.brackeen.javagamelearn.util.LoopingByteInputStream;


public class Filter3DTest extends GameCore {

	public static void main(String [] args){
		new Filter3DTest().run();
	}
	
	private Sprite fly;
	private Sprite listener;
	private Sprite fakeListener;
	private InputManager manager;
	private GameAction exit;
	
	private SimpleSoundPlayer bzzsound;
	private SimpleSoundPlayer bzzStereo;
	private InputStream bzzInputStream;
	private InputStream bzzInputStreamStereoLeft;
	private InputStream bzzInputStreamStereoRight;
	
	public void init(){
		super.init();
		
		exit=new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		manager=new InputManager(screen.getFullScreenWindow());
		manager.mapToKey(exit, KeyEvent.VK_ESCAPE);
		manager.setCursor(manager.INVISIBLE_CURSOR);
		createSprites();
		bzzsound=new SimpleSoundPlayer("sounds/fly-bzz.wav");
		Filter3d filter=new Filter3d(fly, listener, screen.getHeight());
		Filter3d stereoLeft=new Filter3d(fly,listener, screen.getHeight()-listener.getWidth()*3);
		Filter3d stereoRight=new Filter3d(fly,fakeListener, screen.getHeight()-listener.getWidth()*3);
		//bzzInputStream=new FilteredSoundStream(new LoopingByteInputStream(bzzsound.getSamples()), filter);
		bzzInputStreamStereoLeft=new FilteredSoundStream(new LoopingByteInputStream(
				SoundFilterX.convertToStereo(bzzsound.getSamples(), true, false)),stereoLeft);
		bzzInputStreamStereoRight=new FilteredSoundStream(new LoopingByteInputStream(
				SoundFilterX.convertToStereo(bzzsound.getSamples(), false, true)),stereoRight);
		
	//	bzzInputStream=new FilteredSoundStream(new LoopingByteInputStream(buf), filter);
		new Thread(){
			public void run(){
				bzzsound.play(bzzInputStreamStereoRight, true);
			}
		}.start();
		
		new Thread(){
			public void run(){
				bzzsound.play(bzzInputStreamStereoLeft,true);
			}
		}.start();
		
	}
	
	private void createSprites(){
		Animation anim=new Animation();
		Image fly1=loadImage("images/fly1.png");
		Image fly2=loadImage("images/fly2.png");
		Image fly3=loadImage("images/fly3.png");
		Image ear=loadImage("images/ear.png");
		
		anim.addFrame(fly1, 50);
		anim.addFrame(fly2, 50);
		anim.addFrame(fly3, 50);
		anim.addFrame(fly2, 50);
		
		fly=new Sprite(anim);
		
		anim=new Animation();
		anim.addFrame(ear, 0);
		listener=new Sprite(anim);
		listener.setX((screen.getWidth()-listener.getWidth())/2);
		listener.setY((screen.getHeight()-listener.getHeight())/2);
		anim=new Animation();
		anim.addFrame(null, 0);
		fakeListener=new Sprite(anim);
		fakeListener.setX((screen.getWidth()+listener.getWidth())/2);
		fakeListener.setY((screen.getHeight()-listener.getHeight())/2);
	}
	
	public void update(long elapsedTime){
		if (exit.isPressed()){
			stop();
		}else{
			listener.update(elapsedTime);
			fly.update(elapsedTime);
			fly.setX(manager.getMouseX());
			fly.setY(manager.getMouseY());
		}
	}
	
	public void stop(){
		super.stop();
		try{
			//bzzInputStream.close();
			bzzInputStreamStereoLeft.close();
			bzzInputStreamStereoRight.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Graphics2D g2d) {
		// TODO Auto-generated method stub
		g2d.setColor(new Color(0x33cc33));
		g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		g2d.drawImage(listener.getImage(),Math.round( listener.getX()),Math.round(listener.getY()), null);
		g2d.drawImage(fly.getImage(), Math.round(fly.getX()), Math.round(fly.getY()), null);
	}
	
}
