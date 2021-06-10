package com.mygdx.game;

public class HtmlPlatformInfo {

  public final boolean isFirefox;
  public final boolean isChrome;
  public final boolean isLinux;
  public final boolean isWindows;

  public HtmlPlatformInfo(final boolean isFirefox, final boolean isChrome, final boolean isLinux, final boolean isWindows) {
    this.isFirefox = isFirefox;
    this.isChrome = isChrome;
    this.isLinux = isLinux;
    this.isWindows = isWindows;
  }

  @Override
  public String toString() {
    return "HtmlPlatformInfo{" +
        "isFirefox=" + isFirefox +
        ", isChrome=" + isChrome +
        ", isLinux=" + isLinux +
        ", isWindows=" + isWindows +
        '}';
  }
}
