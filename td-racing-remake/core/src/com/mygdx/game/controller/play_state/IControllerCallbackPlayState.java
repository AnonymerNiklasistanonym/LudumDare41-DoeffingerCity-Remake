package com.mygdx.game.controller.play_state;

import com.mygdx.game.controller.IControllerCallbackGenericGlobalButtons;

public interface IControllerCallbackPlayState extends
    IControllerCallbackGenericGlobalButtons {

  /**
   * The button to select a tower to build was clicked
   *
   * @param towerId The ID of the tower to build
   */
  void controllerCallbackSelectTowerToBuild(int towerId);

  /**
   * The button to build a tower was clicked
   */
  void controllerCallbackBuildTower();

  /**
   * Toggle the manual pause
   */
  void controllerCallbackToggleManualPause();

  /**
   * Go back because a back button was pressed
   */
  void controllerCallbackClickBackButton();
}
