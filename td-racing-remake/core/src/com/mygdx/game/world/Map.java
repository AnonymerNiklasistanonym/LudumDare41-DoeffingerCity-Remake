package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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

public class Map extends Entity {

	private static final int NODE_DISTANCE = 10;
	private final Array<Node> nodesList = new Array<>();
	private final Vector2 healthBarPosition;

	private Body mapModel;
	private Body mapGoal;
	private final Body finishLine;
	private Body mapZombieWay;
	private final Vector2 spawnPosition = new Vector2();
	private final Vector2 targetPosition;
	private Sprite map;
	private final Array<Array<Node>> paths = new Array<>();
	private final Array<Array<Node>> motorPaths = new Array<>();
	private final float spawnHeight;
	private final World world;

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
		final PolygonShape ps = (PolygonShape) mapGoal.getFixtureList().first().getShape();
		final Vector2 mapGoalPosition = new Vector2();
		ps.getVertex(0, mapGoalPosition);

		for (int i = 0; i < 2; i++) {
			motorPaths.add(getPath(new Vector2(currentLevel.enemySpawnPosition.x, currentLevel.enemySpawnPosition.y),
					new Vector2(mapGoalPosition.x * PlayState.METER_TO_PIXEL, mapGoalPosition.y * PlayState.METER_TO_PIXEL),0));
		}
		
		for (int i = 0; i < 200; i++) {
			paths.add(getPath(new Vector2(currentLevel.enemySpawnPosition.x, currentLevel.enemySpawnPosition.y),
					new Vector2(mapGoalPosition.x * PlayState.METER_TO_PIXEL, mapGoalPosition.y * PlayState.METER_TO_PIXEL),3));
		}

	}

	public Array<Node> getNodesList() {
		return nodesList;
	}

	public Node getNodesAtPos(final int x, final int y) {
		for (final Node node : nodesList) {
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

	private void createAStarArray() {
		// Fill node list with nodes that are in an area where zombies can move
		long timeStampStateStarted = System.currentTimeMillis();
		final Vector2 nodePosition = new Vector2();
		for (int x = 0; x <= MainGame.GAME_WIDTH; x += NODE_DISTANCE) {
			for (int y = 0; y <= MainGame.GAME_HEIGHT; y += NODE_DISTANCE) {
				nodePosition.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);
				// If the node is in an area where zombies can move add it to the list
				if (isNodeInZombieMoveArea(nodePosition)) {
					nodesList.add(new Node((float) x, (float) y));
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
			final Node nodeMain = nodesList.get(i);
			for (int j = 0; j < nodesList.size; j++) {
				final Node nodeNeighbor = nodesList.get(j);
				for (final Vector2 vector2 : iterationHelper) {
					if (nodeMain.epsilonEqualsWithOffset(nodeNeighbor, vector2)) {
						nodeMain.addNeighbor(nodeNeighbor);
						// Stop now since there is only one position that matches
						break;
					}
				}
			}
		}
		Gdx.app.debug("map:createAStarArray", MainGame.getCurrentTimeStampLogString() + "Every node knows it's neighbors after " + (System.currentTimeMillis() - timeStampStateStarted) + "ms");

		// Set the goal distance at the map goal node to 1 and update recursively all neighbors
		timeStampStateStarted = System.currentTimeMillis();
		Node mapGoal = null;
		for (final Node node : nodesList) {
			if (node.epsilonEquals(targetPosition.cpy().scl(PlayState.METER_TO_PIXEL))) {
				mapGoal = node;
				break;
			}
		}
		if (mapGoal == null) {
			Gdx.app.error("map:createAStarArray", MainGame.getCurrentTimeStampLogString() + "No map goal was found");
			throw new RuntimeException("No map goal was found");
		}
		mapGoal.setH(1);
		// Find all neighbors and update their distance to the goal node etc.
		updateNodeNeighborDistances(mapGoal);
		Gdx.app.debug("map:createAStarArray", MainGame.getCurrentTimeStampLogString() + "All node distances were updated after " + (System.currentTimeMillis() - timeStampStateStarted) + "ms");
	}

	private Vector2 calculateMapGoal() {
		final PolygonShape ps = (PolygonShape) mapGoal.getFixtureList().first().getShape();
		final Vector2 mapGoalPosition = new Vector2();
		ps.getVertex(0, mapGoalPosition);

		// normalize end?
		Vector2 mapGoal = new Vector2(1, 1);
		if (mapGoalPosition.x % NODE_DISTANCE < (NODE_DISTANCE / 2f))
			mapGoal.x = mapGoalPosition.x - mapGoalPosition.x % NODE_DISTANCE;
		if (mapGoalPosition.x % NODE_DISTANCE >= (NODE_DISTANCE / 2f))
			mapGoal.x = mapGoalPosition.x + (NODE_DISTANCE - mapGoalPosition.x % NODE_DISTANCE);
		if (mapGoalPosition.y % NODE_DISTANCE < (NODE_DISTANCE / 2f))
			mapGoal.y = mapGoalPosition.y - mapGoalPosition.y % NODE_DISTANCE;
		if (mapGoalPosition.y % NODE_DISTANCE >= (NODE_DISTANCE / 2f))
			mapGoal.y = mapGoalPosition.y + (NODE_DISTANCE - mapGoalPosition.y % NODE_DISTANCE);

		return mapGoal;
	}

	private void updateNodeNeighborDistances(final Node node) {
		if (node == null || node.getNeighbors() == null) {
			return;
		}
		for (final Node neighbor : node.getNeighbors()) {
			if (neighbor.getH() > node.getH() + 1) {
				neighbor.setH(node.getH() + 1);
				updateNodeNeighborDistances(neighbor);
			}
		}
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

	private Vector2 normalizeVectorForGrid(final Vector2 vector) {
		if (vector.x % NODE_DISTANCE < 5)
			vector.x = vector.x - vector.x % NODE_DISTANCE;
		else
			vector.x = vector.x + (NODE_DISTANCE - vector.x % NODE_DISTANCE);
		if (vector.y % NODE_DISTANCE < 5)
			vector.y = vector.y - vector.y % NODE_DISTANCE;
		else
			vector.y = vector.y + (NODE_DISTANCE - vector.y % NODE_DISTANCE);
		return vector;
	}

	private Array<Node> getPath(final Vector2 startPosition, final Vector2 targetPosition, float maxDiff) {
		Array<Node> openList = new Array<>();
		Array<Node> closedList = new Array<>();
		Array<Node> tempweg = new Array<>();
		Node aktuellerNode;

		// was the way found
		boolean foundWay = false;

		// Which node is the next
		startPosition.set(normalizeVectorForGrid(startPosition));
		targetPosition.set(normalizeVectorForGrid(targetPosition));

		// Which neighbor is the best
		Node startNode = getNodesAtPos((int) startPosition.x, (int) startPosition.y);
		if (startNode == null) {
			Gdx.app.error("map:getPath", MainGame.getCurrentTimeStampLogString() + "Start node is null");
			throw new RuntimeException("Start node is null");
		}

		if (getNodesAtPos((int) targetPosition.x, (int) targetPosition.y) == null) {
			Gdx.app.error("map:getPath", MainGame.getCurrentTimeStampLogString() + "End node is null");
			throw new RuntimeException("End node is null");
		}

		openList.add(startNode);
		aktuellerNode = startNode;

		float lowCost;

		while (!foundWay) {
			lowCost = 999999999;

			for (int i = 0; i < openList.size; i++) {
				final Node node = openList.get(i);
				if (lowCost > node.getCost()) {
					aktuellerNode = node;
					lowCost = node.getCost();
				}
			}

			if (openList.indexOf(aktuellerNode, true) < 0) {
				Gdx.app.error("map:getPath", MainGame.getCurrentTimeStampLogString() + "Current node can not be found in openList (MainMap)\n"
						+ "openList size: " + openList.size + "\n" + "current node position: " + aktuellerNode.getPosition() + "\n"
						+ "tempweg size: " + openList.size);
				throw new RuntimeException("Current node can not be found in openList");
				// return tempweg;
			}

			if (openList.indexOf(aktuellerNode, true) != -1)
				openList.removeIndex(openList.indexOf(aktuellerNode, true));

			closedList.add(aktuellerNode);

			for (int i = 0; i < aktuellerNode.getNeighbors().size; i++) {
				final Node node = aktuellerNode.getNeighbors().get(i);
				if (closedList.indexOf(node, true) == -1) {
					node.setG(aktuellerNode.getG() + 1);
					node.setParent(aktuellerNode);
					if (openList.indexOf(node, true) == -1) {
						node.setCost(node.getCost());
						openList.add(node);
					}
				}
			}

			if (aktuellerNode.getPosition().equals(targetPosition)) {
				foundWay = true;
			}
		}

		Node node;
		while (aktuellerNode != null) {
			// Add for every way that is used an additional difficulty
			node = getNodesAtPos((int) aktuellerNode.getPosition().x, (int) aktuellerNode.getPosition().y);
			if (node != null) {
				node.increaseAdditionalDifficulty(MathUtils.random(0f, maxDiff));
				// Add node to the way
				tempweg.add(aktuellerNode);
			}
			// Do this until there is no "parent" any more
			aktuellerNode = aktuellerNode.getParent();
		}
		return tempweg;
	}

	public Array<Node> getRandomPath() {
		return new Array<>(paths.random());
	}
	
	public Array<Node> getRandomMotorPath() {
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
