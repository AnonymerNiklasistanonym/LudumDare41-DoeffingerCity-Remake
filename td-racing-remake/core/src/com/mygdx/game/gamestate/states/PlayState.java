package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.level.Level;
import com.mygdx.game.level.LevelHandler;
import com.mygdx.game.level.Wave;
import com.mygdx.game.listener.collisions.CollisionCallbackInterface;
import com.mygdx.game.listener.collisions.CollisionListener;
import com.mygdx.game.listener.controller.ControllerCallbackInterface;
import com.mygdx.game.listener.controller.ControllerHelper;
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

public class PlayState extends GameState implements CollisionCallbackInterface, ControllerCallbackInterface,
		ScoreBoardCallbackInterface, EnemyCallbackInterface {

	private final static String STATE_NAME = "Play";

	// TODO Make that and the physics implementation variable from this value so that the fps can
	// TODO be set to other values like for example 240
	public static int foregroundFps = 60;

	// Identify collision entities
	public final static short PLAYER_BOX = 0x1; // 0001
	public final static short ENEMY_BOX = 0x1 << 1; // 0010 or 0x2 in hex
	public final static float TIME_STEP = (float) 1 / foregroundFps; // time for physics step
	public final static float PIXEL_TO_METER = 0.05f;
	public final static float METER_TO_PIXEL = 20f;

	private final Music musicBackground, soundCar;
	private final Sound splatt, soundGetMoney, soundCarStart, soundVictory, soundDamage;
	private final ControllerHelper controllerHelper;
	private final ScoreBoard scoreBoard;
	private final Array<Enemy> enemies, enemiesDead;
	private final Array<Tower> towers;
	private final Array<Sprite> trailerSmokes;
	private float timesincesmoke;
	private final Sprite spritePitStop, spriteCar, spriteFinishLine, spriteSmoke;
	private final ShapeRenderer shapeRenderer;
	private final Level[] levels;
	private Level level;

	private Tower buildingtower;
	private Checkpoint[] checkpoints;
	private CollisionListener collis;
	private World world;
	private Car car;
	private FinishLine finishline;
	private TowerMenu towerMenu;
	private Map map;
	private Box2DDebugRenderer debugRender;
	private String waveText;
	private Vector3 padPos;
	private Vector2 trailerpos;
	private float tutorialtimer, physicsaccumulator, timeToDisplayWaveTextInS;
	private boolean pausedByUser, lastPause, musicOn, lastMusic, lastSound, soundOn, debugBox2D, debugCollision, debugDistance,
			debugWay, unlockAllTowers, padActivated, debugTower, wasAlreadyPaused;
	private int tutorialState, checkPointsCleared, speedFactor;

	public PlayState(final GameStateManager gameStateManager, final int levelNumber) {
		super(gameStateManager, STATE_NAME);

		Gdx.graphics.setForegroundFPS(foregroundFps);

		// scale used font correctly
		MainGame.font70.getData().setScale(0.10f);
		assetManager.load(MainGame.getGameFontFilePath("cornerstone_70"), BitmapFont.class);

		// set static dependencies
		Enemy.callbackInterface = this;

		// create sprite(s)
		spriteCar = createScaledSprite("car/car_standard.png");
		spriteFinishLine = createScaledSprite("map/map_finish_line.png");
		spritePitStop = createScaledSprite("map/map_pit_stop.png");
		spriteSmoke = createScaledSprite("map/map_smoke.png");
		assetManager.load(MainGame.getGameCarFilePath("standard"), Texture.class);
		assetManager.load(MainGame.getGameMapFilePath("finish_line"), Texture.class);
		assetManager.load(MainGame.getGameMapFilePath("pit_stop"), Texture.class);
		assetManager.load(MainGame.getGameMapFilePath("smoke"), Texture.class);

		// set textures (tower buttons)
		TowerMenu.cannonButton = new Texture(Gdx.files.internal("button/button_tower_cannon.png"));
		TowerMenu.laserButton = new Texture(Gdx.files.internal("button/button_tower_laser.png"));
		TowerMenu.sniperButton = new Texture(Gdx.files.internal("button/button_tower_sniper.png"));
		TowerMenu.flameButton = new Texture(Gdx.files.internal("button/button_tower_flame.png"));
		assetManager.load(MainGame.getGameButtonFilePath("tower_cannon"), Texture.class);
		assetManager.load(MainGame.getGameButtonFilePath("tower_laser"), Texture.class);
		assetManager.load(MainGame.getGameButtonFilePath("tower_sniper"), Texture.class);
		assetManager.load(MainGame.getGameButtonFilePath("tower_flame"), Texture.class);

		// set textures (towers)
		MgTower.groundTower = new Texture(Gdx.files.internal("tower/tower_cannon_bottom.png"));
		MgTower.upperTower = new Texture(Gdx.files.internal("tower/tower_cannon_upper.png"));
		MgTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_cannon_firing.png"));
		SniperTower.groundTower = new Texture(Gdx.files.internal("tower/tower_sniper_bottom.png"));
		SniperTower.upperTower = new Texture(Gdx.files.internal("tower/tower_sniper_upper.png"));
		SniperTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_sniper_firing.png"));
		LaserTower.groundTower = new Texture(Gdx.files.internal("tower/tower_laser_bottom.png"));
		LaserTower.upperTower = new Texture(Gdx.files.internal("tower/tower_laser_upper.png"));
		LaserTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_laser_firing.png"));
		FireTower.groundTower = new Texture(Gdx.files.internal("tower/tower_flame_bottom.png"));
		FireTower.upperTower = new Texture(Gdx.files.internal("tower/tower_flame_upper.png"));
		FireTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_flame_firing.png"));
		FireTower.tflame = new Texture(Gdx.files.internal("tower/tower_flame_fire.png"));
		assetManager.load(MainGame.getGameTowerFilePath("cannon_bottom"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("cannon_upper"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("cannon_firing"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("sniper_bottom"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("sniper_upper"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("sniper_firing"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("laser_bottom"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("laser_upper"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("laser_firing"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("flame_bottom"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("flame_upper"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("flame_firing"), Texture.class);
		assetManager.load(MainGame.getGameTowerFilePath("flame_fire"), Texture.class);

		// set textures (enemies)
		EnemySmall.normalTexture = new Texture(Gdx.files.internal("zombie/zombie_standard.png"));
		EnemySmall.deadTexture = new Texture(Gdx.files.internal("zombie/zombie_standard_dead.png"));
		EnemySmall.damageTexture = new Texture(Gdx.files.internal("zombie/zombie_blood.png"));
		EnemyFat.normalTexture = new Texture(Gdx.files.internal("zombie/zombie_fat.png"));
		EnemyFat.deadTexture = new Texture(Gdx.files.internal("zombie/zombie_fat_dead.png"));
		EnemyFat.damageTexture = new Texture(Gdx.files.internal("zombie/zombie_blood.png"));
		EnemySpider.normalTexture = new Texture(Gdx.files.internal("zombie/zombie_spider.png"));
		EnemySpider.deadTexture = new Texture(Gdx.files.internal("zombie/zombie_spider_dead.png"));
		EnemySpider.damageTexture = new Texture(Gdx.files.internal("zombie/zombie_blood_green.png"));
		EnemyBicycle.normalTexture = new Texture(Gdx.files.internal("zombie/zombie_bicycle.png"));
		EnemyBicycle.deadTexture = new Texture(Gdx.files.internal("zombie/zombie_bicycle_dead.png"));
		EnemyBicycle.damageTexture = new Texture(Gdx.files.internal("zombie/zombie_blood.png"));
		EnemyLincoln.normalTexture = new Texture(Gdx.files.internal("zombie/zombie_lincoln.png"));
		EnemyLincoln.deadTexture = new Texture(Gdx.files.internal("zombie/zombie_lincoln_dead.png"));
		EnemyLincoln.damageTexture = new Texture(Gdx.files.internal("zombie/zombie_blood.png"));
		assetManager.load(MainGame.getGameZombieFilePath("blood"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("blood_green"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("standard"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("standard_dead"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("fat"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("fat_dead"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("bicycle"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("bicycle_dead"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("spider"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("spider_dead"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("lincoln"), Texture.class);
		assetManager.load(MainGame.getGameZombieFilePath("lincoln_dead"), Texture.class);

		// set audio files (towers)
		MgTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sound/sound_tower_cannon.wav"));
		SniperTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sound/sound_tower_sniper.wav"));
		LaserTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sound/sound_tower_laser.mp3"));
		FireTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sound/sound_tower_flame.wav"));
		assetManager.load(MainGame.getGameSoundFilePath("tower_cannon"), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("tower_sniper"), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("tower_laser", true), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("tower_flame"), Sound.class);

		// set audio files (other)
		musicBackground = Gdx.audio.newMusic(Gdx.files.internal("music/music_theme.mp3"));
		soundCar = Gdx.audio.newMusic(Gdx.files.internal("sound/sound_car_engine.mp3"));
		splatt = Gdx.audio.newSound(Gdx.files.internal("sound/sound_splatt.wav"));
		soundGetMoney = Gdx.audio.newSound(Gdx.files.internal("sound/sound_cash.wav"));
		soundCarStart = Gdx.audio.newSound(Gdx.files.internal("sound/sound_car_engine_start.mp3"));
		soundVictory = Gdx.audio.newSound(Gdx.files.internal("sound/sound_victory.wav"));
		soundDamage = Gdx.audio.newSound(Gdx.files.internal("sound/sound_trailer_damage.wav"));
		assetManager.load(MainGame.getGameMusicFilePath("theme"), Music.class);
		assetManager.load(MainGame.getGameSoundFilePath("car_engine", true), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("car_engine_start", true), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("splatt"), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("cash"), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("victory"), Sound.class);
		assetManager.load(MainGame.getGameSoundFilePath("trailer_damage"), Sound.class);
		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		// instantiate global fields
		speedFactor = 1;
		checkPointsCleared = 0;
		tutorialtimer = 0;
		unlockAllTowers = false;
		tutorialState = 0;
		physicsaccumulator = 0;
		timesincesmoke = 0;
		pausedByUser = false;
		lastPause = !pausedByUser;
		wasAlreadyPaused = pausedByUser;
		padActivated = false;
		debugTower = false;
		debugBox2D = false;
		debugCollision = false;
		debugWay = false;
		debugDistance = false;

		// instantiate global objects
		// TODO Create a level info object which is loaded and from this level info object load the levels
		levels = LevelHandler.loadLevels();
		scoreBoard = new ScoreBoard(this);
		controllerHelper = new ControllerHelper(this);
		Controllers.addListener(controllerHelper);
		preferencesManager.checkHighscore();
		// preferencesManager.setupIfFirstStart();
		enemies = new Array<Enemy>();
		towers = new Array<Tower>();
		collis = new CollisionListener(this);
		checkpoints = new Checkpoint[4];
		shapeRenderer = new ShapeRenderer();
		trailerpos = new Vector2(0, 0);
		padPos = new Vector3(MainGame.GAME_WIDTH * PIXEL_TO_METER / 2, MainGame.GAME_HEIGHT * PIXEL_TO_METER, 0);
		trailerSmokes = new Array<Sprite>();
		enemiesDead = new Array<Enemy>();

		// activate background music
		musicBackground.setLooping(true);
		musicBackground.setVolume(0.75f);
		soundCar.setLooping(true);
		soundCar.setVolume(1f);

		// things to do in developer mode and not
		soundOn = preferencesManager.getSoundEfectsOn();
		lastSound = !soundOn;
		musicOn = preferencesManager.getMusicOn();
		lastMusic = !musicOn;

		// load level
		loadLevel(levelNumber);
	}

	private void loadLevel(int levelNumber) {
		long time = System.currentTimeMillis();
		Gdx.app.debug("play_state:loadLevel", MainGame.getCurrentTimeStampLogString() + "Load Level #" + levelNumber);
		// set/save level number
		scoreBoard.setLevel(levelNumber);

		if (levelNumber > 1) {
			tutorialState = -1;
		}

		// If the level number is bigger than the level number the game was won
		if (levelNumber > this.levels.length) {
			gameStateManager.setGameState(new GameWonState(gameStateManager, scoreBoard.getScore()));
			return;
		}
		// TODO Find out why the level number needs to be decremented
		levelNumber -= 1;
		level = levels[levelNumber];

		// TODO Place this at the correct position
		if (preferencesManager.getSoundEfectsOn()) {
			soundCarStart.play();
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

		Gdx.app.debug("play_state:loadLevel", MainGame.getCurrentTimeStampLogString() + "Level #" + (levelNumber + 1) + " was loaded in " + (System.currentTimeMillis() - time) + "ms");
	}

	private static Sprite createScaledSprite(String location) {
		final Sprite s = new Sprite(new Texture(Gdx.files.internal(location)));
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
				GameStateManager.toggleFullScreen();
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
			preferencesManager.setSoundEffectsOn(!preferencesManager.getSoundEfectsOn());
		}

		// If escape or back is pressed go back to the menu state
		if (Gdx.input.isCatchKey(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)
				|| controllerBackKeyWasPressed) {
			controllerBackKeyWasPressed = false;
			gameStateManager.setGameState(new MenuState(gameStateManager));
		}

		// Toggle the manual pause
		if (controllerStartKeyWasPressed || Gdx.input.isKeyJustPressed(Keys.P)) {
			controllerStartKeyWasPressed = false;
			pausedByUser = !pausedByUser;
		}

		if (pausedByUser) {
			// When the game was manually paused by the user skip other inputs
			return;
		}

		// Update the cursor position
		cursorPosition.set(GameStateManager.getMousePosition(camera));

		// control the car
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP))
			car.accelarate();
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
			car.brake();
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))
			car.steerLeft();
		if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT))
			car.steerRight();

		// toggle sound
		if (Gdx.input.isKeyJustPressed(Keys.U))
			soundOn = !soundOn;
		// toggle background music
		if (Gdx.input.isKeyJustPressed(Keys.M))
			musicOn = !musicOn;

		// select tower
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1))
			towerMenu.selectTower(0, cursorPosition, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2))
			towerMenu.selectTower(1, cursorPosition, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3))
			towerMenu.selectTower(2, cursorPosition, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4))
			towerMenu.selectTower(3, cursorPosition, enemies);

		// build tower if in building mode
		if (Gdx.input.justTouched() && buildingtower != null)
			buildTowerIfAllowed(true);

		// turn on developer shortcuts
		if (MainGame.DEVELOPER_MODE)
			debugInputs();
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

		// if the pause settings change
		if (lastPause != pausedByUser) {
			lastPause = pausedByUser;
			// pause or resume all sounds
			if (pausedByUser) {
				if (soundOn) {
					soundGetMoney.pause();
					soundCar.pause();
					soundCarStart.pause();
				}
				if (musicOn)
					musicBackground.pause();
			} else {
				if (soundOn) {
					soundCar.play();
					soundCarStart.resume();
					soundGetMoney.resume();
				}
				if (musicOn)
					musicBackground.play();
			}
		}

		if (pausedByUser)
			return;

		// if the sound settings change
		if (lastSound != soundOn) {
			lastSound = soundOn;
			preferencesManager.setSoundEffectsOn(soundOn);
			// turn tower sound on and of
			Tower.setSoundOn(soundOn);
			for (final Tower tower : towers)
				tower.updateSound();
			// turn background music on/off
			if (soundOn) {
				soundCar.play();
				soundCarStart.resume();
				soundGetMoney.resume();
			} else {
				soundGetMoney.pause();
				soundCar.pause();
				soundCarStart.pause();
			}
		}

		// if the sound settings change
		if (lastMusic != musicOn) {
			lastMusic = musicOn;
			preferencesManager.setMusicOn(musicOn);
			// turn background music on/off
			if (musicOn) {
				musicBackground.play();
			} else {
				musicBackground.pause();
			}
		}

		// minimize time for wave text - only if it's not pause
		timeToDisplayWaveTextInS -= deltaTime;

		// update objects
		controllerHelper.update();
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
			buildingtower.update(deltaTime, padActivated ? padPos : cursorPosition);
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

		if (assetManager.update()) {
			if (!assetsLoaded) {
				float progress = assetManager.getProgress() * 100;
				Gdx.app.debug("play_state:render",
						MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
								+ progress + "%");
				assetsLoaded = true;

				// TODO Load assets via the asset manager

				// TODO Set up everything that needed assets to set up
			}
		} else {
			// display loading information
			float progress = assetManager.getProgress() * 100;
			if (progress != assetsLoadedLastProgress) {
				assetsLoadedLastProgress = progress;
				Gdx.app.debug("play_state:render",
						MainGame.getCurrentTimeStampLogString() + "assets are loading - progress is at "
								+ progress + "%");
			}
		}

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
			shapeRenderer.rect(0, 0, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);
			drawPlayerHealthBar(shapeRenderer);
			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
			spriteBatch.begin();
		}

		// draw centered pause or custom wave text
		if (pausedByUser || timeToDisplayWaveTextInS > 0) {
			final Vector2 wavePosition = GameStateManager.calculateCenteredTextPosition(MainGame.font70,
					pausedByUser ? "PAUSE" : waveText, MainGame.GAME_WIDTH * PIXEL_TO_METER,
					MainGame.GAME_HEIGHT * PIXEL_TO_METER);
			MainGame.font70.draw(spriteBatch, pausedByUser ? "PAUSE" : waveText, wavePosition.x, wavePosition.y);
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
		// remove all listeners
		Controllers.removeListener(controllerHelper);
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
		soundCar.dispose();
		soundVictory.dispose();
		shapeRenderer.dispose();

		Gdx.app.debug("menu_state:dispose", "Loaded assets before unloading are:");
		for (final String loadedAsset : assetManager.getAssetNames()) {
			Gdx.app.debug("menu_state:dispose", "- " + loadedAsset);
		}

		assetManager.unload(MainGame.getGameFontFilePath("cornerstone_70"));
		assetManager.unload(MainGame.getGameCarFilePath("standard"));
		assetManager.unload(MainGame.getGameMapFilePath("finish_line"));
		assetManager.unload(MainGame.getGameMapFilePath("pit_stop"));
		assetManager.unload(MainGame.getGameMapFilePath("smoke"));

		assetManager.unload(MainGame.getGameButtonFilePath("tower_cannon"));
		assetManager.unload(MainGame.getGameButtonFilePath("tower_laser"));
		assetManager.unload(MainGame.getGameButtonFilePath("tower_sniper"));
		assetManager.unload(MainGame.getGameButtonFilePath("tower_flame"));

		assetManager.unload(MainGame.getGameTowerFilePath("cannon_bottom"));
		assetManager.unload(MainGame.getGameTowerFilePath("cannon_upper"));
		assetManager.unload(MainGame.getGameTowerFilePath("cannon_firing"));
		assetManager.unload(MainGame.getGameTowerFilePath("sniper_bottom"));
		assetManager.unload(MainGame.getGameTowerFilePath("sniper_upper"));
		assetManager.unload(MainGame.getGameTowerFilePath("sniper_firing"));
		assetManager.unload(MainGame.getGameTowerFilePath("laser_bottom"));
		assetManager.unload(MainGame.getGameTowerFilePath("laser_upper"));
		assetManager.unload(MainGame.getGameTowerFilePath("laser_firing"));
		assetManager.unload(MainGame.getGameTowerFilePath("flame_bottom"));
		assetManager.unload(MainGame.getGameTowerFilePath("flame_upper"));
		assetManager.unload(MainGame.getGameTowerFilePath("flame_firing"));
		assetManager.unload(MainGame.getGameTowerFilePath("flame_fire"));

		assetManager.unload(MainGame.getGameZombieFilePath("blood"));
		assetManager.unload(MainGame.getGameZombieFilePath("blood_green"));
		assetManager.unload(MainGame.getGameZombieFilePath("standard"));
		assetManager.unload(MainGame.getGameZombieFilePath("standard_dead"));
		assetManager.unload(MainGame.getGameZombieFilePath("fat"));
		assetManager.unload(MainGame.getGameZombieFilePath("fat_dead"));
		assetManager.unload(MainGame.getGameZombieFilePath("bicycle"));
		assetManager.unload(MainGame.getGameZombieFilePath("bicycle_dead"));
		assetManager.unload(MainGame.getGameZombieFilePath("spider"));
		assetManager.unload(MainGame.getGameZombieFilePath("spider_dead"));
		assetManager.unload(MainGame.getGameZombieFilePath("lincoln"));
		assetManager.unload(MainGame.getGameZombieFilePath("lincoln_dead"));

		assetManager.unload(MainGame.getGameSoundFilePath("tower_cannon"));
		assetManager.unload(MainGame.getGameSoundFilePath("tower_sniper"));
		assetManager.unload(MainGame.getGameSoundFilePath("tower_laser", true));
		assetManager.unload(MainGame.getGameSoundFilePath("tower_flame"));

		assetManager.unload(MainGame.getGameMusicFilePath("theme"));
		assetManager.unload(MainGame.getGameSoundFilePath("car_engine", true));
		assetManager.unload(MainGame.getGameSoundFilePath("car_engine_start", true));
		assetManager.unload(MainGame.getGameSoundFilePath("splatt"));
		assetManager.unload(MainGame.getGameSoundFilePath("cash"));
		assetManager.unload(MainGame.getGameSoundFilePath("victory"));
		assetManager.unload(MainGame.getGameSoundFilePath("trailer_damage"));

		Gdx.app.debug("menu_state:dispose", "Loaded assets after unloading are:");
		for (final String loadedAsset : assetManager.getAssetNames()) {
			Gdx.app.debug("menu_state:dispose", "- " + loadedAsset);
		}
	}

	@Override
	public void collisionCallbackCarEnemy(final Car car, final Enemy enemy) {
		// if the new health after the hit is smaller than 0 play kill sound
		if ((!enemy.isBodyDeleted() && car.hitEnemy(enemy) <= 0) && soundOn)
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
			if (soundOn)
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
		if (preferencesManager.getSoundEfectsOn()) {
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
					new CreateHighscoreEntryState(gameStateManager, scoreBoard.getScore(), scoreBoard.getLevel(), false));
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
		if (preferencesManager.getSoundEfectsOn()) {
			soundDamage.play(1, MathUtils.random(1f, 1.1f), 0f);
		}
		// Spawn a smoke
		spawnSmoke();
	}

	@Override
	public void controllerCallbackSteerCar(boolean left) {
		// TODO Update controller integration
		if (left)
			car.steerLeft();
		else
			car.steerRight();
	}

	@Override
	public void controllerCallbackStartBuildingMode(final int towerId) {
		// TODO Update controller integration
		if (towerId == -1) {
			// stop building
			padActivated = false;
			stopBuilding();
		} else {
			padActivated = towerMenu.selectTower(towerId, padPos, enemies);
		}
	}

	@Override
	public void controllerCallbackAccelerateCar(final boolean forwards) {
		// TODO Update controller integration
		if (forwards)
			car.accelarate();
		else
			car.brake();
	}

	@Override
	public void controllerCallbackBackButtonPressed() {
		// TODO Update controller integration
		controllerBackKeyWasPressed = true;
	}

	@Override
	public void controllerCallbackToggleFullScreen() {
		// TODO Update controller integration
		controllerToggleFullScreenPressed = true;
	}

	@Override
	public void controllerCallbackToggleSound() {
		// TODO Update controller integration
		controllerToggleMusicPressed = true;
		controllerToggleSoundEffectsPressed = true;
	}

	@Override
	public void controllerCallbackTogglePause() {
		// TODO Update controller integration
		controllerStartKeyWasPressed = true;
	}

	@Override
	public void controllerCallbackMouseChanged(final Vector3 rightPad) {
		// TODO Update controller integration
		if (!padActivated)
			return;

		if (((padPos.x + rightPad.x >= 0) && (padPos.x + rightPad.x <= MainGame.GAME_WIDTH * PIXEL_TO_METER))
				&& ((padPos.y + rightPad.y >= 0) && (padPos.y + rightPad.y <= MainGame.GAME_HEIGHT * PIXEL_TO_METER))) {
			padPos.mulAdd(rightPad, 1);
		}

		if (buildingtower != null)
			buildingtower.update(0, padPos);
	}

	@Override
	public void controllerCallbackBuildTower() {
		// TODO Update controller integration
		// when in building mode try build the current tower
		if (buildingtower != null)
			buildTowerIfAllowed(true);
	}

	@Override
	public void enemyDied(final Enemy enemy) {
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
