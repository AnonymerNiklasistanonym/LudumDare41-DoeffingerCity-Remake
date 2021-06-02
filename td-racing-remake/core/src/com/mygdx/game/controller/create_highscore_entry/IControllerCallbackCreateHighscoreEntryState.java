package com.mygdx.game.controller.create_highscore_entry;

import com.mygdx.game.controller.IControllerCallbackGenericGlobalButtons;

/**
 * Callback interface for controller inputs in the menu state
 */
public interface IControllerCallbackCreateHighscoreEntryState extends
    IControllerCallbackGenericGlobalButtons {

  /**
   * Select left character button
   */
  void controllerCallbackSelectLeftCharacter();

  /**
   * Select right character button
   */
  void controllerCallbackSelectRightCharacter();

  /**
   * Change character
   *
   * @param upwards True if upwards was pressed otherwise downwards
   */
  void controllerCallbackSelectAboveMenuButton(boolean upwards);

  /**
   * Accept the highscore entry and go to the next state
   */
  void controllerCallbackClickSelect();

  /**
   * Go back because a back button was pressed
   */
  void controllerCallbackClickBackButton();
}
