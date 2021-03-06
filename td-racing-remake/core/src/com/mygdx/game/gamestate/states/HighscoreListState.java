package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.generic.one_click.ControllerCallbackGenericOneClick;
import com.mygdx.game.controller.generic.one_click.IControllerCallbackGenericOneClick;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.elements.HighscoreEntry;
import com.mygdx.game.preferences.PreferencesManager;

public class HighscoreListState extends GameState implements IControllerCallbackGenericOneClick {

  private static final String STATE_NAME = "HighscoreList";
  private static final float FONT_SCALE_DEVELOPER_INFO = 1;
  private static final String ASSET_MANAGER_ID_FONT_DEVELOPER_INFO = MainGame
      .getGameFontFilePath("cornerstone");

  private final ControllerCallbackGenericOneClick controllerCallbackGenericOneClick;
  private final int level;
  private final boolean goToGameOverState;
  private HighscoreEntry[] highscoreEntries;
  private BitmapFont fontDeveloperInfo;

  public HighscoreListState(GameStateManager gameStateManager) {
    this(gameStateManager, false, -1);
  }

  public HighscoreListState(GameStateManager gameStateManager, final boolean goToGameOverState,
      final int level) {
    super(gameStateManager, STATE_NAME);
    this.level = level;
    this.goToGameOverState = goToGameOverState;

    // Load assets that are not necessary to be available just yet
    if (MainGame.DEVELOPER_MODE) {
      assetManager.load(ASSET_MANAGER_ID_FONT_DEVELOPER_INFO, BitmapFont.class);
    }
    assetManager.load(HighscoreEntry.ASSET_MANAGER_ID_FONT, BitmapFont.class);
    assetManager.load(HighscoreEntry.ASSET_MANAGER_ID_TEXTURE, Texture.class);

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Register controller callback so that controller input can be managed
    controllerCallbackGenericOneClick = new ControllerCallbackGenericOneClick(this);
    Controllers.addListener(controllerCallbackGenericOneClick);
  }

  private void loadHighScoreList() {
    // Get the current highscore list from the preference manager
    preferencesManager.checkHighscore();

    // Create/Update highscore buttons
    PreferencesManager.HighscoreEntry[] entries = preferencesManager.retrieveHighscore();
    highscoreEntries = new HighscoreEntry[5];
    for (int i = 0; i < 5; i++) {
      highscoreEntries[i] = new HighscoreEntry(i + 1, entries[i].getScore(), entries[i].getLevel(),
          entries[i].getLaps(), entries[i].getName(),
          assetManager, (float) MainGame.GAME_WIDTH / 2,
          (float) MainGame.GAME_HEIGHT / 6 * (5 - i));
    }
  }

  @Override
  protected void handleInput() {
    if (paused || !assetsLoaded) {
      // When the game is paused or assets not loaded don't handle anything
      return;
    }

    // Toggle full screen when full screen keys are pressed on different platforms
    if (Gdx.app.getType() == ApplicationType.Desktop
        || Gdx.app.getType() == ApplicationType.WebGL) {
      if (controllerToggleFullScreenPressed || Gdx.input.isKeyJustPressed(Keys.F)) {
        controllerToggleFullScreenPressed = false;
        gameStateManager.toggleFullScreen();
      }
    }
    if (Gdx.app.getType() == ApplicationType.Desktop) {
      if (Gdx.input.isKeyJustPressed(Keys.F11)) {
        gameStateManager.toggleFullScreen();
      }
    }

    // Turn music on/off
    if (controllerToggleMusicPressed || Gdx.input.isKeyJustPressed(Keys.M)) {
      controllerToggleMusicPressed = false;
      gameStateManager.getPreferencesManager()
          .setMusicOn(!gameStateManager.getPreferencesManager().getMusicOn());
    }
    // Turn sound effects on/off
    if (controllerToggleSoundEffectsPressed || Gdx.input.isKeyJustPressed(Keys.U)) {
      controllerToggleSoundEffectsPressed = false;
      gameStateManager.getPreferencesManager()
          .setSoundEffectsOn(!gameStateManager.getPreferencesManager().getSoundEffectsOn());
    }

    // If a button is touched or the space or enter key is currently pressed or any controller
    // key is currently pressed go back to the menu
    if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input
        .isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
        || controllerAnyKeyWasPressed || Gdx.input.isCatchKey(Keys.BACK)) {
      if (goToGameOverState) {
        gameStateManager.setGameState(new GameOverState(gameStateManager, level));
      } else {
        gameStateManager.setGameState(new MenuState(gameStateManager));
      }
    }

    // Provide additional functionality when in developer mode
    if (MainGame.DEVELOPER_MODE) {
      // Reset the highscore list
      if (Gdx.input.isKeyJustPressed(Keys.C)) {
        preferencesManager.resetHighscore();
        loadHighScoreList();
      }
    }
  }

  @Override
  protected void update(final float deltaTime) {
    // Not necessary to do anything
  }

  @Override
  protected void render(final SpriteBatch spriteBatch, final ShapeRenderer shapeRenderer) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if (assetManager.update()) {
      if (!assetsLoaded) {
        assetsLoaded = true;
        Gdx.app.debug("highscore_list_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loaded:");
        getDebugOutputLoadedAssets();

        if (MainGame.DEVELOPER_MODE) {
          // set font scale to the correct size and disable to use integers for scaling
          fontDeveloperInfo = assetManager.get(ASSET_MANAGER_ID_FONT_DEVELOPER_INFO);
          fontDeveloperInfo.setUseIntegerPositions(false);
          fontDeveloperInfo.getData().setScale(FONT_SCALE_DEVELOPER_INFO);
        }

        // Load highscore list which updates the highscore "buttons"
        loadHighScoreList();
      }
      // Render credits
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();

      // Draw highscore (entry) "buttons"
      for (final HighscoreEntry highscoreEntry : highscoreEntries) {
        highscoreEntry.draw(spriteBatch);
      }

      // If in development mode provide a list with additional available features
      if (MainGame.DEVELOPER_MODE) {
        fontDeveloperInfo.getData().setScale(1);
        fontDeveloperInfo.setColor(1, 1, 1, 1);
        fontDeveloperInfo.draw(spriteBatch, "Reset list: C", 10, 30);
      }

      spriteBatch.end();
    } else {
      // Get and render loading information
      float progress = assetManager.getProgress();
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("highscore_list_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + (progress * 100) + "%");
      }
      drawLoadingProgress(spriteBatch, shapeRenderer, progress);
    }
  }

  @Override
  protected void dispose() {
    Controllers.removeListener(controllerCallbackGenericOneClick);
    HighscoreEntry.texture.dispose();
    for (final HighscoreEntry highscoreEntry : highscoreEntries) {
      highscoreEntry.dispose();
    }

    if (MainGame.DEVELOPER_MODE) {
      unloadAssetManagerResources(new String[]{
          ASSET_MANAGER_ID_FONT_DEVELOPER_INFO,
      });
    }
    unloadAssetManagerResources(new String[]{
        HighscoreEntry.ASSET_MANAGER_ID_FONT,
        HighscoreEntry.ASSET_MANAGER_ID_TEXTURE,
    });
  }

  @Override
  public void pause() {
    Gdx.app.debug("highscore_list_state:pause", MainGame.getCurrentTimeStampLogString() + "pause");
    paused = true;
  }

  @Override
  public void resume() {
    Gdx.app
        .debug("highscore_list_state:resume", MainGame.getCurrentTimeStampLogString() + "resume");
    paused = false;
  }

  @Override
  public void controllerCallbackClickAnyButton() {
    Gdx.app.debug("highscore_list_state:controllerCallbackClickAnyButton",
        MainGame.getCurrentTimeStampLogString());
    controllerAnyKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    Gdx.app.debug("highscore_list_state:controllerCallbackToggleFullScreen",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleFullScreenPressed = true;
  }

  @Override
  public void controllerCallbackToggleMusic() {
    Gdx.app.debug("highscore_list_state:controllerCallbackToggleMusic",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleMusicPressed = true;
  }

  @Override
  public void controllerCallbackToggleSoundEffects() {
    Gdx.app.debug("highscore_list_state:controllerCallbackToggleSoundEffects",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleSoundEffectsPressed = true;
  }
}
