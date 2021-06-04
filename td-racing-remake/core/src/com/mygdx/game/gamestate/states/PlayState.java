package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.play_state.ControllerCallbackPlayState;
import com.mygdx.game.controller.play_state.IControllerCallbackPlayState;
import com.mygdx.game.controller.play_state.SteerCarForwardsBackwards;
import com.mygdx.game.controller.play_state.SteerCarLeftRight;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.level.Level;
import com.mygdx.game.level.LevelHandler;
import com.mygdx.game.level.Wave;
import com.mygdx.game.listener.collisions.CollisionCallbackInterface;
import com.mygdx.game.listener.collisions.CollisionListener;
import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.EnemyCallbackInterface;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;
import com.mygdx.game.objects.Map;
import com.mygdx.game.objects.ScoreBoard;
import com.mygdx.game.objects.ScoreBoardCallbackInterface;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.TowerMenu;
import com.mygdx.game.objects.checkpoints.NormalCheckpoint;
import com.mygdx.game.objects.enemies.EnemyBicycle;
import com.mygdx.game.objects.enemies.EnemyFat;
import com.mygdx.game.objects.enemies.EnemyLincoln;
import com.mygdx.game.objects.enemies.EnemySmall;
import com.mygdx.game.objects.enemies.EnemySpider;
import com.mygdx.game.objects.towers.FireTower;
import com.mygdx.game.objects.towers.LaserTower;
import com.mygdx.game.objects.towers.MgTower;
import com.mygdx.game.objects.towers.SniperTower;
import com.mygdx.game.unsorted.Node;
import java.lang.Thread.State;
import java.util.Date;

public class PlayState extends GameState implements CollisionCallbackInterface, IControllerCallbackPlayState,
		ScoreBoardCallbackInterface, EnemyCallbackInterface {

	private final static String STATE_NAME = "Play";
	private final static String TEXT_LOADING = "LOADING";

	private static final String ASSET_ID_TEXT_FONT = MainGame.getGameFontFilePath("cornerstone_70");

	private static final String ASSET_ID_TEXT_LOADING_FONT = MainGame.getGameFontFilePath("cornerstone_upper_case_big");
	private static final String ASSET_ID_BACKGROUND_LOADING_TEXTURE = MainGame.getGameBackgroundFilePath("loading");

	private final static String ASSET_ID_CAR_TEXTURE = MainGame.getGameCarFilePath("standard");
	private final static String ASSET_ID_FINISH_LINE_TEXTURE = MainGame.getGameMapFilePath("finish_line");
	private final static String ASSET_ID_PIT_STOP_TEXTURE = MainGame.getGameMapFilePath("pit_stop");
	private final static String ASSET_ID_SMOKE_TEXTURE = MainGame.getGameMapFilePath("smoke");

	private final static String ASSET_ID_TOWER_CANNON_TEXTURE = MainGame.getGameButtonFilePath("tower_cannon");
	private final static String ASSET_ID_TOWER_LASER_TEXTURE = MainGame.getGameButtonFilePath("tower_laser");
	private final static String ASSET_ID_TOWER_SNIPER_TEXTURE = MainGame.getGameButtonFilePath("tower_sniper");
	private final static String ASSET_ID_TOWER_FLAME_TEXTURE = MainGame.getGameButtonFilePath("tower_flame");

	private static final String ASSET_ID_TOWER_CANNON_BOTTOM_TEXTURE = MainGame.getGameTowerFilePath("cannon_bottom");
	private static final String ASSET_ID_TOWER_CANNON_UPPER_TEXTURE = MainGame.getGameTowerFilePath("cannon_upper");
	private static final String ASSET_ID_TOWER_CANNON_FIRING_TEXTURE = MainGame.getGameTowerFilePath("cannon_firing");
	private static final String ASSET_ID_TOWER_SNIPER_BOTTOM_TEXTURE = MainGame.getGameTowerFilePath("sniper_bottom");
	private static final String ASSET_ID_TOWER_SNIPER_UPPER_TEXTURE = MainGame.getGameTowerFilePath("sniper_upper");
	private static final String ASSET_ID_TOWER_SNIPER_FIRING_TEXTURE = MainGame.getGameTowerFilePath("sniper_firing");
	private static final String ASSET_ID_TOWER_LASER_BOTTOM_TEXTURE = MainGame.getGameTowerFilePath("laser_bottom");
	private static final String ASSET_ID_TOWER_LASER_UPPER_TEXTURE = MainGame.getGameTowerFilePath("laser_upper");
	private static final String ASSET_ID_TOWER_LASER_FIRING_TEXTURE = MainGame.getGameTowerFilePath("laser_firing");
	private static final String ASSET_ID_TOWER_FLAME_BOTTOM_TEXTURE = MainGame.getGameTowerFilePath("flame_bottom");
	private static final String ASSET_ID_TOWER_FLAME_UPPER_TEXTURE = MainGame.getGameTowerFilePath("flame_upper");
	private static final String ASSET_ID_TOWER_FLAME_FIRING_TEXTURE = MainGame.getGameTowerFilePath("flame_firing");
	private static final String ASSET_ID_TOWER_FLAME_FIRE_TEXTURE = MainGame.getGameTowerFilePath("flame_fire");

	private static final String ASSET_ID_ENEMY_SMALL_NORMAL_TEXTURE = MainGame.getGameZombieFilePath("standard");
	private static final String ASSET_ID_ENEMY_SMALL_DEAD_TEXTURE = MainGame.getGameZombieFilePath("standard_dead");
	private static final String ASSET_ID_ENEMY_FAT_NORMAL_TEXTURE = MainGame.getGameZombieFilePath("fat");
	private static final String ASSET_ID_ENEMY_FAT_DEAD_TEXTURE = MainGame.getGameZombieFilePath("fat_dead");
	private static final String ASSET_ID_ENEMY_SPIDER_NORMAL_TEXTURE = MainGame.getGameZombieFilePath("spider");
	private static final String ASSET_ID_ENEMY_SPIDER_DEAD_TEXTURE = MainGame.getGameZombieFilePath("spider_dead");
	private static final String ASSET_ID_ENEMY_BICYCLE_NORMAL_TEXTURE = MainGame.getGameZombieFilePath("bicycle");
	private static final String ASSET_ID_ENEMY_BICYCLE_DEAD_TEXTURE = MainGame.getGameZombieFilePath("bicycle_dead");
	private static final String ASSET_ID_ENEMY_LINCOLN_NORMAL_TEXTURE = MainGame.getGameZombieFilePath("lincoln");
	private static final String ASSET_ID_ENEMY_LINCOLN_DEAD_TEXTURE = MainGame.getGameZombieFilePath("lincoln_dead");
	private static final String ASSET_ID_ENEMY_BLOOD_TEXTURE = MainGame.getGameZombieFilePath("blood");
	private static final String ASSET_ID_ENEMY_BLOOD_GREEN_TEXTURE = MainGame.getGameZombieFilePath("blood_green");

	private static final String ASSET_ID_TOWER_CANNON_SOUND = MainGame.getGameSoundFilePath("tower_cannon");
	private static final String ASSET_ID_TOWER_SNIPER_SOUND = MainGame.getGameSoundFilePath("tower_sniper");
	private static final String ASSET_ID_TOWER_LASER_SOUND = MainGame.getGameSoundFilePath("tower_laser", true);
	private static final String ASSET_ID_TOWER_FLAME_SOUND = MainGame.getGameSoundFilePath("tower_flame");

	private static final String ASSET_ID_THEME_MUSIC = MainGame.getGameMusicFilePath("theme");
	private static final String ASSET_ID_CAR_ENGINE_MUSIC = MainGame.getGameSoundFilePath("car_engine", true);
	private static final String ASSET_ID_CAR_ENGINE_START_SOUND = MainGame.getGameSoundFilePath("car_engine_start", true);
	private static final String ASSET_ID_SPLATT_SOUND = MainGame.getGameSoundFilePath("splatt");
	private static final String ASSET_ID_CASH_SOUND = MainGame.getGameSoundFilePath("cash");
	private static final String ASSET_ID_VICTORY_SOUND = MainGame.getGameSoundFilePath("victory");
	private static final String ASSET_ID_TRAILER_DAMAGE_SOUND = MainGame.getGameSoundFilePath("trailer_damage");

	// TODO Make that and the physics implementation variable from this value so that the fps can
	// TODO be set to other values like for example 240
	public final static int foregroundFps = 60;

	// Identify collision entities
	public final static short PLAYER_BOX = 0x1; // 0001
	public final static short ENEMY_BOX = 0x1 << 1; // 0010 or 0x2 in hex
	public final static float TIME_STEP = (float) 1 / foregroundFps; // time for physics step
	public final static float PIXEL_TO_METER = 0.05f;
	public final static float METER_TO_PIXEL = 20f;

	private Music musicBackground;
	private Music musicCar;
	private Sound splatt;
	private Sound soundGetMoney;
	private Sound soundCarStart;
	private Sound soundVictory;
	private Sound soundDamage;
	private final ScoreBoard scoreBoard;
	private final Array<Enemy> enemies, enemiesDead;
	private final Array<Tower> towers;
	private final Array<Sprite> trailerSmokes;
	private float timesincesmoke;
	private Sprite spritePitStop;
	private Sprite spriteCar;
	private Sprite spriteFinishLine;
	private Sprite spriteSmoke;
	private final ShapeRenderer shapeRenderer;
	private final Level[] levels;
	private Level level;
	private Texture backgroundLoading;

	private Tower buildingtower;
	private Checkpoint[] checkpoints;
	private final CollisionListener collis;
	private World world;
	private Car car;
	private FinishLine finishline;
	private TowerMenu towerMenu;
	private Map map;
	private Box2DDebugRenderer debugRender;
	private String waveText;
	private final Vector2 trailerpos;
	private float tutorialtimer, physicsaccumulator, timeToDisplayWaveTextInS;
	private boolean pausedByUser, debugBox2D, debugCollision, debugDistance,
			debugWay, unlockAllTowers, debugTower;
	private int tutorialState, checkPointsCleared, speedFactor;
	private final Vector2 loadingTextPosition = new Vector2();
	private String loadingText;

	/**
	 * Tracker if a controller manual pause key was pressed
	 */
	private boolean controllerToggleManualPausePressed = false;
	/**
	 * Tracker which tower was selected by a controller to build key (see controllerSelectTowerPressed)
	 */
	private int controllerSelectTowerId = 0;
	/**
	 * Tracker if a controller key to select a tower to build was pressed
	 */
	private boolean controllerSelectTowerPressed = false;

	private final Vector3 previousMouseCursorPosition = new Vector3();
	private long timeStampMouseCursorPositionWasChanged = 0;
	private final BitmapFont fontLoading, fontText;
	private static final float fontScaleLoading = 0.5f;
	private static final float fontScaleText = 0.1f;

	private final ControllerCallbackPlayState controllerCallbackPlayState;

	private Thread threadLoadFirstLevel;

	private final int levelNumber;

	private boolean levelLoaded = false;

	public PlayState(final GameStateManager gameStateManager, final int levelNumber) {
		super(gameStateManager, STATE_NAME);
		this.levelNumber = levelNumber;

		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// Set fps
		Gdx.graphics.setVSync(true);
		Gdx.graphics.setForegroundFPS(foregroundFps);

		// Load assets used for rendering the loading screen
		assetManager.load(ASSET_ID_TEXT_FONT, BitmapFont.class);
		assetManager.load(ASSET_ID_TEXT_LOADING_FONT, BitmapFont.class);
		assetManager.load(ASSET_ID_BACKGROUND_LOADING_TEXTURE, Texture.class);
		// Finish loading of resources that are necessary for the loading screen
		assetManager.finishLoading();
		// Get assets used for rendering the loading screen
		fontLoading = assetManager.get(ASSET_ID_TEXT_LOADING_FONT);
		fontLoading.setUseIntegerPositions(false);
		fontLoading.getData().setScale(fontScaleLoading);
		backgroundLoading = assetManager.get(ASSET_ID_BACKGROUND_LOADING_TEXTURE);

		// scale used font correctly
		fontText = assetManager.get(ASSET_ID_TEXT_FONT);
		fontText.getData().setScale(fontScaleText);

		// set static dependencies
		Enemy.callbackInterface = this;

		// create sprite(s)
		assetManager.load(ASSET_ID_CAR_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_FINISH_LINE_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_PIT_STOP_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_SMOKE_TEXTURE, Texture.class);

		// set textures (tower buttons)
		assetManager.load(ASSET_ID_TOWER_CANNON_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_LASER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_SNIPER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_FLAME_TEXTURE, Texture.class);

		// set textures (towers)
		assetManager.load(ASSET_ID_TOWER_CANNON_BOTTOM_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_CANNON_UPPER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_CANNON_FIRING_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_SNIPER_BOTTOM_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_SNIPER_UPPER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_SNIPER_FIRING_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_LASER_BOTTOM_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_LASER_UPPER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_LASER_FIRING_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_FLAME_BOTTOM_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_FLAME_UPPER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_FLAME_FIRING_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_FLAME_FIRE_TEXTURE, Texture.class);

		// set textures (enemies)
		assetManager.load(ASSET_ID_ENEMY_SMALL_NORMAL_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_SMALL_DEAD_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_FAT_NORMAL_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_FAT_DEAD_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_SPIDER_NORMAL_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_SPIDER_DEAD_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_BICYCLE_NORMAL_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_BICYCLE_DEAD_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_LINCOLN_NORMAL_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_LINCOLN_DEAD_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_BLOOD_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_ENEMY_BLOOD_GREEN_TEXTURE, Texture.class);

		// set audio files (towers)
		assetManager.load(ASSET_ID_TOWER_CANNON_SOUND, Sound.class);
		assetManager.load(ASSET_ID_TOWER_SNIPER_SOUND, Sound.class);
		assetManager.load(ASSET_ID_TOWER_LASER_SOUND, Sound.class);
		assetManager.load(ASSET_ID_TOWER_FLAME_SOUND, Sound.class);

		// set audio files (other)
		assetManager.load(ASSET_ID_THEME_MUSIC, Music.class);
		assetManager.load(ASSET_ID_CAR_ENGINE_MUSIC, Music.class);
		assetManager.load(ASSET_ID_CAR_ENGINE_START_SOUND, Sound.class);
		assetManager.load(ASSET_ID_SPLATT_SOUND, Sound.class);
		assetManager.load(ASSET_ID_CASH_SOUND, Sound.class);
		assetManager.load(ASSET_ID_VICTORY_SOUND, Sound.class);
		assetManager.load(ASSET_ID_TRAILER_DAMAGE_SOUND, Sound.class);

		// instantiate global fields
		speedFactor = 1;
		checkPointsCleared = 0;
		tutorialtimer = 0;
		unlockAllTowers = false;
		tutorialState = 0;
		physicsaccumulator = 0;
		timesincesmoke = 0;
		pausedByUser = false;
		debugTower = false;
		debugBox2D = false;
		debugCollision = false;
		debugWay = false;
		debugDistance = false;

		// instantiate global objects
		// TODO Create a level info object which is loaded and from this level info object load the levels
		levels = LevelHandler.loadLevels();
		scoreBoard = new ScoreBoard(this);
		preferencesManager.checkHighscore();
		// preferencesManager.setupIfFirstStart();
		enemies = new Array<Enemy>();
		towers = new Array<Tower>();
		collis = new CollisionListener(this);
		checkpoints = new Checkpoint[4];
		shapeRenderer = new ShapeRenderer();
		trailerpos = new Vector2(0, 0);
		trailerSmokes = new Array<Sprite>();
		enemiesDead = new Array<Enemy>();

		// Register controller callback so that controller input can be managed
		controllerCallbackPlayState = new ControllerCallbackPlayState(this);
		Controllers.addListener(controllerCallbackPlayState);
	}

	private void loadLevel(int levelNumber) {
		levelLoaded = false;
		long time = System.currentTimeMillis();
		Gdx.app.debug("play_state:loadLevel", MainGame.getCurrentTimeStampLogString() + "Load Level #" + levelNumber);
		// set/save level number
		scoreBoard.setLevel(levelNumber);

		if (levelNumber > 1) {
			tutorialState = -1;
		}

		// If the level number is bigger than the level number the game was won
		if (levelNumber > this.levels.length) {
			gameStateManager.setGameState(new GameWonState(gameStateManager, scoreBoard.getScore(), scoreBoard.getLevel(), scoreBoard.getLaps()));
			return;
		}
		// TODO Find out why the level number needs to be decremented
		levelNumber -= 1;
		level = levels[levelNumber];

		// TODO Place this at the correct position
		if (preferencesManager.getSoundEffectsOn()) {
			soundCarStart.play();
		}
		if (preferencesManager.getMusicOn()) {
			musicBackground.play();
		}

		// TODO Rewrite most of the next 2 sections

		// clear all enemies and tower
		enemies.clear();
		towers.clear();
		enemiesDead.clear();
		trailerSmokes.clear();

		// create a new world and add contact listener to the new world
		world = new World(new Vector2(), true);
		world.setContactListener(collis);
		// create a new debug renderer
		debugRender = new Box2DDebugRenderer(); // needed?
		// setup new car
		car = new Car(world, spriteCar, level.getCarPos().x, level.getCarPos().y);
		// create a new TowerMenu
		towerMenu = new TowerMenu(world, scoreBoard);
		// unlock/lock the right tower
		for (int i = 0; i < level.getTowersUnlocked().length; i++) {
			if (level.getTowersUnlocked()[i]) {
				towerMenu.unlockTower(i);
			} else {
				towerMenu.unlockTower(i, false);
			}
		}
		finishline = new FinishLine(world, spriteFinishLine,
				level.getFinishLinePosition().x, level.getFinishLinePosition().y);
		map = new Map(level, world, finishline.getBody(), spritePitStop.getHeight());
		map.setSpawnPosition(level.getSpawnPoint());
		trailerpos.set(map.getTargetPosition().x, map.getTargetPosition().y);
		this.spritePitStop.setPosition(level.getPitStopPosition().x * PIXEL_TO_METER,
				level.getPitStopPosition().y * PIXEL_TO_METER);
		for (int j = 0; j < level.getCheckPoints().length; j++)
			checkpoints[j] = new NormalCheckpoint(world,
					level.getCheckPoints()[j].x * PIXEL_TO_METER,
					level.getCheckPoints()[j].y * PIXEL_TO_METER);

		// Update the scoreboard because a new level was loaded
		scoreBoard.resetNewLevelLoaded();
		levelLoaded = true;

		Gdx.app.debug("play_state:loadLevel", MainGame.getCurrentTimeStampLogString() + "Level #" + (levelNumber + 1) + " was loaded in " + (System.currentTimeMillis() - time) + "ms");
	}

	private static Sprite createScaledSprite(AssetManager assetManager, String location) {
		final Texture texture = assetManager.get(location);
		final Sprite s = new Sprite(texture);
		s.setSize(s.getWidth() * PIXEL_TO_METER, s.getHeight() * PIXEL_TO_METER);
		s.setOriginCenter();
		return s;
	}

	private void startBuilding(Tower t) {
		buildingtower = t;
		buildingtower.setBuildingMode(true);
		for (final Tower tower : towers)
			tower.activateRange(true);
	}

	private boolean buildingPositionIsAllowed(final Tower tower) {
		final float[][] cornerPoints = tower.getCornerPoints();
		boolean isAllowed = true;
		for (float[] cornerPoint : cornerPoints) {
			// if tower is placed onto the track do not allow building it
			if (!this.map.isInBody(cornerPoint[0], cornerPoint[1]))
				isAllowed = false;
			// if tower is placed onto the tower menu do not allow building it
			if (this.towerMenu.contains(cornerPoint[0], cornerPoint[1]))
				isAllowed = false;
			// if tower is placed onto a tower do not allow building it
			for (final Tower tower1 : towers) {
				if (tower1.contains(cornerPoint[0], cornerPoint[1]))
					isAllowed = false;
			}
		}
		return isAllowed;
	}

	private boolean buildingMoneyIsEnough(final Tower tower) {
		return tower.getCost() <= scoreBoard.getMoney();
	}

	private void stopBuilding() {
		towerMenu.unselectAll();
		for (final Tower tower : towers)
			tower.activateRange(false);
	}

	public void toggleMusic() {
		if (!assetsLoaded) {
			return;
		}
		if (preferencesManager.getMusicOn() && !pausedByUser) {
			musicBackground.play();
		} else {
			musicBackground.pause();
		}
	}

	public void toggleSoundEffects() {
		if (!assetsLoaded) {
			return;
		}
		if (preferencesManager.getSoundEffectsOn() && !pausedByUser) {
			musicCar.play();
			soundCarStart.resume();
			soundGetMoney.resume();
			Tower.setSoundOn(true);
		} else {
			soundGetMoney.pause();
			musicCar.pause();
			soundCarStart.pause();
			Tower.setSoundOn(false);
		}
		for (final Tower tower : towers)
			tower.updateSound();
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
			toggleMusic();
		}
		// Turn sound effects on/off
		if (controllerToggleSoundEffectsPressed || Gdx.input.isKeyJustPressed(Keys.U)) {
			controllerToggleSoundEffectsPressed = false;
			preferencesManager.setSoundEffectsOn(!preferencesManager.getSoundEffectsOn());
			toggleSoundEffects();
		}

		// If escape or back is pressed go back to the menu state
		if (Gdx.input.isCatchKey(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
				|| controllerBackKeyWasPressed) {
			controllerBackKeyWasPressed = false;
			// Check if currently in building mode and exit this mode before instead of going back to the menu
			if (buildingtower != null) {
				stopBuilding();
			} else {
				// TODO Add dialog that asks if the user wants really to quit?
				gameStateManager.setGameState(new MenuState(gameStateManager));
			}
		}

		// Toggle the manual pause
		if (controllerToggleManualPausePressed || Gdx.input.isKeyJustPressed(Keys.P)) {
			controllerToggleManualPausePressed = false;
			pausedByUser = !pausedByUser;
			toggleMusic();
			toggleSoundEffects();
		}

		if (pausedByUser) {
			// When the game was manually paused by the user skip the rest of this method
			return;
		}

		// Update the cursor position

		// > Use the cursor that was updated last
		Vector3 newMouseCursorPosition = GameStateManager.getMousePosition(camera);
		final boolean mouseCursorWasMoved = !newMouseCursorPosition.cpy().sub(previousMouseCursorPosition).isZero(0.1f);
		if (mouseCursorWasMoved) {
			previousMouseCursorPosition.set(newMouseCursorPosition);
			timeStampMouseCursorPositionWasChanged = new Date().getTime();
		}
		if (timeStampMouseCursorPositionWasChanged > controllerCallbackPlayState.getTimeStampControllerCursorPositionWasChanged()) {
			cursorPosition.set(newMouseCursorPosition);
		} else {
			cursorPosition.set(controllerCallbackPlayState.getControllerCursorPositionPlaceTower(), 0);
		}

		// Check for additional debug inputs when in developer mode
		if (MainGame.DEVELOPER_MODE) {
			debugInputs();
		}

		// Car control
		SteerCarLeftRight controllerSteerCarLeftRight = controllerCallbackPlayState.getSteerCarLeftRight();
		SteerCarForwardsBackwards controllerSteerForwardsBackwards = controllerCallbackPlayState.getSteerCarForwardsBackwards();
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP) || controllerSteerForwardsBackwards == SteerCarForwardsBackwards.FORWARDS) {
			car.accelarate();
		}
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN) || controllerSteerForwardsBackwards == SteerCarForwardsBackwards.BACKWARDS) {
			car.brake();
		}
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT) || controllerSteerCarLeftRight == SteerCarLeftRight.LEFT) {
			car.steerLeft();
		}
		if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT) || controllerSteerCarLeftRight == SteerCarLeftRight.RIGHT) {
			car.steerRight();
		}

		// Select tower to build
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1) || controllerSelectTowerPressed && controllerSelectTowerId == 0) {
			controllerSelectTowerPressed = false;
			towerMenu.selectTower(0, cursorPosition, enemies);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2) || controllerSelectTowerPressed && controllerSelectTowerId == 1) {
			controllerSelectTowerPressed = false;
			towerMenu.selectTower(1, cursorPosition, enemies);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3) || controllerSelectTowerPressed && controllerSelectTowerId == 2) {
			controllerSelectTowerPressed = false;
			towerMenu.selectTower(2, cursorPosition, enemies);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4) || controllerSelectTowerPressed && controllerSelectTowerId == 3) {
			controllerSelectTowerPressed = false;
			towerMenu.selectTower(3, cursorPosition, enemies);
		}

		// Move tower
		/*
		if (((this.padPos.x + padPos.x >= 0) && (this.padPos.x + padPos.x <= MainGame.GAME_WIDTH * PIXEL_TO_METER))
				&& ((this.padPos.y + padPos.y >= 0) && (
				this.padPos.y + padPos.y <= MainGame.GAME_HEIGHT * PIXEL_TO_METER))) {
			this.padPos.mulAdd(padPos, 1);
		}*/

		// Build tower if in building mode and a click or select key was pressed
		if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)
				|| controllerSelectKeyWasPressed) {
			if (buildingtower != null) {
				buildTowerIfAllowed(true);
			}
		}
	}

	private void debugInputs() {
		// toggle developer score board
		if (Gdx.input.isKeyJustPressed(Keys.F8))
			scoreBoard.setDebugDisplay(!scoreBoard.getDebugDisplay());

		// manually instantiate enemies
		if (Gdx.input.isKeyJustPressed(Keys.F)) {
			final Enemy enemy = new EnemySmall(map.getSpawnPosition(), world, map, 0);
			enemy.activateEnemy();
			enemies.add(enemy);
		}
		if (Gdx.input.isKeyJustPressed(Keys.G)) {
			final Enemy enemy = new EnemyFat(map.getSpawnPosition(), world, map, 0);
			enemy.activateEnemy();
			enemies.add(enemy);
		}
		if (Gdx.input.isKeyJustPressed(Keys.H)) {
			final Enemy enemy = new EnemyBicycle(map.getSpawnPosition(), world, map, 0);
			enemy.activateEnemy();
			enemies.add(enemy);
		}
		if (Gdx.input.isKeyJustPressed(Keys.J)) {
			final Enemy enemy = new EnemyLincoln(map.getSpawnPosition(), world, map, 0);
			enemy.activateEnemy();
			enemies.add(enemy);
		}
		if (Gdx.input.isKeyJustPressed(Keys.K)) {
			final Enemy enemy = new EnemySpider(map.getSpawnPosition(), world, map, 0);
			enemy.activateEnemy();
			enemies.add(enemy);
		}

		// debug renderer
		if (Gdx.input.isKeyJustPressed(Keys.Y))
			debugTower = !debugTower;
		if (Gdx.input.isKeyJustPressed(Keys.X))
			debugBox2D = !debugBox2D;
		if (Gdx.input.isKeyJustPressed(Keys.C))
			debugCollision = !debugCollision;
		if (Gdx.input.isKeyJustPressed(Keys.V))
			debugWay = !debugWay;
		if (Gdx.input.isKeyJustPressed(Keys.B))
			debugDistance = !debugDistance;

		// manipulate current level
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5))
			loadLevel(scoreBoard.getLevel() + 1);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_7))
			scoreBoard.addMoney(1000);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_8))
			scoreBoard.debugKillTrailer();
		if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
			for (int i = 0; i < enemies.size; i++) {
				final Enemy enemy = enemies.get(i);
				if (!enemy.isSpawned())
					enemy.activateEnemy();
				if (!enemy.isDead())
					enemy.takeDamage(enemy.getHealth());
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0))
			tutorialState++;

		// other things
		if (Gdx.input.isKeyJustPressed(Keys.T)) {
			unlockAllTowers = !unlockAllTowers;
			if (unlockAllTowers) {
				for (int i = 0; i < 4; i++)
					towerMenu.unlockTower(i);
			} else {
				final boolean[] towersUnlockedForThisLevel = levels[scoreBoard.getLevel() - 1].getTowersUnlocked();
				for (int i = 0; i < towersUnlockedForThisLevel.length; i++)
					towerMenu.unlockTower(i, towersUnlockedForThisLevel[i]);
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.E)) {
			for (final Enemy e : enemies)
				e.activateEnemy();
		}
		if (Gdx.input.isKeyJustPressed(Keys.R))
			scoreBoard.addScore(1000);
		if (Gdx.input.isKeyJustPressed(Keys.COMMA))
			speedFactor = 1;
		if (Gdx.input.isKeyJustPressed(Keys.PERIOD))
			speedFactor += 1;
	}

	private void buildTowerIfAllowed(final boolean userClicked) {
		// if position and money are OK build it
		if (buildingMoneyIsEnough(buildingtower) && buildingPositionIsAllowed(buildingtower)) {
			if (userClicked) {
				// Add tower to the tower list
				towerMenu.unselectAll();
				scoreBoard.addMoney(-this.buildingtower.getCost());
				final Tower newTower = this.buildingtower;
				buildingtower = null;
				newTower.activate();
				newTower.setBuildingMode(false);
				towers.add(newTower);
				stopBuilding();
			} else {
				buildingtower.setBlockBuildingMode(false);
			}
		} else {
			buildingtower.setBlockBuildingMode(true);
		}
	}

	@Override
	protected void update(float deltaTime) {
		if (pausedByUser || !assetsLoaded || !levelLoaded) {
			return;
		}

		// minimize time for wave text - only if it's not pause
		timeToDisplayWaveTextInS -= deltaTime;

		// update objects
		towerMenu.update();
		scoreBoard.update(deltaTime * speedFactor);
		car.update(deltaTime);
		for (final Tower tower : towers)
			tower.update(deltaTime, cursorPosition);

		// check additionally if enemies should be activated
		for (int i = 0; i < enemies.size; i++) {
			final Enemy enemy = enemies.get(i);
			enemy.update(deltaTime);
			if (!enemy.isSpawned() && enemy.getTime() < scoreBoard.getTime())
				enemy.activateEnemy();
		}

		// update building tower
		buildingtower = towerMenu.getCurrentTower();
		if (buildingtower == null) {
			stopBuilding();
		} else {
			startBuilding(buildingtower);
			buildingtower.update(deltaTime, cursorPosition);
			buildTowerIfAllowed(false);
		}

		// garbage collect tower projectiles that should be deleted
		for (final Tower tower : towers) {
			Array<Body> rb = tower.removeProjectiles();
			if (rb != null) {
				for (final Body body : rb) {
					// why compare world?, there is only one
					if (body.getWorld() == world)
						world.destroyBody(body);
				}
			}
		}

		// garbage collect enemies
		for (final Enemy enemy : enemies) {
			// if enemy has a body
			if (!enemy.isBodyDeleted()) {
				// and it's body or itself should be deleted
				if (enemy.isDeleteBody() || enemy.isDelete()) {
					// remove body from the world
					world.destroyBody(enemy.getBody());
					enemy.setBodyDeleted(true);
				}
				// if enemy should be deleted delete the from the list
				if (enemy.isDelete()) {
					enemies.removeValue(enemy, true);
				}
			}
			// If the enemy is dead add him to the other enemy list
			if (enemy.isDead() && !enemy.isDelete()) {
				enemies.removeValue(enemy, true);
				enemiesDead.add(enemy);
			}
		}

		// check if the current wave is dead and a new one should start
		updateWaves();

		updateSmoke();

		// update box2D physics
		updatePhysics(deltaTime);
	}

	private void updateSmoke() {
		if (pausedByUser || scoreBoard.getHealth() == 100)
			return;
		float smokeseconds = 15 / (100f - scoreBoard.getHealth());
		timesincesmoke = timesincesmoke + Gdx.graphics.getDeltaTime();
		while (timesincesmoke > smokeseconds) {
			spawnSmoke();
			timesincesmoke = timesincesmoke - smokeseconds;
		}
		Array<Sprite> deadsmoke = new Array<Sprite>();
		for (Sprite s : trailerSmokes) {
			s.setPosition(s.getX() + MathUtils.random(0.05f), s.getY() + 0.05f);
			if (s.getWidth() > spriteSmoke.getWidth())
				s.setColor(1, 1, 1, s.getColor().a - 0.0000001f);
			s.setSize(s.getWidth() + 0.02f, s.getHeight() + 0.02f);
			s.setRotation(s.getRotation() + MathUtils.random(-2.5f, -0.5f));
			if (s.getColor().a < 0.1f)
				deadsmoke.add(s);

		}
		for (Sprite s : deadsmoke) {
			trailerSmokes.removeValue(s, true);
		}
	}

	private void spawnSmoke() {
		Sprite s = new Sprite(spriteSmoke);
		s.setRotation(MathUtils.random(360));
		s.setSize(s.getWidth() / 8, s.getHeight() / 8);
		s.setPosition((map.getHealthBarPos().x + 50) * PIXEL_TO_METER + MathUtils.random(-1, 1),
				(map.getHealthBarPos().y) * PIXEL_TO_METER - 2f);
		trailerSmokes.add(s);
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
				Gdx.app.debug("play_state:render",
						MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
								+ progress + "%");
				assetsLoaded = true;

				// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
				// viewportHeight/2), with the y-axis pointing up or down.
				camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

				// create sprite(s)
				spriteCar = createScaledSprite(assetManager, ASSET_ID_CAR_TEXTURE);
				spriteFinishLine = createScaledSprite(assetManager, ASSET_ID_FINISH_LINE_TEXTURE);
				spritePitStop = createScaledSprite(assetManager, ASSET_ID_PIT_STOP_TEXTURE);
				spriteSmoke = createScaledSprite(assetManager, ASSET_ID_SMOKE_TEXTURE);

				// set textures (tower buttons)
				TowerMenu.cannonButton = assetManager.get(ASSET_ID_TOWER_CANNON_TEXTURE);
				TowerMenu.laserButton = assetManager.get(ASSET_ID_TOWER_LASER_TEXTURE);
				TowerMenu.sniperButton = assetManager.get(ASSET_ID_TOWER_SNIPER_TEXTURE);
				TowerMenu.flameButton = assetManager.get(ASSET_ID_TOWER_FLAME_TEXTURE);

				// set textures (towers)
				MgTower.groundTower = assetManager.get(ASSET_ID_TOWER_CANNON_BOTTOM_TEXTURE);
				MgTower.upperTower = assetManager.get(ASSET_ID_TOWER_CANNON_UPPER_TEXTURE);
				MgTower.towerFiring = assetManager.get(ASSET_ID_TOWER_CANNON_FIRING_TEXTURE);
				SniperTower.groundTower = assetManager.get(ASSET_ID_TOWER_SNIPER_BOTTOM_TEXTURE);
				SniperTower.upperTower = assetManager.get(ASSET_ID_TOWER_SNIPER_UPPER_TEXTURE);
				SniperTower.towerFiring = assetManager.get(ASSET_ID_TOWER_SNIPER_FIRING_TEXTURE);
				LaserTower.groundTower = assetManager.get(ASSET_ID_TOWER_LASER_BOTTOM_TEXTURE);
				LaserTower.upperTower = assetManager.get(ASSET_ID_TOWER_LASER_UPPER_TEXTURE);
				LaserTower.towerFiring = assetManager.get(ASSET_ID_TOWER_LASER_FIRING_TEXTURE);
				FireTower.groundTower = assetManager.get(ASSET_ID_TOWER_FLAME_BOTTOM_TEXTURE);
				FireTower.upperTower = assetManager.get(ASSET_ID_TOWER_FLAME_UPPER_TEXTURE);
				FireTower.towerFiring = assetManager.get(ASSET_ID_TOWER_FLAME_FIRING_TEXTURE);
				FireTower.tflame = assetManager.get(ASSET_ID_TOWER_FLAME_FIRE_TEXTURE);

				// set textures (enemies)
				EnemySmall.normalTexture = assetManager.get(ASSET_ID_ENEMY_SMALL_NORMAL_TEXTURE);
				EnemySmall.deadTexture = assetManager.get(ASSET_ID_ENEMY_SMALL_DEAD_TEXTURE);
				EnemySmall.damageTexture = assetManager.get(ASSET_ID_ENEMY_BLOOD_TEXTURE);
				EnemyFat.normalTexture = assetManager.get(ASSET_ID_ENEMY_FAT_NORMAL_TEXTURE);
				EnemyFat.deadTexture = assetManager.get(ASSET_ID_ENEMY_FAT_DEAD_TEXTURE);
				EnemyFat.damageTexture = assetManager.get(ASSET_ID_ENEMY_BLOOD_TEXTURE);
				EnemySpider.normalTexture = assetManager.get(ASSET_ID_ENEMY_SPIDER_NORMAL_TEXTURE);
				EnemySpider.deadTexture = assetManager.get(ASSET_ID_ENEMY_SPIDER_DEAD_TEXTURE);
				EnemySpider.damageTexture = assetManager.get(ASSET_ID_ENEMY_BLOOD_GREEN_TEXTURE);
				EnemyBicycle.normalTexture = assetManager.get(ASSET_ID_ENEMY_BICYCLE_NORMAL_TEXTURE);
				EnemyBicycle.deadTexture = assetManager.get(ASSET_ID_ENEMY_BICYCLE_DEAD_TEXTURE);
				EnemyBicycle.damageTexture = assetManager.get(ASSET_ID_ENEMY_BLOOD_TEXTURE);
				EnemyLincoln.normalTexture = assetManager.get(ASSET_ID_ENEMY_LINCOLN_NORMAL_TEXTURE);
				EnemyLincoln.deadTexture = assetManager.get(ASSET_ID_ENEMY_LINCOLN_DEAD_TEXTURE);
				EnemyLincoln.damageTexture = assetManager.get(ASSET_ID_ENEMY_BLOOD_TEXTURE);

				// set audio files (towers)
				MgTower.soundShoot = assetManager.get(ASSET_ID_TOWER_CANNON_SOUND);
				SniperTower.soundShoot = assetManager.get(ASSET_ID_TOWER_SNIPER_SOUND);
				LaserTower.soundShoot = assetManager.get(ASSET_ID_TOWER_LASER_SOUND);
				FireTower.soundShoot = assetManager.get(ASSET_ID_TOWER_FLAME_SOUND);

				// set audio files (other)
				musicBackground = assetManager.get(ASSET_ID_THEME_MUSIC);
				musicCar = assetManager.get(ASSET_ID_CAR_ENGINE_MUSIC);
				splatt = assetManager.get(ASSET_ID_SPLATT_SOUND);
				soundGetMoney = assetManager.get(ASSET_ID_CASH_SOUND);
				soundCarStart = assetManager.get(ASSET_ID_CAR_ENGINE_START_SOUND);
				soundVictory = assetManager.get(ASSET_ID_VICTORY_SOUND);
				soundDamage = assetManager.get(ASSET_ID_TRAILER_DAMAGE_SOUND);

				// activate background music
				musicBackground.setLooping(true);
				musicBackground.setVolume(0.75f);
				musicCar.setLooping(true);
				musicCar.setVolume(1f);

				// load level
				loadLevel(levelNumber);
			} else {
				// set projection matrices
				spriteBatch.setProjectionMatrix(camera.combined);
				shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());

				// draw map
				spriteBatch.begin();
				map.draw(spriteBatch);
				spriteBatch.end();

				// draw transparent tower range shapes
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				shapeRenderer.begin(ShapeType.Filled);
				for (final Tower tower : towers)
					tower.drawRange(shapeRenderer);
				if (buildingtower != null)
					buildingtower.drawRange(shapeRenderer, new Color(1, 0, 0, 0.4f));
				if (MainGame.DEVELOPER_MODE) {
					if (debugTower) {
						for (final Tower tower : towers)
							tower.drawTarget(shapeRenderer);
					}
				}
				shapeRenderer.end();
				Gdx.gl.glDisable(GL20.GL_BLEND);

				// draw enemies
				spriteBatch.begin();
				for (final Enemy e : enemiesDead)
					e.draw(spriteBatch);
				for (final Enemy e : enemies)
					e.draw(spriteBatch);
				spriteBatch.end();

				// draw enemy health bars
				shapeRenderer.begin(ShapeType.Filled);
				for (final Enemy e : enemies)
					e.drawHealthBar(shapeRenderer);
				shapeRenderer.end();

				// draw car and tower menu
				spriteBatch.begin();
				car.draw(spriteBatch);
				towerMenu.draw(spriteBatch);

				// draw normal tower / tower ground
				for (final Tower tower : towers)
					tower.draw(spriteBatch);
				spriteBatch.end();

				// draw tower shooting lines
				shapeRenderer.begin(ShapeType.Filled);
				for (final Tower tower : towers)
					tower.drawLine(shapeRenderer);
				shapeRenderer.end();

				// draw if shooting tower shooting top else normal top
				spriteBatch.begin();
				for (final Tower tower : towers)
					tower.drawUpperBuddy(spriteBatch);

				// draw smoke from damaged trailer
				for (Sprite s : trailerSmokes) {
					s.draw(spriteBatch);
				}

				// draw building tower on top of them all
				if (buildingtower != null) {
					buildingtower.draw(spriteBatch);
					buildingtower.drawUpperBuddy(spriteBatch);
				}

				// draw pitStop and score board above the rest
				spritePitStop.draw(spriteBatch);
				scoreBoard.draw(spriteBatch);

				// render tutorial
				renderTutorial(spriteBatch);

				// draw pause overlay
				if (pausedByUser) {
					spriteBatch.end();
					Gdx.gl.glEnable(GL20.GL_BLEND);
					Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					shapeRenderer.begin(ShapeType.Filled);
					shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.4f));
					shapeRenderer.rect(0, 0, MainGame.GAME_WIDTH * PIXEL_TO_METER,
							MainGame.GAME_HEIGHT * PIXEL_TO_METER);
					drawPlayerHealthBar(shapeRenderer);
					shapeRenderer.end();
					Gdx.gl.glDisable(GL20.GL_BLEND);
					spriteBatch.begin();
				}

				// draw centered pause or custom wave text
				if (pausedByUser || timeToDisplayWaveTextInS > 0) {
					final Vector2 wavePosition = GameStateManager.calculateCenteredTextPosition(fontText,
							pausedByUser ? "PAUSE" : waveText, MainGame.GAME_WIDTH * PIXEL_TO_METER,
							MainGame.GAME_HEIGHT * PIXEL_TO_METER);
					fontText.draw(spriteBatch, pausedByUser ? "PAUSE" : waveText, wavePosition.x, wavePosition.y);
				}
				spriteBatch.end();

				// render health bar
				shapeRenderer.begin(ShapeType.Filled);
				drawPlayerHealthBar(shapeRenderer);
				shapeRenderer.end();

				// render also the following if in developer mode
				if (MainGame.DEVELOPER_MODE) {
					spriteBatch.begin();

					MainGame.font.setColor(1, 1, 1, 1);
					MainGame.font.getData().setScale(PIXEL_TO_METER);
					MainGame.font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 30, 35.5f);

					if (debugCollision)
						renderDebugCollision(spriteBatch);
					if (debugDistance)
						renderDebugEntfernung(spriteBatch);
					if (debugWay)
						renderDebugWay(spriteBatch);

					spriteBatch.end();

					if (debugBox2D)
						debugRender.render(world, camera.combined);

					shapeRenderer.begin(ShapeType.Filled);
					controllerCallbackPlayState.drawDebugInput(shapeRenderer);
					shapeRenderer.end();
				}
			}
		} else {
			// Render loading information
			float progress = assetManager.getProgress() * 100;
			if (progress != assetsLoadedLastProgress) {
				loadingText = TEXT_LOADING + " " + Math.floor(progress);
				loadingTextPosition.set(GameStateManager.calculateCenteredTextPosition(fontLoading, loadingText,
						MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT));
				assetsLoadedLastProgress = progress;
				Gdx.app.debug("play_state:render",
						MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
								+ progress + "%");
			}

			spriteBatch.begin();
			spriteBatch.setProjectionMatrix(camera.combined);

			// draw loading screen
			spriteBatch.draw(backgroundLoading, 0, 0);

			// draw loading text
			fontLoading.draw(spriteBatch, loadingText, loadingTextPosition.x, loadingTextPosition.y);

			spriteBatch.end();
			Gdx.app.debug("play_state:render",
					MainGame.getCurrentTimeStampLogString() + "render loading screen");
		}
	}

	private void drawPlayerHealthBar(final ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(new Color(1, 0, 0, 1));
		shapeRenderer.rect(map.getHealthBarPos().x * PIXEL_TO_METER, map.getHealthBarPos().y * PIXEL_TO_METER,
				200 * PlayState.PIXEL_TO_METER, 6 * PlayState.PIXEL_TO_METER);
		shapeRenderer.setColor(new Color(0, 1, 0, 1));
		shapeRenderer.rect(map.getHealthBarPos().x * PIXEL_TO_METER, map.getHealthBarPos().y * PIXEL_TO_METER,
				200 * PlayState.PIXEL_TO_METER * (scoreBoard.getHealth() / scoreBoard.getMaxHealth()),
				6 * PlayState.PIXEL_TO_METER);
	}

	private void renderDebugWay(SpriteBatch spriteBatch) {
		MainGame.font.getData().setScale(0.06f);
		for (final Enemy e : enemies) {
			if (e.isSpawned() && !e.isDead()) {
				MainGame.font.setColor(e.getColor());
				for (final Node node : e.getWeg())
					MainGame.font.draw(spriteBatch, "x", node.getPosition().x * PlayState.PIXEL_TO_METER,
							node.getPosition().y * PlayState.PIXEL_TO_METER);
			}
		}
	}

	private void renderDebugEntfernung(SpriteBatch spriteBatch) {
		final Node[][] test = this.map.getNodesList();
		MainGame.font.getData().setScale(0.02f);
		for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
			for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
				if (test[i][j].getH() <= 0) // black
					MainGame.font.setColor(0, 0, 0, 0.75f);
				else if (test[i][j].getH() <= 10) // blue
					MainGame.font.setColor(0, 0, 1f, 0.75f);
				else if (test[i][j].getH() <= 20) // teal
					MainGame.font.setColor(0, 1, 0.5f, 0.75f);
				else if (test[i][j].getH() <= 30) // green
					MainGame.font.setColor(0.25f, 1, 0, 0.75f);
				else if (test[i][j].getH() <= 40) // yellow
					MainGame.font.setColor(1, 0.8f, 0, 0.75f);
				else if (test[i][j].getH() <= 50) // orange
					MainGame.font.setColor(1, 0.5f, 0, 0.75f);
				else if (test[i][j].getH() <= 70) // dark red
					MainGame.font.setColor(1, 0.25f, 0.1f, 0.75f);
				else if (test[i][j].getH() <= 100) // pink
					MainGame.font.setColor(1, 0, 0.5f, 0.57f);
				else if (test[i][j].getH() <= 200) // purple
					MainGame.font.setColor(0.6f, 0.1f, 1, 0.75f);
				else
					MainGame.font.setColor(1, 0, 0, 0.75f);

				MainGame.font.draw(spriteBatch, test[i][j].getH() + "", i * PlayState.PIXEL_TO_METER,
						j * PlayState.PIXEL_TO_METER);
			}
		}
	}

	private void renderDebugCollision(SpriteBatch spriteBatch) {
		final Node[][] test = this.map.getNodesList();
		MainGame.font.getData().setScale(0.05f);
		for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
			for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
				if (test[i][j].getNoUse()) {
					MainGame.font.setColor(0, 0, 1, 0.5f);
					MainGame.font.draw(spriteBatch, "O", i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER);
				} else {
					MainGame.font.setColor(1, 0, 0, 0.5f);
					MainGame.font.draw(spriteBatch, "I", i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER);
				}
			}
		}
	}

	/**
	 * Draws the Tutorial
	 *
	 * -1: Tutorial disabled/finished 0: Learn how to drive the car 1: Finish laps
	 * to earn money 2: Keep finishing laps until enough money for towers 3: Select
	 * a tower 4: Build a tower 5: Learn what to protect
	 *
	 * @param spriteBatch
	 */
	private void renderTutorial(SpriteBatch spriteBatch) {
		if (tutorialState == -1)
			return;

		// check appropriate stage
		switch (tutorialState) {
		case 0:
			if (checkPointsCleared > 1)
				tutorialState++;
			break;
		case 1:
			// Checked in lineFinished();
			break;
		case 2:
			if (scoreBoard.getMoney() > 100)
				tutorialState++;
			break;
		case 3:
			if (buildingtower != null)
				tutorialState++;
			break;
		case 4:
			if (towers.size > 0) {
				tutorialState++;
				tutorialtimer = 30f;
			}
			break;
		case 5:
			if (tutorialtimer > 0)
				tutorialtimer = tutorialtimer - Gdx.graphics.getDeltaTime();
			else
				tutorialState++;
			break;
		case 6:
			tutorialState = -1;
			break;
		}

		MainGame.fontOutline.getData().setScale(PlayState.PIXEL_TO_METER + 0.05f);
		MainGame.fontOutline.setColor(1, 1, 1, 1);

		// Then write the text
		switch (tutorialState) {
		case 0:
			MainGame.fontOutline.draw(spriteBatch, "USE -WASD- TO DRIVE YOUR CAR", car.getX() - 6, car.getY() - 1);
			break;
		case 1:
			MainGame.fontOutline.draw(spriteBatch, "FINISH LAPS TO EARN MONEY!", finishline.getX() - 6,
					finishline.getY() + 4.7f);
			break;
		case 2:
			MainGame.fontOutline.draw(spriteBatch, "GO FAST TO EARN BONUS CASH!", finishline.getX() - 6,
					finishline.getY() + 4.7f);
			break;
		case 3:
			MainGame.fontOutline.draw(spriteBatch, "PRESS 1 OR 2 TO SELECT TOWER", towerMenu.getStart().x - 6.5f,
					towerMenu.getStart().y + 7);
			break;
		case 4:
			MainGame.fontOutline.draw(spriteBatch, "LEFT CLICK TO BUILD", cursorPosition.x - 5.5f, cursorPosition.y - 1);
			break;
		case 5:
			MainGame.fontOutline.draw(spriteBatch, "PROTECT YOUR TRAILER FROM THE ZOMBIES!", trailerpos.x - 20.5f,
					trailerpos.y + 0.5f);
			break;
		}

	}

	private void updatePhysics(final float deltaTime) {
		if (pausedByUser)
			return;

		physicsaccumulator += Math.min(deltaTime, 0.25f);
		while (physicsaccumulator >= TIME_STEP) {
			world.step(TIME_STEP * this.speedFactor, 6, 2);
			physicsaccumulator -= TIME_STEP;
		}
	}

	@Override
	protected void dispose() {
		// Remove controller listener
		Controllers.removeListener(controllerCallbackPlayState);

		// dispose loaded objects
		for (final Enemy enemy : enemies)
			enemy.dispose();
		enemies.clear();
		for (final Tower tower : towers)
			tower.dispose();
		towers.clear();
		car.dispose();
		towerMenu.dispose();
		// dispose images, sounds and other resources
		MgTower.groundTower.dispose();
		MgTower.upperTower.dispose();
		MgTower.towerFiring.dispose();
		LaserTower.groundTower.dispose();
		LaserTower.upperTower.dispose();
		LaserTower.towerFiring.dispose();
		EnemySmall.normalTexture.dispose();
		EnemySmall.deadTexture.dispose();
		EnemyFat.normalTexture.dispose();
		EnemyFat.deadTexture.dispose();
		EnemyBicycle.normalTexture.dispose();
		EnemyBicycle.deadTexture.dispose();
		TowerMenu.cannonButton.dispose();
		TowerMenu.laserButton.dispose();
		TowerMenu.flameButton.dispose();
		MgTower.soundShoot.dispose();
		LaserTower.soundShoot.dispose();
		musicBackground.dispose();
		splatt.dispose();
		soundGetMoney.dispose();
		musicCar.dispose();
		soundVictory.dispose();
		shapeRenderer.dispose();

		Gdx.app.debug("play_state:dispose", "Loaded assets before unloading are:");
		for (final String loadedAsset : assetManager.getAssetNames()) {
			Gdx.app.debug("play_state:dispose", "- " + loadedAsset);
		}

		assetManager.unload(ASSET_ID_TEXT_FONT);

		assetManager.unload(ASSET_ID_TEXT_LOADING_FONT);
		assetManager.unload(ASSET_ID_BACKGROUND_LOADING_TEXTURE);

		assetManager.unload(ASSET_ID_CAR_TEXTURE);
		assetManager.unload(ASSET_ID_FINISH_LINE_TEXTURE);
		assetManager.unload(ASSET_ID_PIT_STOP_TEXTURE);
		assetManager.unload(ASSET_ID_SMOKE_TEXTURE);

		assetManager.unload(ASSET_ID_TOWER_CANNON_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_LASER_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_SNIPER_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_FLAME_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_CANNON_BOTTOM_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_CANNON_UPPER_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_CANNON_FIRING_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_SNIPER_BOTTOM_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_SNIPER_UPPER_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_SNIPER_FIRING_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_LASER_BOTTOM_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_LASER_UPPER_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_LASER_FIRING_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_FLAME_BOTTOM_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_FLAME_UPPER_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_FLAME_FIRING_TEXTURE);
		assetManager.unload(ASSET_ID_TOWER_FLAME_FIRE_TEXTURE);

		assetManager.unload(ASSET_ID_ENEMY_SMALL_NORMAL_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_SMALL_DEAD_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_FAT_NORMAL_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_FAT_DEAD_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_SPIDER_NORMAL_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_SPIDER_DEAD_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_BICYCLE_NORMAL_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_BICYCLE_DEAD_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_LINCOLN_NORMAL_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_LINCOLN_DEAD_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_BLOOD_TEXTURE);
		assetManager.unload(ASSET_ID_ENEMY_BLOOD_GREEN_TEXTURE);

		assetManager.unload(ASSET_ID_TOWER_CANNON_SOUND);
		assetManager.unload(ASSET_ID_TOWER_SNIPER_SOUND);
		assetManager.unload(ASSET_ID_TOWER_LASER_SOUND);
		assetManager.unload(ASSET_ID_TOWER_FLAME_SOUND);

		assetManager.unload(ASSET_ID_THEME_MUSIC);
		assetManager.unload(ASSET_ID_CAR_ENGINE_MUSIC);

		assetManager.unload(ASSET_ID_CAR_ENGINE_START_SOUND);
		assetManager.unload(ASSET_ID_SPLATT_SOUND);
		assetManager.unload(ASSET_ID_CASH_SOUND);
		assetManager.unload(ASSET_ID_VICTORY_SOUND);
		assetManager.unload(ASSET_ID_TRAILER_DAMAGE_SOUND);

		Gdx.app.debug("play_state:dispose", "Loaded assets after unloading are:");
		for (final String loadedAsset : assetManager.getAssetNames()) {
			Gdx.app.debug("play_state:dispose", "- " + loadedAsset);
		}
	}

	@Override
	public void collisionCallbackCarEnemy(final Car car, final Enemy enemy) {
		// if the new health after the hit is smaller than 0 play kill sound
		if ((!enemy.isBodyDeleted() && car.hitEnemy(enemy) <= 0) && preferencesManager.getSoundEffectsOn())
			splatt.play(1, MathUtils.random(0.5f, 2f), 0);
	}

	@Override
	public void collisionCallbackCarCheckpoint(final Car car, final Checkpoint checkpoint) {
		// activate checkpoints when the car collides with them
		checkpoint.setActivated(true);
		// count checkpoints
		checkPointsCleared++;
	}

	/**
	 * Returns true when all checkpoints were checked
	 */
	private boolean allCheckPointsChecked() {
		for (final Checkpoint checkpoint : this.checkpoints) {
			if (checkpoint.isActivated() == false)
				return false;
		}
		return true;
	}

	private void lapFinished() {
		// get if all checkpoints are checked
		final boolean allCheckpointsChecked = allCheckPointsChecked();
		// disable all checkpoints
		for (final Checkpoint checkpoint : this.checkpoints)
			checkpoint.setActivated(false);
		// when all checkpoints were checked
		if (allCheckpointsChecked) {
			// add fast bonus and money per lap to the purse
			int lapmoney = (int) levels[scoreBoard.getLevel() - 1].getMoneyPerLap() - towers.size;
			final int fastBonus = (int) (levels[scoreBoard.getLevel() - 1].getTimebonus()
					- scoreBoard.getCurrentTime() * 2);
			scoreBoard.newLap((fastBonus > 0) ? lapmoney + fastBonus : lapmoney);
			// play cash sound if sound activated
			if (preferencesManager.getSoundEffectsOn())
				soundGetMoney.play(1);

			if (tutorialState == 1)
				tutorialState++;
		}
	}

	@Override
	public void collisionCallbackCarFinishLine(final Car car, final FinishLine finishLine) {
		lapFinished();
	}

	@Override
	public void collisionCallbackFlameEnemy(final Enemy enemy, final Flame flame) {
		enemy.takeDamage(flame.getDamage());
	}

	private void updateWaves() {
		// Check if all enemies did spawn and are dead
		if (areAllEnemiesSpawned() && areAllEnemiesDead()) {
			// TODO Rewrite this so that there is only a single level object
			final Array<Wave> currentLevelWaves = levels[scoreBoard.getLevel() - 1].getWaves();
			final int currentWave = scoreBoard.getWaveNumber();
			// and the current wave is the maximum wave
			if (currentWave >= currentLevelWaves.size) {
				// means the level is finished
				loadNextLevel();
			} else {
				// if not the maximum wave load the next wave
				scoreBoard.setWaveNumber(currentWave + 1);
				// display feedback of new wave on screen
				if (currentWave + 1 < currentLevelWaves.size)
					setWaveText("WAVE " + (currentWave + 1));
				else
					setWaveText("FINAL WAVE");
				// create and add all enemies of the current wave to all enemies
				enemies.addAll(currentLevelWaves.get(currentWave).createEnemies(map.getSpawnPosition(), world, map,
						scoreBoard.getTime()));
			}
		}
	}

	/**
	 * @return Returns true if all enemies were spawned (and are not waiting somewhere waiting to be
	 * visible and walk to the trailer)
	 */
	private boolean areAllEnemiesSpawned() {
		for (final Enemy enemy : enemies) {
			if (!enemy.isSpawned()) {
				return false;
			}
		}
		return true;
	}

	private void setWaveText(final String waveText) {
		this.waveText = waveText;
		timeToDisplayWaveTextInS = 2f;
	}

	private void loadNextLevel() {
		Gdx.app.debug("play_state:loadNextLevel", MainGame.getCurrentTimeStampLogString() + "Level " + (scoreBoard.getLevel() + 1) + "was finished");
		if (preferencesManager.getSoundEffectsOn()) {
			soundVictory.play();
		}
		// load the next level
		loadLevel(scoreBoard.getLevel() + 1);
	}

	/**
	 * @return Returns true if all currently spawned enemies are dead
	 */
	private boolean areAllEnemiesDead() {
		for (final Enemy enemy : enemies) {
			// If any enemy is still not dead return false, otherwise return true
			if (!enemy.isDead()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void trailerHealthIs0() {
		// If the user got a top 5 score go to the high score create entry state otherwise go to the game over state
		if (preferencesManager.scoreIsInTop5(scoreBoard.getScore()))
			gameStateManager.setGameState(
					new CreateHighscoreEntryState(gameStateManager, scoreBoard.getScore(), scoreBoard.getLevel(), scoreBoard.getLaps(), false));
		else {
			gameStateManager.setGameState(new GameOverState(gameStateManager, scoreBoard.getLevel()));
		}
	}

	@Override
	public void enemyHitsHomeCallback(final Enemy enemy) {
		// Log the trailer hit in the scoreboard
		scoreBoard.trailerHitByEnemy(enemy);
		// TODO What does that mean?
		// Mark enemy as to be deleted
		enemy.setDelete(true);
		// If sound effects are on play the damage sound
		if (preferencesManager.getSoundEffectsOn()) {
			soundDamage.play(1, MathUtils.random(1f, 1.1f), 0f);
		}
		// Spawn a smoke
		spawnSmoke();
	}

	@Override
	public void controllerCallbackSelectTowerToBuild(int towerId) {
		controllerSelectTowerId = towerId;
		controllerSelectTowerPressed = true;
	}

	@Override
	public void controllerCallbackBuildTower() {
		Gdx.app.debug("play_state:controllerCallbackBuildTower",
				MainGame.getCurrentTimeStampLogString());
		// TODO Update controller integration
		// when in building mode try build the current tower
		if (buildingtower != null) {
			buildTowerIfAllowed(true);
		}
	}

	@Override
	public void controllerCallbackToggleManualPause() {
		Gdx.app.debug("play_state:controllerCallbackToggleManualPause",
				MainGame.getCurrentTimeStampLogString());
		controllerToggleManualPausePressed = true;
	}

	@Override
	public void controllerCallbackClickBackButton() {
		Gdx.app.debug("play_state:controllerCallbackClickBackButton",
				MainGame.getCurrentTimeStampLogString());
		controllerBackKeyWasPressed = true;
	}

	@Override
	public void controllerCallbackToggleFullScreen() {
		Gdx.app.debug("play_state:controllerCallbackToggleFullScreen",
				MainGame.getCurrentTimeStampLogString());
		controllerToggleFullScreenPressed = true;
	}

	@Override
	public void controllerCallbackToggleMusic() {
		Gdx.app.debug("play_state:controllerCallbackToggleMusic",
				MainGame.getCurrentTimeStampLogString());
		controllerToggleMusicPressed = true;
	}

	@Override
	public void controllerCallbackToggleSoundEffects() {
		Gdx.app.debug("play_state:controllerCallbackToggleSoundEffects",
				MainGame.getCurrentTimeStampLogString());
		controllerToggleSoundEffectsPressed = true;
	}

	@Override
	public void enemyDied(final Enemy enemy) {
		Gdx.app.debug("play_state:enemyDied", MainGame.getCurrentTimeStampLogString() + "\"" + enemy.getName() + "\" enemy killed (score + " + enemy.getScore() + ")");
		// Log the kill in the scoreboard
		scoreBoard.killEnemy(enemy);
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		paused = false;
	}
}
