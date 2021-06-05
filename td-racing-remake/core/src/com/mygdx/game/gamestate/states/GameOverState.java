package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.generic.menu_button_grid.ControllerCallbackGenericMenuButtonGrid;
import com.mygdx.game.controller.generic.menu_button_grid.IControllerCallbackGenericMenuButtonGrid;
import com.mygdx.game.controller.generic.menu_button_grid.NextMenuButtonDirection;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.elements.button.MenuButton;
import com.mygdx.game.gamestate.elements.button.MenuButtonSmall;
import com.mygdx.game.helper.menu.HelperMenu;
import com.mygdx.game.helper.menu.HelperMenuButtonNavigation;

/**
 * Creates the game over state which renders the a menu and handles keyboard, touch and controller
 * input
 */
public class GameOverState extends GameState implements IControllerCallbackGenericMenuButtonGrid {

  /**
   * The menu button ID for (re)starting the game
   */
  private static final String PLAY_AGAIN_ID = "PLAY_AGAIN_ID";
  /**
   * The menu button ID for (re)starting the current level of the game
   */
  private static final String PLAY_LEVEL_AGAIN_ID = "PLAY_LEVEL_AGAIN_ID";
  /**
   * The menu button ID for opening the highscore list page
   */
  private static final String HIGHSCORE_ID = "HIGHSCORE_ID";
  /**
   * The menu button ID for opening the about page
   */
  private static final String ABOUT_ID = "ABOUT_ID";

  /**
   * The game state name for this game state
   */
  private static final String STATE_NAME = "GameOver";
  /**
   * The game over text
   */
  private static final String GAME_OVER_TEXT = "GAME OVER";
  /**
   * The game over text font scale
   */
  private static final float GAME_OVER_FONT_SCALE = 1;
  /**
   * Controller callback class that gets this class in its constructor which implements some
   * callback methods and can then be added as a controller listener which can then call the
   * interface implemented methods in this class on corresponding controller input
   */
  private final ControllerCallbackGenericMenuButtonGrid controllerCallbackGenericMenuButtonGrid;
  /**
   * The current level of the game right before death
   */
  private final int level;
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

  private static final String ASSET_ID_FONT_TEXT_GAME_OVER = MainGame.getGameFontFilePath("cornerstone_upper_case_big");
  private static final String ASSET_ID_TEXTURE_BACKGROUND_GAME_OVER = MainGame.getGameBackgroundFilePath("game_over");

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

    // Load assets that are not necessary to be available just yet
    assetManager.load(ASSET_ID_FONT_TEXT_GAME_OVER, BitmapFont.class);
    assetManager.load(ASSET_ID_TEXTURE_BACKGROUND_GAME_OVER, Texture.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_FONT, BitmapFont.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT, Texture.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED, Texture.class);

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
      if (controllerToggleFullScreenPressed || Gdx.input.isKeyJustPressed(Keys.F11)) {
        controllerToggleFullScreenPressed = false;
        gameStateManager.toggleFullScreen();
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
  public void render(final SpriteBatch spriteBatch, final ShapeRenderer shapeRenderer) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if (assetManager.update()) {
      if (!assetsLoaded) {
        Gdx.app.debug("game_over_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + (assetManager.getProgress() * 100) + "%");
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
        gameOverFont.getData().setScale(GAME_OVER_FONT_SCALE);
        gameOverTextPosition = GameStateManager.calculateCenteredTextPosition(gameOverFont,
            GAME_OVER_TEXT,
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
      gameOverFont.draw(spriteBatch, GAME_OVER_TEXT, gameOverTextPosition.x, gameOverTextPosition.y);

      spriteBatch.end();
    } else {
      // Get and render loading information
      float progress = assetManager.getProgress();
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("game_over_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + (progress * 100) + "%");
      }
      drawLoadingProgress(spriteBatch, shapeRenderer, progress);
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
    unloadAssetManagerResources(new String[]{
        ASSET_ID_FONT_TEXT_GAME_OVER,
        ASSET_ID_TEXTURE_BACKGROUND_GAME_OVER,
        MenuButtonSmall.ASSET_MANAGER_ID_FONT,
        MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT,
        MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED,
    });
  }

  /**
   * Open/CLick the a menu button by its ID
   */
  private void openMenuButtonById(final String menuButtonId) {
    Gdx.app.debug("game_over_state:openMenuButtonById",
        MainGame.getCurrentTimeStampLogString() + "\"" + menuButtonId + "\"");
    switch (menuButtonId) {
      case PLAY_AGAIN_ID:
        gameStateManager.setGameState(new PlayState(gameStateManager, 1));
        break;
      case PLAY_LEVEL_AGAIN_ID:
        gameStateManager.setGameState(new PlayState(gameStateManager, level));
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
  public void controllerCallbackSelectMenuButton(NextMenuButtonDirection direction) {
    Gdx.app.debug("game_over_state:controllerCallbackSelectMenuButton",
        MainGame.getCurrentTimeStampLogString() + direction.name());
    if (menuButtons != null) {
      switch (direction) {
        case ABOVE:
          controllerUpKeyWasPressed = true;
          break;
        case BELOW:
          controllerDownKeyWasPressed = true;
          break;
        case RIGHT:
          controllerRightKeyWasPressed = true;
          break;
        case LEFT:
          controllerLeftKeyWasPressed = true;
          break;
      }
    }
  }

  @Override
  public void controllerCallbackClickStartMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackClickStartMenuButton",
        MainGame.getCurrentTimeStampLogString());
    if (menuButtons != null) {
      controllerStartKeyWasPressed = true;
    }
  }

  @Override
  public void controllerCallbackClickMenuButton() {
    Gdx.app.debug("game_over_state:controllerCallbackClickMenuButton",
        MainGame.getCurrentTimeStampLogString());
    if (menuButtons != null) {
      controllerSelectKeyWasPressed = true;
    }
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
    controllerToggleFullScreenPressed = true;
  }

  @Override
  public void controllerCallbackToggleMusic() {
    Gdx.app.debug("game_over_state:controllerCallbackToggleMusic",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleMusicPressed = true;
  }

  @Override
  public void controllerCallbackToggleSoundEffects() {
    Gdx.app.debug("game_over_state:controllerCallbackToggleSoundEffects",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleSoundEffectsPressed = true;
  }
}
