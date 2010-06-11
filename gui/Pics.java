/*
 * Created on 13.01.2008
 *
 */
package gui;

import java.io.IOException;

import javax.microedition.lcdui.Image;

public class Pics {
	public static Image FISH;
	public static Image STAR;
	public static Image VEGETARIAN;
	public static Image PORK;
	public static Image BEEF;
	public static Image MENSAFUCHS;
	public static Image SHEEP;
	public static Image GAME;
	public static Image CHICKEN;
	public static Image CAMERA;
	public static Image UNKNOWN;
	public static Image TOP;
	
	public static void loadPictures() {
		try {
			STAR = Image.createImage("/res/star-icon.png");
			VEGETARIAN = Image.createImage("/res/veg-icon.png");
			PORK = Image.createImage("/res/pork-icon.png");
			BEEF = Image.createImage("/res/beef-icon.png");
			MENSAFUCHS = Image.createImage("/res/mensafuchs-big.png");
			FISH = Image.createImage("/res/fish-icon.png");
			SHEEP = Image.createImage("/res/sheep-icon.png");
			GAME = Image.createImage("/res/game-icon.png");
			CHICKEN = Image.createImage("/res/chicken-icon.png");
			CAMERA = Image.createImage("/res/camera-icon.png");
			UNKNOWN = Image.createImage("/res/unknown-icon.png");
			TOP = Image.createImage("/res/top-icon.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
