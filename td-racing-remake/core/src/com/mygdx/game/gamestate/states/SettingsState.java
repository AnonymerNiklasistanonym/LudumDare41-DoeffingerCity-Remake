package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
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
import com.mygdx.game.gamestate.elements.button.MenuButtonMini;
import com.mygdx.game.gamestate.elements.button.MenuButtonSmall;
import com.mygdx.game.helper.menu.HelperMenu;
import com.mygdx.game.helper.menu.HelperMenuButtonNavigation;
import com.mygdx.game.preferences.PreferencesManager;

/**
 * Creates the main menu state which renders the main menu and handles keyboard, touch and
 * controller input
 */
public class SettingsState extends GameState implements IControllerCallbackGenericMenuButtonGrid {

  private static final String VSYNC_ID = "VSYNC_ID";
  private static final String FPS_ID = "FPS_ID";

  /**
   * The game state name for this game state
   */
  private static final String STATE_NAME = "Settings";

  private static final String ASSET_ID_BACKGROUND_STARS_TEXTURE = MainGame
      .getGameBackgroundFilePath("stars");
  private static final String ASSET_MANAGER_ID_FONT_TEXT = MainGame
      .getGameFontFilePath("cornerstone_big");
  private static final String TEXT_MENU_BUTTON_VSYNC = "VSYNC";
  private static final String TEXT_MENU_BUTTON_FPS = "FPS";
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
   * Tracker for the menu button id that was selected before the current one
   */
  private String lastSelectedMenuButtonId = VSYNC_ID;
  private BitmapFont fontText;

  /**
   * Constructor that creates the main menu (state)
   *
   * @param gameStateManager The global game state manager
   */
  public SettingsState(GameStateManager gameStateManager) {
    super(gameStateManager, STATE_NAME);

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Load assets that are not necessary to be available just yet
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_FONT, BitmapFont.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT, Texture.class);
    assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED, Texture.class);
    assetManager.load(MenuButtonMini.ASSET_MANAGER_ID_FONT, BitmapFont.class);
    assetManager.load(MenuButtonMini.ASSET_MANAGER_ID_TEXTURE_DEFAULT, Texture.class);
    assetManager.load(MenuButtonMini.ASSET_MANAGER_ID_TEXTURE_SELECTED, Texture.class);
    assetManager.load(ASSET_ID_BACKGROUND_STARS_TEXTURE, Texture.class);
    assetManager.load(ASSET_MANAGER_ID_FONT_TEXT, BitmapFont.class);

    // Register controller callback so that controller input can be managed
    controllerCallbackGenericMenuButtonGrid = new ControllerCallbackGenericMenuButtonGrid(this);
    Controllers.addListener(controllerCallbackGenericMenuButtonGrid);
  }

  @Override
  public void handleInput(final float deltaTime) {
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
      gameStateManager.setGameState(new MenuState(gameStateManager));
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
        Gdx.app.debug("settings_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loaded:");
        getDebugOutputLoadedAssets();

        backgroundStars = assetManager.get(ASSET_ID_BACKGROUND_STARS_TEXTURE);

        // Create menu buttons
        menuButtons = new MenuButton[][]{
            {
                new MenuButtonSmall(VSYNC_ID, TEXT_MENU_BUTTON_VSYNC, assetManager,
                    (float) MainGame.GAME_WIDTH / 2 * 0.7f,
                    (float) MainGame.GAME_HEIGHT / 6 * 4, true)
            },
            {
                new MenuButtonSmall(FPS_ID, TEXT_MENU_BUTTON_FPS, assetManager,
                    (float) MainGame.GAME_WIDTH / 2 * 0.7f,
                    (float) MainGame.GAME_HEIGHT / 6 * 2)
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
      // > Draw current fps
      fontText.setColor(1, 1, 1, 1);
      fontText.draw(spriteBatch, preferencesManager.getVsync() ? "ON" : "OFF",
          (float) MainGame.GAME_WIDTH / 2 * 1.2f, (float) MainGame.GAME_HEIGHT / 6 * 4.5f);
      int currentFps = preferencesManager.getFps();
      fontText.draw(spriteBatch, currentFps == 0 ? "UNLIMITED" : (currentFps + ""),
          (float) MainGame.GAME_WIDTH / 2 * 1.2f, (float) MainGame.GAME_HEIGHT / 6 * 2.5f);
      spriteBatch.end();
    } else {
      // Get and render loading information
      float progress = assetManager.getProgress();
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("settings_state:render",
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
        MenuButtonMini.ASSET_MANAGER_ID_FONT,
        MenuButtonMini.ASSET_MANAGER_ID_TEXTURE_DEFAULT,
        MenuButtonMini.ASSET_MANAGER_ID_TEXTURE_SELECTED,
        MenuButtonSmall.ASSET_MANAGER_ID_FONT,
        MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT,
        MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED,
        ASSET_ID_BACKGROUND_STARS_TEXTURE,
        ASSET_MANAGER_ID_FONT_TEXT,
    });
  }

  /**
   * Open/CLick the a menu button by its ID
   */
  private void openMenuButtonById(final String menuButtonId) {
    Gdx.app.debug("settings_state:openMenuButtonById",
        MainGame.getCurrentTimeStampLogString() + "\"" + menuButtonId + "\"");
    switch (menuButtonId) {
      case VSYNC_ID:
        // TODO: Update button displays?
        preferencesManager.setVsync(!preferencesManager.getVsync());
        //gameStateManager.setGameState(new PlayState(gameStateManager, 0));
        break;
      case FPS_ID:
        // TODO: Update button displays?
        int currentFps = preferencesManager.getFps();
        if (currentFps >= 240) {
          preferencesManager.setFps(0);
        } else if (currentFps >= 144) {
          preferencesManager.setFps(240);
        } else if (currentFps >= 120) {
          preferencesManager.setFps(144);
        } else if (currentFps >= 60) {
          preferencesManager.setFps(120);
        } else if (currentFps >= 0) {
          preferencesManager.setFps(60);
        }
        //gameStateManager.setGameState(new HighscoreListState(gameStateManager));
        break;
      default:
        Gdx.app.error("settings_state:openMenuButtonById",
            MainGame.getCurrentTimeStampLogString() + "Unknown button id \"" + menuButtonId + "\"");
    }
  }

  /**
   * Open/CLick the currently selected menu button
   */
  private void openSelectedMenuButton() {
    Gdx.app.debug("settings_state:openSelectedMenuButton", MainGame.getCurrentTimeStampLogString());
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
    Gdx.app.debug("settings_state:pause", MainGame.getCurrentTimeStampLogString() + "pause");
    paused = true;
  }

  @Override
  public void resume() {
    Gdx.app.debug("settings_state:resume", MainGame.getCurrentTimeStampLogString() + "resume");
    paused = false;
  }

  @Override
  public void controllerCallbackSelectMenuButton(NextMenuButtonDirection direction) {
    Gdx.app.debug("settings_state:controllerCallbackSelectMenuButton",
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
    Gdx.app.debug("settings_state:controllerCallbackClickStartMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerStartKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickMenuButton() {
    Gdx.app.debug("settings_state:controllerCallbackClickMenuButton",
        MainGame.getCurrentTimeStampLogString());
    controllerSelectKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickBackButton() {
    Gdx.app.debug("settings_state:controllerCallbackClickBackButton",
        MainGame.getCurrentTimeStampLogString());
    controllerBackKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    Gdx.app.debug("settings_state:controllerCallbackToggleFullScreen",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleFullScreenPressed = true;
  }

  @Override
  public void controllerCallbackToggleMusic() {
    Gdx.app.debug("settings_state:controllerCallbackToggleMusic",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleMusicPressed = true;
  }

  @Override
  public void controllerCallbackToggleSoundEffects() {
    Gdx.app.debug("settings_state:controllerCallbackToggleSoundEffects",
        MainGame.getCurrentTimeStampLogString());
    controllerToggleSoundEffectsPressed = true;
  }
}
