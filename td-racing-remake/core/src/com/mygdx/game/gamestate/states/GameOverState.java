package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
//import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.resources.MenuButton;
import com.mygdx.game.gamestate.states.resources.MenuButtonBig;
import com.mygdx.game.gamestate.states.resources.MenuButtonSmall;
import com.mygdx.game.listener.controller.ControllerHelperMenu;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;

public class GameOverState extends GameState implements ControllerMenuCallbackInterface {

	private final MenuButton[] menuButtons;

	private final Texture backgroundGameOver;

	private final static String PLAY_AGAIN_ID = "PLAY_AGAIN_ID";
	private final static String PLAY_LEVEL_AGAIN_ID = "PLAY_LEVEL_AGAIN_ID";
	private final static String HIGHSCORE_ID = "HIGHSCORE_ID";
	private final static String ABOUT_ID = "ABOUT_ID";

	private static final String STATE_NAME = "Game Over";

	private final Vector3 touchPos;

	private String loadingText;

	/**
	 * The text font
	 */
	private final BitmapFont fontText;

	private Vector2 loadingTextPosition;

	private boolean blockStickInput = false;
	private float stickTimeHelper;
	private float controllerTimeHelper;
	private final ControllerListener controllerHelperMenu;

	private final int level;

	/**
	 * The global asset manager to load and get resources (it uses reference counting to easily
	 * dispose not needed resource any more after they were unloaded)
	 */
	private final AssetManager assetManager;

	public GameOverState(final GameStateManager gameStateManager, final int level) {
		super(gameStateManager, STATE_NAME);

		this.assetManager = gameStateManager.getAssetManager();
		this.assetManager.load(MainGame.getGameFontFilePath("cornerstone_upper_case_big"), BitmapFont.class);
		this.assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_FONT, BitmapFont.class);
		this.assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT, Texture.class);
		this.assetManager.load(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED, Texture.class);
		this.assetManager.load(MainGame.getGameBackgroundFilePath("game_over"), Texture.class);
		this.assetManager.finishLoading();

		this.level = level;

		// set font scale to the correct size and disable to use integers for scaling
		fontText = this.assetManager.get(MainGame.getGameFontFilePath("cornerstone_upper_case_big"));
		fontText.getData().setScale(1);

		// set camera to a scenery of 1280x720
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		touchPos = new Vector3();

		// calculate text coordinates
		this.loadingText = "GAME OVER";
		this.loadingTextPosition = GameStateManager.calculateCenteredTextPosition(fontText, loadingText,
				MainGame.GAME_WIDTH, (float) MainGame.GAME_HEIGHT / 5 * 8);

		backgroundGameOver = this.assetManager.get(MainGame.getGameBackgroundFilePath("game_over"));

		menuButtons = new MenuButton[] {
				new MenuButtonSmall(PLAY_AGAIN_ID, "RESTART", assetManager, (float) MainGame.GAME_WIDTH / 4, (float) MainGame.GAME_HEIGHT / 6 * 3,
						true),
				new MenuButtonSmall(PLAY_LEVEL_AGAIN_ID, "...LEVEL", assetManager, MainGame.GAME_WIDTH - (float) MainGame.GAME_WIDTH / 4,
						(float) MainGame.GAME_HEIGHT / 6 * 3),
				new MenuButtonSmall(HIGHSCORE_ID, "HIGHSCORES", assetManager, (float) MainGame.GAME_WIDTH / 4, (float) MainGame.GAME_HEIGHT / 6 * 1),
				new MenuButtonSmall(ABOUT_ID, "ABOUT", assetManager, MainGame.GAME_WIDTH - (float) MainGame.GAME_WIDTH / 4,
						(float) MainGame.GAME_HEIGHT / 6 * 1) };

		// controller setup
		controllerHelperMenu = new ControllerHelperMenu(this);
		Controllers.addListener(controllerHelperMenu);
		blockStickInput = false;
		stickTimeHelper = 0;
		controllerTimeHelper = 0;
	}

	@Override
	public void handleInput() {
		GameStateManager.toggleFullScreen(true);
		touchPos.set(GameStateManager.getMousePosition(camera));

		// determine on which button the mouse cursor is and select this button
		boolean oneIsSelected = false;
		for (final MenuButton menuButton : menuButtons) {
			if (menuButton.contains(touchPos))
				oneIsSelected = true;
		}
		if (oneIsSelected) {
			for (final MenuButton menuButton : menuButtons)
				menuButton.setSelected(menuButton.contains(touchPos));
		}

		// If a button is touched do something or Space or Enter is pressed execute the
		// action for the selected button
		if (Gdx.input.justTouched()
				|| (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE))) {
			for (final MenuButton menuButton : menuButtons) {
				if (menuButton.isSelected()) {
					switch (menuButton.getId()) {
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
					}
				}
			}
		}

		// if escape or back is pressed quit
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
	}

	@Override
	public void update(final float deltaTime) {
		controllerTimeHelper += deltaTime;
		stickTimeHelper += deltaTime;
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		spriteBatch.draw(backgroundGameOver, 0, 0);
		for (final MenuButton menuButton : menuButtons)
			menuButton.draw(spriteBatch);
		fontText.getData().setScale(1);
		fontText.draw(spriteBatch, loadingText, loadingTextPosition.x, loadingTextPosition.y);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		Controllers.removeListener(controllerHelperMenu);
		backgroundGameOver.dispose();
		fontText.dispose();

		// Reduce the reference to used resources in this state (when no object is referencing the
		// resource any more it is automatically disposed by the global asset manager)
		Gdx.app.debug("menu_state:dispose", "Loaded assets before unloading are:");
		for (final String loadedAsset : assetManager.getAssetNames()) {
			Gdx.app.debug("menu_state:dispose", "- " + loadedAsset);
		}
		assetManager.unload(MenuButtonSmall.ASSET_MANAGER_ID_FONT);
		assetManager.unload(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_DEFAULT);
		assetManager.unload(MenuButtonSmall.ASSET_MANAGER_ID_TEXTURE_SELECTED);
		assetManager.unload(MainGame.getGameBackgroundFilePath("game_over"));
		Gdx.app.debug("menu_state:dispose", "Loaded assets after unloading are:");
		for (final String loadedAsset : assetManager.getAssetNames()) {
			Gdx.app.debug("menu_state:dispose", "- " + loadedAsset);
		}
	}

	private void openSelectedMenuButton() {
		for (final MenuButton menuButton : menuButtons) {
			if (menuButton.isSelected()) {
				switch (menuButton.getId()) {
				case PLAY_AGAIN_ID:
					gameStateManager.setGameState(new LoadingState(gameStateManager, this.level));
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

	@Override
	public void controllerCallbackBackPressed() {
		// exit application
		Gdx.app.exit();
	}

	@Override
	public void controllerCallbackButtonPressed(int buttonId) {
		if (controllerTimeHelper < 0.2)
			return;
		// open selected button
		if (buttonId == ControllerWiki.BUTTON_A)
			openSelectedMenuButton();
		if (buttonId == ControllerWiki.BUTTON_START)
			GameStateManager.toggleFullScreen();
	}

	private void selectNextButton(boolean below) {
		for (int i = 0; i < menuButtons.length; i++) {
			if (menuButtons[i].isSelected()) {
				menuButtons[i].setSelected(false);
				if (below)
					menuButtons[(i + 1) % menuButtons.length].setSelected(true);
				else
					menuButtons[(i - 1 + menuButtons.length) % menuButtons.length].setSelected(true);
				return;
			}
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
		if (blockStickInput && stickTimeHelper >= 0.3)
			blockStickInput = false;
		if (!blockStickInput && (value > 0.3 || value < -0.3)) {
			selectNextButton(value > 0.3);
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
