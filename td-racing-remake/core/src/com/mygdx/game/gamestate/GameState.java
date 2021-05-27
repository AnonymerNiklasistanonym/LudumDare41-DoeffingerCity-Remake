package com.mygdx.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;

/**
 * Abstract class that contains methods that every GameState class needs to implement to seamlessly
 * work with the GameStateManager class
 */
public abstract class GameState {

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

}
