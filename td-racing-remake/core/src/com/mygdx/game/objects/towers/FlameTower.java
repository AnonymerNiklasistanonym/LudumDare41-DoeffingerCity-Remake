package com.mygdx.game.objects.towers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Flame;
import com.mygdx.game.objects.Tower;

public class FlameTower extends Tower {

	// static properties
	public Texture tflame;

	private final Array<Flame> flames;
	private final Sprite sflame;

	private static final String TOWER_NAME = "Flame";
	public static final String ASSET_ID_TEXTURE_BOTTOM = MainGame.getGameTowerFilePath("flame_bottom");
	public static final String ASSET_ID_TEXTURE_UPPER = MainGame.getGameTowerFilePath("flame_upper");
	public static final String ASSET_ID_TEXTURE_FIRING = MainGame.getGameTowerFilePath("flame_firing");
	public static final String ASSET_ID_SOUND_SHOOT = MainGame.getGameSoundFilePath("tower_flame");
	public static final int COST = 300;
	private static final int RANGE = 8;

	public static final String ASSET_ID_TEXTURE_FLAME = MainGame.getGameTowerFilePath("flame_fire");

	private static final Color COLOR_TOWER_RANGE = new Color(1, 0, 0, 0.3f);
	private static final float TIME_FIRING_SPRITE = 0.2f;
	private static final float POWER_SHOOT = 0.1f;
	private static final float SPEED_SHOOT = 0.04f;
	private static final float SPEED_TURN = 700;
	private static final boolean SOUND_LOOP = true;
	private static final float SOUND_VOLUME = 1;

	public FlameTower(final Vector2 position, final Array<Enemy> enemies, final World world,
			final AssetManager assetManager) {
		super(TOWER_NAME, position, assetManager, ASSET_ID_TEXTURE_BOTTOM, ASSET_ID_TEXTURE_UPPER, ASSET_ID_TEXTURE_FIRING, enemies, world, RANGE, ASSET_ID_SOUND_SHOOT);

		color = COLOR_TOWER_RANGE;
		cost = COST;
		firingSpriteTime = TIME_FIRING_SPRITE;
		flames = new Array<Flame>();
		maxHealth = -1;
		permanentsound = SOUND_LOOP;
		power = POWER_SHOOT;
		tflame = assetManager.get(ASSET_ID_TEXTURE_FLAME);
		sflame = new Sprite(tflame);
		sflame.setSize(sflame.getWidth() * PlayState.PIXEL_TO_METER, sflame.getHeight() * PlayState.PIXEL_TO_METER);
		speed = SPEED_SHOOT;
		turnspeed = SPEED_TURN;
		this.soundVolume = SOUND_VOLUME;
	}

	@Override
	public void drawProjectile(final SpriteBatch spriteBatch) {
		for (final Flame flame : flames)
			flame.draw(spriteBatch);
	}

	@Override
	public void updateProjectiles(final float deltaTime) {
		for (final Flame flame : flames)
			flame.update(deltaTime);
	}

	@Override
	public void shoot(final Enemy enemy, float deltaTime) {
		if (isTargetInRange(enemy)) {
			final Vector2 aim = new Vector2(2500, 0);
			aim.rotate(getDegrees());
			aim.rotate90(1);

			this.timesincelastshot = 0;

			final Flame flame = new Flame(body.getPosition(), sflame, world, power);
			flame.getBody().applyForceToCenter(aim, true);
			flames.add(flame);
			if (soundOn)
				soundShoot.play(soundVolume, MathUtils.random(1f, 1.1f), 0f);
		} else
			target = null;
	}

	@Override
	public Array<Body> removeProjectiles() {
		final Array<Body> bodystoremove = new Array<Body>();
		for (final Flame flame : flames) {
			if (flame.isKillme()) {
				flames.removeValue(flame, true);
				bodystoremove.add(flame.getBody());
			}
		}
		return bodystoremove;
	}

	@Override
	public void drawProjectile(final ShapeRenderer shapeRenderer) {
		// No shape to draw
	}

	@Override
	public void dispose() {
		super.disposeMedia();
		sflame.getTexture().dispose();
		for (final Flame flame : flames)
			flame.dispose();
	}

}