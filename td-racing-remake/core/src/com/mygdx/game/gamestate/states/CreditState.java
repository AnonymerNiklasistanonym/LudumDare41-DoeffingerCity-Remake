package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.one_click.ControllerCallbackGenericOneClick;
import com.mygdx.game.controller.one_click.IControllerCallbackGenericOneClick;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.unsorted.PreferencesManager;

public class CreditState extends GameState implements IControllerCallbackGenericOneClick {

  private static final String STATE_NAME = "Credits";
  private static final String[] textCredits = new String[]{"THIS GAME WAS MADE BY", "DANIEL CZEPPEL",
      "NIKLAS MIKELER", "PATRICK ULMER", "",
      "MUSIC BY SASCHA CZEPPEL"};
  /**
   * Variable for the font scale of the credits text
   */
  private static final float fontScaleCredits = 0.5f;
  private final ControllerCallbackGenericOneClick controllerCallbackGenericOneClick;
  private Vector2[] textContentPosition;
  /**
   * Variable for the texture of the stars background
   */
  private Music musicBackground;
  /**
   * Variable for the font of the credits text
   */
  private BitmapFont fontCredits;
  private final boolean goToHighscoreListState;

  public CreditState(final GameStateManager gameStateManager) {
    this(gameStateManager, false);
  }

  public CreditState(final GameStateManager gameStateManager, final boolean goToHighscoreListState) {
    super(gameStateManager, STATE_NAME);

    this.goToHighscoreListState = goToHighscoreListState;

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Load assets that are not necessary to be available just yet
    assetManager.load(MainGame.getGameMusicFilePath("theme"), Music.class);
    assetManager.load(MainGame.getGameFontFilePath("cornerstone_upper_case_big"), BitmapFont.class);

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
        GameStateManager.toggleFullScreen();
      }
    }

    // Turn music on/off
    if (controllerToggleMusicPressed || Gdx.input.isKeyJustPressed(Keys.M)) {
      controllerToggleMusicPressed = false;
      gameStateManager.getPreferencesManager().setMusicOn(!gameStateManager.getPreferencesManager().getMusicOn());
      if (gameStateManager.getPreferencesManager().getMusicOn()) {
        musicBackground.play();
      } else {
        musicBackground.stop();
      }
    }
    // Turn sound effects on/off
    if (controllerToggleSoundEffectsPressed || Gdx.input.isKeyJustPressed(Keys.U)) {
      controllerToggleSoundEffectsPressed = false;
      gameStateManager.getPreferencesManager().setSoundEffectsOn(!gameStateManager.getPreferencesManager().getSoundEfectsOn());
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
  protected void render(final SpriteBatch spriteBatch) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if (assetManager.update()) {
      if (!assetsLoaded) {
        float progress = assetManager.getProgress() * 100;
        Gdx.app.debug("credit_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
        assetsLoaded = true;
        musicBackground = assetManager.get(MainGame.getGameMusicFilePath("theme"));
        musicBackground.setLooping(true);
				if (gameStateManager.getPreferencesManager().getMusicOn()) {
					musicBackground.play();
				}

        // set font scale to the correct size and disable to use integers for scaling
        fontCredits = assetManager.get(MainGame.getGameFontFilePath("cornerstone_upper_case_big"));
        fontCredits.setUseIntegerPositions(false);
        fontCredits.getData().setScale(fontScaleCredits);
        // calculate the text positions so that every line is centered
        textContentPosition = GameStateManager.calculateCenteredMultiLineTextPositions(fontCredits,
            textCredits, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
      }
      // Render credits
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();

      // render the text that should be displayed
			for (int i = 0; i < textCredits.length; i++) {
				fontCredits.draw(spriteBatch, textCredits[i], textContentPosition[i].x,
						textContentPosition[i].y);
			}

      spriteBatch.end();
    } else {
      // display loading information
      float progress = assetManager.getProgress() * 100;
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("credit_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
      }
    }
  }

  @Override
  protected void dispose() {
    // Remove controller listener
    Controllers.removeListener(controllerCallbackGenericOneClick);

    // Reduce the reference to used resources in this state (when no object is referencing the
    // resource any more it is automatically disposed by the global asset manager)
    Gdx.app.debug("credit_state:dispose", "Loaded assets before unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("credit_state:dispose", "- " + loadedAsset);
    }
    assetManager.unload(MainGame.getGameMusicFilePath("theme"));
    assetManager.unload(MainGame.getGameFontFilePath("cornerstone_upper_case_big"));
    Gdx.app.debug("credit_state:dispose", "Loaded assets after unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("credit_state:dispose", "- " + loadedAsset);
    }
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
