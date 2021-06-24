package com.mygdx.game.world.pathfinder;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EnemyGridNode extends PathFinderNode {

  /**
   * The position of the node on the map
   */
  private final Vector2 position;
  /**
   * The nodes that are directly connected to this node
   */
  private final Array<EnemyGridNode> successors = new Array<>();
  /**
   * Counts the amount of times the permanent additional cost was increased of this node
   */
  private int permanentAdditionalCostCounter = 0;
  /**
   * A temporary additional cost that is only kept for a single path
   */
  private float temporaryAdditionalCost = 0;
  /**
   * A permanent additional cost for this node that is kept across finding new paths of a level
   */
  private float permanentAdditionalCost = 0;
  /**
   * The current g(x) value of the node where g(x) is the cost from the start to the node
   */
  private float g = Float.POSITIVE_INFINITY;
  /**
   * The currently fastest previous node from the start to this node
   */
  private EnemyGridNode predecessor = null;

  /**
   * When constructing a node the position needs to be given.
   *
   * @param x The x position on the map
   * @param y The y position on the map
   */
  public EnemyGridNode(final float x, final float y, final float distanceToGoalNode) {
    super(distanceToGoalNode);
    position = new Vector2(x, y);
  }

  public Vector2 getPosition() {
    return position;
  }

  public void resetTemporaryAdditionalCost() {
    temporaryAdditionalCost = 0;
  }

  public void resetAdditionalCost() {
    temporaryAdditionalCost = 0;
    permanentAdditionalCostCounter = 0;
    permanentAdditionalCost = 0;
  }

  public void setTemporaryAdditionalCost(final float temporaryAdditionalCost) {
    this.temporaryAdditionalCost = temporaryAdditionalCost;
  }

  public void increasePermanentAdditionalCost(final float value) {
    permanentAdditionalCost += (value / ++permanentAdditionalCostCounter);
  }

  public void addSuccessor(final EnemyGridNode successor) {
    successors.add(successor);
  }

  public float measureDistanceToNode(final EnemyGridNode otherNode) {
    return (float) Math.sqrt(Math.pow(position.x + otherNode.position.x, 2) + Math
        .pow(position.y + otherNode.position.y, 2));
  }

  @Override
  public <T extends PathFinderNode> float getDistanceToSuccessor(T successor) {
    return measureDistanceToNode((EnemyGridNode) successor)
        + ((EnemyGridNode) successor).permanentAdditionalCost
        + ((EnemyGridNode) successor).temporaryAdditionalCost;
  }

  @Override
  public <T extends PathFinderNode> Array<T> getSuccessors() {
    return (Array<T>) successors;
  }

  @Override
  public float getF() {
    return getG() + distanceToGoalNode;
  }

  @Override
  public <T extends PathFinderNode> void setG(float newG, T newPredecessor) {
    g = newG;
    predecessor = (EnemyGridNode) newPredecessor;
  }

  @Override
  public <T extends PathFinderNode> T getPredecessor() {
    return (T) predecessor;
  }

  @Override
  public float getG() {
    return g + temporaryAdditionalCost + permanentAdditionalCost;
  }

  @Override
  public String toString() {
    return "EnemyGridNode{" +
        "position=" + position +
        ", permanentAdditionalCostCounter=" + permanentAdditionalCostCounter +
        ", temporaryAdditionalCost=" + temporaryAdditionalCost +
        ", permanentAdditionalCost=" + permanentAdditionalCost +
        ", distanceToGoalNode=" + distanceToGoalNode +
        ", g=" + g +
        ", f=" + getF() +
        ", predecessor=" + predecessor +
        '}';
  }

  @Override
  public float getH() {
    return distanceToGoalNode;
  }

  /**
   * Get the distance between this nodes position and the given position
   *
   * @param position The position to which the distance should be measured
   * @return The distance between the positions
   */
  public float measureDistanceToPosition(final Vector2 position) {
    return (float) Math.sqrt(
        Math.pow(this.position.x + position.x, 2) + Math.pow(this.position.y + position.y, 2));
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

  /**
   * Get if another node has the same position as this node.
   *
   * @param otherNode The other node
   * @param offset    An offset that is applied to the position of this node
   * @return True if both nodes (taking into account the offset) have the same position
   */
  public boolean epsilonEqualsWithOffset(final EnemyGridNode otherNode, final Vector2 offset) {
    return position.cpy().add(offset)
        .epsilonEquals(otherNode.getPosition(), MathUtils.FLOAT_ROUNDING_ERROR);
  }
}
