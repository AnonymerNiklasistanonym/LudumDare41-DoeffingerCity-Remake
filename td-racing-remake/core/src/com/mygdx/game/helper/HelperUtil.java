package com.mygdx.game.helper;

public class HelperUtil {

  /**
   * This method is an implementation of JAVA 8 Math.floorMod(a,b) but this method can not be used
   * since otherwise all Android versions below 7 would not run this game any more...
   * Also it should behave like the % operator in Python.
   *
   * @param a a
   * @param b b
   * @return Positive modulo value from a % b
   */
  public static int moduloWithPositiveReturnValues(final int a, final int b) {
    int remainder = a % b;
    if (remainder < 0) {
      return remainder + b;
    } else {
      return remainder;
    }
  }
}
