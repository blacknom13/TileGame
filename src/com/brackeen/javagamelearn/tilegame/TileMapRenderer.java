package com.brackeen.javagamelearn.tilegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import com.brackeen.javagamelearn.graphics.Sprite;
import com.brackeen.javagamelearn.tilegame.sprites.Creature;

/**
    The TileMapRenderer class draws a TileMap on the screen.
    It draws all tiles, sprites, and an optional background image
    centered around the position of the player.

    <p>If the width of background image is smaller the width of
    the tile map, the background image will appear to move
    slowly, creating a parallax background effect.

    <p>Also, three static methods are provided to convert pixels
    to tile positions, and vice-versa.

    <p>This TileMapRender uses a tile size of 64.
*/
public class TileMapRenderer {

    private static final int TILE_SIZE = 64;
    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int TILE_SIZE_BITS =6;

    private ArrayList<Image> backgrounds;

    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(int pixels) {
        // use shifting to get correct values for negative pixels
        return pixels >> TILE_SIZE_BITS;

        // or, for tile sizes that aren't a power of two,
        // use the floor function:
        //return (int)Math.floor((float)pixels / TILE_SIZE);
    }


    /**
        Converts a tile position to a pixel position.
    */
    public static int tilesToPixels(int numTiles) {
        // no real reason to use shifting here.
        // it's slighty faster, but doesn't add up to much
        // on modern processors.
        return numTiles << TILE_SIZE_BITS;

        // use this if the tile size isn't a power of 2:
        //return numTiles * TILE_SIZE;
    }


    /**
        Sets the background to draw.
    */
    public void setBackground(ArrayList <Image> background,int screenWidth, int screenHeight) {
    	this.backgrounds=new ArrayList<>();
        this.backgrounds = background;
        fitBackgrounds(screenWidth, screenHeight);
    }


    private void fitBackgrounds(int screenWidth, int screenHeight) {
		// TODO Auto-generated method stub
    	int scaleFitter=0;
		for (int i=0;i<backgrounds.size();i++){
			scaleFitter=i;
			backgrounds.set(i,
					backgrounds.get(i).getScaledInstance((int)(screenWidth * (2+scaleFitter)), screenHeight, 0));
		}
	}


	/**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g, TileMap map,
        int screenWidth, int screenHeight)
    {
        Sprite player = map.getPlayer();
        int mapWidth = tilesToPixels(map.getWidth());

        // get the scrolling position of the map
        // based on player's position
        int offsetX = screenWidth / 2 -
            Math.round(player.getX()) - TILE_SIZE;
        System.out.println(offsetX);
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);

        // get the y offset to draw all sprites and tiles
        int offsetY = screenHeight -
            tilesToPixels(map.getHeight());

        // draw black background, if needed
        if (backgrounds == null ||
            screenHeight > backgrounds.get(0).getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }

        // draw parallax background image
        if (backgrounds != null) {
        	for (int i=0;i<backgrounds.size();i++){
	            int x = offsetX *
	                (screenWidth - backgrounds.get(i).getWidth(null)) /
	                (screenWidth - mapWidth);
	            int y = screenHeight - backgrounds.get(i).getHeight(null);
	
	            g.drawImage(backgrounds.get(i), x, y, null);
        	}
        }

        // draw the visible tiles
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX +
            pixelsToTiles(screenWidth) + 1;
        for (int y=0; y<map.getHeight(); y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }

        // draw player
        g.drawImage(player.getImage(),
            Math.round(player.getX()) + offsetX,
            Math.round(player.getY()) + offsetY,
            null);

        // draw sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);

            // wake up the creature when it's on screen
            if (sprite instanceof Creature &&
                x >= 0 && x < screenWidth)
            {
                ((Creature)sprite).wakeUp();
            }
        }
    }

}
