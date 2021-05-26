package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// set window width and height
		config.height = MainGame.GAME_HEIGHT;
		config.width = MainGame.GAME_WIDTH;

		// set window title
		config.title = MainGame.GAME_NAME;
		config.fullscreen = false;

		// set window icon
		for (int size : MainGame.GAME_ICON_SIZES) {
			config.addIcon(MainGame.getGameIconFilePath(size), Files.FileType.Internal);
		}

		new LwjglApplication(new MainGame(), config);
	}
}
