package com.mygdx.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A node in the map for plotting zombie paths
 */
public class Node {

	/**
	 * The position of the node on the map
	 */
	private final Vector2 position;
	/**
	 * Cost from start to this node
	 */
	private float g = 1;
	/**
	 * Cost from this node to the target
	 */
	private float h = 99999;
	private float additionalDifficulty = 0;
	private Node parentNode;
	private final Array<Node> neighbors = new Array<>();

	public Node(final float x, final float y) {
		position = new Vector2(x, y);
	}

	public Node(final Vector2 position) {
		this.position = position.cpy();
	}

	public float getCost() {
		return g + h * 7 * additionalDifficulty;
	}

	public Vector2 getPosition() {
		return position;
	}

	public float getH() {
		return h;
	}

	public Array<Node> getNeighbors() {
		return neighbors;
	}

	public void addNeighbor(final Node neighbor) {
		neighbors.add(neighbor);
	}

	public boolean epsilonEqualsWithOffset(final Node nodeTwo, final Vector2 offset) {
		return (position.cpy().add(offset)).epsilonEquals(nodeTwo.getPosition(), MathUtils.FLOAT_ROUNDING_ERROR);
	}

	public boolean epsilonEquals(final Node nodeTwo) {
		return position.epsilonEquals(nodeTwo.getPosition(), MathUtils.FLOAT_ROUNDING_ERROR);
	}

	public boolean epsilonEquals(final Vector2 position2) {
		return position.epsilonEquals(position2, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	public float getG() {
		return g;
	}

	public void setG(final float g) {
		this.g = g;
	}

	public void setParent(final Node node) {
		parentNode = node;
	}

	public float getAdditionalDifficulty() {
		return additionalDifficulty;
	}

	public Node getParent() {
		return parentNode;
	}

	public void increaseAdditionalDifficulty(final float aD) {
		additionalDifficulty = additionalDifficulty + aD;
	}

	public void setH(final float h) {
		this.h = h;
	}

	public void setCost(final float cost) {
		// TODO What happened with this method
	}

	@Override
	public String toString() {
		return "Node{" +
				"position=" + position +
				", g=" + g +
				", h=" + h +
				", additionalDifficulty=" + additionalDifficulty +
				", parentNode=" + parentNode +
				'}';
	}
}
