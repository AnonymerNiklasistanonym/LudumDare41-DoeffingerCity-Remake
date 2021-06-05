package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainGame;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Map;

public class EnemyLincoln extends Enemy {

	private static final float DAMAGE = 40;
	private static final float HEALTH = 3000;
	private static final float MONEY = 10000;
	private static final float SPEED = 2f;
	private static final float SCORE = 1000;
	private static final boolean HEALTH_BAR = true;

	private static final String ENEMY_NAME = "Lincoln";
	public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("lincoln");
	public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("lincoln_dead");
	public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame.getGameZombieFilePath("blood");

	public EnemyLincoln(final Vector2 position, final World world, final AssetManager assetManager, final Map map, final float time) {
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
