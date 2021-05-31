package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.one_click.ControllerCallbackGenericOneClick;
import com.mygdx.game.controller.one_click.IControllerCallbackGenericOneClick;
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
   * Variable tzo keep track of the achieved score
   */
  private final int score;
  /**
   * Variable for the texture of the game won background
   */
  private Texture backgroundGameWon;
  /**
   * Variable for the sound victory of the game won background
   */
  private Sound soundVictory;
  /**
   * Indicator if all assets are already loaded
   */
  private boolean assetsLoaded = false;
  /**
   * Indicator if the application is currently paused
   */
  private boolean paused = false;
  /**
   * Progress tracker for asset loading that contains the last progress loading percentage (0-1.0)
   */
  private float assetsLoadedLastProgress = -1;
  /**
   * Tracker if any controller key was pressed (besides the full screen toggle keys)
   */
  private boolean controllerAnyKeyWasPressed = false;
  /**
   * Tracker if a controller full screen toggle key was pressed
   */
  private boolean controllerFullScreenToggleKeyPressed = false;
  /**
   * Tracker if a controller music toggle key was pressed
   */
  private boolean controllerToggleMusicPressed = false;
  /**
   * Tracker if a controller sound effects toggle key was pressed
   */
  private boolean controllerToggleSoundEffectsPressed = false;

  /**
   * Constructor that creates the game won (state)
   *
   * @param gameStateManager The global game state manager
   */
  public GameWonState(GameStateManager gameStateManager, final int score) {
    super(gameStateManager, STATE_NAME);
    this.score = score;

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Get asset manager from the game state manager
    assetManager = gameStateManager.getAssetManager();
    // Load assets that are not necessary to be available just yet
    assetManager.load(MainGame.getGameBackgroundFilePath("game_won"), Texture.class);
    assetManager.load(MainGame.getGameSoundFilePath("victory"), Sound.class);
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

    if (Gdx.app.getType() == ApplicationType.Desktop) {
      // Toggle full screen when full screen keys are pressed
      if (controllerFullScreenToggleKeyPressed || Gdx.input.isKeyJustPressed(Keys.F11)) {
        controllerFullScreenToggleKeyPressed = false;
        GameStateManager.toggleFullScreen();
      }
    }

    // If a button is touched or the space or enter key is currently pressed or any controller
    // key is currently pressed go to the highscore list which then redirects to the credits
    if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input
        .isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
        || controllerAnyKeyWasPressed || Gdx.input.isCatchKey(Keys.BACK)) {
      gameStateManager.setGameState(new CreateHighscoreEntryState(gameStateManager, score, true));
    }
  }

  @Override
  public void update(final float deltaTime) {
    // Not necessary to do anything
  }

  @Override
  public void render(final SpriteBatch spriteBatch) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if (assetManager.update()) {
      if (!assetsLoaded) {
        float progress = assetManager.getProgress() * 100;
        Gdx.app.debug("game_won_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
        assetsLoaded = true;
        backgroundGameWon = assetManager.get(MainGame.getGameBackgroundFilePath("game_won"));
        soundVictory = assetManager.get(MainGame.getGameSoundFilePath("victory"));
        if (gameStateManager.getPreferencesManager().getSoundEfectsOn()) {
          soundVictory.play();
        }
      }
      // Render menu
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();

      spriteBatch.draw(backgroundGameWon, 0, 0);

      spriteBatch.end();
    } else {
      // display loading information
      float progress = assetManager.getProgress() * 100;
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("game_won_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
      }
    }
  }

  @Override
  public void dispose() {
    // Remove controller listener
    Controllers.removeListener(controllerCallbackGenericOneClick);
    backgroundGameWon.dispose();
    soundVictory.dispose();

    // Reduce the reference to used resources in this state (when no object is referencing the
    // resource any more it is automatically disposed by the global asset manager)
    Gdx.app.debug("game_won_state:dispose", "Loaded assets before unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("game_won_state:dispose", "- " + loadedAsset);
    }
    assetManager.unload(MainGame.getGameBackgroundFilePath("game_won"));
    assetManager.unload(MainGame.getGameSoundFilePath("victory"));
    Gdx.app.debug("game_won_state:dispose", "Loaded assets after unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("game_won_state:dispose", "- " + loadedAsset);
    }
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
    controllerFullScreenToggleKeyPressed = true;
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
