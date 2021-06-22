package com.mygdx.game.astar;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.world.pathfinder.PathFinderNode;

public class TestNode extends PathFinderNode {

  public TestNode(final float distanceToGoalNode) {
    super(distanceToGoalNode);
  }

  private Array<TestNode> successors = new Array<>();
  private Array<Float> successorDistances = new Array<>();
  private float g = Float.POSITIVE_INFINITY;
  private TestNode predecessor;

  public void addSuccessor(final TestNode successor, final float distanceToSuccessor) {
    successors.add(successor);
    successorDistances.add(distanceToSuccessor);
  }

  @Override
  public <T extends PathFinderNode> float getDistanceToNeighbor(T neighbor) {
    int indexOfSuccessor = successors.indexOf((TestNode) neighbor, true);
    if (indexOfSuccessor > -1) {
      return successorDistances.get(indexOfSuccessor);
    }
    return Float.POSITIVE_INFINITY;
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
    predecessor = (TestNode) newPredecessor;
  }

  @Override
  public <T extends PathFinderNode> T getPredecessor() {
    return (T) predecessor;
  }

  @Override
  public float getG() {
    return g;
  }

  @Override
  public String toString() {
    return "TestNode{" +
        "distanceToGoalNode=" + distanceToGoalNode +
        ", g=" + g +
        ", f=" + getF() +
        ", predecessor=" + predecessor +
        '}';
  }

}
