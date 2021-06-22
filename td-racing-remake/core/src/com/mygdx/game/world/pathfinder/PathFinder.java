package com.mygdx.game.world.pathfinder;

import com.badlogic.gdx.utils.Array;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

/**
 * Class that helps finding paths
 */
public class PathFinder {

  /**
   * This method will find a path using A* with an additional difficulty factor to get different
   * paths.
   *
   * @param nodes
   * @param <T>
   * @return Null if no path was found otherwise the path from start to goal
   */
  public static <T extends PathFinderNode> Array<T> findPathAStar(final Array<T> nodes,
      final T startNode, final T goalNode) {
    // The A* algorithm tries to check the nearest nodes which probably are the closest to the end/goal.
    // To find the "nearest node" x the metric f(x) = g(x) + h(x) is used.
    // g(x) is the cost from the start to the node and h(x) is the estimated cost to the end/goal.

    // At the start of the search all nodes are unknown

    if (startNode == null) {
      throw new RuntimeException("Start node is null");
    }

    if (goalNode == null) {
      throw new RuntimeException("Goal node is null");
    }

    // All nodes that were checked will be saved in a priority queue with their current f value being the deciding heuristic
    final PriorityQueue<T> openList = new PriorityQueue<T>(5, new PathFinderNodeComparator());
    // All nodes to which the shortest possible path was already found will be put in this set
    final Set<T> closedList = new HashSet<>();

    // Add the start node to the open list which has a f value of 0
    startNode.setG(0, null);
    openList.add(startNode);

    // Repeat the following steps until the open list is empty
    T currentNode;
    do {
      // Remove the node with the smallest f value
      currentNode = openList.remove();
      // Now check if this node is the goal node
      if (currentNode == goalNode) {
        // If yes then return the shortest path
        final Array<T> shortestPath = new Array<>();
        T currentPathNode = currentNode;
        while (currentPathNode != startNode) {
          shortestPath.add(currentPathNode);
          currentPathNode = currentPathNode.getPredecessor();
        }
        shortestPath.add(startNode);
        shortestPath.reverse();
        return shortestPath;
      }
      // To avoid cycles the node that is not the goal node is put into the closed list
      closedList.add(currentNode);
      // Now all child nodes of this node will be put into the open list
      for (final T nodeSuccessor : currentNode.<T>getSuccessors()) {
        // When the neighbor node is already on the close list do nothing
        if (closedList.contains(nodeSuccessor)) {
          continue;
        }
        // Else calculate the new g(x) value
        float tentative_g = currentNode.getG() + currentNode.getDistanceToNeighbor(nodeSuccessor);
        // If the node is already on the open list but the g value is not smaller ignore it
        if (openList.contains(nodeSuccessor) && tentative_g >= nodeSuccessor.getG()) {
          continue;
        }
        // If it is smaller set the predecessor node and the new g value
        nodeSuccessor.setG(tentative_g, currentNode);
        // If the successor node is already in the open list do nothing
        // float f = tentative_g + nodeSuccessor.getH();
        if (openList.contains(nodeSuccessor)) {
          continue;
        }
        // Else add it to the open list
        openList.add(nodeSuccessor);
      }
    } while (!openList.isEmpty());

    return null;
  }

  private static float getNextRandomAdditionalDifficulty(final Random randomGenerator,
      final float minAdditionalDifficulty, final float maxAdditionalDifficulty) {
    /*
     final float minAdditionalDifficulty, final float maxAdditionalDifficulty, final long seed
     Random generator = new Random(seed);
     float randomAdditionalDifficulty = getNextRandomAdditionalDifficulty(generator, minAdditionalDifficulty, maxAdditionalDifficulty);
    */
    return randomGenerator.nextFloat() * (maxAdditionalDifficulty - minAdditionalDifficulty)
        + minAdditionalDifficulty;
  }

}
