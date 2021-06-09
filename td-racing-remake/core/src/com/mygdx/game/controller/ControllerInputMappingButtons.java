package com.mygdx.game.controller;

/**
 * A set of generic controller buttons onto which all controllers buttons are being mapped
 */
public enum ControllerInputMappingButtons {
  BUTTON_X,
  BUTTON_Y,
  BUTTON_A,
  BUTTON_B,
  BUTTON_BACK,
  BUTTON_START,
  BUTTON_LB,
  BUTTON_RB,
  BUTTON_UP,
  BUTTON_DOWN,
  BUTTON_LEFT,
  BUTTON_RIGHT,
  /**
   * In the HTML run time the LT trigger is recognized as button instead of an axis
   */
  BUTTON_LT_HTML_COMPATIBILITY,
  /**
   * In the HTML run time the RT trigger is recognized as button instead of an axis
   */
  BUTTON_RT_HTML_COMPATIBILITY,
  UNKNOWN
}
