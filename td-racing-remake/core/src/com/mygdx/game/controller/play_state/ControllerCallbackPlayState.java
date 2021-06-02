package com.mygdx.game.controller.play_state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerInputMapping;

public class ControllerCallbackPlayState implements ControllerListener {

  /**
   * Class that implements the controller callbacks
   */
  private final IControllerCallbackPlayState controllerCallbackClass;

  public ControllerCallbackPlayState(
      IControllerCallbackPlayState controllerCallbackClass) {
    Gdx.app.debug("controller_callback_play_state:constructor",
        MainGame.getCurrentTimeStampLogString());
    this.controllerCallbackClass = controllerCallbackClass;
  }

  @Override
  public void connected(Controller controller) {
    Gdx.app.debug("controller_callback_play_state:connected",
        MainGame.getCurrentTimeStampLogString() + "controller connected with the id \"" + controller
            .getName() + "\"");
  }

  @Override
  public void disconnected(Controller controller) {
    Gdx.app.debug("controller_callback_play_state:disconnected",
        MainGame.getCurrentTimeStampLogString() + "controller disconnected with the id \""
            + controller.getName() + "\"");
  }

  @Override
  public boolean buttonDown(Controller controller, int buttonCode) {
    // The button pressed reaction is donne in the buttonPressed method
    buttonPressed(controller, buttonCode, true);
    return false;
  }

  @Override
  public boolean buttonUp(Controller controller, int buttonCode) {
    // The button pressed reaction is donne in the buttonPressed method
    buttonPressed(controller, buttonCode, false);
    return false;
  }

  @Override
  public boolean axisMoved(Controller controller, int axisCode, float value) {
    Gdx.app.debug("controller_callback_play_state:axisMoved",
        MainGame.getCurrentTimeStampLogString() + "controller axis moved \"" + axisCode
            + "\" with the value " + value);

    // TODO Left, Right, Up, Down input handling is missing
    // final boolean isXAxis = axisCode == ControllerWiki.AXIS_LEFT_X || axisCode == ControllerWiki.AXIS_RIGHT_X;
    // controllerMenuCallbackInterface.controllerCallbackStickMoved(isXAxis, value);
    return false;
  }

  private void buttonPressed(Controller controller, int buttonId, boolean pressed) {
    Gdx.app.debug("controller_callback_play_state:buttonPressed",
        MainGame.getCurrentTimeStampLogString() + "controller button" + (pressed ? "" : " not")
            + " pressed \"" + buttonId + "\"");

    if (pressed) {
      switch (ControllerInputMapping.getControllerButton(controller, buttonId)) {
        case BUTTON_A:
          controllerCallbackClass.controllerCallbackBuildTower();
          break;
        case BUTTON_B:
        case BUTTON_BACK:
          controllerCallbackClass.controllerCallbackClickBackButton();
          break;
        case BUTTON_START:
          controllerCallbackClass.controllerCallbackToggleManualPause();
          break;
        case BUTTON_LB:
        case BUTTON_RB:
          controllerCallbackClass.controllerCallbackToggleFullScreen();
          break;
        case BUTTON_UP:
          controllerCallbackClass.controllerCallbackSelectTowerToBuild(0);
          break;
        case BUTTON_RIGHT:
          controllerCallbackClass.controllerCallbackSelectTowerToBuild(1);
          break;
        case BUTTON_DOWN:
          controllerCallbackClass.controllerCallbackSelectTowerToBuild(2);
          break;
        case BUTTON_LEFT:
          controllerCallbackClass.controllerCallbackSelectTowerToBuild(3);
          break;
        case BUTTON_X:
          controllerCallbackClass.controllerCallbackToggleMusic();
        case BUTTON_Y:
          controllerCallbackClass.controllerCallbackToggleSoundEffects();
        default:
          // not important
      }
    }
  }

}
