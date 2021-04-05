package com.brackeen.javagamelearn.tilegame.sprites;

import com.brackeen.javagamelearn.graphics.Animation;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Fly extends Creature {

	//Random x=new Random();
    public Fly(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.2f;
    	//return x.nextFloat();
    }


    public boolean isFlying() {
        return isAlive();
    }

}
