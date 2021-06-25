package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.file.LevelInfoCsvFile;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.world.pathfinder.EnemyGridNode;
import com.mygdx.game.world.pathfinder.PathFinder;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public class Map extends Entity {

	private static final int NODE_DISTANCE = 10;
	private final Array<EnemyGridNode> nodesList = new Array<>();
	private final Vector2 healthBarPosition;

	private Body mapModel;
	private Body mapGoal;
	private final Body finishLine;
	private Body mapZombieWay;
	private final Vector2 spawnPosition = new Vector2();
	private final Vector2 targetPosition;
	private Sprite map;
	private final Array<Array<EnemyGridNode>> paths = new Array<>();
	private final Array<Array<EnemyGridNode>> motorPaths = new Array<>();
	private final float spawnHeight;
	private final World world;

	private EnemyGridNode enemyGridGoalNode = null;
	private EnemyGridNode enemyGridStartNode = null;

	@Override
	public void removeFromWorld() {
			Gdx.app.debug("map:removeFromWorld", MainGame.getCurrentTimeStampLogString() + "remove Map " + name + " from world");
		if (mapModel != null) {
			world.destroyBody(mapModel);
		}
		if (mapGoal != null) {
			world.destroyBody(mapGoal);
		}
		if (finishLine != null) {
			world.destroyBody(finishLine);
		}
		if (mapZombieWay != null) {
			world.destroyBody(mapZombieWay);
		}
	}

	public Map(final LevelInfoCsvFile currentLevel, final World world, final Body finishLine, final float sizePitStop) {
		super(currentLevel.mapName, world);
		createSolidMap(currentLevel.mapName, world);
		this.finishLine = finishLine;
		this.world = world;
		targetPosition = calculateMapGoal();
		createAStarArray();
		healthBarPosition = currentLevel.healthBarPosition;
		spawnHeight = currentLevel.pitStopPosition.y * PlayState.PIXEL_TO_METER + sizePitStop;

		// Create x calculated ways
		//final PolygonShape ps = (PolygonShape) mapGoal.getFixtureList().first().getShape();
		//final Vector2 mapGoalPosition = new Vector2();
		//ps.getVertex(0, mapGoalPosition);

		// Precaculate zombie paths
		for (int i = 0; i < 10; i++) {
			motorPaths.add(getPath(currentLevel.enemySpawnPosition, targetPosition.cpy().scl(PlayState.METER_TO_PIXEL), 100));
		}
		for (int i = 0; i < 200; i++) {
			paths.add(getPath(currentLevel.enemySpawnPosition, targetPosition.cpy().scl(PlayState.METER_TO_PIXEL), 200));
		}
	}

	public Array<EnemyGridNode> getNodesList() {
		return nodesList;
	}

	public EnemyGridNode getNearestNodeAtPos(final Vector2 position) {
		EnemyGridNode nearestNode = nodesList.get(0);
		float nearestDistance = Float.POSITIVE_INFINITY;
		float currentDistance;
		for (final EnemyGridNode node : nodesList) {
			currentDistance = node.getPosition().dst(position);
			if (currentDistance < nearestDistance) {
				nearestDistance = currentDistance;
				nearestNode = node;
			}
		}
		return nearestNode;
	}

	public EnemyGridNode getNodeAtPos(final Vector2 position) {
		for (final EnemyGridNode node : nodesList) {
			if (node.epsilonEquals(position)) {
				return node;
			}
		}
		return null;
	}

	public EnemyGridNode getNodeAtPos(final float x, final float y) {
		for (final EnemyGridNode node : nodesList) {
			if (node.epsilonEquals(new Vector2(x, y))) {
				return node;
			}
		}
		return null;
	}

	public void createSolidMap(String mapName, final World world) {

		map = new Sprite(new Texture(Gdx.files.internal("map/" + mapName + ".png")));
		map.setSize(map.getWidth() * PlayState.PIXEL_TO_METER, map.getHeight() * PlayState.PIXEL_TO_METER);

		final BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("map/" + mapName + "_solid.json"));
		final BodyEditorLoader loaderZiel = new BodyEditorLoader(Gdx.files.internal("map/" + mapName + "_goal.json"));
		final BodyEditorLoader loaderZombieWay = new BodyEditorLoader(
				Gdx.files.internal("map/" + mapName + "_zombie_way.json"));

		// 1. Create a BodyDef, as usual.
		final BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;

		final BodyDef ziel = new BodyDef();
		ziel.type = BodyType.StaticBody;

		final BodyDef zombieway = new BodyDef();
		zombieway.type = BodyType.StaticBody;

		// 2. Create a FixtureDef, as usual.
		final FixtureDef solid = new FixtureDef();
		solid.density = 1;
		solid.friction = 0.5f;
		solid.restitution = 0.3f;

		final FixtureDef nonSolid = new FixtureDef();
		nonSolid.density = 1;
		nonSolid.friction = 0.5f;
		nonSolid.restitution = 0.3f;
		nonSolid.isSensor = true;

		// 3. Create a Body, as usual.
		mapModel = world.createBody(bd);
		mapGoal = world.createBody(ziel);
		mapZombieWay = world.createBody(ziel);

		// // 4. Create the body fixture automatically by using the loader.
		loader.attachFixture(mapModel, "Map", solid, MainGame.GAME_WIDTH * PlayState.PIXEL_TO_METER);
		loaderZiel.attachFixture(mapGoal, "Ziel", nonSolid, MainGame.GAME_WIDTH * PlayState.PIXEL_TO_METER);
		loaderZombieWay.attachFixture(mapZombieWay, "Zombieway", nonSolid,
				MainGame.GAME_WIDTH * PlayState.PIXEL_TO_METER);
	}

	public boolean isInBody(final float xPosition, final float yPosition) {
		for (final Fixture f : mapModel.getFixtureList()) {
			if (f.testPoint(xPosition, yPosition))
				return true;
		}
		return false;
	}

	private boolean isNodeInZombieMoveArea(final Vector2 position) {
		for (final Fixture fixture : mapZombieWay.getFixtureList()) {
			if (fixture.testPoint(position)) {
				return false;
			}
		}
		for (final Fixture f : finishLine.getFixtureList()) {
			if (f.testPoint(position))
				return false;
		}
		return true;
	}

	/**
	 * Create an array of nodes that is used for path finding
	 */
	private void createAStarArray() {
		// Fill node list with nodes that are in an area where zombies can move
		long timeStampStateStarted = System.currentTimeMillis();
		final Vector2 nodePosition = new Vector2();
		for (int x = 0; x <= MainGame.GAME_WIDTH; x += NODE_DISTANCE) {
			for (int y = 0; y <= MainGame.GAME_HEIGHT; y += NODE_DISTANCE) {
				nodePosition.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);
				// If the node is in an area where zombies can move add it to the list
				if (isNodeInZombieMoveArea(nodePosition)) {
					nodesList.add(new EnemyGridNode((float) x, (float) y, nodePosition.dst(targetPosition)));
				}
			}
		}
		Gdx.app.debug("map:createAStarArray", MainGame.getCurrentTimeStampLogString() + nodesList.size + " nodes were created in " + (System.currentTimeMillis() - timeStampStateStarted) + "ms");

		// Now add all neighbors of each node
		timeStampStateStarted = System.currentTimeMillis();
		final Vector2[] iterationHelper = new Vector2[] {
				new Vector2(NODE_DISTANCE, 0), new Vector2(0, NODE_DISTANCE),
				new Vector2(-NODE_DISTANCE, 0), new Vector2(0, -NODE_DISTANCE)
		};
		for (int i = 0; i < nodesList.size; i++) {
			final EnemyGridNode nodeMain = nodesList.get(i);
			for (int j = 0; j < nodesList.size; j++) {
				final EnemyGridNode nodeNeighbor = nodesList.get(j);
				for (final Vector2 vector2 : iterationHelper) {
					if (nodeMain.epsilonEqualsWithOffset(nodeNeighbor, vector2)) {
						nodeMain.addSuccessor(nodeNeighbor);
						// Stop now since there is only one position that matches
						break;
					}
				}
			}
		}
		Gdx.app.debug("map:createAStarArray", MainGame.getCurrentTimeStampLogString() + "Every node knows it's neighbors after " + (System.currentTimeMillis() - timeStampStateStarted) + "ms");
	}

	/**
	 * Calculate the target/goal position from the hit box
	 *
	 * TODO Do not just use the first vertex but get the average of all vertices
	 *
	 * @return The map goal position
	 */
	private Vector2 calculateMapGoal() {
		final PolygonShape ps = (PolygonShape) mapGoal.getFixtureList().first().getShape();
		final Vector2 mapGoalPosition = new Vector2();
		ps.getVertex(0, mapGoalPosition);
		return mapGoalPosition;
	}

	public Vector2 getSpawnPosition() {
		return spawnPosition;
	}

	public void setSpawnPosition(final Vector2 spawnPosition) {
		this.spawnPosition.set(spawnPosition);
	}

	public void draw(SpriteBatch spriteBatch) {
		map.draw(spriteBatch);
	}

	public Vector2 getTargetPosition() {
		return targetPosition;
	}

	public EnemyGridNode getEnemyGridGoalNode() {
		return enemyGridGoalNode;
	}
	public EnemyGridNode getEnemyGridStartNode() {
		return enemyGridStartNode;
	}


	private Vector2 normalizeVectorForGrid(final Vector2 vector) {
		final Vector2 newVector = vector.cpy();
		if (vector.x % NODE_DISTANCE < (NODE_DISTANCE / 2f))
			newVector.x = vector.x - vector.x % NODE_DISTANCE;
		else
			newVector.x = vector.x + (NODE_DISTANCE - vector.x % NODE_DISTANCE);
		if (vector.y % NODE_DISTANCE < (NODE_DISTANCE / 2f))
			newVector.y = vector.y - vector.y % NODE_DISTANCE;
		else
			newVector.y = vector.y + (NODE_DISTANCE - vector.y % NODE_DISTANCE);
		return newVector;
	}

	/**
	 * TODO Add description
	 *
	 * Search for a path from a given start position to the given target position using the A* algorithm and return this path.
	 * If there is no path found then the method will throw an exception.
	 *
	 * @param startPosition The position from which the path should start
	 * @param targetPosition The position to where the path should end
	 * @param maxRandomAdditionalDifficulty The maximum value of random additional difficulty that is added on the nodes of the found path so that other paths will be calculated on reruns of this method
	 * @return The "shortest" (when taking into account the current additional difficulty on the nodes) path between the given start and target position
	 */
	private Array<EnemyGridNode> getPath(final Vector2 startPosition, final Vector2 targetPosition, float maxRandomAdditionalDifficulty) {

		// Find the nearest nodes for the given start and target positions (throw exception if no node was found)
		final EnemyGridNode startNode = getNearestNodeAtPos(startPosition);

		if (startNode == null) {
			Gdx.app.error("map:getPath", MainGame.getCurrentTimeStampLogString() + "Start node is null");
			throw new RuntimeException("Start node is null");
		}
		enemyGridStartNode = startNode;

		final EnemyGridNode goalNode = getNearestNodeAtPos(targetPosition);
		if (goalNode == null) {
			Gdx.app.error("map:getPath", MainGame.getCurrentTimeStampLogString() + "End node is null");
			throw new RuntimeException("End node is null");
		}
		enemyGridGoalNode = goalNode;

		Gdx.app.debug("map:getPath", MainGame.getCurrentTimeStampLogString() + "Start node: " + startNode);
		Gdx.app.debug("map:getPath", MainGame.getCurrentTimeStampLogString() + "Goal node: " + goalNode);



		final float minAdditionalDifficulty = 20f;
		final float maxAdditionalDifficulty = minAdditionalDifficulty + (maxRandomAdditionalDifficulty * 10);
		// final long seed = 42;
		Random generator = new Random();
		float randomAdditionalDifficulty;

		// Add for each path to find a random additional difficulty to each path so that each path will be different and does not just go straight to the map goal
		for (final EnemyGridNode node : nodesList) {
			randomAdditionalDifficulty = PathFinder.getNextRandomAdditionalDifficulty(generator, minAdditionalDifficulty, maxAdditionalDifficulty);
			node.resetTemporaryAdditionalCost();
			// If a node has less successors increase the additional difficulty
			node.setTemporaryAdditionalCost(randomAdditionalDifficulty * 8 / node.getSuccessors().size);
		}

		// Try to find the "shortest" path between the start and goal note in respect of the permanent and temporary additional difficulty
		Array<EnemyGridNode> path = PathFinder.findPathAStar(nodesList, startNode, goalNode);

		// If a path was found increase the permanent additional cost for each node in this path
		if (path != null) {
			for (final EnemyGridNode node : path) {
				randomAdditionalDifficulty = PathFinder.getNextRandomAdditionalDifficulty(generator, minAdditionalDifficulty, maxAdditionalDifficulty * 10);
				// If a node has less successors increase the additional difficulty
				node.increasePermanentAdditionalCost(randomAdditionalDifficulty * 8 / node.getSuccessors().size);
			}
		}

		return path;
	}

	public Array<EnemyGridNode> getRandomPath() {
		return new Array<>(paths.random());
	}
	
	public Array<EnemyGridNode> getRandomMotorPath() {
		return new Array<>(motorPaths.random());
	}

	public Vector2 getHealthBarPos() {
		return healthBarPosition;
	}

	public float getSpawnHeight() {
		return spawnHeight;
	}

	@Override
	public void dispose() {
		// Nothing to dispose right now
		// TODO Dispose loaded files?
	}
}
