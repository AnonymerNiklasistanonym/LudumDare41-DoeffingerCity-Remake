package com.mygdx.game.controller;

import com.badlogic.gdx.math.Vector3;

/**
 * Callback interface for controller inputs in the menu state
 */
public interface IControllerCallbackMenuState {

  /**
   * Select left menu button
   */
  void controllerCallbackSelectLeftMenuButton();

  /**
   * Select right menu button
   */
  void controllerCallbackSelectRightMenuButton();

  /**
   * Select above (up) menu button
   */
  void controllerCallbackSelectAboveMenuButton();

  /**
   * Select below (down) menu button
   */
  void controllerCallbackSelectBelowMenuButton();

  /**
   * Click the start game menu button
   */
  void controllerCallbackClickStartMenuButton();

  /**
   * Click the currently selected menu button
   */
  void controllerCallbackClickMenuButton();

  /**
   * Exit the application because a back button was pressed
   */
  void controllerCallbackClickExitButton();

  /**
   * Toggle full screen
   */
  void controllerCallbackToggleFullScreen();

}
