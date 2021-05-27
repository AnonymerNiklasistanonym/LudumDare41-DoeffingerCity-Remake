package com.mygdx.game.objects.towers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Tower;

public class MgTower extends Tower {

	// static properties
	public static Sound soundShoot;
	public static Texture groundTower;
	public static Texture towerFiring;
	public static Texture upperTower;

	// static final properties
	public static final int COST = 100;
	public static final int RANGE = 11;

	public MgTower(final Vector2 position, final Array<Enemy> enemies, final World world) {
		super(position, groundTower, upperTower, towerFiring, enemies, world, RANGE, soundShoot);
		color = new Color(0.5f, 0.1f, 0.7f, 0.3f);
		cost = COST;
		firingSpriteTime = 0.1f;
		maxHealth = -1;
		power = 1f;
		speed = 0.4f;
		turnspeed = 30;
	}

	@Override
	public void drawProjectile(final ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.YELLOW);
		shapeRenderer.rectLine(center, shotposition, 0.2f);
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}

	@Override
	public void drawProjectile(final SpriteBatch spriteBatch) {
		// No sprite or else to draw
	}
}