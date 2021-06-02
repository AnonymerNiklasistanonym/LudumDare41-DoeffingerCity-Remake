package com.mygdx.game.gamestate.states;

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
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.elements.HighscoreSelectCharacterDisplay;
import com.mygdx.game.gamestate.states.elements.HighscoreSelectCharacterDisplayInputState;
import com.mygdx.game.listener.controller.ControllerHelperMenu;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;

public class CreateHighscoreEntryState extends GameState implements
    ControllerMenuCallbackInterface {

  private static final String STATE_NAME = "CreateHighscoreEntry";
  private static final String highscoreText = "YOU REACHED THE TOP 5!";
  /**
   * Variable for the font scale of the credits text
   */
  private static final float fontScaleYouReachedTop5 = 0.65f;
  private final ShapeRenderer shapeRenderer;
  private final String scoreText;
  private final int score;
  private final ControllerListener controllerHelperMenu;
  private final boolean goToCreditStage;
  private final int level;
  private HighscoreSelectCharacterDisplay[] highscoreCharacterButtons;
  private Vector2 highscoreTextPosition, scoreTextPosition;
  private int currentIndex = 0;
  private boolean blockStickInput = false;
  private float stickTimeHelper;
  private float controllerTimeHelper;
  /**
   * Variable for the font of the credits text
   */
  private BitmapFont fontYouReachedTop5;

  public CreateHighscoreEntryState(GameStateManager gameStateManager, final int score,
      final int level,
      final boolean goToCreditStage) {
    super(gameStateManager, STATE_NAME);

    this.score = score;
    this.level = level;
    this.goToCreditStage = goToCreditStage;
    scoreText = "" + score;

    // Create a shape renderer
    shapeRenderer = new ShapeRenderer();

    // Initialize game camera/canvas
    camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

    // Load assets that are not necessary to be available just yet
    assetManager
        .load(HighscoreSelectCharacterDisplay.ASSET_MANAGER_ID_CHARACTER_FONT, BitmapFont.class);
    assetManager.load(MainGame.getGameFontFilePath("cornerstone_upper_case_big"), BitmapFont.class);

    // Register controller callback so that controller input can be managed
    controllerHelperMenu = new ControllerHelperMenu(this);
    Controllers.addListener(controllerHelperMenu);
  }

  public CreateHighscoreEntryState(GameStateManager gameStateManager, final int score,
      final boolean goToCreditStage) {
    this(gameStateManager, score, 0, goToCreditStage);
  }

  @Override
  protected void handleInput() {
    GameStateManager.toggleFullScreen(true);

		if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A)) {
			selectNextCharacterButton(false);
		}
		if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D)) {
			selectNextCharacterButton(true);
		}

		if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
			selectNextCharacter(true);
		}
		if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
			selectNextCharacter(false);
		}

		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			saveHighscoreAndGoToList();
		}

		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input
				.isCatchBackKey())) {
			goBack();
		}
  }

  private void goBack() {
		if (goToCreditStage) {
			gameStateManager.setGameState(new CreditState(gameStateManager, true));
		} else {
			gameStateManager.setGameState(new HighscoreListState(gameStateManager));
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
  protected void render(final SpriteBatch spriteBatch) {
    if (paused) {
      // When the game is paused don't render anything
      return;
    }
    if (assetManager.update()) {
      if (!assetsLoaded) {
        float progress = assetManager.getProgress() * 100;
        Gdx.app.debug("menu_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
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

        fontYouReachedTop5 = assetManager
            .get(MainGame.getGameFontFilePath("cornerstone_upper_case_big"));
        fontYouReachedTop5.getData().setScale(fontScaleYouReachedTop5);
        fontYouReachedTop5.setUseIntegerPositions(false);
        highscoreTextPosition = GameStateManager.calculateCenteredTextPosition(fontYouReachedTop5,
            highscoreText, MainGame.GAME_WIDTH, (float) MainGame.GAME_HEIGHT / 3 * 5);
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
          .draw(spriteBatch, highscoreText, highscoreTextPosition.x, highscoreTextPosition.y);
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
      // display loading information
      float progress = assetManager.getProgress() * 100;
      if (progress != assetsLoadedLastProgress) {
        assetsLoadedLastProgress = progress;
        Gdx.app.debug("menu_state:render",
            MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
                + progress + "%");
      }
    }
  }

  @Override
  protected void dispose() {
    Controllers.removeListener(controllerHelperMenu);
    shapeRenderer.dispose();
  }

  private void saveHighscoreAndGoToList() {
    String name = "";
		for (final HighscoreSelectCharacterDisplay highscoreCharacterButton : highscoreCharacterButtons) {
			name += highscoreCharacterButton.getCurrentSelectedCharacter();
		}
    preferencesManager.saveHighscore(name, this.score);
		if (goToCreditStage) {
			gameStateManager.setGameState(new CreditState(gameStateManager, true));
		} else {
			gameStateManager.setGameState(new GameOverState(gameStateManager, level));
		}
  }

  @Override
  public void controllerCallbackBackPressed() {
    goBack();
  }

  @Override
  public void controllerCallbackButtonPressed(int buttonId) {
		if (controllerTimeHelper < 0.2) {
			return;
		}
		if (buttonId == ControllerWiki.BUTTON_A) {
			saveHighscoreAndGoToList();
		}
		if (buttonId == ControllerWiki.BUTTON_START) {
			GameStateManager.toggleFullScreen();
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

  @Override
  public void controllerCallbackStickMoved(final boolean xAxis, final float value) {
    // select next button
		if (blockStickInput && stickTimeHelper >= 0.3) {
			blockStickInput = false;
		}
    if ((!blockStickInput && !xAxis) && (value > 0.3 || value < -0.3)) {
      selectNextCharacter(value > 0.3);
      stickTimeHelper = 0;
      blockStickInput = true;
    }
    if ((!blockStickInput && xAxis) && (value > 0.3 || value < -0.3)) {
      selectNextCharacterButton(value > 0.3);
      stickTimeHelper = 0;
      blockStickInput = true;
    }
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

}
