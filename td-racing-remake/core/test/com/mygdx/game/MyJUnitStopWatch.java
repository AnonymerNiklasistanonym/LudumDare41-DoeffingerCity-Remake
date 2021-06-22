package com.mygdx.game;

import java.util.concurrent.TimeUnit;
import org.junit.AssumptionViolatedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

/**
 * Source code copied from https://stackoverflow.com/a/33503889 from the user javaJavaJava
 */
public class MyJUnitStopWatch extends Stopwatch {

  private static void logInfo(Description description, String status, long nanos) {
    String testName = description.getMethodName();
    System.out.printf("Test %s %s, spent %d microseconds%n",
        testName, status, TimeUnit.NANOSECONDS.toMicros(nanos));
  }

  @Override
  protected void succeeded(long nanos, Description description) {
    logInfo(description, "succeeded", nanos);
  }

  @Override
  protected void failed(long nanos, Throwable e, Description description) {
    logInfo(description, "failed", nanos);
  }

  @Override
  protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
    logInfo(description, "skipped", nanos);
  }

  @Override
  protected void finished(long nanos, Description description) {
    logInfo(description, "finished", nanos);
  }
}
