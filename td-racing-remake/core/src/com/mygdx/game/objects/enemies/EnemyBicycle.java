package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Map;

public class EnemyBicycle extends Enemy {

	private static final float DAMAGE = 2;
	private static final float HEALTH = 15;
	private static final float MONEY = 2;
	private static final float SPEED = 15;
	private static final float SCORE = 20;
	private static final boolean HEALTH_BAR = true;
	private static final float DENSITY = 5f;

	private static final String ENEMY_NAME = "Bicycle";
	public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("bicycle");
	public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("bicycle_dead");
	public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame.getGameZombieFilePath("blood");

	public EnemyBicycle(final Vector2 position, final World world, final AssetManager assetManager, final Map map, final float time) {
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
	protected FixtureDef createFixture() {
		final PolygonShape zBox = new PolygonShape();
		zBox.setAsBox(textureAlive.getWidth() * PlayState.PIXEL_TO_METER * 0.4f,
				textureAlive.getHeight() * PlayState.PIXEL_TO_METER * 0.4f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = zBox;
		fdef.density = DENSITY;
		// fdef.isSensor=true;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;
		return fdef;
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}
	
	@Override
	protected void findWay() {
		weg = map.getRandomMotorPath();
		if (weg.size < 1)
			System.out.println("Ich hab keinen gueltigen Weg bekommen :(");
	}
}
