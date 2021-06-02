package com.mygdx.game.controller.generic.one_click;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerInputMapping;

/**
 * Controller listener for a credit like (game) state. Any class that implements the interface
 * IControllerCallbackGenericOneClick can be inserted in the constructor of an instance of this
 * class (for example via this) so that callbacks to controller inputs can provided to this class
 * via the implemented methods of the interface. This class needs to be added as controller listener
 * and removed again on closing of the game state.
 */
public class ControllerCallbackGenericOneClick implements ControllerListener {

  /**
   * Class that implements the controller callbacks
   */
  private final IControllerCallbackGenericOneClick controllerCallbackClass;

  public ControllerCallbackGenericOneClick(
      IControllerCallbackGenericOneClick controllerCallbackClass) {
    Gdx.app.debug("controller_callback_generic_one_click:constructor",
        MainGame.getCurrentTimeStampLogString());
    this.controllerCallbackClass = controllerCallbackClass;
  }

  @Override
  public void connected(Controller controller) {
    Gdx.app.debug("controller_callback_generic_one_click:connected",
        MainGame.getCurrentTimeStampLogString() + "controller connected with the id \"" + controller
            .getName() + "\"");
  }

  @Override
  public void disconnected(Controller controller) {
    Gdx.app.debug("controller_callback_generic_one_click:disconnected",
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
    // Ignore axes inputs
    return false;
  }

  private void buttonPressed(Controller controller, int buttonId, boolean pressed) {
    Gdx.app.debug("controller_callback_generic_one_click:buttonPressed",
        MainGame.getCurrentTimeStampLogString() + "controller button" + (pressed ? "" : " not")
            + " pressed \"" + buttonId + "\"");

    if (pressed) {
      switch (ControllerInputMapping.getControllerButton(controller, buttonId)) {
        case BUTTON_A:
        case BUTTON_B:
        case BUTTON_BACK:
        case BUTTON_START:
        case BUTTON_UP:
        case BUTTON_DOWN:
        case BUTTON_LEFT:
        case BUTTON_RIGHT:
          controllerCallbackClass.controllerCallbackClickAnyButton();
          break;
        case BUTTON_LB:
        case BUTTON_RB:
          controllerCallbackClass.controllerCallbackToggleFullScreen();
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
