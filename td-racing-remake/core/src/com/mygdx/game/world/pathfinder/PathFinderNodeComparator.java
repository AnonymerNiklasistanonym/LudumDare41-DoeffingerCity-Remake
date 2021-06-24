package com.mygdx.game.world.pathfinder;

import java.util.Comparator;

public class PathFinderNodeComparator implements Comparator<PathFinderNode> {

  @Override
  public int compare(PathFinderNode x, PathFinderNode y) {
    // Return the node with a lower f value

    //  0 - if they are equal
    //  1 - if x value is greater
    // -1 - if y value is lesser

    return Float.compare(x.getF(), y.getF());
  }
}
