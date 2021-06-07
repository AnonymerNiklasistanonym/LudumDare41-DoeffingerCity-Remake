package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.world.Map;
import com.mygdx.game.world.Node;

public abstract class Zombie implements Disposable {

	/**
	 * TODO Do anything to not be public static
	 */
	public static ZombieCallbackInterface callbackInterface;


	private final float density;

	protected final Sprite sprite;
	private final Sprite spriteDamage;
	private final Texture textureDead;
	private final float spawnTimeStamp;

	private float health;
	private final float maxHealth;
	private final float money;
	private final float score;
	private final float speed;
	private final float damage;
	private float timeAlive = 0;
	private float distanceToTarget = 0f;
	private float timeSinceLastNode = 0f;

	private float timesincedeepsearch = 0;
	private final Body body;
	protected Map map;
	protected Array<Node> path;
	private final float distancetonode;
	private float wasHitTime;
	private final Vector2 hitRandom = new Vector2();
	private final Color color;
	private boolean dead = false;
	private boolean delete = false;
	private boolean deleteBody = false;
	private boolean leftSpawn = false;

	private boolean bodyDeleted = false;
	private boolean spawned = false;
	private final boolean showHealthBar;
	private final String name;

	public Zombie(final String name, final Vector2 position, final float damage, final float health,
			final float money, final float score, final float spawnTimeStamp, final float speed,
			final World world, final AssetManager assetManager, final String textureSpriteAlive,
			final String textureSpriteDead, final String textureSpriteDamage, final Map map,
			final ZombieOptions zombieOptions) {
		this.name = name;
		this.damage = damage;
		maxHealth = health;
		this.health = maxHealth;
		this.money = money;
		this.score = score;
		this.speed = speed;
		this.spawnTimeStamp = spawnTimeStamp;
		this.map = map;
		showHealthBar = zombieOptions.showHealthBar;
		density = zombieOptions.density;

		textureDead = assetManager.get(textureSpriteDead);
		Texture textureAlive = assetManager.get(textureSpriteAlive);
		sprite = new Sprite(textureAlive);
		sprite.setSize(sprite.getWidth() * PlayState.PIXEL_TO_METER, sprite.getHeight() * PlayState.PIXEL_TO_METER);
		sprite.setOriginCenter();

		Texture textureDamage = assetManager.get(textureSpriteDamage);
		spriteDamage = new Sprite(textureDamage);
		spriteDamage.setSize(spriteDamage.getWidth() * PlayState.PIXEL_TO_METER,
				spriteDamage.getHeight() * PlayState.PIXEL_TO_METER);
		spriteDamage.setOriginCenter();

		// create a random color for every enemy
		color = new Color(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 0.7f);

		// create body for box2D
		body = createBody(position, world);

		distancetonode = sprite.getWidth();

		path = findPath();
		if (path.size < 1) {
			Gdx.app.error("enemy:findWay", MainGame.getCurrentTimeStampLogString() + "the zombie \"" + name + "\" did not find a path");
		}
	}

	protected FixtureDef createFixture() {
		final CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(sprite.getHeight() * 0.35f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.friction = 0;
		fdef.density = density;
		return fdef;
	}

	private Body createBody(final Vector2 position, final World world) {
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(position.x * PlayState.PIXEL_TO_METER, position.y * PlayState.PIXEL_TO_METER);

		final Body body = world.createBody(bodydef);
		// Do not activate the body until the zombie has spawned
		body.setActive(false);

		body.createFixture(createFixture());
		body.setUserData(this);

		return body;
	}

	public void spawn() {
		spawned = true;
		body.setActive(true);
	}

	/*
	public void steerLeft() {
		body.applyTorque(45, true);
	}

	public void steerRight() {
		body.applyTorque(45 * -1, true);
	}
	*/

	private void die() {
		// set dead
		dead = true;
		// set position of dead sprite to the current one
		sprite.setTexture(textureDead);
		sprite.setSize(textureDead.getWidth() * PlayState.PIXEL_TO_METER,
				textureDead.getHeight() * PlayState.PIXEL_TO_METER);
		sprite.setRotation(MathUtils.random(360));
		callbackInterface.enemyDied(this);
		deleteBody = true;
		wasHitTime = 0;
	}

	public void takeDamage(float amount) {
		if (isValidTarget()) {
			health -= amount;
			wasHitTime = 0.15f;
		}
	}

	protected Array<Node> findPath() {
		return map.getRandomPath();
	}

	public Array<Node> getPath() {
		return path;
	}

	public float getX() {
		return body.getPosition().x - sprite.getWidth() / 2;
	}

	public float getY() {
		return body.getPosition().y - sprite.getHeight() / 2;
	}

	public float getBodyX() {
		return body.getPosition().x;
	}

	public float getBodyY() {
		return body.getPosition().y;
	}

	public void drawHealthBar(final ShapeRenderer shapeRenderer) {
		if (dead || !spawned || !showHealthBar || (int) health == (int) maxHealth || health <= 0f)
			return;
		shapeRenderer.setColor(new Color(1, 0, 0, 1));
		shapeRenderer.rect(getBodyX() - 25 * PlayState.PIXEL_TO_METER, getBodyY() + sprite.getHeight() / 2,
				50 * PlayState.PIXEL_TO_METER, 3 * PlayState.PIXEL_TO_METER);
		shapeRenderer.setColor(new Color(0, 1, 0, 1));
		shapeRenderer.rect(getBodyX() - 25 * PlayState.PIXEL_TO_METER, getBodyY() + sprite.getHeight() / 2,
				50 * PlayState.PIXEL_TO_METER * (health / maxHealth), 3 * PlayState.PIXEL_TO_METER);
	}

	public void update(final float deltaTime, float gameTimeStamp) {
		// When zombie already dead don't do anything
		if (isDead()) {
			return;
		}

		// If zombie is not yet spawned check if it can now be spawned
		if (!spawned) {
			if (spawnTimeStamp < gameTimeStamp) {
				spawn();
			} else {
				return;
			}
		}

		timesincedeepsearch = timesincedeepsearch + deltaTime;
		timeAlive = timeAlive + deltaTime;
		timeSinceLastNode = timeSinceLastNode + deltaTime;
		if (timeAlive > 60 && !hasLeftSpawn())
			die();
		if(timeSinceLastNode>60)
			die();

		if (wasHitTime > 0) {
			wasHitTime -= deltaTime;
			hitRandom.x = MathUtils.random(-sprite.getWidth() / 4, sprite.getWidth() / 4);
			hitRandom.y = MathUtils.random(-sprite.getHeight() / 4, sprite.getHeight() / 4);
			spriteDamage.setPosition(
					getX() + sprite.getWidth() / 2 - spriteDamage.getWidth() / 2 + hitRandom.x,
					getY() + sprite.getHeight() / 2 - spriteDamage.getHeight() / 2 + hitRandom.y);
		}

		if (getHealth() <= 0)
			die();

		if (path.size > 0) {
			final float angle = (float) ((Math.atan2(
					path.get(path.size - 1).getPosition().x * PlayState.PIXEL_TO_METER - getBodyX(),
					-(path.get(path.size - 1).getPosition().y * PlayState.PIXEL_TO_METER - getBodyY())) * 180.0d
					/ Math.PI));
			body.setTransform(body.getPosition(), (float) Math.toRadians(angle - 90));
			final Vector2 velo = new Vector2(speed, 0);
			velo.rotateRad(body.getAngle());

			body.applyForceToCenter(velo, true);
			reduceToMaxSpeed(speed);
			
			killLateral(0.2f);

			float oldDistance = distanceToTarget;
			distanceToTarget = getDistanceToTarget(path.get(path.size - 1));
			float distanceTraveled = Math.abs(oldDistance - distanceToTarget);
			if (timeAlive > 7f && distanceTraveled != 0 && timeSinceLastNode > 2f) {
				if (distanceTraveled < 0.001f) {
					// System.out.println("Doing deep search OLD/NEW/TRAVELLED:
					// "+oldDistance+"/"+distanceToTarget+"/"+distanceTraveled);
					doDeepSearch(timeSinceLastNode);
				}
				if (oldDistance < distanceToTarget) {
					// System.out.println("Farther away OLD/NEW/TRAVELLED:
					// "+oldDistance+"/"+distanceToTarget+"/"+distanceTraveled);
					doDeepSearch(timeSinceLastNode);
				}
			}
			if (timeSinceLastNode > 6f) {
				// System.out.println("Farther away OLD/NEW/TRAVELLED:
				// "+oldDistance+"/"+distanceToTarget+"/"+distanceTraveled);
				doDeepSearch(timeSinceLastNode);
			}

			if (isCloseEnough(path.get(path.size - 1), distancetonode)) {
				path.removeIndex(path.size - 1);
				distanceToTarget = 100;
				timeSinceLastNode = 0;
			}

			/*
			if (path.size > 0) {
				// TODO Try to rework this in giving a SMALL bonus in score for early kills (long path to trailer)
				// score = path.get(path.size - 1).getH();
				// Gdx.app.debug("enemy:update", MainGame.getCurrentTimeStampLogString() + "the score " + score + " was replaced by " + path.get(path.size - 1).getH() + "????");
			}
			*/

		} else {
			callbackInterface.enemyHitsHomeCallback(this);
			deleteBody = true;
			dead = true;
		}

		sprite.setPosition(getX(), getY());
		sprite.setRotation(MathUtils.radDeg * body.getAngle());
	}

	private boolean isCloseEnough(Node n, float distance) {
		return getDistanceToTarget(n) < distance;
	}

	private float getDistanceToTarget(Node n) {
		return body.getPosition().dst(n.getPosition().x * PlayState.PIXEL_TO_METER,
				n.getPosition().y * PlayState.PIXEL_TO_METER);
	}

	private void doDeepSearch(float factor) {
		Node skipnode = null;
		for (Node n : path) {
			if (isCloseEnough(n, distancetonode * factor)) {
				if(path.indexOf(n, true)> path.size/2)
				if (skipnode == null)
					skipnode = n;
			}
		}
		if (skipnode != null) {
			while (path.get(path.size - 1) != skipnode) {
				path.removeIndex(path.size - 1);
				timeSinceLastNode = 0f;
			}
		}
	}

	public boolean isDeleteBody() {
		return deleteBody;
	}

	public boolean isDelete() {
		return delete;
	}

	private void killLateral(float drift) {
		float lat = getVelocityVector().dot(getOrthogonal());
		body.applyLinearImpulse(getOrthogonal().scl(drift).scl(lat).scl(-1).scl(density), body.getPosition(), true);
	}

	private Vector2 getVelocityVector() {
		return body.getLinearVelocity();
	}

	private Vector2 getOrthogonal() {
		final Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(body.getAngle());
		ort.rotate90(1);
		return ort;
	}

	private void reduceToMaxSpeed(float maxspeed) {
		float speed = getForwardVelocity().x;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;

		final Vector2 newSpeed = new Vector2(speed, getForwardVelocity().y);
		newSpeed.rotateRad(body.getAngle());
		body.setLinearVelocity(newSpeed);
	}

	private Vector2 getForwardVelocity() {
		final Vector2 velo = getVelocityVector();
		velo.rotateRad(body.getAngle() * -1);
		return velo;
	}

	public void draw(final SpriteBatch spriteBatch) {
		if (spawned)
			sprite.draw(spriteBatch);
		if (!isDead() && wasHitTime > 0)
			spriteDamage.draw(spriteBatch);
	}

	public float getScore() {
		return score;
	}

	public float getMoney() {
		return money;
	}

	public Body getBody() {
		return body;
	}

	public float getHealth() {
		return health;
	}

	public float getDamage() {
		return damage;
	}

	public boolean isDead() {
		return dead;
	}

	public boolean isSpawned() {
		return spawned;
	}

	/**
	 * For memory safety this method cannot be overridden by sub classes
	 */
	public final void dispose() {
		// Dispose resources from the sub classes if there are any
		disposeZombieResources();
		// Nothing else to dispose per default since everything is loaded via the asset manager
	}

	/**
	 * Each tower must implement a method to dispose resources
	 */
	protected abstract void disposeZombieResources();

	public Color getColor() {
		return color;
	}

	public void setBodyDeleted(boolean b) {
		bodyDeleted = b;
	}

	public boolean isBodyDeleted() {
		return bodyDeleted;
	}

	public Vector2 getCenter() {
		Vector2 cnt = new Vector2();
		cnt.x = getBodyX() + (sprite.getWidth() * 0.5f * PlayState.PIXEL_TO_METER);
		cnt.y = getBodyY() + (sprite.getHeight() * 0.5f * PlayState.PIXEL_TO_METER);
		return cnt;
	}

	public void setDelete(final boolean delete) {
		this.delete = delete;
	}

	public boolean isValidTarget() {
		return spawned && !dead;

	}

	public boolean hasLeftSpawn() {
		if (getY() > map.getSpawnheighty())
			leftSpawn = true;

		return leftSpawn;
	}

	public Vector2 getCenteredPosition() {
		return new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
	}

  public String getName() {
		return name;
	}
}
