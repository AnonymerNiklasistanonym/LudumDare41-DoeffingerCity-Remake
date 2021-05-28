package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.resources.MenuButton;
import com.mygdx.game.gamestate.states.resources.MenuButtonBig;
import com.mygdx.game.gamestate.states.resources.MenuButtonSmall;
import com.mygdx.game.helper.HelperMenu;
import com.mygdx.game.helper.HelperMenuButtonNavigation;
import com.mygdx.game.helper.HelperUtil;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;

public class MenuState extends GameState implements ControllerMenuCallbackInterface {

  private final static String START_ID = "START_ID";
  private final static String HIGHSCORE_ID = "HIGHSCORE_ID";
  private final static String ABOUT_ID = "ABOUT_ID";
  private static final String STATE_NAME = "Menu";
  private final MenuButton[][] menuButtons;
  private Texture backgroundStars;
  private Texture title;
  private boolean assetsLoaded = false;
  private float assetsLoadedLastProgress = -1;
  // private final ControllerHelperMenu controllerHelperMenu;
  private final Vector3 cursorPosition;

  private String lastSelectedButtonId = null;

  private boolean blockStickInput = false;
  private float stickTimeHelper;
  private float controllerTimeHelper;
  private final AssetManager assetManager;

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

    /*
    controllerHelperMenu = new ControllerHelperMenu(this);
    Controllers.addListener(controllerHelperMenu);
    blockStickInput = false;
    stickTimeHelper = 0;
     */
  }

  @Override
  public void handleInput() {
    if (Gdx.app.getType() == ApplicationType.Desktop) {
      // Toggle full screen when full screen key is pressed
      GameStateManager.toggleFullScreen(true);
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
      // TODO
    }


    // If a button is touched do something or Space or Enter is pressed execute the
    // action for the selected button
    if (Gdx.input.justTouched()
        || (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE))) {
      openSelectedMenuButton();
    }

    // if escape or back is pressed quit
    if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
      Gdx.app.exit();
    }

    // TODO Controller input
  }

  private void selectNextButton(final int key) {
    if (key == Keys.DOWN) {
      outerloop:
      for (int i = 0; i < menuButtons.length; i++) {
        for (int j = 0; j < menuButtons[i].length; j++) {
          // If the menu button that is selected was found
          if (menuButtons[i][j].isSelected()) {
            // Deselect it
            menuButtons[i][j].setSelected(false);
            // And select the button in the row below (or if its the last row the row top above)
            final int menuButtonRowBelowIndex = HelperUtil
                .moduloWithPositiveReturnValues(i + 1, menuButtons.length);
            final int menuButtonIndexInRowBelow = HelperUtil
                .moduloWithPositiveReturnValues(j, menuButtons[menuButtonRowBelowIndex].length);
            menuButtons[menuButtonRowBelowIndex][menuButtonIndexInRowBelow].setSelected(true);
            break outerloop;
          }
        }
      }
    }
    if (key == Keys.UP) {
      outerloop:
      for (int i = 0; i < menuButtons.length; i++) {
        for (int j = 0; j < menuButtons[i].length; j++) {
          // If the menu button that is selected was found
          if (menuButtons[i][j].isSelected()) {
            // Deselect it
            menuButtons[i][j].setSelected(false);
            // And select the button in the row above (or if its the first row the row down below)
            final int menuButtonRowAboveIndex = HelperUtil
                .moduloWithPositiveReturnValues(i - 1, menuButtons.length);
            final int menuButtonIndexInRowAbove = HelperUtil
                .moduloWithPositiveReturnValues(j, menuButtons[menuButtonRowAboveIndex].length);
            menuButtons[menuButtonRowAboveIndex][menuButtonIndexInRowAbove].setSelected(true);
            break outerloop;
          }
        }
      }
    }
    /*
    if (Gdx.input.isKeyJustPressed(Keys.RIGHT))
      selectNextButton(Keys.RIGHT);
    if (Gdx.input.isKeyJustPressed(Keys.UP))
      selectNextButton(Keys.UP);
    if (Gdx.input.isKeyJustPressed(Keys.LEFT))
      selectNextButton(Keys.LEFT);
    if (Gdx.input.isKeyJustPressed(Keys.TAB))
      selectNextButton(Keys.TAB);
     */
  }

  @Override
  public void update(final float deltaTime) {
    stickTimeHelper += deltaTime;
    controllerTimeHelper += deltaTime;
  }

  @Override
  public void render(final SpriteBatch spriteBatch) {
    if(this.assetManager.update()) {
      if (!assetsLoaded) {
        float progress = this.assetManager.getProgress() * 100;
        Gdx.app.log("menu_state:render", "assets are loading - progress is at " + progress + "%");
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
        Gdx.app.log("menu_state:render", "assets are loading - progress is at " + progress + "%");
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

  /*
    @Override
    public void controllerCallbackDPadButtonPressed(PovDirection direction) {
      // select next button
      if (direction == ControllerWiki.BUTTON_DPAD_DOWN || direction == ControllerWiki.BUTTON_DPAD_RIGHT)
        selectNextButton(true);
      if (direction == ControllerWiki.BUTTON_DPAD_UP || direction == ControllerWiki.BUTTON_DPAD_LEFT)
        selectNextButton(false);

    }
  */
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

  @Override
  public void pause() {
    // Nothing to do

  }

  @Override
  public void resume() {
    // Nothing to do
  }

}
