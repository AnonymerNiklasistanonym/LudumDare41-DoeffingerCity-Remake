package com.mygdx.game.astar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyJUnitStopWatch;
import com.mygdx.game.world.pathfinder.PathFinder;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class TestPathFinder {

  private final Array<TestNode> nodes = new Array<>();
  private final Array<TestNode> correctPath = new Array<>();
  @Rule
  public MyJUnitStopWatch stopwatch = new MyJUnitStopWatch();
  private TestNode start = null;
  private TestNode goal = null;
  private Array<TestNode> path = null;

  @After
  public void resetClassVariables() {
    start = null;
    goal = null;
    path = null;
    nodes.clear();
    correctPath.clear();
  }

  @Test
  public void testFindPathNotPossible() {
    start = new TestNode(200);
    goal = new TestNode(0);
    nodes.add(start, goal);
    path = PathFinder.findPathAStar(nodes, start, goal);
    assertNull("path could not be found [null]", path);
  }

  @Test
  public void testFindPathPossible() {
    start = new TestNode(200);
    goal = new TestNode(0);
    nodes.add(start, goal);
    nodes.shuffle();
    start.addSuccessor(goal, 2);
    path = PathFinder.findPathAStar(nodes, start, goal);
    correctPath.add(start, goal);
    assertNotNull("path could be found [not null]", path);
    assertEquals("path could be found [size]", correctPath.size, path.size);
    for (int i = 0; i < path.size; i++) {
      assertEquals("path could be found [node " + i + " is correct]", correctPath.get(i),
          path.get(i));
    }
  }

  @Test
  public void testFindShortestPathPossible() {
    start = new TestNode(200);
    goal = new TestNode(0);
    final TestNode fasterNode = new TestNode(20);
    final TestNode slowerNode = new TestNode(40);
    nodes.add(start, goal, fasterNode, slowerNode);
    nodes.shuffle();
    start.addSuccessor(fasterNode, 2);
    start.addSuccessor(slowerNode, 2);
    // The faster node is closer to the goal than the slower node
    fasterNode.addSuccessor(goal, 1);
    slowerNode.addSuccessor(goal, 2);
    path = PathFinder.findPathAStar(nodes, start, goal);
    correctPath.add(start, fasterNode, goal);
    assertNotNull("path could be found [not null]", path);
    assertEquals("path could be found [size]", correctPath.size, path.size);
    for (int i = 0; i < path.size; i++) {
      assertEquals("path could be found [node " + i + " is correct]", correctPath.get(i),
          path.get(i));
    }
  }
}