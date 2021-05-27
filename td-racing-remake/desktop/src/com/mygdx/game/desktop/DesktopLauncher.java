package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MainGame;

public class DesktopLauncher {

  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

    // Set custom window properties
    config.height = MainGame.GAME_HEIGHT;
    config.width = MainGame.GAME_WIDTH;
    config.title = MainGame.GAME_NAME;
    config.fullscreen = false;
    for (int size : MainGame.GAME_ICON_SIZES) {
      config.addIcon(MainGame.getGameIconFilePath(size), Files.FileType.Internal);
    }

    // The following line is necessary for a safe exit when pressing the window close button
    // (https://gamedev.stackexchange.com/a/109253)
    config.forceExit = false;

    new LwjglApplication(new MainGame(), config);
  }
}
