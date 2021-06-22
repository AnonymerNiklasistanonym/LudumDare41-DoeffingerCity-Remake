package com.mygdx.game;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class ExampleJUnitTest {

  @Rule
  public MyJUnitStopWatch stopwatch = new MyJUnitStopWatch();

  @BeforeClass
  public static void setUpClass() {
    System.out.println("this is run before all tests");
  }

  @AfterClass
  public static void cleanUpClass() {
    System.out.println("this is run after all tests");
  }

  @Before
  public void setUp() {
    System.out.println("this is run before each test");
  }

  @After
  public void cleanUp() {
    System.out.println("this is run after each test");
  }

  @Test
  public void testMultiply() {
    System.out.println("test multiply");
    assertEquals("Regular multiplication should work", 4 * 5, 20);
  }

  @Test
  public void testMultiplyWithZero() {
    System.out.println("test multiply with 0");
    assertEquals("Multiple with zero should be zero", 0, 0 * System.currentTimeMillis());
  }

}