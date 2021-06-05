package com.mygdx.game.controller.generic.menu_button_grid;

import com.mygdx.game.controller.IControllerCallbackGenericGlobalButtons;

/**
 * Callback interface for controller inputs in the menu state
 */
public interface IControllerCallbackGenericMenuButtonGrid extends
    IControllerCallbackGenericGlobalButtons {

  /**
   * Select a menu button
   *
   * @param direction The direction in which the next menu button was selected
   */
  void controllerCallbackSelectMenuButton(NextMenuButtonDirection direction);

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
