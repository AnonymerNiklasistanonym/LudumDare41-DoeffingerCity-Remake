package com.mygdx.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.preferences.PreferencesManager;
import java.util.Stack;

/**
 * Class that manages all GameStates which also means a simple input handling, updating, rendering
 * and disposing of content
 */
public class GameStateManager {

  /**
   * Stack of GameStates
   */
  private final Stack<GameState> gameStateStack;
  /**
   * Loads and stores assets like textures, bitmap fonts, sounds, music, ...
   */
  private final AssetManager assetManager;
  /**
   * Manages application wide options and preferences
   */
  private final PreferencesManager preferencesManager;

  /**
   * Constructor that creates a new GameState stack
   */
  public GameStateManager(final AssetManager assetManager,
      final PreferencesManager preferencesManager) {
    Gdx.app.log("game_state_manager:constructor", MainGame.getCurrentTimeStampLogString());
    this.gameStateStack = new Stack<>();
    this.assetManager = assetManager;
    this.preferencesManager = preferencesManager;
  }

  /**
   * Get the current mouse position
   *
   * @param camera the game state camera
   * @return the current mouse position
   */
  public static Vector3 getMousePosition(final Camera camera) {
    return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
  }

  /**
   * Get the coordinates to render text centered
   *
   * @param font   BitmapFont which will be used to draw the text
   * @param text   text that should be displayed
   * @param width  width of screen
   * @param height height of screen
   * @return x and y coordinate for drawing the text
   */
  public static Vector2 calculateCenteredTextPosition(final BitmapFont font, final String text,
      final float width,
      final float height) {
    final GlyphLayout temp = new GlyphLayout(font, text);
    return new Vector2(width / 2 - temp.width / 2, height / 2 + temp.height / 2);
  }

  /**
   * Get the coordinates to render text centered
   *
   * @param font   BitmapFont which will be used to draw the text
   * @param text   array of text that should be displayed
   * @param width  width of screen
   * @param height height of screen
   * @return x and y coordinates for drawing the array of text
   */
  public static Vector2[] calculateCenteredMultiLineTextPositions(final BitmapFont font,
      final String[] text,
      final float width, final float height) {
    final Vector2[] positions = new Vector2[text.length];
    for (int i = 0; i < text.length; i++) {
      final GlyphLayout temp = new GlyphLayout(font, text[i]);
      positions[i] = new Vector2(width / 2 - temp.width / 2,
          height / (text.length + 1) * (text.length - i) + temp.height / 2);
    }
    return positions;
  }

  /**
   * Get global asset manager
   */
  public AssetManager getAssetManager() {
    Gdx.app.log("game_state_manager:getAssetManager", MainGame.getCurrentTimeStampLogString());
    return assetManager;
  }

  /**
   * Get global preferences manager
   */
  public PreferencesManager getPreferencesManager() {
    Gdx.app
        .log("game_state_manager:getPreferencesManager", MainGame.getCurrentTimeStampLogString());
    return preferencesManager;
  }

  /**
   * Toggle full screen
   */
  public void toggleFullScreen() {
    preferencesManager.setFullscreen(!Gdx.graphics.isFullscreen());
    if (Gdx.graphics.isFullscreen()) {
      Gdx.graphics.setWindowedMode(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
    } else {
      Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }
  }

  /**
   * Push a new state on the stack
   *
   * @param gameState Game state that is run after the current game state
   */
  public void pushState(final GameState gameState) {
    Gdx.app.log("game_state_manager:pushState",
        MainGame.getCurrentTimeStampLogString() + gameState.stateName);
    gameStateStack.push(gameState);
  }

  /**
   * Set instantly a new state
   *
   * @param gameState Game state that will be run instantly after popping the current game state
   */
  public void setGameState(final GameState gameState) {
    Gdx.app.log("game_state_manager:setGameState",
        MainGame.getCurrentTimeStampLogString() + gameState.stateName);
    popGameState();
    pushState(gameState);
  }

  /**
   * Directly dispose state after popping/removing it
   */
  public void popGameState() {
    if (!gameStateStack.empty()) {
      Gdx.app.log("game_state_manager:popGameState",
          MainGame.getCurrentTimeStampLogString() + gameStateStack.peek().stateName);
      gameStateStack.pop().dispose();
    } else {
      Gdx.app.error("game_state_manager:popGameState",
          MainGame.getCurrentTimeStampLogString() + "no game state was found in stack");
    }
  }

  /**
   * Update everything (input and then updates)
   *
   * @param deltaTime the time span between the current frame and the last frame in seconds
   */
  public void update(final float deltaTime) {
    if (!gameStateStack.empty()) {
      gameStateStack.peek().handleInput();
      gameStateStack.peek().update(deltaTime);
    } else {
      Gdx.app.error("game_state_manager:popGameState",
          MainGame.getCurrentTimeStampLogString() + "no game state was found in stack");
    }
  }

  /**
   * Render everything
   *
   * @param spriteBatch a batch/collection of draw calls for rendering with OpenGL
   */
  public void render(final SpriteBatch spriteBatch, final ShapeRenderer shapeRenderer) {
    if (!gameStateStack.empty()) {
      gameStateStack.peek().render(spriteBatch, shapeRenderer);
    } else {
      Gdx.app.error("game_state_manager:render",
          MainGame.getCurrentTimeStampLogString() + "no game state was found in stack");
    }
  }

  public void pause() {
    if (!gameStateStack.empty()) {
      gameStateStack.peek().pause();
    } else {
      Gdx.app.error("game_state_manager:pause",
          MainGame.getCurrentTimeStampLogString() + "no game state was found in stack");
    }
  }

  public void resume() {
    if (!gameStateStack.empty()) {
      gameStateStack.peek().resume();
    } else {
      Gdx.app.error("game_state_manager:resume",
          MainGame.getCurrentTimeStampLogString() + "no game state was found in stack");
    }
  }

}