package com.mygdx.game.controller;

/**
 * A set of generic controller axes onto which all controllers buttons are being mapped
 */
public enum ControllerInputMappingAxes {
  AXIS_LT,
  AXIS_RT,
  AXIS_LEFT_PAD_HORIZONTAL,
  AXIS_LEFT_PAD_VERTICAL,
  AXIS_RIGHT_PAD_HORIZONTAL,
  AXIS_RIGHT_PAD_VERTICAL,
  /**
   * In the HTML run time on Firefox the bottom left pad is recognized as two axes instead of 4 buttons
   */
  AXIS_BOTTOM_LEFT_PAD_HORIZONTAL_HTML_COMPATIBILITY,
  /**
   * In the HTML run time on Firefox the bottom left pad is recognized as two axes instead of 4 buttons
   */
  AXIS_BOTTOM_LEFT_PAD_VERTICAL_HTML_COMPATIBILITY,
  UNKNOWN
}
