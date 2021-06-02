package com.mygdx.game.controller.generic.menu_button_grid;

import com.mygdx.game.controller.IControllerCallbackGenericGlobalButtons;

/**
 * Callback interface for controller inputs in the menu state
 */
public interface IControllerCallbackGenericMenuButtonGrid extends
    IControllerCallbackGenericGlobalButtons {

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
   * Go back because a back button was pressed
   */
  void controllerCallbackClickBackButton();

}
