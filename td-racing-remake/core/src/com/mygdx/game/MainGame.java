package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.file.LevelInfoCsvFile;
import com.mygdx.game.file.LevelWaveCsvFile;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.MenuState;
import com.mygdx.game.preferences.PreferencesManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainGame implements ApplicationListener {

  public static final String VERSION = "1.0";
  /**
   * Height of the game screen
   */
  public static final int GAME_HEIGHT = 720;
  /**
   * Width of the game screen
   */
  public static final int GAME_WIDTH = 1280;
  /**
   * Name of the game
   */
  public static final String GAME_NAME = "TnT (Tracks `n Towers)";
  /**
   * The provided icon sizes
   */
  public static final int[] GAME_ICON_SIZES = {16, 32, 64};

  /**
   * Indicator if release or development version of the game
   */
  public static final boolean DEVELOPER_MODE = true;
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

  // Remove fonts later when asset manager works begin
  /**
   * Font "cornerstone_upper_case_big"
   */
  public static BitmapFont fontUpperCaseBig;
  /**
   * The game state manager that manages states, input handling, rendering
   */
  private GameStateManager gameStateManager;
  /**
   * A batch/collection of draw calls for rendering with OpenGL
   */
  private SpriteBatch spriteBatch;
  /**
   * Helps drawing shapes
   */
  private ShapeRenderer shapeRenderer;
  /**
   * Loads and stores assets like textures, bitmap fonts, sounds, music, ...
   */
  private AssetManager assetManager;
  /**
   * Manages application wide options and preferences
   */
  private PreferencesManager preferencesManager;
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

  /**
   * Get the filepath of a game music given its name
   *
   * @param musicName The name of the game music
   */
  public static String getGameMusicFilePath(final String musicName) {
    return "music/music_" + musicName + ".mp3";
  }

  /**
   * Get the filepath of a game sound given its name
   *
   * @param soundName The name of the game sound
   */
  public static String getGameSoundFilePath(final String soundName) {
    return getGameSoundFilePath(soundName, false);
  }

  /**
   * Get the filepath of a game sound given its name
   *
   * @param soundName The name of the game sound
   * @param mp3File   Indicator if the sound is a *.mp3 file instead of the default *.wav file
   */
  public static String getGameSoundFilePath(final String soundName, final boolean mp3File) {
    return "sound/sound_" + soundName + "." + (mp3File ? "mp3" : "wav");
  }

  /**
   * Get the filepath of a game car skin given its name
   *
   * @param carName The name of the game car skin
   */
  public static String getGameCarFilePath(final String carName) {
    return "car/car_" + carName + ".png";

  }

  public static String getGameMapFilePath(String mapName) {
    return "map/map_" + mapName + ".png";
  }

  public static String getGameTowerFilePath(String towerName) {
    return "tower/tower_" + towerName + ".png";
  }

  public static String getGameZombieFilePath(String zombieName) {
    return "zombie/zombie_" + zombieName + ".png";
  }

  private static HtmlPlatformInfo htmlPlatformInfo;

  public MainGame() {
    System.out.println(MainGame.GAME_NAME + " v" + MainGame.VERSION);
  }
  public MainGame(HtmlPlatformInfo htmlPlatformInfo) {
    MainGame.htmlPlatformInfo = htmlPlatformInfo;
    System.out.println(MainGame.GAME_NAME + " v" + MainGame.VERSION + " (html platform info: " + htmlPlatformInfo + ")");
  }

  public static HtmlPlatformInfo getPlatformInfo() {
    return htmlPlatformInfo;
  }

  @Override
  public void create() {
    // If in developer mode proved additional logging output, otherwise only errors
    if (DEVELOPER_MODE) {
      Gdx.app.setLogLevel(Application.LOG_DEBUG);
    } else {
      Gdx.app.setLogLevel(Application.LOG_ERROR);
    }

    // Catch the back key on Android devices so that the application does not just close
    // TODO This crashes the application
    // Gdx.input.setCatchKey(Input.Keys.BACK, true);

    // Create preferences manager
    preferencesManager = new PreferencesManager();

    // Activate fullscreen if activated
    if (preferencesManager.getFullscreen()) {
      Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    } else {
      Gdx.graphics.setWindowedMode(GAME_WIDTH, GAME_HEIGHT);
    }

    // Create sprite batch
    Gdx.app.debug("main:create", getCurrentTimeStampLogString() + "create sprite batch");
    spriteBatch = new SpriteBatch();
    // Create shape renderer
    Gdx.app.debug("main:create", getCurrentTimeStampLogString() + "create shape renderer");
    shapeRenderer = new ShapeRenderer();
    // Create asset manager
    Gdx.app.debug("main:create", getCurrentTimeStampLogString() + "create asset manager");
    assetManager = new AssetManager();

    // Load fonts (remove when asset manager works)
    Gdx.app.debug("main:create", getCurrentTimeStampLogString() + "load fonts");
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
    Gdx.app.debug("main:create", getCurrentTimeStampLogString() + "create game state manager");
    gameStateManager = new GameStateManager(assetManager, preferencesManager);

    // Test of new level info reader (delete later)
    ArrayList<LevelInfoCsvFile> levelInfo = LevelInfoCsvFile
        .readCsvFile(Gdx.files.internal("level/level_info.csv"));
    for (LevelInfoCsvFile entry : levelInfo) {
      Gdx.app.debug("testing", MainGame.getCurrentTimeStampLogString() + entry.toString());
      ArrayList<LevelWaveCsvFile> waveInfo = LevelWaveCsvFile
          .readCsvFile(Gdx.files.internal("level/level_0" + entry.levelNumber + "_waves.csv"));
      Gdx.app.debug("testing", MainGame.getCurrentTimeStampLogString() + "waves:");
      try {
        for (LevelWaveCsvFile wave : waveInfo) {
          Gdx.app.debug("testing", MainGame.getCurrentTimeStampLogString() + wave.toString());
        }
      } catch (NullPointerException exception) {
        exception.printStackTrace();
      }
    }

    // Switch to the menu state
    Gdx.app.debug("main:create", getCurrentTimeStampLogString() + "switch to menu state");
    gameStateManager.pushState(new MenuState(gameStateManager));
  }

  @Override
  public void dispose() {
    // Dispose sprite batch
    if (spriteBatch != null) {
      spriteBatch.dispose();
    }
    // Dispose shape renderer
    if (shapeRenderer != null) {
      shapeRenderer.dispose();
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
    gameStateManager.render(spriteBatch, shapeRenderer);
  }

  @Override
  public void resize(final int width, final int height) {
    Gdx.app.debug("main:resize",
        getCurrentTimeStampLogString() + "the game was resized to " + width + "x" + height);

    // On window resize calculate a new viewport so that the game is always displayed with the
    // original aspect ratio
    final Vector2 viewportSize = Scaling.fit.apply(GAME_WIDTH, GAME_HEIGHT, width, height);
    final int viewportX = (int) (width - viewportSize.x) / 2;
    final int viewportY = (int) (height - viewportSize.y) / 2;
    final int viewportWidth = (int) viewportSize.x;
    final int viewportHeight = (int) viewportSize.y;
    Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

    Gdx.app.debug("main:resize",
        getCurrentTimeStampLogString() + "the new viewport is " + viewportWidth + "x"
            + viewportHeight);
  }

  @Override
  public void pause() {
    Gdx.app.debug("main:pause", getCurrentTimeStampLogString() + "the game was paused");
    gameStateManager.pause();
  }

  @Override
  public void resume() {
    Gdx.app.debug("main:resume", getCurrentTimeStampLogString() + "the game was resumed");
    gameStateManager.resume();
  }
}
