package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.generic.menu_button_grid.ControllerCallbackGenericMenuButtonGrid;
import com.mygdx.game.controller.generic.menu_button_grid.IControllerCallbackGenericMenuButtonGrid;
import com.mygdx.game.controller.generic.menu_button_grid.NextMenuButtonDirection;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.elements.button.MenuButton;
import com.mygdx.game.gamestate.elements.button.MenuButtonBig;
import com.mygdx.game.gamestate.elements.button.MenuButtonSmall;
import com.mygdx.game.helper.menu.HelperMenu;
import com.mygdx.game.helper.menu.HelperMenuButtonNavigation;

/**
 * Creates the main menu state which renders the main menu and handles keyboard, touch and
 * controller input
 */
public class MenuState extends GameState implements IControllerCallbackGenericMenuButtonGrid {

  /**
   * The menu button ID for starting the game
   */
  private static final String START_ID = "START_ID";
  /**
   * The menu button ID for switching to the highscore list
   */
  private static final String HIGHSCORE_ID = "HIGHSCORE_ID";
  /**
   * The menu button ID for switching to the about screen
   */
  private static final String ABOUT_ID = "ABOUT_ID";
  /**
   * The game state name for this game state
   */
  private static final String STATE_NAME = "Menu";
  private static final String ASSET_ID_BACKGROUND_STARS_TEXTURE = MainGame
      .getGameBackgroundFilePath("stars");
  private static final String ASSET_ID_LOGO_TNT_TEXTURE = MainGame.getGameLogoFilePath("tnt");
  private static final String ASSET_MANAGER_ID_FONT_TEXT = MainGame
      .getGameFontFilePath("cornerstone");
  private static final String TEXT_MENU_BUTTON_START = "START";
  private static final String TEXT_MENU_BUTTON_ABOUT = "ABOUT";
  private static final String TEXT_MENU_BUTTON_HIGHSCORES = "HIGHSCORES";
  private static final float FONT_SCALE_TEXT = 1;
  /**
   * Controller callback class that gets this class in its constructor which implements some
   * callback methods and can then be added as a controller listener which can then call the
   * interface implemented methods in this class on corresponding controller input
   */
  private final ControllerCallbackGenericMenuButtonGrid controllerCallbackGenericMenuButtonGrid;
  /**
   * The menu button grid where all buttons are sorted as they are displayed on the screen: `{ {
   * Button1Row1, Button2Row2 }, { Button1Row2 }, { Button1Row3, Button2Row3 } }`
   */
  private MenuButton[][] menuButtons;
  /**
   * Variable for the texture of the stars background
   */
  private Texture backgroundStars;
  /**
   * Variable for the texture of the game logo
   */
  private Texture logoTnt;
  /**
   * Tracker for the menu button id that was selected before the current one
   */
  private String lastSelectedMenuButtonId = START_ID;
  private BitmapFont fontText;

  /**
   * Constructor that creates the main menu (state)
   *
   * @param gameStateManager The global game state manager
   */
  public MenuState(GameStateManager gameStateManager) {
    super(gameStateManager, STATE_NAME);

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Load assets that are not necessary to be available just yet
    assetManager.load(MenuButtonBig.ASSET_MANAGER_ID_FONT, BitmapFont.class);
    assetManager.load(MenuButtonBig.ASSET_MANAGER_ID_TEXTURE_DEFAULT, Texture.class);
    assetManager.load(MenuButtonBig.ASSET_MANAGER_ID_TEXTURE_SELECTED, Texture.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_FONT, BitmapFont.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT, Texture.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED, Texture.class);
    assetManager.load(ASSET_ID_BACKGROUND_STARS_TEXTURE, Texture.class);
    assetManager.load(ASSET_ID_LOGO_TNT_TEXTURE, Texture.class);
    assetManager.load(ASSET_MANAGER_ID_FONT_TEXT, BitmapFont.class);

    // Register controller callback so that controller input can be managed
    controllerCallbackGenericMenuButtonGrid = new ControllerCallbackGenericMenuButtonGrid(this);
    Controllers.addListener(controllerCallbackGenericMenuButtonGrid);
  }

  @Override
  public void handleInput() {
    if (paused || !assetsLoaded) {
      // When the game is paused or assets not loaded don't handle anything
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

    // Turn music on/off
    if (controllerToggleMusicPressed || Gdx.input.isKeyJustPressed(Keys.M)) {
      controllerToggleMusicPressed = false;
      gameStateManager.getPreferencesManager()
          .setMusicOn(!gameStateManager.getPreferencesManager().getMusicOn());
    }
    // Turn sound effects on/off
    if (controllerToggleSoundEffectsPressed || Gdx.input.isKeyJustPressed(Keys.U)) {
      controllerToggleSoundEffectsPressed = false;
      gameStateManager.getPreferencesManager()
          .setSoundEffectsOn(!gameStateManager.getPreferencesManager().getSoundEffectsOn());
    }

    // If escape or back is pressed quit
    if (Gdx.input.isCatchKey(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
        || controllerBackKeyWasPressed) {
      controllerBackKeyWasPressed = false;
      Gdx.app.exit();
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
        openMenuButtonById(START_ID);
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

    // Provide additional functionality when in developer mode
    if (MainGame.DEVELOPER_MODE) {
      // Reset all preferences
      if (Gdx.input.isKeyJustPressed(Keys.R)) {
        preferencesManager.reset();
        // > Set fullscreen on/off based on the default preference value
        if (preferencesManager.getFullscreen()) {
          Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
          Gdx.graphics.setWindowedMode(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
        }
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
        Gdx.app.debug("menu_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loaded:");
        getDebugOutputLoadedAssets();

        backgroundStars = assetManager.get(ASSET_ID_BACKGROUND_STARS_TEXTURE);
        logoTnt = assetManager.get(ASSET_ID_LOGO_TNT_TEXTURE);

        // Create menu buttons
        menuButtons = new MenuButton[][]{
            {
                new MenuButtonBig(START_ID, TEXT_MENU_BUTTON_START, assetManager,
                    (float) MainGame.GAME_WIDTH / 2,
                    (float) MainGame.GAME_HEIGHT / 6 * 2.8f, true)
            },
            {
                new MenuButtonSmall(ABOUT_ID, TEXT_MENU_BUTTON_ABOUT, assetManager,
                    (float) MainGame.GAME_WIDTH / 4,
                    (float) MainGame.GAME_HEIGHT / 6 * 1),
                new MenuButtonSmall(HIGHSCORE_ID, TEXT_MENU_BUTTON_HIGHSCORES, assetManager,
                    (float) MainGame.GAME_WIDTH / 2 + (float) MainGame.GAME_WIDTH / 4,
                    (float) MainGame.GAME_HEIGHT / 6 * 1)
            }
        };

        fontText = assetManager.get(ASSET_MANAGER_ID_FONT_TEXT);
        fontText.setUseIntegerPositions(false);
        fontText.getData().setScale(FONT_SCALE_TEXT);
      }
      // Render menu
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();
      // > Draw menu buttons
      spriteBatch.draw(backgroundStars, 0, 0);
      for (final MenuButton[] menuButtonLine : menuButtons) {
        for (final MenuButton menuButton : menuButtonLine) {
          menuButton.draw(spriteBatch);
        }
      }
      // > Draw logo
      spriteBatch.draw(logoTnt, 0, 0);
      // > Draw version
      fontText.getData().setScale(1);
      fontText.setColor(1, 1, 1, 1);
      fontText.draw(spriteBatch, "v" + MainGame.VERSION, 10, 30);
      // > Draw additional elements when in developer mode
      if (MainGame.DEVELOPER_MODE) {
        fontText.getData().setScale(1);
        fontText.setColor(1, 1, 1, 1);
        fontText.draw(spriteBatch, "Reset preferences: R", 1030, 30);
      }
      spriteBatch.end();
    } else {
      // Get and render loading information
      float progress = assetManager.getProgress();
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("menu_state:render",
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
        MenuButtonBig.ASSET_MANAGER_ID_FONT,
        MenuButtonBig.ASSET_MANAGER_ID_TEXTURE_DEFAULT,
        MenuButtonBig.ASSET_MANAGER_ID_TEXTURE_SELECTED,
        MenuButtonSmall.ASSET_MANAGER_ID_FONT,
        MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT,
        MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED,
        ASSET_ID_BACKGROUND_STARS_TEXTURE,
        ASSET_ID_LOGO_TNT_TEXTURE,
        ASSET_MANAGER_ID_FONT_TEXT,
    });
  }

  /**
   * Open/CLick the a menu button by its ID
   */
  private void openMenuButtonById(final String menuButtonId) {
    Gdx.app.debug("menu_state:openMenuButtonById",
        MainGame.getCurrentTimeStampLogString() + "\"" + menuButtonId + "\"");
    switch (menuButtonId) {
      case START_ID:
        gameStateManager.setGameState(new PlayState(gameStateManager, 0));
        break;
      case HIGHSCORE_ID:
        gameStateManager.setGameState(new HighscoreListState(gameStateManager));
        break;
      case ABOUT_ID:
        gameStateManager.setGameState(new CreditState(gameStateManager));
        break;
      default:
        Gdx.app.error("menu_state:openMenuButtonById",
            MainGame.getCurrentTimeStampLogString() + "Unknown button id \"" + menuButtonId + "\"");
    }
  }

  /**
   * Open/CLick the currently selected menu button
   */
  private void openSelectedMenuButton() {
    Gdx.app.debug("menu_state:openSelectedMenuButton", MainGame.getCurrentTimeStampLogString());
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
    Gdx.app.debug("menu_state:pause", MainGame.getCurrentTimeStampLogString() + "pause");
    paused = true;
  }

  @Override
  public void resume() {
    Gdx.app.debug("menu_state:resume", MainGame.getCurrentTimeStampLogString() + "resume");
    paused = false;
  }

  @Override
  public void controllerCallbackSelectMenuButton(NextMenuButtonDirection direction) {
    Gdx.app.debug("menu_state:controllerCallbackSelectMenuButton",
        MainGame.getCurrentTimeStampLogString() + direction.name());
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

  @Override
  public void controllerCallbackClickStartMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackClickStartMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerStartKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackClickMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerSelectKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickBackButton() {
    Gdx.app.debug("menu_state:controllerCallbackClickBackButton",
        MainGame.getCurrentTimeStampLogString());
    controllerBackKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    Gdx.app.debug("menu_state:controllerCallbackToggleFullScreen",
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
