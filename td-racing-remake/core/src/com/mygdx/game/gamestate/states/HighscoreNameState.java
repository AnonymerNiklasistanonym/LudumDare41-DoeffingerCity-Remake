package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
//import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.resources.HighscoreCharacterButton;
import com.mygdx.game.listener.controller.ControllerHelperMenu;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;
import com.mygdx.game.unsorted.PreferencesManager;

public class HighscoreNameState extends GameState implements ControllerMenuCallbackInterface {

	private static final String STATE_NAME = "Highscore > Name";

	private final HighscoreCharacterButton[] highscoreCharacterButtons;
	private final ShapeRenderer shapeRenderer;
	private final PreferencesManager preferencesManager;
	private final String highscoreText, scoreText;
	private final Vector2 highscoreTextPosition, scoreTextPosition;
	private final int score;

	private int currentIndex = 0;

	private boolean blockStickInput = false;
	private float stickTimeHelper;
	private float controllerTimeHelper;
	private final ControllerListener controllerHelperMenu;

	private final boolean goToCreditStage;
	private final int level;

	public HighscoreNameState(GameStateManager gameStateManager, final int score, final int level,
			final boolean goToCreditStage) {
		super(gameStateManager, STATE_NAME);

		this.score = score;
		this.level = level;

		this.goToCreditStage = goToCreditStage;

		shapeRenderer = new ShapeRenderer();
		preferencesManager = new PreferencesManager();

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		highscoreCharacterButtons = new HighscoreCharacterButton[6];
		char startChar = 'A';
		for (int i = 0; i < highscoreCharacterButtons.length; i++) {
			highscoreCharacterButtons[i] = new HighscoreCharacterButton(startChar++,
					MainGame.GAME_WIDTH / 8 * ((i + 1) * 2 + 1));
		}

		highscoreCharacterButtons[currentIndex].activate(true);

		MainGame.fontUpperCaseBig.getData().setScale(0.65f);
		MainGame.fontUpperCaseBig.setUseIntegerPositions(false);
		this.highscoreText = "YOU REACHED THE TOP 10!";
		this.highscoreTextPosition = GameStateManager.calculateCenteredTextPosition(MainGame.fontUpperCaseBig,
				highscoreText, MainGame.GAME_WIDTH, (float)MainGame.GAME_HEIGHT / 3 * 5);
		this.scoreText = "" + score;
		this.scoreTextPosition = GameStateManager.calculateCenteredTextPosition(MainGame.fontUpperCaseBig, scoreText,
				MainGame.GAME_WIDTH, (float)MainGame.GAME_HEIGHT / 3);

		final char[] name = preferencesManager.getName();
		if (name != null && name.length == highscoreCharacterButtons.length) {
			for (int i = 0; i < highscoreCharacterButtons.length; i++)
				highscoreCharacterButtons[i].setNewCharacter(name[i]);
		}

		// controller setup
		controllerHelperMenu = new ControllerHelperMenu(this);
		Controllers.addListener(controllerHelperMenu);
		blockStickInput = false;
		stickTimeHelper = 0;
		controllerTimeHelper = 0;
	}

	@Override
	protected void handleInput() {
		GameStateManager.toggleFullScreen(true);

		if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A))
			selectNextCharacterButton(false);
		if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D))
			selectNextCharacterButton(true);

		if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
			selectNextCharacter(true);
		if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S))
			selectNextCharacter(false);

		if (Gdx.input.isKeyJustPressed(Keys.ENTER))
			saveHighscoreAndGoToList();

		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isCatchBackKey()))
			goBack();
	}

	private void goBack() {
		if (goToCreditStage)
			gameStateManager.setGameState(new CreditState(gameStateManager));
		else
			gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	@Override
	protected void update(final float deltaTime) {
		controllerTimeHelper += deltaTime;
		stickTimeHelper += deltaTime;
	}

	@Override
	protected void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(this.camera.combined);
		spriteBatch.begin();
		MainGame.fontUpperCaseBig.draw(spriteBatch, highscoreText, highscoreTextPosition.x, highscoreTextPosition.y);
		MainGame.fontUpperCaseBig.draw(spriteBatch, scoreText, scoreTextPosition.x, scoreTextPosition.y);
		for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
			highscoreCharacterButton.draw(spriteBatch);
		spriteBatch.end();
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1, 1, 1, 1);
		for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
			highscoreCharacterButton.drawTriangels(shapeRenderer);
		shapeRenderer.end();
	}

	@Override
	protected void dispose() {
		Controllers.removeListener(controllerHelperMenu);
		shapeRenderer.dispose();
	}

	private void saveHighscoreAndGoToList() {
		String name = "";
		for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
			name += highscoreCharacterButton.getCurrentCharacter();
		preferencesManager.saveHighscore(name, this.score);
		if (goToCreditStage)
			gameStateManager.setGameState(new CreditState(gameStateManager));
		else
			gameStateManager.setGameState(new GameOverState(gameStateManager, level));
	}

	@Override
	public void controllerCallbackBackPressed() {
		goBack();
	}

	@Override
	public void controllerCallbackButtonPressed(int buttonId) {
		if (controllerTimeHelper < 0.2)
			return;
		if (buttonId == ControllerWiki.BUTTON_A)
			saveHighscoreAndGoToList();
		if (buttonId == ControllerWiki.BUTTON_START)
			GameStateManager.toggleFullScreen();
	}

	private void selectNextCharacterButton(boolean left) {
		if (left)
			currentIndex = (currentIndex + 1 == highscoreCharacterButtons.length) ? 0 : currentIndex + 1;
		else
			currentIndex = (currentIndex - 1 < 0) ? highscoreCharacterButtons.length - 1 : currentIndex - 1;

		for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
			highscoreCharacterButton.activate(false);
		highscoreCharacterButtons[currentIndex].activate(true);
	}
/*
	@Override
	public void controllerCallbackDPadButtonPressed(PovDirection direction) {
		// select character next button
		if (direction == ControllerWiki.BUTTON_DPAD_RIGHT)
			selectNextCharacterButton(true);
		if (direction == ControllerWiki.BUTTON_DPAD_LEFT)
			selectNextCharacterButton(false);
		if (direction == ControllerWiki.BUTTON_DPAD_UP)
			selectNextCharacter(true);
		if (direction == ControllerWiki.BUTTON_DPAD_DOWN)
			selectNextCharacter(false);
	}
*/
	@Override
	public void controllerCallbackStickMoved(final boolean xAxis, final float value) {
		// select next button
		if (blockStickInput && stickTimeHelper >= 0.3)
			blockStickInput = false;
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
		final char currentChar = highscoreCharacterButtons[currentIndex].getCurrentCharacter();
		if (upwards) {
			highscoreCharacterButtons[currentIndex]
					.setNewCharacter((currentChar + 1 > 'Z') ? 'A' : (char) (currentChar + 1));
		} else {
			highscoreCharacterButtons[currentIndex]
					.setNewCharacter((currentChar - 1 < 'A') ? 'Z' : (char) (currentChar - 1));
		}

		for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
			highscoreCharacterButton.activate(false);
		highscoreCharacterButtons[currentIndex].activate(true);

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
