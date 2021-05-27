package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Map;

public class EnemySmall extends Enemy {

	private static final float DAMAGE = 1;
	private static final float HEALTH = 12;
	private static final float MONEY = 1;
	private static final float SPEED = 2;
	private static final float SCORE = 2;
	private static final boolean HEALTH_BAR = false;

	public static Texture damageTexture;
	public static Texture deadTexture;
	public static Texture normalTexture;

	public EnemySmall(final Vector2 position, final World world, final Map map, final float time) {
		super(position, world, normalTexture, deadTexture, damageTexture, map, time);
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
