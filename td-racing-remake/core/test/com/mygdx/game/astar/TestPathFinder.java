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

  private static String pathToString(final Array<TestNode> path) {
    if (path == null) {
      return "null";
    }
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < path.size; i++) {
      str.append(path.get(i).getId());
      if (i < path.size - 1) {
        str.append(", ");
      }
    }
    return str.toString();
  }

  private static String pathExpectedString(final Array<TestNode> expected,
      final Array<TestNode> path) {
    return "[expected={" + pathToString(expected) + "}, path={" + pathToString(path) + "}]";
  }

  private static void checkIfPathIsCorrect(final Array<TestNode> expectedPath,
      final Array<TestNode> path) {
    final String pathExpectedString = pathExpectedString(expectedPath, path);
    if (expectedPath == null) {
      assertNull("path could not be found [null]", path);
    } else {
      assertNotNull("path could be found [not null] " + pathExpectedString, path);
      assertEquals("path could be found [size] " + pathExpectedString, expectedPath.size,
          path.size);
      for (int i = 0; i < path.size; i++) {
        assertEquals("path could be found [node " + i + " is correct] " + pathExpectedString,
            expectedPath.get(i), path.get(i));
      }
    }
    System.out.printf("Path correct: %s\n", pathExpectedString);
  }

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
    start = new TestNode("start", 200);
    goal = new TestNode("goal", 0);
    nodes.add(start, goal);
    path = PathFinder.findPathAStar(nodes, start, goal);

    checkIfPathIsCorrect(null, path);
  }

  @Test
  public void testFindPathPossible() {
    start = new TestNode("start", 200);
    goal = new TestNode("goal", 0);
    nodes.add(start, goal);
    nodes.shuffle();
    start.addSuccessor(goal, 2);
    path = PathFinder.findPathAStar(nodes, start, goal);
    correctPath.add(start, goal);

    checkIfPathIsCorrect(correctPath, path);
  }

  @Test
  public void testFindShortestPathPossible1() {
    /*
     * START             GOAL
     *  (20)             (0)
     *  | |    faster    | |
     *  | |     (20)     | |
     *  | [2]----o-----[1] |
     *  |        |         |
     *  |       [1]        |
     *  |        |         |
     *  |      slower      |
     *  |       (20)       |
     *  [2]------o-------[2]
     */
    start = new TestNode("start", 20);
    goal = new TestNode("goal", 0);
    final TestNode faster = new TestNode("faster", 20);
    final TestNode slower = new TestNode("slower", 20);
    nodes.add(start, goal, faster, slower);
    nodes.shuffle();

    start.addSuccessor(faster, 2);
    start.addSuccessor(slower, 2);

    faster.addSuccessor(goal, 1);
    faster.addSuccessor(slower, 1);
    faster.addSuccessor(start, 2);

    slower.addSuccessor(goal, 2);
    slower.addSuccessor(faster, 1);
    faster.addSuccessor(start, 2);

    path = PathFinder.findPathAStar(nodes, start, goal);
    correctPath.add(start, faster, goal);

    checkIfPathIsCorrect(correctPath, path);
  }

  @Test
  public void testFindShortestPathPossible2() {
    /*
     * START             GOAL
     *  (20)             (0)
     *  | |     two      | |
     *  | |     (20)     | |
     *  | [3]----o-----[1] |
     *  |        |         |
     *  |       [1]        |
     *  |        |         |
     *  |       one        |
     *  |       (20)       |
     *  [1]------o-------[3]
     */
    start = new TestNode("start", 20);
    goal = new TestNode("goal", 0);
    final TestNode one = new TestNode("one", 20);
    final TestNode two = new TestNode("two", 20);
    nodes.add(start, goal, one, two);
    nodes.shuffle();

    start.addSuccessor(one, 1);
    start.addSuccessor(two, 3);

    one.addSuccessor(goal, 3);
    one.addSuccessor(two, 1);
    one.addSuccessor(start, 1);

    two.addSuccessor(goal, 1);
    two.addSuccessor(one, 1);
    two.addSuccessor(start, 3);

    path = PathFinder.findPathAStar(nodes, start, goal);
    correctPath.add(start, one, two, goal);

    // checkIfPathIsCorrect(correctPath, path);
  }


  @Test
  public void testFindShortestPathPossibleWikipedia1() {
    /*
     * https://upload.wikimedia.org/wikipedia/commons/6/62/Astar-germany0.svg
     *
     * Saarbrücken -> Würzburg
     */
    start = new TestNode("Saarbrücken", 222);
    goal = new TestNode("Würzburg", 0);
    final TestNode kaiserslautern = new TestNode("Kaiserslautern", 158);
    final TestNode karlsruhe = new TestNode("Karlsruhe", 140);
    final TestNode heilbronn = new TestNode("Heilbronn", 87);
    final TestNode ludwigshafen = new TestNode("Ludwigshafen", 108);
    final TestNode frankfurt = new TestNode("Frankfurt", 96);

    nodes.add(start, goal, kaiserslautern, karlsruhe);
    nodes.add(heilbronn, ludwigshafen, frankfurt);
    nodes.shuffle();

    start.addSuccessor(kaiserslautern, 70);
    start.addSuccessor(karlsruhe, 145);

    goal.addSuccessor(frankfurt, 116);
    goal.addSuccessor(ludwigshafen, 183);
    goal.addSuccessor(heilbronn, 102);

    kaiserslautern.addSuccessor(start, 70);
    kaiserslautern.addSuccessor(ludwigshafen, 53);
    kaiserslautern.addSuccessor(frankfurt, 96);

    frankfurt.addSuccessor(goal, 116);
    frankfurt.addSuccessor(kaiserslautern, 96);

    ludwigshafen.addSuccessor(goal, 183);
    ludwigshafen.addSuccessor(kaiserslautern, 53);

    heilbronn.addSuccessor(goal, 102);
    heilbronn.addSuccessor(karlsruhe, 84);

    karlsruhe.addSuccessor(start, 145);
    karlsruhe.addSuccessor(heilbronn, 84);

    path = PathFinder.findPathAStar(nodes, start, goal);
    correctPath.add(start, kaiserslautern, frankfurt, goal);

    checkIfPathIsCorrect(correctPath, path);
  }
}