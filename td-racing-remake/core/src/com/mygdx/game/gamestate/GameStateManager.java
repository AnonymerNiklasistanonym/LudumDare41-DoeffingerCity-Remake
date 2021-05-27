package com.mygdx.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
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
   * Constructor that creates a new GameState stack
   */
  public GameStateManager() {
    Gdx.app.log("game_state_manager:constructor", MainGame.getCurrentTimeStampLogString());
    this.gameStateStack = new Stack<>();
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
    this.popGameState();
    this.pushState(gameState);
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
  public void render(final SpriteBatch spriteBatch) {
    if (!gameStateStack.empty()) {
      gameStateStack.peek().render(spriteBatch);
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