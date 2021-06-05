package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.generic.one_click.ControllerCallbackGenericOneClick;
import com.mygdx.game.controller.generic.one_click.IControllerCallbackGenericOneClick;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;

public class CreditState extends GameState implements IControllerCallbackGenericOneClick {

  private static final String STATE_NAME = "Credits";
  private static final String[] TEXT_CREDITS = new String[]{
      "THIS GAME WAS MADE BY",
      "DANIEL CZEPPEL",
      "NIKLAS MIKELER",
      "PATRICK ULMER",
      "",
      "MUSIC BY SASCHA CZEPPEL"
  };
  /**
   * Variable for the font scale of the credits text
   */
  private static final float FONT_SCALE_CREDITS = 0.5f;
  private static final String ASSET_MANAGER_ID_MUSIC_THEME = MainGame.getGameMusicFilePath("theme");
  private static final String ASSET_MANAGER_ID_FONT_CREDITS = MainGame
      .getGameFontFilePath("cornerstone_upper_case_big");

  private final ControllerCallbackGenericOneClick controllerCallbackGenericOneClick;
  private final boolean goToHighscoreListState;
  private Vector2[] textContentPosition;
  /**
   * Variable for the texture of the stars background
   */
  private Music musicBackground;
  /**
   * Variable for the font of the credits text
   */
  private BitmapFont fontCredits;

  public CreditState(final GameStateManager gameStateManager) {
    this(gameStateManager, false);
  }

  public CreditState(final GameStateManager gameStateManager,
      final boolean goToHighscoreListState) {
    super(gameStateManager, STATE_NAME);

    // Save if on click or back the next state should be the highscore list instead of the menu
    this.goToHighscoreListState = goToHighscoreListState;

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Load assets that are not necessary to be available just yet
    assetManager.load(ASSET_MANAGER_ID_MUSIC_THEME, Music.class);
    assetManager.load(ASSET_MANAGER_ID_FONT_CREDITS, BitmapFont.class);

    // Register controller callback so that controller input can be managed
    controllerCallbackGenericOneClick = new ControllerCallbackGenericOneClick(this);
    Controllers.addListener(controllerCallbackGenericOneClick);
  }

  @Override
  protected void handleInput() {
    if (paused || !assetsLoaded) {
      // When the game is paused or assets not loaded don't handle anything
      return;
    }

    if (Gdx.app.getType() == ApplicationType.Desktop) {
      // Toggle full screen when full screen keys are pressed
      if (controllerToggleFullScreenPressed || Gdx.input.isKeyJustPressed(Keys.F11)) {
        controllerToggleFullScreenPressed = false;
        gameStateManager.toggleFullScreen();
      }
    }

    // Turn music on/off
    if (controllerToggleMusicPressed || Gdx.input.isKeyJustPressed(Keys.M)) {
      controllerToggleMusicPressed = false;
      preferencesManager.setMusicOn(!preferencesManager.getMusicOn());
      if (preferencesManager.getMusicOn()) {
        musicBackground.play();
      } else {
        musicBackground.stop();
      }
    }
    // Turn sound effects on/off
    if (controllerToggleSoundEffectsPressed || Gdx.input.isKeyJustPressed(Keys.U)) {
      controllerToggleSoundEffectsPressed = false;
      preferencesManager.setSoundEffectsOn(!preferencesManager.getSoundEffectsOn());
    }

    // If a button is touched or the space or enter key is currently pressed or any controller
    // key is currently pressed go back to the menu
    if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input
        .isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
        || controllerAnyKeyWasPressed || Gdx.input.isCatchKey(Keys.BACK)) {
      if (goToHighscoreListState) {
        gameStateManager.setGameState(new HighscoreListState(gameStateManager));
      } else {
        gameStateManager.setGameState(new MenuState(gameStateManager));
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
        Gdx.app.debug("credit_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + (assetManager.getProgress() * 100) + "%");
        assetsLoaded = true;
        musicBackground = assetManager.get(ASSET_MANAGER_ID_MUSIC_THEME);
        musicBackground.setLooping(true);
        if (preferencesManager.getMusicOn()) {
          musicBackground.play();
        }

        // set font scale to the correct size and disable to use integers for scaling
        fontCredits = assetManager.get(ASSET_MANAGER_ID_FONT_CREDITS);
        fontCredits.setUseIntegerPositions(false);
        fontCredits.getData().setScale(FONT_SCALE_CREDITS);
        // calculate the text positions so that every line is centered
        textContentPosition = GameStateManager.calculateCenteredMultiLineTextPositions(fontCredits,
            TEXT_CREDITS, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
      }
      // Render credits
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();

      // Render the credits (text)
      for (int i = 0; i < TEXT_CREDITS.length; i++) {
        fontCredits.draw(spriteBatch, TEXT_CREDITS[i], textContentPosition[i].x,
            textContentPosition[i].y);
      }

      spriteBatch.end();
    } else {
      // Get and render loading information
      float progress = assetManager.getProgress();
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("credit_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + (progress * 100) + "%");
      }
      drawLoadingProgress(spriteBatch, shapeRenderer, progress);
    }
  }

  @Override
  protected void dispose() {
    // Remove controller listener
    Controllers.removeListener(controllerCallbackGenericOneClick);

    // Reduce the reference to used resources in this state (when no object is referencing the
    // resource any more it is automatically disposed by the global asset manager)
    unloadAssetManagerResources(new String[]{
        ASSET_MANAGER_ID_MUSIC_THEME,
        ASSET_MANAGER_ID_FONT_CREDITS,
    });
  }

  @Override
  public void pause() {
    Gdx.app.debug("credit_state:pause", MainGame.getCurrentTimeStampLogString() + "pause");
    paused = true;
  }

  @Override
  public void resume() {
    Gdx.app.debug("credit_state:resume", MainGame.getCurrentTimeStampLogString() + "resume");
    paused = false;
  }

  @Override
  public void controllerCallbackClickAnyButton() {
    Gdx.app.debug("credit_state:controllerCallbackClickAnyButton",
        MainGame.getCurrentTimeStampLogString());
    controllerAnyKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    Gdx.app.debug("credit_state:controllerCallbackToggleFullScreen",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleFullScreenPressed = true;
  }

  @Override
  public void controllerCallbackToggleMusic() {
    Gdx.app.debug("menu_state:controllerCallbackToggleMusic",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleMusicPressed = true;
  }

  @Override
  public void controllerCallbackToggleSoundEffects() {
    Gdx.app.debug("menu_state:controllerCallbackToggleSoundEffects",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleSoundEffectsPressed = true;
  }
}
