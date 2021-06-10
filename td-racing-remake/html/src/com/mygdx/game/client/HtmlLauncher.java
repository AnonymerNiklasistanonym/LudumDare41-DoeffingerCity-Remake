package com.mygdx.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.mygdx.game.HtmlPlatformInfo;
import com.mygdx.game.MainGame;

public class HtmlLauncher extends GwtApplication {

  @Override
  public GwtApplicationConfiguration getConfig() {
    // Resizable application, uses available space in browser
    //return new GwtApplicationConfiguration(true);
    // Fixed size application:
    return new GwtApplicationConfiguration(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
  }

  @Override
  public ApplicationListener createApplicationListener() {
    return new MainGame(
        new HtmlPlatformInfo(agentInfo().isFirefox(), agentInfo().isChrome(), agentInfo().isLinux(),
            agentInfo().isWindows()));
  }
}