package com.mygdx.game.controller.play_state;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.controller.IControllerCallbackGenericGlobalButtons;

public interface IControllerCallbackPlayState extends
    IControllerCallbackGenericGlobalButtons {

  /**
   * If the car is accelerated return the direction in which it is accelerated
   *
   * @param forwards If true the car is accelerated forwards otherwise backwards
   */
  void controllerCallbackAccelerateCar(boolean forwards);

  /**
   * If the car is steered return the direction in which it is steered
   *
   * @param left If true the car is steered left otherwise right
   */
  void controllerCallbackSteerCar(boolean left);

  /**
   * The button to select a tower to build was clicked
   *
   * @param towerId The ID of the tower to build
   */
  void controllerCallbackSelectTowerToBuild(int towerId);

  /**
   * The cursor position to place a tower has changed
   *
   * @param cursorPos The cursor position change (needs to be applied to the current position since
   *                  controller pads have not an absolute location like the mouse cursor position)
   */
  void controllerCallbackPlaceTowerCursorPositionChanged(Vector3 cursorPos);

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

  Vector3 getCurrentMouseCursorPosition();
}
