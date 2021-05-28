package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.MenuState;
import java.sql.Timestamp;
import java.util.Date;

public class MainGame implements ApplicationListener {

  /**
   * Height of the game screen
   */
  public final static int GAME_HEIGHT = 720;
  /**
   * Width of the game screen
   */
  public final static int GAME_WIDTH = 1280;
  /**
   * Name of the game
   */
  public final static String GAME_NAME = "TnT (Tracks `n Towers)";
  /**
   * The provided icon sizes
   */
  public final static int[] GAME_ICON_SIZES = {16, 32, 64};

  /**
   * Indicator if release or development version of the game
   */
  public static final boolean DEVELOPER_MODE = true;

  /**
   * The game state manager that manages states, input handling, rendering
   */
  private GameStateManager gameStateManager;
  /**
   * A batch/collection of draw calls for rendering with OpenGL
   */
  private SpriteBatch spriteBatch;
  /**
   * Loads and stores assets like textures, bitmap fonts, sounds, music, ...
   */
  private AssetManager assetManager;

  // Remove fonts later when asset manager works begin
  /**
   * Font "cornerstone_70"
   */
  public static BitmapFont font70;
  /**
   * Font "cornerstone"
   */
  public static BitmapFont font;
  /**
   * Font "cornerstone_big"
   */
  public static BitmapFont fontBig;
  /**
   * Font "cornerstone_outline"
   */
  public static BitmapFont fontOutline;
  /**
   * Font "cornerstone_upper_case_big"
   */
  public static BitmapFont fontUpperCaseBig;
  // Remove fonts later when asset manager works end

  /**
   * Get the filepath of a game icon given its size
   *
   * @param iconSize The size of the game icon
   */
  public static String getGameIconFilePath(final int iconSize) {
    return "icon/icon_" + iconSize + ".png";
  }

  /**
   * Get the filepath of a game font given its name
   *
   * @param fontName The name of the game font
   */
  public static String getGameFontFilePath(final String fontName) {
    return "font/font_" + fontName + ".fnt";
  }

  /**
   * Get the filepath of a game button given its name
   *
   * @param buttonName The name of the game button
   */
  public static String getGameButtonFilePath(final String buttonName) {
    return "button/button_" + buttonName + ".png";
  }

  /**
   * Get the filepath of a game background given its name
   *
   * @param backgroundName The name of the game background
   */
  public static String getGameBackgroundFilePath(final String backgroundName) {
    return "background/background_" + backgroundName + ".png";
  }

  /**
   * Get the filepath of a game background given its name
   *
   * @param logoName The name of the game background
   */
  public static String getGameLogoFilePath(final String logoName) {
    return "logo/logo_" + logoName + ".png";
  }

  /**
   * Get a current time stamp string for logging behaviour
   */
  public static String getCurrentTimeStampLogString() {
    String timestamp = new Timestamp(new Date().getTime()).toString();
    return "(" + timestamp + "000".substring(timestamp.length() - 20) + ") ";
  }

  @Override
  public void create() {
    if (DEVELOPER_MODE) {
      Gdx.app.setLogLevel(Application.LOG_DEBUG);
    } else {
      Gdx.app.setLogLevel(Application.LOG_ERROR);
    }

    // Create sprite batch
    Gdx.app.log("main:create", getCurrentTimeStampLogString() + "create sprite batch");
    spriteBatch = new SpriteBatch();
    // Create asset manager
    assetManager = new AssetManager();

    // Load fonts (remove when asset manager works)
    Gdx.app.log("main:create", getCurrentTimeStampLogString() + "load fonts");
    font = new BitmapFont(Gdx.files.internal(getGameFontFilePath("cornerstone")));
    font.setUseIntegerPositions(false);
    font70 = new BitmapFont(Gdx.files.internal(getGameFontFilePath("cornerstone_70")));
    font70.setUseIntegerPositions(false);
    fontBig = new BitmapFont(Gdx.files.internal(getGameFontFilePath("cornerstone_big")));
    fontBig.setUseIntegerPositions(false);
    fontOutline = new BitmapFont(Gdx.files.internal(getGameFontFilePath("cornerstone_outline")));
    fontOutline.setUseIntegerPositions(false);
    fontUpperCaseBig = new BitmapFont(
        Gdx.files.internal(getGameFontFilePath("cornerstone_upper_case_big")));
    fontUpperCaseBig.setUseIntegerPositions(false);

    // Create game state manager
    Gdx.app.log("main:create", getCurrentTimeStampLogString() + "create game state manager");
    gameStateManager = new GameStateManager(assetManager);

    // Switch to the menu state
    Gdx.app.log("main:create", getCurrentTimeStampLogString() + "switch to menu state");
    gameStateManager.pushState(new MenuState(gameStateManager));
  }

  @Override
  public void dispose() {
    // Dispose sprite batch
    if (spriteBatch != null) {
      spriteBatch.dispose();
    }
    // Dispose loaded assets
    if (assetManager != null) {
      assetManager.dispose();
    }
  }

  @Override
  public void render() {
    // Clear canvas with black color
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Update game state
    // (deltaTime gives the time span between the current frame and the last frame in seconds)
    gameStateManager.update(Gdx.graphics.getDeltaTime());

    // Render the current state
    gameStateManager.render(spriteBatch);
  }

  @Override
  public void resize(final int width, final int height) {
    Gdx.app.log("main:resize",
        getCurrentTimeStampLogString() + "the game was resized to " + width + "x" + height);

    // On window resize calculate a new viewport so that the game is always displayed with the
    // original aspect ratio
    final Vector2 viewportSize = Scaling.fit.apply(GAME_WIDTH, GAME_HEIGHT, width, height);
    final int viewportX = (int) (width - viewportSize.x) / 2;
    final int viewportY = (int) (height - viewportSize.y) / 2;
    final int viewportWidth = (int) viewportSize.x;
    final int viewportHeight = (int) viewportSize.y;
    Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

    Gdx.app.log("main:resize",
        getCurrentTimeStampLogString() + "the new viewport is " + viewportWidth + "x"
            + viewportHeight);
  }

  @Override
  public void pause() {
    Gdx.app.log("main:pause", getCurrentTimeStampLogString() + "the game was paused");
    gameStateManager.pause();
  }

  @Override
  public void resume() {
    Gdx.app.log("main:resume", getCurrentTimeStampLogString() + "the game was resumed");
    gameStateManager.resume();
  }
}
