package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.create_highscore_entry.ChangeCharacterDirection;
import com.mygdx.game.controller.create_highscore_entry.ControllerCallbackCreateHighscoreEntryState;
import com.mygdx.game.controller.create_highscore_entry.IControllerCallbackCreateHighscoreEntryState;
import com.mygdx.game.controller.create_highscore_entry.NextCharacterEntryDirection;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.elements.HighscoreSelectCharacterDisplay;
import com.mygdx.game.gamestate.elements.HighscoreSelectCharacterDisplayInputState;

public class CreateHighscoreEntryState extends GameState implements
    IControllerCallbackCreateHighscoreEntryState {

  private static final String STATE_NAME = "CreateHighscoreEntry";
  private static final String HIGHSCORE_TEXT = "YOU REACHED THE TOP 5!";
  private static final String ASSET_ID_YOU_REACHED_TOP_5_FONT = MainGame
      .getGameFontFilePath("cornerstone_upper_case_big");
  /**
   * Variable for the font scale of the credits text
   */
  private static final float FONT_SCALE_YOU_REACHED_TOP_5 = 0.65f;

  private final String scoreText;
  private final int score;
  private final int laps;
  private final ControllerListener controllerCallbackCreateHighscoreEntryState;
  private final boolean goToCreditStage;
  private final int level;
  private HighscoreSelectCharacterDisplay[] highscoreCharacterButtons;
  private Vector2 highscoreTextPosition, scoreTextPosition;
  private int currentIndex = 0;

  /**
   * Variable for the font of the credits text
   */
  private BitmapFont fontYouReachedTop5;

  public CreateHighscoreEntryState(GameStateManager gameStateManager, final int score,
      final int level, final int laps, final boolean goToCreditStage) {
    super(gameStateManager, STATE_NAME);

    this.score = score;
    this.level = level;
    this.laps = laps;
    this.goToCreditStage = goToCreditStage;
    scoreText = "" + score;

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Load assets that are not necessary to be available just yet
    assetManager
        .load(HighscoreSelectCharacterDisplay.ASSET_MANAGER_ID_CHARACTER_FONT, BitmapFont.class);
    assetManager.load(ASSET_ID_YOU_REACHED_TOP_5_FONT, BitmapFont.class);

    // Register controller callback so that controller input can be managed
    controllerCallbackCreateHighscoreEntryState = new ControllerCallbackCreateHighscoreEntryState(
        this);
    Controllers.addListener(controllerCallbackCreateHighscoreEntryState);
  }

  @Override
  protected void handleInput() {
    if (paused || !assetsLoaded) {
      // When the game is paused or assets not loaded don't handle anything
      return;
    }

    if (Gdx.app.getType() == ApplicationType.Desktop) {
      // Toggle full screen when full screen keys are pressed (desktop only)
      if (controllerToggleFullScreenPressed || Gdx.input.isKeyJustPressed(Keys.F11)) {
        controllerToggleFullScreenPressed = false;
        gameStateManager.toggleFullScreen();
      }
    }

    // Turn music on/off
    if (controllerToggleMusicPressed || Gdx.input.isKeyJustPressed(Keys.M)) {
      controllerToggleMusicPressed = false;
      preferencesManager.setMusicOn(!preferencesManager.getMusicOn());
    }
    // Turn sound effects on/off
    if (controllerToggleSoundEffectsPressed || Gdx.input.isKeyJustPressed(Keys.U)) {
      controllerToggleSoundEffectsPressed = false;
      preferencesManager.setSoundEffectsOn(!preferencesManager.getSoundEffectsOn());
    }

    if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A)
        || controllerLeftKeyWasPressed) {
      controllerLeftKeyWasPressed = false;
      selectNextCharacterButton(false);
    }
    if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D)
        || controllerRightKeyWasPressed) {
      controllerRightKeyWasPressed = false;
      selectNextCharacterButton(true);
    }

    if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)
        || controllerUpKeyWasPressed) {
      controllerUpKeyWasPressed = false;
      selectNextCharacter(true);
    }
    if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)
        || controllerDownKeyWasPressed) {
      controllerDownKeyWasPressed = false;
      selectNextCharacter(false);
    }

    if (Gdx.input.isKeyJustPressed(Keys.ENTER) || controllerSelectKeyWasPressed) {
      controllerSelectKeyWasPressed = false;
      saveHighscoreAndGoToList();
    }

    if (Gdx.input.isCatchKey(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
        || controllerBackKeyWasPressed) {
      controllerBackKeyWasPressed = false;
      if (goToCreditStage) {
        gameStateManager.setGameState(new CreditState(gameStateManager, true));
      } else {
        gameStateManager.setGameState(new HighscoreListState(gameStateManager));
      }
    }
  }

  @Override
  protected void update(final float deltaTime) {
    if (highscoreCharacterButtons != null) {
      for (final HighscoreSelectCharacterDisplay highscoreCharacterButton : highscoreCharacterButtons) {
        highscoreCharacterButton.update(deltaTime);
      }
    }
  }

  @Override
  protected void render(final SpriteBatch spriteBatch, final ShapeRenderer shapeRenderer) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if (assetManager.update()) {
      if (!assetsLoaded) {
        Gdx.app.debug("menu_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + (assetManager.getProgress() * 10) + "%");
        assetsLoaded = true;

        highscoreCharacterButtons = new HighscoreSelectCharacterDisplay[6];
        char startChar = 'A';
        for (int i = 0; i < highscoreCharacterButtons.length; i++) {
          highscoreCharacterButtons[i] = new HighscoreSelectCharacterDisplay(startChar++,
              assetManager, (float) MainGame.GAME_WIDTH / 16 * ((i + 1) * 2 + 1),
              (float) MainGame.GAME_HEIGHT / 2,
              (i != currentIndex) ? HighscoreSelectCharacterDisplayInputState.NOT_ACTIVE
                  : HighscoreSelectCharacterDisplayInputState.ACTIVE);
        }

        fontYouReachedTop5 = assetManager.get(ASSET_ID_YOU_REACHED_TOP_5_FONT);
        fontYouReachedTop5.getData().setScale(FONT_SCALE_YOU_REACHED_TOP_5);
        fontYouReachedTop5.setUseIntegerPositions(false);
        highscoreTextPosition = GameStateManager.calculateCenteredTextPosition(fontYouReachedTop5,
            HIGHSCORE_TEXT, MainGame.GAME_WIDTH, (float) MainGame.GAME_HEIGHT / 3 * 5);
        scoreTextPosition = GameStateManager
            .calculateCenteredTextPosition(fontYouReachedTop5, scoreText,
                MainGame.GAME_WIDTH, (float) MainGame.GAME_HEIGHT / 3);

        final char[] name = preferencesManager.getName();
        if (name != null && name.length == highscoreCharacterButtons.length) {
          for (int i = 0; i < highscoreCharacterButtons.length; i++) {
            highscoreCharacterButtons[i].setNewCharacter(name[i]);
          }
        }
      }
      // Render highscore entry
      spriteBatch.setProjectionMatrix(camera.combined);
      spriteBatch.begin();
      fontYouReachedTop5
          .draw(spriteBatch, HIGHSCORE_TEXT, highscoreTextPosition.x, highscoreTextPosition.y);
      fontYouReachedTop5.draw(spriteBatch, scoreText, scoreTextPosition.x, scoreTextPosition.y);
      for (final HighscoreSelectCharacterDisplay highscoreCharacterButton : highscoreCharacterButtons) {
        highscoreCharacterButton.draw(spriteBatch);
      }
      spriteBatch.end();
      shapeRenderer.begin(ShapeType.Filled);
      for (final HighscoreSelectCharacterDisplay highscoreCharacterButton : highscoreCharacterButtons) {
        highscoreCharacterButton.drawUpDownInput(shapeRenderer);
      }
      shapeRenderer.end();
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
  protected void dispose() {
    Controllers.removeListener(controllerCallbackCreateHighscoreEntryState);
  }

  private void saveHighscoreAndGoToList() {
    StringBuilder name = new StringBuilder();
    for (final HighscoreSelectCharacterDisplay highscoreCharacterButton : highscoreCharacterButtons) {
      name.append(highscoreCharacterButton.getCurrentSelectedCharacter());
    }
    preferencesManager.saveHighscore(name.toString(), this.score, this.level, this.laps);
    if (goToCreditStage) {
      gameStateManager.setGameState(new CreditState(gameStateManager, true));
    } else {
      gameStateManager.setGameState(new GameOverState(gameStateManager, level));
    }
  }

  private void selectNextCharacterButton(boolean left) {
    if (left) {
      currentIndex = (currentIndex + 1 == highscoreCharacterButtons.length) ? 0 : currentIndex + 1;
    } else {
      currentIndex =
          (currentIndex - 1 < 0) ? highscoreCharacterButtons.length - 1 : currentIndex - 1;
    }

    for (final HighscoreSelectCharacterDisplay highscoreCharacterButton : highscoreCharacterButtons) {
      highscoreCharacterButton.setUpDownInput(HighscoreSelectCharacterDisplayInputState.NOT_ACTIVE);
    }
    highscoreCharacterButtons[currentIndex]
        .setUpDownInput(HighscoreSelectCharacterDisplayInputState.ACTIVE);
  }

  private void selectNextCharacter(boolean upwards) {
    final char currentChar = highscoreCharacterButtons[currentIndex].getCurrentSelectedCharacter();
    if (upwards) {
      highscoreCharacterButtons[currentIndex]
          .setNewCharacter((currentChar + 1 > 'Z') ? 'A' : (char) (currentChar + 1));
    } else {
      highscoreCharacterButtons[currentIndex]
          .setNewCharacter((currentChar - 1 < 'A') ? 'Z' : (char) (currentChar - 1));
    }

    for (final HighscoreSelectCharacterDisplay highscoreCharacterButton : highscoreCharacterButtons) {
      highscoreCharacterButton.setUpDownInput(HighscoreSelectCharacterDisplayInputState.NOT_ACTIVE);
    }
    highscoreCharacterButtons[currentIndex].setUpDownInput(
        upwards ? HighscoreSelectCharacterDisplayInputState.UP
            : HighscoreSelectCharacterDisplayInputState.DOWN);

  }

  @Override
  public void pause() {
    // Nothing to do

  }

  @Override
  public void resume() {
    // Nothing to do
  }

  @Override
  public void controllerCallbackToggleFullScreen() {
    controllerToggleFullScreenPressed = true;
  }

  @Override
  public void controllerCallbackToggleMusic() {
    controllerToggleMusicPressed = true;
  }

  @Override
  public void controllerCallbackToggleSoundEffects() {
    controllerToggleSoundEffectsPressed = true;
  }

  @Override
  public void controllerCallbackSelectCharacterEntry(NextCharacterEntryDirection direction) {
    switch (direction) {
      case LEFT:
        controllerLeftKeyWasPressed = true;
        break;
      case RIGHT:
        controllerRightKeyWasPressed = true;
        break;
    }
  }

  @Override
  public void controllerCallbackChangeCharacterEntry(ChangeCharacterDirection direction) {
    switch (direction) {
      case UPWARDS:
        controllerUpKeyWasPressed = true;
        break;
      case DOWNWARDS:
        controllerDownKeyWasPressed = true;
        break;
    }
  }

  @Override
  public void controllerCallbackClickSelect() {
    controllerSelectKeyWasPressed = true;
  }

  @Override
  public void controllerCallbackClickBackButton() {
    controllerBackKeyWasPressed = true;
  }
}
