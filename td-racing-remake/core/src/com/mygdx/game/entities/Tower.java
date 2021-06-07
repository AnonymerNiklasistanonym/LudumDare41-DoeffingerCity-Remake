package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.states.PlayState;

public abstract class Tower implements Disposable {

	// TODO Update protected and public variables to be private if possible!
	protected static boolean soundOn;

	public Body body;
	private boolean buildingModeBlocked;
	protected Vector2 center;
	protected Color color;
	protected final int cost;
	protected float soundVolume;
	private final Array<Zombie> zombies;
	protected float firingLineTime = 0.1f;
	protected float firingSpriteTime;
	private boolean isactive = false;
	private boolean isInBuildingMode;
	protected final boolean permanentsound;
	protected final float power;
	protected final float range;
	private boolean rangeActivated = false;
	protected Vector2 shotposition;
	protected Sound soundShoot;
	protected final float speed;
	protected Sprite spriteBody;
	protected Sprite spriteFiring;
	protected Sprite spriteUpperBody;
	protected Zombie target = null;
	protected float timesincelastshot;
	protected final float turnspeed;
	protected final World world;
	protected final String name;

	protected Tower(final String name, final Vector2 position, final int cost, final int range,
			float powerShoot, float speedShoot, float speedTurn, final AssetManager assetManager,
			final String assetIdBottom,
			final String assetIdUpper,
			final String assetIdFiring, final String assetIdSoundShoot, final World world,
			final Array<Zombie> zombies, final TowerOptions towerOptions) {
		Gdx.app.debug("tower:constructor", MainGame.getCurrentTimeStampLogString() + "create tower \"" + name + "\"");
		this.name = name;
		soundShoot = assetManager.get(assetIdSoundShoot);
		this.zombies = zombies;
		this.range = range;
		Texture bottom = assetManager.get(assetIdBottom);
		this.spriteBody = new Sprite(bottom);
		Texture upper = assetManager.get(assetIdUpper);
		this.spriteUpperBody = new Sprite(upper);
		Texture firing = assetManager.get(assetIdFiring);
		this.spriteFiring = new Sprite(firing);
		this.spriteBody.setSize(spriteBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteUpperBody.setSize(spriteUpperBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteUpperBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteFiring.setSize(spriteFiring.getWidth() * PlayState.PIXEL_TO_METER,
				spriteFiring.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteBody.setOriginCenter();
		this.spriteUpperBody.setOriginCenter();
		this.spriteFiring.setOriginCenter();
		this.world = world;
		this.cost = cost;
		this.speed = speedShoot;
		this.power = powerShoot;
		this.turnspeed = speedTurn;

		firingSpriteTime = towerOptions.firingSpriteTime;
		color = towerOptions.rangeColor;
		permanentsound = towerOptions.loopSoundShoot;
		soundVolume = towerOptions.volumeSoundShoot;

		timesincelastshot = 10;
		buildingModeBlocked = false;

		// create box2D body and add it to the world
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.KinematicBody;
		bodydef.position.set(position);
		body = world.createBody(bodydef);
		final PolygonShape towerBaseBox = new PolygonShape();
		towerBaseBox.setAsBox(spriteBody.getWidth() * 0.5f * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * 0.5f * PlayState.PIXEL_TO_METER);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = towerBaseBox;
		fdef.isSensor = true;
		body.createFixture(fdef);
		body.setUserData(this);
	}

	public void activate() {
		isactive = true;
	}

	public void activateRange(final boolean rangeActivated) {
		this.rangeActivated = rangeActivated;
	}

	public boolean contains(final float xPos, final float yPos) {
		return (xPos >= spriteBody.getX() && xPos <= spriteBody.getX() + spriteBody.getWidth())
				&& (yPos >= spriteBody.getY() && yPos <= spriteBody.getY() + spriteBody.getHeight());
	}

	public void disposeMedia() {
		// Nothing to dispose since everything is loaded via the asset manager
	}

	public void draw(final SpriteBatch spriteBatch) {
		spriteBody.draw(spriteBatch);
	}

	public void drawLine(final ShapeRenderer shapeRenderer) {
		if (firingLineTime > timesincelastshot)
			drawProjectile(shapeRenderer);
	}

	public abstract void drawProjectile(final SpriteBatch spriteBatch);

	public abstract void drawProjectile(final ShapeRenderer shapeRenderer);

	public void drawRange(final ShapeRenderer shapeRenderer) {
		if (rangeActivated) {
			shapeRenderer.setColor(color);
			shapeRenderer.circle(spriteBody.getX() + spriteBody.getWidth() / 2,
					spriteBody.getY() + spriteBody.getHeight() / 2, range);
		}
	}

	public void drawRange(final ShapeRenderer shapeRenderer, final Color color) {
		if (rangeActivated) {
			shapeRenderer.setColor(color);
			shapeRenderer.circle(spriteBody.getX() + spriteBody.getWidth() / 2,
					spriteBody.getY() + spriteBody.getHeight() / 2, range);
		}
	}

	public void drawUpperBuddy(final SpriteBatch spriteBatch) {
		if (firingSpriteTime > timesincelastshot) {
			drawProjectile(spriteBatch);
			spriteFiring.draw(spriteBatch);
		} else {
			spriteUpperBody.draw(spriteBatch);
		}
	}

	public float getAngleToEnemy(Zombie e) {
		float angle = 0;
		Vector2 epos = new Vector2(center.x, center.y);
		Vector2 tpos = new Vector2(e.getBodyX(), e.getBodyY());

		angle = center.angle(epos);
		angle = (float) ((Math.atan2(epos.x - tpos.x, -(epos.y - tpos.y)) * 180.0d / Math.PI));
		return angle;

	}

	public Vector2 getCenter() {
		return center;
	}

	public float[][] getCornerPoints() {
		float[][] cornerPoints = new float[4][2];
		// left bottom
		cornerPoints[0][0] = spriteBody.getX();
		cornerPoints[0][1] = spriteBody.getY();
		// right top
		cornerPoints[1][0] = spriteBody.getX() + spriteBody.getHeight();
		cornerPoints[1][1] = spriteBody.getY() + spriteBody.getHeight();
		// left top
		cornerPoints[2][0] = spriteBody.getX();
		cornerPoints[2][1] = spriteBody.getY() + spriteBody.getHeight();
		// right bottom
		cornerPoints[3][0] = spriteBody.getX() + spriteBody.getHeight();
		cornerPoints[3][1] = spriteBody.getY();
		return cornerPoints;
	}

	public int getCost() {
		return cost;
	}

	public float getDegrees() {
		return spriteUpperBody.getRotation();
	}

	public float getX() {
		return spriteBody.getX();
	}

	public float getY() {
		return spriteBody.getY();
	}

	protected boolean isTargetInRange(Zombie e) {
		Vector2 epos = new Vector2(e.getBodyX(), e.getBodyY());
		Vector2 tpos = new Vector2(center.x, center.y);
		float dist = epos.dst(tpos);
		boolean inrange = false;
		if (dist < range)
			inrange = true;
		return inrange;
	}

	public Array<Body> removeProjectiles() {
		return null;
	}

	private void selectNewTarget() {

		Zombie best = null;
		for (Zombie e : zombies) {
			if (best == null) {
				if (isTargetInRange(e) && e.isValidTarget()&&e.hasLeftSpawn())
					best = e;
			} else {
				if (e.getScore() < best.getScore() && isTargetInRange(e) && e.isValidTarget()&&e.hasLeftSpawn())
					best = e;
			}
		}
		target = best;
	}

	public void setBlockBuildingMode(final boolean b) {
		buildingModeBlocked = b;

		if (buildingModeBlocked) {
			spriteBody.setColor(1, 0, 0, 0.5f);
			spriteUpperBody.setColor(1, 0, 0, 0.5f);
			spriteFiring.setColor(1, 0, 0, 0.5f);
		} else {
			spriteBody.setColor(1, 1, 1, 1);
			spriteUpperBody.setColor(1, 1, 1, 1);
			spriteFiring.setColor(1, 1, 1, 1);
		}
	}

	public void setBuildingMode(final boolean buildingMode) {
		isInBuildingMode = buildingMode;

		if (isInBuildingMode) {
			spriteBody.setColor(1, 1, 1, 0.5f);
			spriteUpperBody.setColor(1, 1, 1, 0.5f);
			spriteFiring.setColor(1, 1, 1, 0.5f);
		} else {
			spriteBody.setColor(1, 1, 1, 1);
			spriteUpperBody.setColor(1, 1, 1, 1);
			spriteFiring.setColor(1, 1, 1, 1);
		}
	}

	public void setCenter(Vector2 center) {
		this.center = center;
	}

	public void setDegrees(float degrees) {
		spriteUpperBody.setRotation(degrees);
		spriteFiring.setRotation(degrees);
	}

	public void shoot(Zombie e, float deltaTime) {
		if (isTargetInRange(e)) {
			float damage = power;
			if (speed == 0) {
				damage = power * deltaTime;
			}
			e.takeDamage(damage);
			timesincelastshot = 0;
			shotposition = e.getCenter();
			if (soundOn) {
				soundShoot.play(soundVolume, MathUtils.random(1f, 1.1f), 0f);
			}
		} else {
			target = null;
		}
	}

	public void tryshoot(float deltaTime) {

		// if target is dead stop everything
		if (target == null) {
			return;
		}

		if (target.isDead() || !isTargetInRange(target)) {
			target = null;
			return;
		}

		// check if enemy can be locked
		float newDegrees;
		final float maximumDegreeChange = turnspeed * deltaTime;
		final float degreeChangeOne = Math.abs(getDegrees() - getAngleToEnemy(target));
		final float degreeChangeTwo = Math.abs(getAngleToEnemy(target) - getDegrees());

		if (degreeChangeOne <= maximumDegreeChange * 2f || degreeChangeTwo <= maximumDegreeChange * 2f) {
			newDegrees = getAngleToEnemy(target);
			if (timesincelastshot > speed)
				shoot(target, deltaTime);
		} else if (turnClockWise(getDegrees() % 360, getAngleToEnemy(target) % 360)) {
			// System.out.println("Turning clockwise because of: getDegrees() = " +
			// getDegrees() + ", getAngleToEnemy() = "
			// + getAngleToEnemy(target));
			newDegrees = getDegrees() - turnspeed * deltaTime;
		} else {
			// System.out.println("Turning counter clockwise because of: getDegrees() = " +
			// getDegrees()
			// + ", getAngleToEnemy() = " + getAngleToEnemy(target));
			newDegrees = getDegrees() + turnspeed * deltaTime;
		}

		setDegrees(newDegrees % 360);
	}

	public void drawTarget(final ShapeRenderer shapeRenderer) {
		if (target == null)
			return;

		shapeRenderer.setColor(target.getColor());
		shapeRenderer.circle(target.getCenteredPosition().x, target.getCenteredPosition().y, 1f);
		shapeRenderer.line(getCenteredPosition(), target.getCenteredPosition());
	}

	private Vector2 getCenteredPosition() {
		return new Vector2(spriteBody.getX() + spriteBody.getWidth() / 2,
				spriteBody.getY() + spriteBody.getHeight() / 2);
	}

	private boolean turnClockWise(final float currentAngle, final float angleToEnemy) {
		if (currentAngle >= 0 && angleToEnemy < 0)
			return (currentAngle - angleToEnemy) % 360 <= 180;
		else if (currentAngle < 0 && angleToEnemy >= 0)
			return (angleToEnemy - currentAngle) % 360 >= 180;
		else
			return currentAngle > angleToEnemy;
	}

	public void update(final float timeDelta, final Vector3 mousePos) {

		// when in building mode move tower sprite(s) according to mouse/pad position
		if (isInBuildingMode)
			updateSprites(new Vector2(mousePos.x, mousePos.y));

		// when the tower is not yet active do nothing more
		if (!isactive)
			return;

		// make time since last shot bigger on every computer
		timesincelastshot += timeDelta;

		// if there is currently no target
		if (target == null) {
			// find one
			selectNewTarget();
			// and stop playing the shooting sound
			soundShoot.stop();
		} else {
			// else rotate to the target and shoot it
			tryshoot(timeDelta);
		}

		updateProjectiles(timeDelta);

	}

	public void updateProjectiles(float delta) {

	}

	public void updateSprites(final Vector2 position) {

		// set body
		body.setTransform(position, body.getAngle());
		position.add(new Vector2(-spriteBody.getWidth() / 2, -spriteBody.getWidth() / 2));

		// set body to new position
		spriteBody.setPosition(position.x, position.y);
		// set upper body to new position
		spriteUpperBody.setPosition(position.x + spriteBody.getWidth() / 2 - spriteUpperBody.getHeight() / 2,
				position.y + spriteBody.getWidth() / 2 - spriteUpperBody.getHeight() / 2);
		// fire position to new position
		spriteFiring.setPosition(position.x + spriteBody.getWidth() / 2 - spriteFiring.getHeight() / 2,
				position.y + spriteBody.getWidth() / 2 - spriteFiring.getHeight() / 2);

		center = new Vector2(position.x + spriteBody.getWidth() / 2, position.y + spriteBody.getWidth() / 2);
	}

	public static void setSoundOn(boolean soundOn) {
		Tower.soundOn = soundOn;
	}

	public void updateSound() {
		if (!soundOn)
			soundShoot.stop();
	}

}
