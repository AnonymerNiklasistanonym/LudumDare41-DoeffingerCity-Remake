package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.menu_button_grid.ControllerCallbackGenericMenuButtonGrid;
import com.mygdx.game.controller.menu_button_grid.IControllerCallbackGenericMenuButtonGrid;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.resources.MenuButton;
import com.mygdx.game.gamestate.states.resources.MenuButtonSmall;
import com.mygdx.game.helper.HelperMenu;
import com.mygdx.game.helper.HelperMenuButtonNavigation;

/**
 * Creates the game over state which renders the a menu and handles keyboard, touch and controller
 * input
 */
public class GameOverState extends GameState implements IControllerCallbackGenericMenuButtonGrid {

  /**
   * The menu button ID for (re)starting the game
   */
  private final static String PLAY_AGAIN_ID = "PLAY_AGAIN_ID";
  /**
   * The menu button ID for (re)starting the current level of the game
   */
  private final static String PLAY_LEVEL_AGAIN_ID = "PLAY_LEVEL_AGAIN_ID";
  /**
   * The menu button ID for opening the highscore list page
   */
  private final static String HIGHSCORE_ID = "HIGHSCORE_ID";
  /**
   * The menu button ID for opening the about page
   */
  private final static String ABOUT_ID = "ABOUT_ID";

  /**
   * The game state name for this game state
   */
  private static final String STATE_NAME = "GameOver";
  /**
   * Controller callback class that gets this class in its constructor which implements some
   * callback methods and can then be added as a controller listener which can then call the
   * interface implemented methods in this class on corresponding controller input
   */
  private final ControllerCallbackGenericMenuButtonGrid controllerCallbackGenericMenuButtonGrid;
  /**
   * The current cursor position
   */
  private final Vector3 cursorPosition;
  /**
   * The global asset manager to load and get resources (it uses reference counting to easily
   * dispose not needed resource any more after they were unloaded)
   */
  private final AssetManager assetManager;
  /**
   * The current level of the game right before death
   */
  private final int level;
  /**
   * The game over text
   */
  private final String gameOverText = "GAME OVER";
  /**
   * The game over text font scale
   */
  private final float gameOverFontScale = 1;
  /**
   * The menu button grid where all buttons are sorted as they are displayed on the screen: `{ {
   * Button1Row1, Button2Row2 }, { Button1Row2 }, { Button1Row3, Button2Row3 } }`
   */
  private MenuButton[][] menuButtons;
  /**
   * Variable for the texture of the game over background
   */
  private Texture backgroundGameOver;
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
   * Tracker for the menu button id that was selected before the current one
   */
  private String lastSelectedMenuButtonId = PLAY_AGAIN_ID;
  /**
   * The game over text font
   */
  private BitmapFont gameOverFont;
  /**
   * The game over text position
   */
  private Vector2 gameOverTextPosition;
  /**
   * Tracker if a controller down key was pressed
   */
  private boolean controllerDownKeyWasPressed = false;
  /**
   * Tracker if a controller up key was pressed
   */
  private boolean controllerUpKeyWasPressed = false;
  /**
   * Tracker if a controller left key was pressed
   */
  private boolean controllerLeftKeyWasPressed = false;
  /**
   * Tracker if a controller right key was pressed
   */
  private boolean controllerRightKeyWasPressed = false;
  /**
   * Tracker if a controller selection key was pressed
   */
  private boolean controllerSelectKeyWasPressed = false;
  /**
   * Tracker if a controller start key was pressed
   */
  private boolean controllerStartKeyWasPressed = false;
  /**
   * Tracker if a controller back key was pressed
   */
  private boolean controllerBackKeyWasPressed = false;
  /**
   * Tracker if a controller full screen toggle key was pressed
   */
  private boolean controllerFullScreenToggleKeyPressed = false;

  /**
   * Constructor that creates the game over (state)
   *
   * @param gameStateManager The global game state manager
   * @param level            The current level in the game
   */
  public GameOverState(GameStateManager gameStateManager, int level) {
    super(gameStateManager, STATE_NAME);

    // Save the current level
    this.level = level;

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Initialize variable for the cursor position
    cursorPosition = new Vector3();

    // Get asset manager from the game state manager
    assetManager = gameStateManager.getAssetManager();
    // Load assets that are not necessary to be available just yet
    assetManager.load(MainGame.getGameFontFilePath("cornerstone_upper_case_big"), BitmapFont.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_FONT, BitmapFont.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT, Texture.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED, Texture.class);
    assetManager.load(MainGame.getGameBackgroundFilePath("game_over"), Texture.class);

    // Register controller callback so that controller input can be managed
    controllerCallbackGenericMenuButtonGrid = new ControllerCallbackGenericMenuButtonGrid(this);
    Controllers.addListener(controllerCallbackGenericMenuButtonGrid);
  }

  @Override
  public void handleInput() {
    if (paused) {
      // When the game is paused don't handle anything
      return;
    }

    if (Gdx.app.getType() == ApplicationType.Desktop) {
      // Toggle full screen when full screen keys are pressed
      if (controllerFullScreenToggleKeyPressed || Gdx.input.isKeyJustPressed(Keys.F11)) {
        controllerFullScreenToggleKeyPressed = false;
        GameStateManager.toggleFullScreen();
      }
    }

    // Update the cursor position
    cursorPosition.set(GameStateManager.getMousePosition(camera));

    // Be sure to only allow navigating the menu buttons if they are initialized
    if (menuButtons != null) {
      // Mouse input (select the button on which the mouse is currently located)
      boolean buttonCurrentlySelectedByCursor = false;
      outerloop:
      for (final MenuButton[] menuButtonLine : menuButtons) {
        for (final MenuButton menuButton : menuButtonLine) {
          if (menuButton.contains(cursorPosition)) {
            buttonCurrentlySelectedByCursor = true;
            break outerloop;
          }
        }
      }
      // If a cursor is currently selecting a menu button update the selected menu buttons
      // else leave the last selected button selected
      if (buttonCurrentlySelectedByCursor) {
        for (final MenuButton[] menuButtonLine : menuButtons) {
          for (final MenuButton menuButton : menuButtonLine) {
            if (menuButton.isSelected() && !menuButton.contains(cursorPosition)) {
              lastSelectedMenuButtonId = menuButton.getId();
            }
            menuButton.setSelected(menuButton.contains(cursorPosition));
          }
        }
      }

      // Keyboard input
      if (!buttonCurrentlySelectedByCursor) {
        if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.DOWN,
                  lastSelectedMenuButtonId);
        }
        if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.TAB)) {
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.RIGHT,
                  lastSelectedMenuButtonId);
        }
        if (Gdx.input.isKeyJustPressed(Keys.UP)) {
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.UP,
                  lastSelectedMenuButtonId);
        }
        if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.LEFT,
                  lastSelectedMenuButtonId);
        }
      }

      // Controller input
      if (!buttonCurrentlySelectedByCursor) {
        if (controllerDownKeyWasPressed) {
          controllerDownKeyWasPressed = false;
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.DOWN,
                  lastSelectedMenuButtonId);
        }
        if (controllerUpKeyWasPressed) {
          controllerUpKeyWasPressed = false;
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.UP,
                  lastSelectedMenuButtonId);
        }
        if (controllerLeftKeyWasPressed) {
          controllerLeftKeyWasPressed = false;
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.LEFT,
                  lastSelectedMenuButtonId);
        }
        if (controllerRightKeyWasPressed) {
          controllerRightKeyWasPressed = false;
          lastSelectedMenuButtonId = HelperMenu
              .selectNextButton(menuButtons, HelperMenuButtonNavigation.RIGHT,
                  lastSelectedMenuButtonId);
        }
      } else {
        controllerDownKeyWasPressed = false;
        controllerUpKeyWasPressed = false;
        controllerLeftKeyWasPressed = false;
        controllerRightKeyWasPressed = false;
        controllerStartKeyWasPressed = false;
        controllerSelectKeyWasPressed = false;
      }
      if (controllerStartKeyWasPressed) {
        controllerStartKeyWasPressed = false;
        openMenuButtonById(PLAY_AGAIN_ID);
      }

      // If a button is touched or the space or enter key is currently pressed or a controller select
      // key is currently pressed execute the action for the selected menu button
      if ((buttonCurrentlySelectedByCursor && Gdx.input.justTouched()) || Gdx.input
          .isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)
          || controllerSelectKeyWasPressed) {
        controllerSelectKeyWasPressed = false;
        openSelectedMenuButton();
      }
    } else {
      controllerDownKeyWasPressed = false;
      controllerUpKeyWasPressed = false;
      controllerLeftKeyWasPressed = false;
      controllerRightKeyWasPressed = false;
      controllerStartKeyWasPressed = false;
      controllerSelectKeyWasPressed = false;
    }

    // If escape or back is pressed quit
    if (Gdx.input.isCatchKey(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
        || controllerBackKeyWasPressed) {
      controllerBackKeyWasPressed = false;
      gameStateManager.setGameState(new CreditState(gameStateManager));
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
        Gdx.app.debug("game_over_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
        assetsLoaded = true;
        backgroundGameOver = assetManager.get(MainGame.getGameBackgroundFilePath("game_over"));

        // Create menu buttons
        menuButtons = new MenuButton[][]{
            {
                new MenuButtonSmall(PLAY_AGAIN_ID, "RESTART", assetManager,
                    (float) MainGame.GAME_WIDTH / 4, (float) MainGame.GAME_HEIGHT / 6 * 3,
                    true),
                new MenuButtonSmall(PLAY_LEVEL_AGAIN_ID, "...LEVEL", assetManager,
                    MainGame.GAME_WIDTH - (float) MainGame.GAME_WIDTH / 4,
                    (float) MainGame.GAME_HEIGHT / 6 * 3),
            },
            {
                new MenuButtonSmall(HIGHSCORE_ID, "HIGHSCORES", assetManager,
                    (float) MainGame.GAME_WIDTH / 4, (float) MainGame.GAME_HEIGHT / 6 * 1),
                new MenuButtonSmall(ABOUT_ID, "ABOUT", assetManager,
                    MainGame.GAME_WIDTH - (float) MainGame.GAME_WIDTH / 4,
                    (float) MainGame.GAME_HEIGHT / 6 * 1)
            }
        };

        gameOverFont = assetManager.get(MainGame.getGameFontFilePath("cornerstone_upper_case_big"));
        gameOverFont.getData().setScale(gameOverFontScale);
        gameOverTextPosition = GameStateManager.calculateCenteredTextPosition(gameOverFont,
            gameOverText,
            MainGame.GAME_WIDTH, (float) MainGame.GAME_HEIGHT / 5 * 8);

      }
      // Render menu
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();

      spriteBatch.draw(backgroundGameOver, 0, 0);
      for (final MenuButton[] menuButtonLine : menuButtons) {
        for (final MenuButton menuButton : menuButtonLine) {
          menuButton.draw(spriteBatch);
        }
      }

      gameOverFont.getData().setScale(1);
      gameOverFont.draw(spriteBatch, gameOverText, gameOverTextPosition.x, gameOverTextPosition.y);

      spriteBatch.end();
    } else {
      // display loading information
      float progress = assetManager.getProgress() * 100;
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("game_over_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
      }
    }
  }

  @Override
  public void dispose() {
    // Remove controller listener
    Controllers.removeListener(controllerCallbackGenericMenuButtonGrid);

    // Dispose all menu buttons
    for (final MenuButton[] menuButtonLine : menuButtons) {
      for (final MenuButton menuButton : menuButtonLine) {
        menuButton.dispose();
      }
    }

    // Reduce the reference to used resources in this state (when no object is referencing the
    // resource any more it is automatically disposed by the global asset manager)
    Gdx.app.debug("game_over_state:dispose", "Loaded assets before unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("game_over_state:dispose", "- " + loadedAsset);
    }
    assetManager.unload(MainGame.getGameFontFilePath("cornerstone_upper_case_big"));
    assetManager.unload(MenuButtonSmall.ASSET_MANAGER_ID_FONT);
    assetManager.unload(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT);
    assetManager.unload(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED);
    assetManager.unload(MainGame.getGameBackgroundFilePath("game_over"));
    Gdx.app.debug("game_over_state:dispose", "Loaded assets after unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("game_over_state:dispose", "- " + loadedAsset);
    }
  }

  /**
   * Open/CLick the a menu button by its ID
   */
  private void openMenuButtonById(final String menuButtonId) {
    Gdx.app.debug("game_over_state:openMenuButtonById",
        MainGame.getCurrentTimeStampLogString() + "\"" + menuButtonId + "\"");
    switch (menuButtonId) {
      case PLAY_AGAIN_ID:
        gameStateManager.setGameState(new LoadingState(gameStateManager, 1));
        break;
      case PLAY_LEVEL_AGAIN_ID:
        gameStateManager.setGameState(new LoadingState(gameStateManager, level));
        break;
      case HIGHSCORE_ID:
        gameStateManager.setGameState(new HighscoreListState(gameStateManager));
        break;
      case ABOUT_ID:
        gameStateManager.setGameState(new CreditState(gameStateManager));
        break;
      default:
        Gdx.app.error("game_over_state:openMenuButtonById",
            MainGame.getCurrentTimeStampLogString() + "Unknown button id \"" + menuButtonId + "\"");
    }
  }

  /**
   * Open/CLick the currently selected menu button
   */
  private void openSelectedMenuButton() {
    Gdx.app
        .debug("game_over_state:openSelectedMenuButton", MainGame.getCurrentTimeStampLogString());
    for (final MenuButton[] menuButtonLine : menuButtons) {
      for (final MenuButton menuButton : menuButtonLine) {
        if (menuButton.isSelected()) {
          openMenuButtonById(menuButton.getId());
        }
      }
    }
  }

  @Override
  public void pause() {
    Gdx.app.debug("game_over_state:pause", MainGame.getCurrentTimeStampLogString() + "pause");
    paused = true;
  }

  @Override
  public void resume() {
    Gdx.app.debug("game_over_state:resume", MainGame.getCurrentTimeStampLogString() + "resume");
    paused = false;
  }

  @Override
  public void controllerCallbackSelectLeftMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackSelectLeftMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerLeftKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackSelectRightMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackSelectRightMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerRightKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackSelectAboveMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackSelectAboveMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerUpKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackSelectBelowMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackSelectBelowMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerDownKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickStartMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackClickStartMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerStartKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackClickMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerSelectKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickBackButton() {
    Gdx.app.debug("game_over_state:controllerCallbackClickBackButton",
        MainGame.getCurrentTimeStampLogString());
    controllerBackKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    Gdx.app.debug("game_over_state:controllerCallbackToggleFullScreen",
        MainGame.getCurrentTimeStampLogString());
    controllerFullScreenToggleKeyPressed = true;
  }
}
