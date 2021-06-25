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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.play_state.ControllerCallbackPlayState;
import com.mygdx.game.controller.play_state.IControllerCallbackPlayState;
import com.mygdx.game.controller.play_state.SteerCarForwardsBackwards;
import com.mygdx.game.controller.play_state.SteerCarLeftRight;
import com.mygdx.game.file.LevelInfoCsvFile;
import com.mygdx.game.file.LevelWaveCsvFile;
import com.mygdx.game.file.LevelWaveCsvFile.ZombieSpawn;
import com.mygdx.game.file.LevelWaveCsvFile.ZombieType;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.world.CollisionCallbackInterface;
import com.mygdx.game.world.CollisionListener;
import com.mygdx.game.entities.Car;
import com.mygdx.game.world.Checkpoint;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.entities.ZombieCallbackInterface;
import com.mygdx.game.world.FinishLine;
import com.mygdx.game.entities.towers.FlameTowerFire;
import com.mygdx.game.world.Map;
import com.mygdx.game.world.ScoreBoard;
import com.mygdx.game.world.ScoreBoardCallbackInterface;
import com.mygdx.game.entities.Tower;
import com.mygdx.game.world.TowerMenu;
import com.mygdx.game.world.NormalCheckpoint;
import com.mygdx.game.entities.zombies.ZombieBicycle;
import com.mygdx.game.entities.zombies.ZombieFat;
import com.mygdx.game.entities.zombies.ZombieLincoln;
import com.mygdx.game.entities.zombies.ZombieSmall;
import com.mygdx.game.entities.zombies.ZombieSpider;
import com.mygdx.game.entities.towers.FlameTower;
import com.mygdx.game.entities.towers.LaserTower;
import com.mygdx.game.entities.towers.CannonTower;
import com.mygdx.game.entities.towers.SniperTower;
import com.mygdx.game.world.pathfinder.EnemyGridNode;
import java.util.ArrayList;
import java.util.Date;

public class PlayState extends GameState implements CollisionCallbackInterface, IControllerCallbackPlayState,
		ScoreBoardCallbackInterface, ZombieCallbackInterface {

	private static final String STATE_NAME = "Play";
	private static final String TEXT_LOADING = "LOADING";

	private static final String ASSET_ID_TEXT_FONT = MainGame.getGameFontFilePath("cornerstone_70");

	private static final String ASSET_ID_TEXT_LOADING_FONT = MainGame.getGameFontFilePath("cornerstone_upper_case_big");
	private static final String ASSET_ID_BACKGROUND_LOADING_TEXTURE = MainGame.getGameBackgroundFilePath("loading");

	private static final String ASSET_ID_CAR_TEXTURE = MainGame.getGameCarFilePath("standard");
	private static final String ASSET_ID_FINISH_LINE_TEXTURE = MainGame.getGameMapFilePath("finish_line");
	private static final String ASSET_ID_PIT_STOP_TEXTURE = MainGame.getGameMapFilePath("pit_stop");
	private static final String ASSET_ID_SMOKE_TEXTURE = MainGame.getGameMapFilePath("smoke");

	private static final String ASSET_ID_TOWER_CANNON_TEXTURE = MainGame.getGameButtonFilePath("tower_cannon");
	private static final String ASSET_ID_TOWER_LASER_TEXTURE = MainGame.getGameButtonFilePath("tower_laser");
	private static final String ASSET_ID_TOWER_SNIPER_TEXTURE = MainGame.getGameButtonFilePath("tower_sniper");
	private static final String ASSET_ID_TOWER_FLAME_TEXTURE = MainGame.getGameButtonFilePath("tower_flame");

	private static final String ASSET_ID_THEME_MUSIC = MainGame.getGameMusicFilePath("theme");
	private static final String ASSET_ID_CAR_ENGINE_MUSIC = MainGame.getGameSoundFilePath("car_engine", true);
	private static final String ASSET_ID_CAR_ENGINE_START_SOUND = MainGame.getGameSoundFilePath("car_engine_start", true);
	private static final String ASSET_ID_SPLATT_SOUND = MainGame.getGameSoundFilePath("splatt");
	private static final String ASSET_ID_CASH_SOUND = MainGame.getGameSoundFilePath("cash");
	private static final String ASSET_ID_VICTORY_SOUND = MainGame.getGameSoundFilePath("victory");
	private static final String ASSET_ID_TRAILER_DAMAGE_SOUND = MainGame.getGameSoundFilePath("trailer_damage");

	// TODO Make that and the physics implementation variable from this value so that the fps can
	// TODO be set to other values like for example 240
	public static final int foregroundFps = 60;

	// Identify collision entities
	public static final short ZOMBIE_BICYCLE_BOX = 0x1; // 0001
	public static final short CAR_BOX = 0x1 << 1; // 0010 or 0x2 in hex
	public static final float TIME_STEP = (float) 1 / foregroundFps; // time for physics step
	public static final float PIXEL_TO_METER = 0.05f;
	public static final float METER_TO_PIXEL = 20f;

	private Music musicBackground;
	private Music musicCar;
	private Sound splatt;
	private Sound soundGetMoney;
	private Sound soundCarStart;
	private Sound soundVictory;
	private Sound soundDamage;
	private final ScoreBoard scoreBoard;
	private final Array<Zombie> zombies = new Array<>();
	private final Array<Zombie> enemiesDead = new Array<>();
	private final Array<Tower> towers = new Array<>();
	private final Array<Sprite> trailerSmokes = new Array<>();
	private final Array<Checkpoint> checkpoints = new Array<>();
	private final ArrayList<LevelWaveCsvFile> levelWaves = new ArrayList<>();
	private final ArrayList<LevelInfoCsvFile> levelInfo;
	private Sprite spritePitStop;
	private Sprite spriteCar;
	private Sprite spriteFinishLine;
	private Sprite spriteSmoke;
	// private final Level[] levels;
	//private Level level;
	private final Texture backgroundLoading;

	private Tower buildingtower;
	// private final CollisionListener collis;
	private final World world = new World(new Vector2(), true);
	private Car car;
	private FinishLine finishline;
	private TowerMenu towerMenu;
	private Map map;
	private final Box2DDebugRenderer debugRender = new Box2DDebugRenderer();
	private String waveText;
	private final Vector2 trailerpos = new Vector2(0, 0);
	private float tutorialtimer = 0;
	private float timeToDisplayWaveTextInS;
	private boolean unlockAllTowers = false;
	private final Vector2 loadingTextPosition = new Vector2();
	private String loadingText;
	private int speedFactor = 1;
	private int checkPointsCleared = 0;
	/**
	 * TODO Make this an enum or boolean (check how it is implemented)
	 */
	private int tutorialState = 0;
	private boolean loadNextState = false;

	public enum TutorialState {
		SHOW_HOW_TO_DRIVE,
		FINISHED
	};
	private TutorialState newTutorialState = TutorialState.SHOW_HOW_TO_DRIVE;
	private float physicsaccumulator = 0;
	private float timesincesmoke = 0;
	private boolean pausedByUser = false;
	private boolean debugTower = false;
	private boolean debugBox2D = false;
	private boolean debugCollision = false;
	private boolean debugWay = false;
	private boolean debugDistance = false;

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

	/**
	 * The number of the current level
	 */
	private final int levelNumberWithWhichTheStateWasCalled;
	private long timeStampStateStarted = System.currentTimeMillis();

	private boolean levelLoaded = false;
	private boolean firstWave = false;

	float mapEnemyGridNodeAverageH;
	float mapEnemyGridNodeMinimumH;
	float mapEnemyGridNodeMaximumH;

	Vector2 healthBarPos;

	public PlayState(final GameStateManager gameStateManager, final int levelNumberWithWhichTheStateWasCalled) {
		super(gameStateManager, STATE_NAME);
		this.levelNumberWithWhichTheStateWasCalled = levelNumberWithWhichTheStateWasCalled;

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

		// Scale used fonts correctly
		fontText = assetManager.get(ASSET_ID_TEXT_FONT);
		fontText.getData().setScale(fontScaleText);

		// Load resources for the car
		assetManager.load(ASSET_ID_CAR_TEXTURE, Texture.class);

		// Load resources for the map
		assetManager.load(ASSET_ID_FINISH_LINE_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_PIT_STOP_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_SMOKE_TEXTURE, Texture.class);
		// TODO Map resources missing (is there a way to not preload them?)

		// Load resources for the tower menu
		assetManager.load(ASSET_ID_TOWER_CANNON_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_LASER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_SNIPER_TEXTURE, Texture.class);
		assetManager.load(ASSET_ID_TOWER_FLAME_TEXTURE, Texture.class);

		// Load resources for the towers
		assetManager.load(CannonTower.ASSET_ID_TEXTURE_BOTTOM, Texture.class);
		assetManager.load(CannonTower.ASSET_ID_TEXTURE_UPPER, Texture.class);
		assetManager.load(CannonTower.ASSET_ID_TEXTURE_FIRING, Texture.class);
		assetManager.load(LaserTower.ASSET_ID_TEXTURE_BOTTOM, Texture.class);
		assetManager.load(LaserTower.ASSET_ID_TEXTURE_UPPER, Texture.class);
		assetManager.load(LaserTower.ASSET_ID_TEXTURE_FIRING, Texture.class);
		assetManager.load(SniperTower.ASSET_ID_TEXTURE_BOTTOM, Texture.class);
		assetManager.load(SniperTower.ASSET_ID_TEXTURE_UPPER, Texture.class);
		assetManager.load(SniperTower.ASSET_ID_TEXTURE_FIRING, Texture.class);
		assetManager.load(FlameTower.ASSET_ID_TEXTURE_BOTTOM, Texture.class);
		assetManager.load(FlameTower.ASSET_ID_TEXTURE_UPPER, Texture.class);
		assetManager.load(FlameTower.ASSET_ID_TEXTURE_FIRING, Texture.class);
		assetManager.load(FlameTower.ASSET_ID_TEXTURE_FLAME_FIRE, Texture.class);

		// Load resources for the zombies
		assetManager.load(ZombieBicycle.ASSET_ID_TEXTURE_ALIVE, Texture.class);
		assetManager.load(ZombieBicycle.ASSET_ID_TEXTURE_DAMAGE, Texture.class);
		assetManager.load(ZombieBicycle.ASSET_ID_TEXTURE_DEAD, Texture.class);
		assetManager.load(ZombieFat.ASSET_ID_TEXTURE_ALIVE, Texture.class);
		assetManager.load(ZombieFat.ASSET_ID_TEXTURE_DAMAGE, Texture.class);
		assetManager.load(ZombieFat.ASSET_ID_TEXTURE_DEAD, Texture.class);
		assetManager.load(ZombieSmall.ASSET_ID_TEXTURE_ALIVE, Texture.class);
		assetManager.load(ZombieSmall.ASSET_ID_TEXTURE_DAMAGE, Texture.class);
		assetManager.load(ZombieSmall.ASSET_ID_TEXTURE_DEAD, Texture.class);
		assetManager.load(ZombieSpider.ASSET_ID_TEXTURE_ALIVE, Texture.class);
		assetManager.load(ZombieSpider.ASSET_ID_TEXTURE_DAMAGE, Texture.class);
		assetManager.load(ZombieSpider.ASSET_ID_TEXTURE_DEAD, Texture.class);
		assetManager.load(ZombieLincoln.ASSET_ID_TEXTURE_ALIVE, Texture.class);
		assetManager.load(ZombieLincoln.ASSET_ID_TEXTURE_DAMAGE, Texture.class);
		assetManager.load(ZombieLincoln.ASSET_ID_TEXTURE_DEAD, Texture.class);
		assetManager.load(CannonTower.ASSET_ID_SOUND_SHOOT, Sound.class);
		assetManager.load(SniperTower.ASSET_ID_SOUND_SHOOT, Sound.class);
		assetManager.load(LaserTower.ASSET_ID_SOUND_SHOOT, Sound.class);
		assetManager.load(FlameTower.ASSET_ID_SOUND_SHOOT, Sound.class);

		// Load other audio resources
		assetManager.load(ASSET_ID_THEME_MUSIC, Music.class);
		assetManager.load(ASSET_ID_CAR_ENGINE_MUSIC, Music.class);
		assetManager.load(ASSET_ID_CAR_ENGINE_START_SOUND, Sound.class);
		assetManager.load(ASSET_ID_SPLATT_SOUND, Sound.class);
		assetManager.load(ASSET_ID_CASH_SOUND, Sound.class);
		assetManager.load(ASSET_ID_VICTORY_SOUND, Sound.class);
		assetManager.load(ASSET_ID_TRAILER_DAMAGE_SOUND, Sound.class);

		// Create scoreboard and allow callbacks to this class
		scoreBoard = new ScoreBoard(this);
		// Create a new Box2D world and enable callbacks to this class
		world.setContactListener(new CollisionListener(this));

		// Get the level info
		levelInfo = LevelInfoCsvFile.readCsvFile(Gdx.files.internal("level/level_info.csv"));
		// TODO Replace levels with levelInfo so that the new classes will be used
		// levels = LevelHandler.loadLevels();

		// Register controller callback so that controller input can be managed
		controllerCallbackPlayState = new ControllerCallbackPlayState(this);
		Controllers.addListener(controllerCallbackPlayState);
	}

	/**
	 * Load the level
	 *
	 * @param levelNumber The number of the level (the index 0/1 represents level 1/2)
	 */
	private void loadLevel(final int levelNumber) {
		// Indicate that a level is being loaded
		levelLoaded = false;
		// Track how long it takes to load a level
		final long timeStampLevelLoadingWasStarted = System.currentTimeMillis();
		Gdx.app.debug("play_state:loadLevel", MainGame.getCurrentTimeStampLogString() + "Load Level " + (levelNumber + 1));
		// Synchronize level with scoreboard
		scoreBoard.setLevel(levelNumber);

		// Fix tutorial state (for example when someone uses the debug inputs to advance the level
		if (levelNumber > 0 && newTutorialState != TutorialState.FINISHED) {
			newTutorialState = TutorialState.FINISHED;
		}

		// Check if the last level was already beaten
		if (levelNumber >= levelInfo.size()) {
			gameStateManager.setGameState(new GameWonState(gameStateManager, scoreBoard.getScore(), scoreBoard.getLevel(), scoreBoard.getLaps()));
			return;
		}

		// Clear all level related lists
		for (final Zombie zombie : zombies) {
			zombie.removeFromWorld();
		}
		zombies.clear();
		for (final Tower tower : towers) {
			tower.removeFromWorld();
		}
		towers.clear();
		enemiesDead.clear();
		trailerSmokes.clear();
		checkpoints.clear();
		levelWaves.clear();

		// Load level wave info
		final LevelInfoCsvFile currentLevelInfo = levelInfo.get(levelNumber);
		levelWaves.addAll(LevelWaveCsvFile.readCsvFile(Gdx.files.internal("level/level_0" + currentLevelInfo.levelNumber + "_waves.csv")));

		// Setup the car
		if (car != null) {
			car.removeFromWorld();
		}
		car = new Car(world, spriteCar, currentLevelInfo.carStartPosition, currentLevelInfo.carStartAngle);
		// Setup the finish line
		finishline = new FinishLine(world, spriteFinishLine, currentLevelInfo.finishLinePosition, currentLevelInfo.finishLineAngle);

		// Setup the map
		if (map != null) {
			map.removeFromWorld();
		}
		map = new Map(currentLevelInfo, world, finishline.getBody(), spritePitStop.getHeight());
		// Get the health bar position
		healthBarPos = map.getHealthBarPos().cpy().scl(PIXEL_TO_METER);
		// Calculate the average, maximum and minimum H (distance to map goal) value for debugging purposes
		mapEnemyGridNodeAverageH = 0;
		mapEnemyGridNodeMinimumH = Float.POSITIVE_INFINITY;
		mapEnemyGridNodeMaximumH = Float.NEGATIVE_INFINITY;
		float nodeH;
		for (final EnemyGridNode node : map.getNodesList()) {
			nodeH = node.getH();
			mapEnemyGridNodeAverageH += nodeH;
			if (nodeH > mapEnemyGridNodeMaximumH) {
				mapEnemyGridNodeMaximumH = nodeH;
			} else if (nodeH < mapEnemyGridNodeMinimumH) {
				mapEnemyGridNodeMinimumH = nodeH;
			}
		}
		mapEnemyGridNodeAverageH /= map.getNodesList().size;

		// unlock/lock the right tower
		for (int i = 0; i < currentLevelInfo.towerUnlocked.size(); i++) {
			towerMenu.unlockTower(i, currentLevelInfo.towerUnlocked.get(i));
		}

		// Update checkpoints
		for (final Vector2 checkpoint : currentLevelInfo.checkpointPositions) {
			checkpoints.add(new NormalCheckpoint(world,
					checkpoint.x * PIXEL_TO_METER,
					checkpoint.y * PIXEL_TO_METER));
		}

		// Set the positions of already instantiated objects
		// TODO finishline.setPosition(currentLevelInfo.finishLinePosition, currentLevelInfo.finishLineAngle);
		map.setSpawnPosition(currentLevelInfo.enemySpawnPosition);
		// TODO car.setPosition(currentLevelInfo.carStartPosition, currentLevelInfo.carStartAngle);
		trailerpos.set(map.getTargetPosition().x, map.getTargetPosition().y);
		spritePitStop.setPosition(currentLevelInfo.pitStopPosition.x * PIXEL_TO_METER,
				currentLevelInfo.pitStopPosition.y * PIXEL_TO_METER);

		// Update the scoreboard because a new level was loaded
		scoreBoard.resetNewLevelLoaded();
		levelLoaded = true;
		firstWave = true;

		// Play sounds if the user enabled sounds and the game is currently not paused
		if (preferencesManager.getSoundEffectsOn() && !pausedByUser) {
			soundCarStart.play();
		}
		if (preferencesManager.getMusicOn() && !pausedByUser) {
			musicBackground.play();
		}

		Gdx.app.debug("play_state:loadLevel", MainGame.getCurrentTimeStampLogString() + "Level #" + (levelNumber + 1) + " was loaded in " + (System.currentTimeMillis() - timeStampLevelLoadingWasStarted) + "ms");
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
			towerMenu.selectTower(0, cursorPosition, zombies, assetManager);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2) || controllerSelectTowerPressed && controllerSelectTowerId == 1) {
			controllerSelectTowerPressed = false;
			towerMenu.selectTower(1, cursorPosition, zombies, assetManager);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3) || controllerSelectTowerPressed && controllerSelectTowerId == 2) {
			controllerSelectTowerPressed = false;
			towerMenu.selectTower(2, cursorPosition, zombies, assetManager);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4) || controllerSelectTowerPressed && controllerSelectTowerId == 3) {
			controllerSelectTowerPressed = false;
			towerMenu.selectTower(3, cursorPosition, zombies, assetManager);
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
			final Zombie zombie = new ZombieSmall(map.getSpawnPosition(), world, assetManager, map, 0, this, "[debug]");
			zombies.add(zombie);
		}
		if (Gdx.input.isKeyJustPressed(Keys.G)) {
			final Zombie zombie = new ZombieFat(map.getSpawnPosition(), world, assetManager, map, 0, this, "[debug]");
			zombies.add(zombie);
		}
		if (Gdx.input.isKeyJustPressed(Keys.H)) {
			final Zombie zombie = new ZombieBicycle(map.getSpawnPosition(), world, assetManager, map, 0, this, "[debug]");
			zombies.add(zombie);
		}
		if (Gdx.input.isKeyJustPressed(Keys.J)) {
			final Zombie zombie = new ZombieLincoln(map.getSpawnPosition(), world, assetManager, map, 0, this, "[debug]");
			zombies.add(zombie);
		}
		if (Gdx.input.isKeyJustPressed(Keys.K)) {
			final Zombie zombie = new ZombieSpider(map.getSpawnPosition(), world, assetManager, map, 0, this, "[debug]");
			zombies.add(zombie);
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
			for (final Zombie zombie : zombies) {
				zombie.die();
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0))
			tutorialState++;

		// other things
		if (Gdx.input.isKeyJustPressed(Keys.T)) {
			unlockAllTowers = !unlockAllTowers;
			if (unlockAllTowers) {
				for (int i = 0; i < levelInfo.get(0).towerUnlocked.size(); i++)
					towerMenu.unlockTower(i);
			} else {
				for (int i = 0; i < levelInfo.get(0).towerUnlocked.size(); i++)
					towerMenu.unlockTower(i, levelInfo.get(0).towerUnlocked.get(i));
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.E)) {
			for (final Zombie e : zombies)
				e.spawn();
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
		if (paused || pausedByUser || !assetsLoaded || !levelLoaded) {
			return;
		}

		// Handle loading the next state (so that all race conditions can be prevented)
		if (loadNextState) {
			// Check if highscore values are properly set in the preferences
			preferencesManager.checkHighscore();
			// If the user got a top 5 score go to the high score create entry state otherwise go to the game over state
			if (preferencesManager.scoreIsInTop5(scoreBoard.getScore()))
				gameStateManager.setGameState(
						new CreateHighscoreEntryState(gameStateManager, scoreBoard.getScore(),
								scoreBoard.getLevel(), scoreBoard.getLaps(), false));
			else {
				gameStateManager.setGameState(new GameOverState(gameStateManager, scoreBoard.getLevel()));
			}
			return;
		}

		// minimize time for wave text - only if it's not pause
		timeToDisplayWaveTextInS -= deltaTime;

		// update objects
		towerMenu.update();
		scoreBoard.update(deltaTime * speedFactor);
		car.update(deltaTime);
		for (final Tower tower : towers) {
			tower.update(deltaTime, cursorPosition);
		}

		// Update all zombies
		for (int i=0; i < zombies.size; i++) {
			zombies.get(i).update(deltaTime, scoreBoard.getTime());
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

		// TODO Move the whole loop into the zombie update method
		// garbage collect enemies
		for (final Zombie zombie : zombies) {
			// if enemy has a body
			if (!zombie.isBodyDeleted()) {
				// and it's body or itself should be deleted
				if (zombie.isDeleteBody() || zombie.isDelete()) {
					// remove body from the world
					world.destroyBody(zombie.getBody());
					zombie.setBodyDeleted(true);
				}
				// if enemy should be deleted delete the from the list
				if (zombie.isDelete()) {
					zombies.removeValue(zombie, true);
				}
			}
			// If the enemy is dead add him to the other enemy list
			if (zombie.isDead() && !zombie.isDelete()) {
				zombies.removeValue(zombie, true);
				enemiesDead.add(zombie);
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
				s.setColor(1, 1, 1, s.getColor().a - 0.01f);
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
	public void render(final SpriteBatch spriteBatch, final ShapeRenderer shapeRenderer) {
		if (paused) {
			// When the game is paused don't render anything
			return;
		}
		if (assetManager.update()) {
			if (!assetsLoaded) {
				assetsLoaded = true;
				Gdx.app.debug("play_state:render",
						MainGame.getCurrentTimeStampLogString() + "assets are loaded:");
				getDebugOutputLoadedAssets();

				// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
				// viewportHeight/2), with the y-axis pointing up or down.
				camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

				// create sprite(s)
				spriteCar = createScaledSprite(assetManager, ASSET_ID_CAR_TEXTURE);
				spriteFinishLine = createScaledSprite(assetManager, ASSET_ID_FINISH_LINE_TEXTURE);
				spritePitStop = createScaledSprite(assetManager, ASSET_ID_PIT_STOP_TEXTURE);
				spriteSmoke = createScaledSprite(assetManager, ASSET_ID_SMOKE_TEXTURE);

				// TODO Remove static textures and implement it like in the other classes
				// set textures (tower buttons)
				TowerMenu.cannonButton = assetManager.get(ASSET_ID_TOWER_CANNON_TEXTURE);
				TowerMenu.laserButton = assetManager.get(ASSET_ID_TOWER_LASER_TEXTURE);
				TowerMenu.sniperButton = assetManager.get(ASSET_ID_TOWER_SNIPER_TEXTURE);
				TowerMenu.flameButton = assetManager.get(ASSET_ID_TOWER_FLAME_TEXTURE);

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

				// Setup the tower menu
				towerMenu = new TowerMenu(world, scoreBoard);

				// Output how long it took to load all resources
				Gdx.app.debug("play_state:render", MainGame.getCurrentTimeStampLogString() +
						"It took " + (System.currentTimeMillis() - timeStampStateStarted) +
						"ms to load all resources");

				// load level
				loadLevel(levelNumberWithWhichTheStateWasCalled);
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
				for (final Zombie e : enemiesDead)
					e.draw(spriteBatch);
				for (final Zombie e : zombies)
					e.draw(spriteBatch);
				spriteBatch.end();

				// draw enemy health bars
				shapeRenderer.begin(ShapeType.Filled);
				for (final Zombie e : zombies)
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
						renderDebugEnemyGridNodeDistanceToMapGoal(spriteBatch);
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
				loadingText = TEXT_LOADING + " " + (int) Math.floor(progress) + "%";
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
		}
	}

	private void drawPlayerHealthBar(final ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(1, 0, 0, 1);
		shapeRenderer.rect(healthBarPos.x, healthBarPos.y, 200 * PlayState.PIXEL_TO_METER,
				6 * PlayState.PIXEL_TO_METER);
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.rect(healthBarPos.x, healthBarPos.y,
				200 * PlayState.PIXEL_TO_METER * (scoreBoard.getHealth() / scoreBoard.getMaxHealth()),
				6 * PlayState.PIXEL_TO_METER);
	}

	private void renderDebugWay(SpriteBatch spriteBatch) {
		MainGame.font.getData().setScale(0.06f);
		Vector2 currentNodePos;
		for (final Zombie zombie : zombies) {
			if (zombie.isSpawned() && !zombie.isDead()) {
				MainGame.font.setColor(zombie.getColor());
				for (final EnemyGridNode node : zombie.getPath()) {
					currentNodePos = node.getPosition().cpy().scl(PlayState.PIXEL_TO_METER);
					MainGame.font.draw(spriteBatch, "x", currentNodePos.x, currentNodePos.y);
				}
			}
		}
	}

	private void renderDebugEnemyGridNodeDistanceToMapGoal(SpriteBatch spriteBatch) {
		// Draw all enemy grid nodes
		float nodeH;
		Vector2 currentPosition;
		// Reduce font size for readability
		MainGame.font.getData().setScale(0.02f);
		for (final EnemyGridNode node : map.getNodesList()) {
			// Set color depending on the distance to the map goal
			nodeH = node.getH();
			if (nodeH <= mapEnemyGridNodeMinimumH) // black
				MainGame.font.setColor(0, 0, 0, 0.75f);
			else if (nodeH <= mapEnemyGridNodeAverageH * 1/4) // blue
				MainGame.font.setColor(0, 0, 1f, 0.75f);
			else if (nodeH <= mapEnemyGridNodeAverageH * 2/4) // teal
				MainGame.font.setColor(0, 1, 0.5f, 0.75f);
			else if (nodeH <= mapEnemyGridNodeAverageH * 3/4) // green
				MainGame.font.setColor(0.25f, 1, 0, 0.75f);
			else if (nodeH <= mapEnemyGridNodeAverageH) // yellow
				MainGame.font.setColor(1, 0.8f, 0, 0.75f);
			else if (nodeH <= mapEnemyGridNodeAverageH * 5/4) // orange
				MainGame.font.setColor(1, 0.5f, 0, 0.75f);
			else if (nodeH <= mapEnemyGridNodeAverageH * 6/4) // dark red
				MainGame.font.setColor(1, 0.25f, 0.1f, 0.75f);
			else if (nodeH <= mapEnemyGridNodeAverageH * 7/4) // pink
				MainGame.font.setColor(1, 0, 0.5f, 0.57f);
			else if (nodeH <= mapEnemyGridNodeAverageH * 2) // purple
				MainGame.font.setColor(0.6f, 0.1f, 1, 0.75f);
			else if (nodeH <= mapEnemyGridNodeMaximumH) {
				MainGame.font.setColor(1, 0, 0, 0.75f);
			}
			currentPosition = node.getPosition().cpy().scl(PlayState.PIXEL_TO_METER);
			MainGame.font.draw(spriteBatch, "o", currentPosition.x, currentPosition.y);

		}
		// Draw map start and goal position
		MainGame.font.getData().setScale(0.2f);
		MainGame.font.setColor(0, 0, 0, 1);
		final Vector2 startPosition = map.getEnemyGridStartNode().getPosition().cpy().scl(PlayState.PIXEL_TO_METER);
		MainGame.font.draw(spriteBatch, "START", startPosition.x, startPosition.y);
		final Vector2 goalPosition = map.getEnemyGridGoalNode().getPosition().cpy().scl(PlayState.PIXEL_TO_METER);
		MainGame.font.draw(spriteBatch, "GOAL", goalPosition.x, goalPosition.y);
	}

	private void renderDebugCollision(SpriteBatch spriteBatch) {
		// What does this mean?
		MainGame.font.getData().setScale(0.05f);
		Vector2 currentPosition;
		for (final EnemyGridNode node : map.getNodesList()) {
			MainGame.font.setColor(1, 0, 0, 0.5f);
			currentPosition = node.getPosition();
			MainGame.font.draw(spriteBatch, "o", currentPosition.x * PlayState.PIXEL_TO_METER, currentPosition.y * PlayState.PIXEL_TO_METER);
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

		// TODO What is this code section doing
		physicsaccumulator += Math.min(deltaTime, 0.25f);
		while (physicsaccumulator >= TIME_STEP) {
			world.step(TIME_STEP * speedFactor, 6 * speedFactor, 2 * speedFactor);
			physicsaccumulator -= TIME_STEP;
		}
	}

	@Override
	protected void dispose() {
		// Remove controller listener
		Controllers.removeListener(controllerCallbackPlayState);

		// dispose loaded objects
		for (final Zombie zombie : zombies) {
			zombie.dispose();
		}
		for (final Tower tower : towers) {
			tower.dispose();
		}
		car.dispose();
		towerMenu.dispose();
		map.dispose();
		// dispose images, sounds and other resources
		unloadAssetManagerResources(new String[]{
				ASSET_ID_TEXT_FONT,
				ASSET_ID_TEXT_LOADING_FONT,
				ASSET_ID_BACKGROUND_LOADING_TEXTURE,
				ASSET_ID_CAR_TEXTURE,
				ASSET_ID_FINISH_LINE_TEXTURE,
				ASSET_ID_PIT_STOP_TEXTURE,
				ASSET_ID_SMOKE_TEXTURE,
				ASSET_ID_TOWER_CANNON_TEXTURE,
				ASSET_ID_TOWER_LASER_TEXTURE,
				ASSET_ID_TOWER_SNIPER_TEXTURE,
				ASSET_ID_TOWER_FLAME_TEXTURE,
				CannonTower.ASSET_ID_TEXTURE_BOTTOM,
				CannonTower.ASSET_ID_TEXTURE_UPPER,
				CannonTower.ASSET_ID_TEXTURE_FIRING,
				LaserTower.ASSET_ID_TEXTURE_BOTTOM,
				LaserTower.ASSET_ID_TEXTURE_UPPER,
				LaserTower.ASSET_ID_TEXTURE_FIRING,
				SniperTower.ASSET_ID_TEXTURE_BOTTOM,
				SniperTower.ASSET_ID_TEXTURE_UPPER,
				SniperTower.ASSET_ID_TEXTURE_FIRING,
				FlameTower.ASSET_ID_TEXTURE_BOTTOM,
				FlameTower.ASSET_ID_TEXTURE_UPPER,
				FlameTower.ASSET_ID_TEXTURE_FIRING,
				FlameTower.ASSET_ID_TEXTURE_FLAME_FIRE,
				CannonTower.ASSET_ID_SOUND_SHOOT,
				SniperTower.ASSET_ID_SOUND_SHOOT,
				LaserTower.ASSET_ID_SOUND_SHOOT,
				FlameTower.ASSET_ID_SOUND_SHOOT,
				ZombieBicycle.ASSET_ID_TEXTURE_ALIVE,
				ZombieBicycle.ASSET_ID_TEXTURE_DAMAGE,
				ZombieBicycle.ASSET_ID_TEXTURE_DEAD,
				ZombieFat.ASSET_ID_TEXTURE_ALIVE,
				ZombieFat.ASSET_ID_TEXTURE_DAMAGE,
				ZombieFat.ASSET_ID_TEXTURE_DEAD,
				ZombieSmall.ASSET_ID_TEXTURE_ALIVE,
				ZombieSmall.ASSET_ID_TEXTURE_DAMAGE,
				ZombieSmall.ASSET_ID_TEXTURE_DEAD,
				ZombieSpider.ASSET_ID_TEXTURE_ALIVE,
				ZombieSpider.ASSET_ID_TEXTURE_DAMAGE,
				ZombieSpider.ASSET_ID_TEXTURE_DEAD,
				ZombieLincoln.ASSET_ID_TEXTURE_ALIVE,
				ZombieLincoln.ASSET_ID_TEXTURE_DAMAGE,
				ZombieLincoln.ASSET_ID_TEXTURE_DEAD,
				ASSET_ID_THEME_MUSIC,
				ASSET_ID_CAR_ENGINE_MUSIC,
				ASSET_ID_CAR_ENGINE_START_SOUND,
				ASSET_ID_SPLATT_SOUND,
				ASSET_ID_CASH_SOUND,
				ASSET_ID_VICTORY_SOUND,
				ASSET_ID_TRAILER_DAMAGE_SOUND,
		});
	}

	@Override
	public void collisionCallbackCarEnemy(final Car car, final Zombie zombie) {
		// if the new health after the hit is smaller than 0 play kill sound
		if ((!zombie.isBodyDeleted() && car.hitEnemy(zombie) <= 0) && preferencesManager.getSoundEffectsOn())
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
			int lapmoney = levelInfo.get(levelNumberWithWhichTheStateWasCalled).moneyPerLap;
			final int fastBonus = (int) (levelInfo.get(levelNumberWithWhichTheStateWasCalled).timeBonus -
					scoreBoard.getCurrentTime() * 2);
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
	public void collisionCallbackFlameEnemy(final Zombie zombie, final FlameTowerFire flameTowerFire) {
		zombie.takeDamage(flameTowerFire.getDamage());
	}

	private void updateWaves() {
		// Check if all enemies did spawn and are dead
		if (areAllEnemiesSpawned() && areAllEnemiesDead()) {
			// TODO Rewrite this so that there is only a single level object
			//final Array<Wave> currentLevelWaves = levels[scoreBoard.getLevel() - 1].getWaves();
			final int currentWave = scoreBoard.getWaveNumber();
			// If the current wave was the last wave of the level load the next level
			// TODO Somehow the wave is not increment when reach wave 5y
			if (currentWave >= levelWaves.size()) {
				Gdx.app.debug("play_state:updateWaves", MainGame.getCurrentTimeStampLogString() + "current wave " + currentWave + " >= level waves " + levelWaves.size() + " -> load next level");
				loadNextLevel();
			} else {
				// If not the first wave increase the wave number
				if (firstWave) {
					firstWave = false;
				} else {
					scoreBoard.setWaveNumber(currentWave + 1);
				}
				final int newWave = scoreBoard.getWaveNumber();
				Gdx.app.debug("play_state:updateWaves", MainGame.getCurrentTimeStampLogString() + "-> the new wave number is " + newWave + " [old one is " + currentWave + "]");

				// Update the wave text
				if (newWave + 1 < levelWaves.size()) {
					setWaveText("WAVE " + (newWave + 1));
				} else {
					setWaveText("FINAL WAVE");
				}
				// create and add all enemies of the current wave to all enemies
				for (final Zombie zombie : zombies) {
					zombie.removeFromWorld();
				}
				zombies.clear();
				zombies.addAll(createEnemiesForCurrentWave(scoreBoard.getWaveNumber(), map.getSpawnPosition(), scoreBoard.getTime()));
			}
		}
	}

	public Array<Zombie> createEnemiesForCurrentWave(final int waveIndex, final Vector2 entryPosition, final float currentTime) {
		final Array<Zombie> allEnemies = new Array<>();

		// Sanity check
		if (waveIndex >= levelWaves.size()) {
			Gdx.app.error("play_state:createEnemiesForCurrentWave", MainGame.getCurrentTimeStampLogString() + "give wave index " + waveIndex + " is bigger than the given level count " + levelWaves.size());
			return allEnemies;
		}

		final String extra = "[level=" + (scoreBoard.getLevel() + 1) + ",wave=" + (waveIndex + 1) + "]";

		int counter;
		for (final ZombieSpawn zombieSpawn : levelWaves.get(waveIndex).zombieSpawns.get(ZombieType.SPIDER)) {
			counter = 0;
			for (int i = 0; i < zombieSpawn.count; i++) {
				allEnemies.add(new ZombieSpider(entryPosition, world, assetManager, map,
						currentTime + zombieSpawn.timeAfterWaveStarted + (counter++ * zombieSpawn.timeDelta), this, extra));
			}
		}
		for (final ZombieSpawn zombieSpawn : levelWaves.get(waveIndex).zombieSpawns.get(ZombieType.BICYCLE)) {
			counter = 0;
			for (int i = 0; i < zombieSpawn.count; i++) {
				allEnemies.add(new ZombieBicycle(entryPosition, world, assetManager, map,
						currentTime + zombieSpawn.timeAfterWaveStarted + (counter++ * zombieSpawn.timeDelta), this, extra));
			}
		}
		for (final ZombieSpawn zombieSpawn : levelWaves.get(waveIndex).zombieSpawns.get(ZombieType.FAT)) {
			counter = 0;
			for (int i = 0; i < zombieSpawn.count; i++) {
				allEnemies.add(new ZombieFat(entryPosition, world, assetManager, map,
						currentTime + zombieSpawn.timeAfterWaveStarted + (counter++ * zombieSpawn.timeDelta), this, extra));
			}
		}
		for (final ZombieSpawn zombieSpawn : levelWaves.get(waveIndex).zombieSpawns.get(ZombieType.SMALL)) {
			counter = 0;
			for (int i = 0; i < zombieSpawn.count; i++) {
				allEnemies.add(new ZombieSmall(entryPosition, world, assetManager, map,
						currentTime + zombieSpawn.timeAfterWaveStarted + (counter++ * zombieSpawn.timeDelta), this, extra));
			}
		}
		for (final ZombieSpawn zombieSpawn : levelWaves.get(waveIndex).zombieSpawns.get(ZombieType.LINCOLN)) {
			counter = 0;
			for (int i = 0; i < zombieSpawn.count; i++) {
				allEnemies.add(new ZombieLincoln(entryPosition, world, assetManager, map,
						currentTime + zombieSpawn.timeAfterWaveStarted + (counter++ * zombieSpawn.timeDelta), this, extra));
			}
		}

		return allEnemies;
	}

	/**
	 * @return Returns true if all enemies were spawned (and are not waiting somewhere waiting to be
	 * visible and walk to the trailer)
	 */
	private boolean areAllEnemiesSpawned() {
		for (final Zombie zombie : zombies) {
			if (!zombie.isSpawned()) {
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
		for (final Zombie zombie : zombies) {
			// If any enemy is still not dead return false, otherwise return true
			if (!zombie.isDead()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void trailerHealthIs0() {
			loadNextState = true;
	}

	@Override
	public void enemyHitsHomeCallback(final Zombie zombie) {
		// Log the trailer hit in the scoreboard
		scoreBoard.trailerHitByEnemy(zombie);
		// TODO What does that mean?
		// Mark enemy as to be deleted
		zombie.setDelete(true);
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
	public void enemyDied(final Zombie zombie) {
		Gdx.app.debug("play_state:enemyDied", MainGame.getCurrentTimeStampLogString() + "\"" + zombie.getName() + "\" enemy killed (score + " + zombie
				.getScore() + ")");
		// Log the kill in the scoreboard
		scoreBoard.killEnemy(zombie);
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
