package com.mygdx.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerCallbackVariables;
import com.mygdx.game.preferences.PreferencesManager;

/**
 * Abstract class that contains methods that every GameState class needs to implement to seamlessly
 * work with the GameStateManager class
 */
public abstract class GameState extends ControllerCallbackVariables {

  /**
   * The name of the game state
   */
  final public String stateName;
  /**
   * Game screen camera
   */
  protected final OrthographicCamera camera;
  /**
   * Game state manager
   */
  protected final GameStateManager gameStateManager;
  /**
   * The global asset manager to load and get resources (it uses reference counting to easily
   * dispose not needed resource any more after they were unloaded)
   */
  protected final AssetManager assetManager;
  /**
   * Preferences manager
   */
  protected final PreferencesManager preferencesManager;
  /**
   * The current cursor position
   */
  protected final Vector3 cursorPosition;
  /**
   * Indicator if all assets are already loaded
   */
  protected boolean assetsLoaded = false;
  /**
   * Indicator if the application is currently paused
   */
  protected boolean paused = false;
  /**
   * Progress tracker for asset loading that contains the last progress loading percentage (0-1.0)
   */
  protected float assetsLoadedLastProgress = -1;

  /**
   * Constructor
   *
   * @param gameStateManager The game state manager
   * @param stateName        The name of the game state
   */
  protected GameState(final GameStateManager gameStateManager, final String stateName) {
    Gdx.app.log("game_state:constructor",
        MainGame.getCurrentTimeStampLogString() + "create new state: \"" + stateName + "\"");
    this.stateName = stateName;
    this.gameStateManager = gameStateManager;
    this.camera = new OrthographicCamera();

    // Get the asset and preferences manager from the game state manager
    assetManager = this.gameStateManager.getAssetManager();
    preferencesManager = this.gameStateManager.getPreferencesManager();

    // Initialize variable for the cursor position
    cursorPosition = new Vector3();
  }

  /**
   * Handle input
   */
  protected abstract void handleInput();

  /**
   * Update everything to the current frame
   *
   * @param deltaTime the time span between the current frame and the last frame in seconds
   */
  protected abstract void update(final float deltaTime);

  /**
   * Render method
   *
   * @param spriteBatch a batch/collection of draw calls for rendering with OpenGL
   */
  protected abstract void render(final SpriteBatch spriteBatch);

  /**
   * Dispose any resource for a better memory management
   */
  protected abstract void dispose();

  /**
   * Callback in case the state is paused
   */
  public abstract void pause();

  /**
   * Callback in case the state is resumed
   */
  public abstract void resume();

  /**
   * Method that unloads all given resources with additional logging. Unloading a resource reduces
   * the reference (when assetManager.get() is called it increases the reference count) - when the
   * count is zero the resource is automatically disposed by the asset manager.
   */
  protected void unloadAssetManagerResources(final String[] resourcesToUnload) {
    Gdx.app.debug("game_state:unloadAssetManagerResources", "Loaded assets before unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("game_state:unloadAssetManagerResources", "- " + loadedAsset);
    }
    for (final String resourceToUnload : resourcesToUnload) {
      assetManager.unload(resourceToUnload);
    }
    Gdx.app.debug("game_state:unloadAssetManagerResources", "Loaded assets after unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("game_state:unloadAssetManagerResources", "- " + loadedAsset);
    }
  }

}
