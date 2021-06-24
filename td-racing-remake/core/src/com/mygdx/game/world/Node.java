package com.mygdx.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.Objects;

/**
 * A node in the map for plotting paths
 */
public class Node {

	/**
	 * The position of the node on the map
	 */
	private final Vector2 position;
	/**
	 * The neighbors of the node
	 */
	private final Array<Node> neighbors = new Array<>();

	/**
	 * When constructing a node the position needs to be given.
	 *
	 * @param x The x position on the map
	 * @param y The y position on the map
	 */
	public Node(final float x, final float y) {
		position = new Vector2(x, y);
	}

	public Node(final Vector2 position) {
		this.position = position.cpy();
	}

	public Vector2 getPosition() {
		return position;
	}

	public Array<Node> getNeighbors() {
		return neighbors;
	}

	public void addNeighbor(final Node neighbor) {
		neighbors.add(neighbor);
	}

	/**
	 * Get if another node has the same position as this node.
	 *
	 * @param otherNode The other node
	 * @param offset An offset that is applied to the position of this node
	 * @return True if both nodes (taking into account the offset) have the same position
	 */
	public boolean epsilonEqualsWithOffset(final Node otherNode, final Vector2 offset) {
		return position.cpy().add(offset).epsilonEquals(otherNode.getPosition(), MathUtils.FLOAT_ROUNDING_ERROR);
	}

	/**
	 * Get if another node has the same position as this node.
	 *
	 * @param otherNode The other node
	 * @return True if both nodes have the same position
	 */
	public boolean epsilonEquals(final Node otherNode) {
		return position.epsilonEquals(otherNode.getPosition(), MathUtils.FLOAT_ROUNDING_ERROR);
	}

	/**
	 * Get if another position is the same position as the one this node has.
	 *
	 * @param otherPosition The position that is compared to the node position
	 * @return True if both positions are the same
	 */
	public boolean epsilonEquals(final Vector2 otherPosition) {
		return position.epsilonEquals(otherPosition, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	@Override
	public String toString() {
		return "Node{" +
				"position=" + position +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Node node = (Node) o;
		return position.epsilonEquals(node.position, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	@Override
	public int hashCode() {
		return Objects.hash(position);
	}

	/**
	 * Get the distance between this nodes position and the given position
	 *
	 * @param position The position to which the distance should be measured
	 * @return The distance between the positions
	 */
	public float measureDistanceToPosition(final Vector2 position) {
			return (float) Math.sqrt(Math.pow(this.position.x + position.x, 2) + Math.pow(this.position.y + position.y, 2));
	}
}
