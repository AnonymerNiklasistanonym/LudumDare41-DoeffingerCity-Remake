package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerCallbackMenuState;
import com.mygdx.game.controller.IControllerCallbackMenuState;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.resources.MenuButton;
import com.mygdx.game.gamestate.states.resources.MenuButtonBig;
import com.mygdx.game.gamestate.states.resources.MenuButtonSmall;
import com.mygdx.game.helper.HelperMenu;
import com.mygdx.game.helper.HelperMenuButtonNavigation;

public class MenuState extends GameState implements IControllerCallbackMenuState {

  private final static String START_ID = "START_ID";
  private final static String HIGHSCORE_ID = "HIGHSCORE_ID";
  private final static String ABOUT_ID = "ABOUT_ID";
  private static final String STATE_NAME = "Menu";
  private final MenuButton[][] menuButtons;
  private Texture backgroundStars;
  private Texture title;
  private boolean assetsLoaded = false;
  private boolean paused = false;
  private float assetsLoadedLastProgress = -1;
  private final ControllerCallbackMenuState controllerCallbackMenuState;
  private final Vector3 cursorPosition;

  private String lastSelectedButtonId = null;

  private boolean blockStickInput = false;
  private float stickTimeHelper;
  private float controllerTimeHelper;
  private final AssetManager assetManager;
  private boolean controllerDownKeyWasPressed = false;
  private boolean controllerUpKeyWasPressed = false;
  private boolean controllerLeftKeyWasPressed = false;
  private boolean controllerRightKeyWasPressed = false;
  private boolean controllerSelectKeyWasPressed = false;
  private boolean controllerStartKeyWasPressed = false;
  private boolean controllerExitKeyWasPressed = false;
  private boolean controllerFullScreenToggleKeyPressed = false;

  public MenuState(GameStateManager gameStateManager) {
    super(gameStateManager, STATE_NAME);

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Initialize variable for the cursor position
    cursorPosition = new Vector3();

    // Get asset manager from the game state manager
    this.assetManager = gameStateManager.getAssetManager();
    // Load resources that are necessary to create the buttons
    assetManager.load(MainGame.getGameButtonFilePath("menu_active"), Texture.class);
    assetManager.load(MainGame.getGameButtonFilePath("menu_not_active"), Texture.class);
    assetManager.load(MainGame.getGameButtonFilePath("menu_active_small"), Texture.class);
    assetManager.load(MainGame.getGameButtonFilePath("menu_not_active_small"), Texture.class);
    // Wait until the textures necessary to create the buttons are loaded
    assetManager.finishLoading();
    // Get loaded assets necessary to create the buttons
    Texture textureMenuButtonBigSelected = assetManager.get(MainGame.getGameButtonFilePath("menu_active"));
    Texture textureMenuButtonBigDefault = assetManager.get(MainGame.getGameButtonFilePath("menu_not_active"));
    Texture textureMenuButtonSmallSelected = assetManager.get(MainGame.getGameButtonFilePath("menu_not_active_small"));
    Texture textureMenuButtonSmallDefault = assetManager.get(MainGame.getGameButtonFilePath("menu_active_small"));

    // Load other assets that are not necessary to be available just yet
    assetManager.load(MainGame.getGameBackgroundFilePath("stars"), Texture.class);
    assetManager.load(MainGame.getGameLogoFilePath("tnt"), Texture.class);

    // Create menu buttons
    menuButtons = new MenuButton[][]{
        {
        new MenuButtonBig(START_ID, "START", textureMenuButtonBigDefault,
            textureMenuButtonBigSelected, (float) MainGame.GAME_WIDTH / 2,
            (float) MainGame.GAME_HEIGHT / 6 * 2.8f, true)
        },
        {
        new MenuButtonSmall(ABOUT_ID, "ABOUT", textureMenuButtonSmallDefault,
            textureMenuButtonSmallSelected, (float) MainGame.GAME_WIDTH / 4,
            (float) MainGame.GAME_HEIGHT / 6 * 1),
        new MenuButtonSmall(HIGHSCORE_ID, "HIGHSCORES", textureMenuButtonSmallDefault,
            textureMenuButtonSmallSelected,
            (float) MainGame.GAME_WIDTH / 2 + (float) MainGame.GAME_WIDTH / 4,
            (float) MainGame.GAME_HEIGHT / 6 * 1)
        }
    };

    // Register controller callback so that controller input can be managed
    controllerCallbackMenuState = new ControllerCallbackMenuState(this);
    Controllers.addListener(controllerCallbackMenuState);
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
            lastSelectedButtonId = menuButton.getId();
          }
          menuButton.setSelected(menuButton.contains(cursorPosition));
        }
      }
    }

    // TODO Keyboard input (select the next button when left right key is pressed)
    if (!buttonCurrentlySelectedByCursor) {
      if (Gdx.input.isKeyJustPressed(Keys.DOWN))
        lastSelectedButtonId = HelperMenu.selectNextButton(menuButtons, HelperMenuButtonNavigation.DOWN, lastSelectedButtonId);
      if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.TAB))
        lastSelectedButtonId = HelperMenu.selectNextButton(menuButtons, HelperMenuButtonNavigation.RIGHT, lastSelectedButtonId);
      if (Gdx.input.isKeyJustPressed(Keys.UP))
        lastSelectedButtonId = HelperMenu.selectNextButton(menuButtons, HelperMenuButtonNavigation.UP, lastSelectedButtonId);
      if (Gdx.input.isKeyJustPressed(Keys.LEFT))
        lastSelectedButtonId = HelperMenu.selectNextButton(menuButtons, HelperMenuButtonNavigation.LEFT, lastSelectedButtonId);
    }

    // TODO Controller input (select the next button when left right button or pad is pressed)
    if (!buttonCurrentlySelectedByCursor) {
      if (controllerDownKeyWasPressed) {
        controllerDownKeyWasPressed = false;
        lastSelectedButtonId = HelperMenu
            .selectNextButton(menuButtons, HelperMenuButtonNavigation.DOWN, lastSelectedButtonId);
      }
      if (controllerUpKeyWasPressed) {
        controllerUpKeyWasPressed = false;
        lastSelectedButtonId = HelperMenu.selectNextButton(menuButtons, HelperMenuButtonNavigation.UP, lastSelectedButtonId);
      }
      if (controllerLeftKeyWasPressed) {
        controllerLeftKeyWasPressed = false;
        lastSelectedButtonId = HelperMenu.selectNextButton(menuButtons, HelperMenuButtonNavigation.LEFT, lastSelectedButtonId);
      }
      if (controllerRightKeyWasPressed) {
        controllerRightKeyWasPressed = false;
        lastSelectedButtonId = HelperMenu.selectNextButton(menuButtons, HelperMenuButtonNavigation.RIGHT, lastSelectedButtonId);
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

    // if escape or back is pressed quit
    if (Gdx.input.isCatchKey(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.ESCAPE) || controllerExitKeyWasPressed) {
      controllerExitKeyWasPressed = false;
      Gdx.app.exit();
    }
  }

  @Override
  public void update(final float deltaTime) {
    stickTimeHelper += deltaTime;
    controllerTimeHelper += deltaTime;
  }

  @Override
  public void render(final SpriteBatch spriteBatch) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if(this.assetManager.update()) {
      if (!assetsLoaded) {
        float progress = this.assetManager.getProgress() * 100;
        Gdx.app.log("menu_state:render", MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at " + progress + "%");
        assetsLoaded = true;
        backgroundStars = assetManager.get(MainGame.getGameBackgroundFilePath("stars"));
        title = assetManager.get(MainGame.getGameLogoFilePath("tnt"));
      }
      // Render menu
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();

      spriteBatch.draw(backgroundStars, 0, 0);
      for (final MenuButton[] menuButtonLine : menuButtons) {
        for (final MenuButton menuButton : menuButtonLine) {
          menuButton.draw(spriteBatch);
        }
      }

      spriteBatch.draw(title, 0, 0);
      spriteBatch.end();
    } else {
      // display loading information
      float progress = this.assetManager.getProgress() * 100;
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.log("menu_state:render", MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at " + progress + "%");
      }
    }
  }

  @Override
  public void dispose() {
    // Controllers.removeListener(controllerHelperMenu);
    for (final MenuButton[] menuButtonLine : menuButtons) {
      for (final MenuButton menuButton : menuButtonLine) {
        menuButton.dispose();
      }
    }
    // Reduce the reference to used resources in this method (when no object is referencing the
    // resource any more it is automatically disposed)
    Gdx.app.debug("menu_state:dispose", "Loaded assets before unloading are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("menu_state:dispose", "- " + loadedAsset);
    }
    assetManager.unload(MainGame.getGameButtonFilePath("menu_active"));
    assetManager.unload(MainGame.getGameButtonFilePath("menu_not_active"));
    assetManager.unload(MainGame.getGameButtonFilePath("menu_active_small"));
    assetManager.unload(MainGame.getGameButtonFilePath("menu_not_active_small"));
    assetManager.unload(MainGame.getGameBackgroundFilePath("stars"));
    assetManager.unload(MainGame.getGameLogoFilePath("tnt"));
    Gdx.app.debug("menu_state:dispose", "Loaded assets are:");
    for (final String loadedAsset : assetManager.getAssetNames()) {
      Gdx.app.debug("menu_state:dispose", "- " + loadedAsset);
    }

  }

  private void openSelectedMenuButton() {
    for (final MenuButton[] menuButtonLine : menuButtons) {
      for (final MenuButton menuButton : menuButtonLine) {
        if (menuButton.isSelected()) {
          switch (menuButton.getId()) {
            case START_ID:
              gameStateManager.setGameState(new LoadingState(gameStateManager, 1));
              break;
            case HIGHSCORE_ID:
              gameStateManager.setGameState(new HighscoreListState(gameStateManager));
              break;
            case ABOUT_ID:
              gameStateManager.setGameState(new CreditState(gameStateManager));
              break;
          }
        }
      }
    }
  }
  /*
  @Override
  public void controllerCallbackBackPressed() {
    if (controllerTimeHelper < 0.2) {
      return;
    }
    // exit application
    Gdx.app.exit();
  }

  @Override
  public void controllerCallbackButtonPressed(int buttonId) {
    if (controllerTimeHelper < 0.2) {
      return;
    }
    // open selected button
    if (buttonId == ControllerWiki.BUTTON_A) {
      openSelectedMenuButton();
    }
    if (buttonId == ControllerWiki.BUTTON_START) {
      GameStateManager.toggleFullScreen();
    }
  }


    @Override
    public void controllerCallbackDPadButtonPressed(PovDirection direction) {
      // select next button
      if (direction == ControllerWiki.BUTTON_DPAD_DOWN || direction == ControllerWiki.BUTTON_DPAD_RIGHT)
        selectNextButton(true);
      if (direction == ControllerWiki.BUTTON_DPAD_UP || direction == ControllerWiki.BUTTON_DPAD_LEFT)
        selectNextButton(false);

    }

  @Override
  public void controllerCallbackStickMoved(final boolean xAxis, final float value) {
    // select next button
    if (blockStickInput && stickTimeHelper >= 0.3) {
      blockStickInput = false;
    }
    if (!blockStickInput && (value > 0.3 || value < -0.3)) {
      // selectNextButton(value > 0.3);
      stickTimeHelper = 0;
      blockStickInput = true;
    }
  }
  */
  @Override
  public void pause() {
    // Nothing to do
    paused = true;
    Gdx.app.debug("menu_state:pause", MainGame.getCurrentTimeStampLogString() + "pause");
  }

  @Override
  public void resume() {
    // Nothing to do
    paused = false;
    Gdx.app.debug("menu_state:resume", MainGame.getCurrentTimeStampLogString() + "resume");
  }

  @Override
  public void controllerCallbackSelectLeftMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackSelectLeftMenuButton", MainGame.getCurrentTimeStampLogString());
    controllerLeftKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackSelectRightMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackSelectRightMenuButton", MainGame.getCurrentTimeStampLogString());
    controllerRightKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackSelectAboveMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackSelectAboveMenuButton", MainGame.getCurrentTimeStampLogString());
    controllerUpKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackSelectBelowMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackSelectBelowMenuButton", MainGame.getCurrentTimeStampLogString());
    controllerDownKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickStartMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackClickStartMenuButton", MainGame.getCurrentTimeStampLogString());
    controllerStartKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickMenuButton() {
    Gdx.app.debug("menu_state:controllerCallbackClickMenuButton", MainGame.getCurrentTimeStampLogString());
    controllerSelectKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickExitButton() {
    Gdx.app.debug("menu_state:controllerCallbackClickExitButton", MainGame.getCurrentTimeStampLogString());
    controllerExitKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    Gdx.app.debug("menu_state:controllerCallbackToggleFullScreen", MainGame.getCurrentTimeStampLogString());
    controllerFullScreenToggleKeyPressed = true;
  }
}
