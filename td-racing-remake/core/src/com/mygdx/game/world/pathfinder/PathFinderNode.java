package com.mygdx.game.world.pathfinder;

import com.badlogic.gdx.utils.Array;

public abstract class PathFinderNode {

  /**
   * The distance to the goal node
   */
  protected final float distanceToGoalNode;

  public PathFinderNode(final float distanceToGoalNode) {
    this.distanceToGoalNode = distanceToGoalNode;
  }

  /**
   * Get the distance to neighbor node
   *
   * @param neighbor The neighbor node
   * @return The distance
   */
  public abstract <T extends PathFinderNode> float getDistanceToNeighbor(T neighbor);

  /**
   * Get the successor/neighbor nodes
   *
   * @return The nodes that are connected with this node
   */
  public abstract <T extends PathFinderNode> Array<T> getSuccessors();

  /**
   * Get the current f(x) = g(x) + h(x) value of the node where g(x) is the cost from the start to
   * the node and h(x) is the estimated cost to the end/goal
   *
   * @return The current shortest calculated cost to the goal from the start node over this node
   */
  public abstract float getF();

  /**
   * Set the current g(x) value of the node where g(x) is the cost from the start to the node. This
   * value can change when a new shorter way to the node is found.
   *
   * @param newG The new shortest distance from the start node to this node via the predecessor
   */
  public abstract <T extends PathFinderNode> void setG(final float newG, final T newPredecessor);

  /**
   * Get the current predecessor over which the shortest possible path from the start node can be
   * found
   *
   * @return The node over which it is the fastest from the start node to this node
   */
  public abstract <T extends PathFinderNode> T getPredecessor();

  /**
   * Get the current g(x) value of the node where g(x) is the cost from the start to the node
   *
   * @return The current shortest calculated cost from the start node to this node
   */
  public abstract float getG();
}
