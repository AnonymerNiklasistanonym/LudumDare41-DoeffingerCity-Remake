package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.generic.one_click.ControllerCallbackGenericOneClick;
import com.mygdx.game.controller.generic.one_click.IControllerCallbackGenericOneClick;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;

/**
 * Creates the game over state which renders the a menu and handles keyboard, touch and controller
 * input
 */
public class GameWonState extends GameState implements IControllerCallbackGenericOneClick {

  /**
   * The game state name for this game state
   */
  private static final String STATE_NAME = "GameWon";
  /**
   * Controller callback class that gets this class in its constructor which implements some
   * callback methods and can then be added as a controller listener which can then call the
   * interface implemented methods in this class on corresponding controller input
   */
  private final ControllerCallbackGenericOneClick controllerCallbackGenericOneClick;
  /**
   * The global asset manager to load and get resources (it uses reference counting to easily
   * dispose not needed resource any more after they were unloaded)
   */
  private final AssetManager assetManager;
  /**
   * Variable to keep track of the achieved score
   */
  private final int score;
  /**
   * Variable to keep track of the reached level
   */
  private final int level;
  /**
   * Variable to keep track of the achieved laps
   */
  private final int laps;
  /**
   * Variable for the texture of the game won background
   */
  private Texture backgroundGameWon;
  /**
   * Variable for the sound victory of the game won background
   */
  private Sound soundVictory;

  private static final String ASSET_ID_BACKGROUND_GAME_WON = MainGame.getGameBackgroundFilePath("game_won");
  private static final String ASSET_ID_SOUND_VICTORY = MainGame.getGameSoundFilePath("victory");

  /**
   * Constructor that creates the game won (state)
   *
   * @param gameStateManager The global game state manager
   */
  public GameWonState(GameStateManager gameStateManager, final int score, final int level,
      final int laps) {
    super(gameStateManager, STATE_NAME);
    this.score = score;
    this.level = level;
    this.laps = laps;

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Get asset manager from the game state manager
    assetManager = gameStateManager.getAssetManager();
    // Load assets that are not necessary to be available just yet
    assetManager.load(ASSET_ID_BACKGROUND_GAME_WON, Texture.class);
    assetManager.load(ASSET_ID_SOUND_VICTORY, Sound.class);

    // Register controller callback so that controller input can be managed
    controllerCallbackGenericOneClick = new ControllerCallbackGenericOneClick(this);
    Controllers.addListener(controllerCallbackGenericOneClick);
  }

  @Override
  public void handleInput() {
    if (paused || !assetsLoaded) {
      // When the game is paused or still loading assets don't handle anything
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

    // If a button is touched or the space or enter key is currently pressed or any controller
    // key is currently pressed go to the highscore list which then redirects to the credits
    if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input
        .isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
        || controllerAnyKeyWasPressed || Gdx.input.isCatchKey(Keys.BACK)) {
      if (preferencesManager.scoreIsInTop5(score)) {
        gameStateManager.setGameState(
            new CreateHighscoreEntryState(gameStateManager, score, level, laps, true));
      } else {
        gameStateManager.setGameState(new CreditState(gameStateManager, true));
      }
    }
  }

  @Override
  public void update(final float deltaTime) {
    // Not necessary to do anything
  }

  @Override
  public void render(final SpriteBatch spriteBatch, final ShapeRenderer shapeRenderer) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if (assetManager.update()) {
      if (!assetsLoaded) {
        assetsLoaded = true;
        Gdx.app.debug("game_won_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loaded:");
        getDebugOutputLoadedAssets();

        backgroundGameWon = assetManager.get(ASSET_ID_BACKGROUND_GAME_WON);
        soundVictory = assetManager.get(ASSET_ID_SOUND_VICTORY);
        if (gameStateManager.getPreferencesManager().getSoundEffectsOn()) {
          soundVictory.play();
        }
      }
      // Render menu
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();

      spriteBatch.draw(backgroundGameWon, 0, 0);

      spriteBatch.end();
    } else {
      // Get and render loading information
      float progress = assetManager.getProgress();
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("game_won_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + (progress * 100) + "%");
      }
      drawLoadingProgress(spriteBatch, shapeRenderer, progress);
    }
  }

  @Override
  public void dispose() {
    // Remove controller listener
    Controllers.removeListener(controllerCallbackGenericOneClick);

    // Reduce the reference to used resources in this state (when no object is referencing the
    // resource any more it is automatically disposed by the global asset manager)
    unloadAssetManagerResources(new String[]{
        ASSET_ID_BACKGROUND_GAME_WON,
        ASSET_ID_SOUND_VICTORY,
    });
  }

  @Override
  public void pause() {
    Gdx.app.debug("game_won_state:pause", MainGame.getCurrentTimeStampLogString() + "pause");
    paused = true;
  }

  @Override
  public void resume() {
    Gdx.app.debug("game_won_state:resume", MainGame.getCurrentTimeStampLogString() + "resume");
    paused = false;
  }


  @Override
  public void controllerCallbackClickAnyButton() {
    Gdx.app.debug("game_won_state:controllerCallbackClickAnyButton",
        MainGame.getCurrentTimeStampLogString());
    controllerAnyKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    Gdx.app.debug("game_won_state:controllerCallbackToggleFullScreen",
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
