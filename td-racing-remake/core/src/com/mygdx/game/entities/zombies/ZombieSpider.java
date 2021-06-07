package com.mygdx.game.entities.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.world.Map;

public class ZombieSpider extends Zombie {

	private static final float DAMAGE = 1;
	private static final float HEALTH = 2;
	private static final float MONEY = 10;
	private static final float SPEED = 10;
	private static final float SCORE = 50;
	private static final boolean HEALTH_BAR = false;

	private static final String ENEMY_NAME = "Spider";
	public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("spider");
	public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("spider_dead");
	public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame.getGameZombieFilePath("blood_green");

	public ZombieSpider(final Vector2 position, final World world, final AssetManager assetManager, final Map map, final float time) {
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
