package com.mygdx.game.entities.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.world.Map;

public class ZombieSmall extends Zombie {

	private static final float DAMAGE = 1;
	private static final float HEALTH = 12;
	private static final float MONEY = 1;
	private static final float SPEED = 2;
	private static final float SCORE = 2;
	private static final boolean HEALTH_BAR = false;

	private static final String ENEMY_NAME = "Small";
	public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("small");
	public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("small_dead");
	public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame.getGameZombieFilePath("blood");

	public ZombieSmall(final Vector2 position, final World world, final AssetManager assetManager, final Map map, final float time) {
		super(ENEMY_NAME, position, world, assetManager, ASSET_ID_TEXTURE_ALIVE, ASSET_ID_TEXTURE_DEAD, ASSET_ID_TEXTURE_DAMAGE, map, time);
		damage = DAMAGE;
		health = HEALTH;
		maxHealth = HEALTH;
		money = MONEY;
		speed = SPEED;
		score = SCORE;
		healthBar = HEALTH_BAR;
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}

}