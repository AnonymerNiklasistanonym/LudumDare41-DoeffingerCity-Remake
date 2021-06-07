package com.mygdx.game.entities.towers;

import com.badlogic.gdx.assets.AssetManager;
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
import com.mygdx.game.entities.TowerOptions;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.entities.Tower;

public class FlameTower extends Tower {

	// static properties
	public Texture textureFlameFire;

	private final Array<FlameTowerFire> flames;
	private final Sprite spriteFlameFire;

	private static final String TOWER_NAME = "Flame";
	public static final String ASSET_ID_TEXTURE_BOTTOM = MainGame.getGameTowerFilePath("flame_bottom");
	public static final String ASSET_ID_TEXTURE_UPPER = MainGame.getGameTowerFilePath("flame_upper");
	public static final String ASSET_ID_TEXTURE_FIRING = MainGame.getGameTowerFilePath("flame_firing");
	public static final String ASSET_ID_SOUND_SHOOT = MainGame.getGameSoundFilePath("tower_flame");
	public static final int COST = 300;
	private static final int RANGE = 8;

	public static final String ASSET_ID_TEXTURE_FLAME_FIRE = MainGame.getGameTowerFilePath("flame_fire");

	private static final Color COLOR_TOWER_RANGE = new Color(1, 0, 0, 0.3f);
	private static final float TIME_FIRING_SPRITE = 0.2f;
	private static final float POWER_SHOOT = 0.1f;
	private static final float SPEED_SHOOT = 0.04f;
	private static final float SPEED_TURN = 700;
	private static final boolean SOUND_LOOP = true;

	private static final TowerOptions TOWER_OPTIONS = getTowerOptions();

	public FlameTower(final Vector2 position, final Array<Zombie> enemies, final World world,
			final AssetManager assetManager) {
		super(TOWER_NAME, position, COST, RANGE, POWER_SHOOT, SPEED_SHOOT, SPEED_TURN,
				assetManager, ASSET_ID_TEXTURE_BOTTOM, ASSET_ID_TEXTURE_UPPER,
				ASSET_ID_TEXTURE_FIRING, ASSET_ID_SOUND_SHOOT, world, enemies, TOWER_OPTIONS);
		flames = new Array<>();
		textureFlameFire = assetManager.get(ASSET_ID_TEXTURE_FLAME_FIRE);
		spriteFlameFire = new Sprite(textureFlameFire);
		spriteFlameFire.setSize(
				spriteFlameFire.getWidth() * PlayState.PIXEL_TO_METER, spriteFlameFire.getHeight() * PlayState.PIXEL_TO_METER);
	}

	private static TowerOptions getTowerOptions() {
		final TowerOptions towerOptions = new TowerOptions();
		towerOptions.rangeColor = COLOR_TOWER_RANGE;
		towerOptions.firingSpriteTime = TIME_FIRING_SPRITE;
		towerOptions.loopSoundShoot = SOUND_LOOP;
		return towerOptions;
	}

	@Override
	public void drawProjectile(final SpriteBatch spriteBatch) {
		for (final FlameTowerFire flameTowerFire : flames)
			flameTowerFire.draw(spriteBatch);
	}

	@Override
	public void updateProjectiles(final float deltaTime) {
		for (final FlameTowerFire flameTowerFire : flames)
			flameTowerFire.update(deltaTime);
	}

	@Override
	public void shoot(final Zombie zombie, float deltaTime) {
		if (isTargetInRange(zombie)) {
			final Vector2 aim = new Vector2(2500, 0);
			aim.rotate(getDegrees());
			aim.rotate90(1);

			this.timesincelastshot = 0;

			final FlameTowerFire flameTowerFire = new FlameTowerFire(body.getPosition(), spriteFlameFire, world, power);
			flameTowerFire.getBody().applyForceToCenter(aim, true);
			flames.add(flameTowerFire);
			if (soundOn)
				soundShoot.play(soundVolume, MathUtils.random(1f, 1.1f), 0f);
		} else
			target = null;
	}

	@Override
	public Array<Body> removeProjectiles() {
		final Array<Body> bodystoremove = new Array<Body>();
		for (final FlameTowerFire flameTowerFire : flames) {
			if (flameTowerFire.isKillme()) {
				flames.removeValue(flameTowerFire, true);
				bodystoremove.add(flameTowerFire.getBody());
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
		spriteFlameFire.getTexture().dispose();
		for (final FlameTowerFire flameTowerFire : flames)
			flameTowerFire.dispose();
	}

}