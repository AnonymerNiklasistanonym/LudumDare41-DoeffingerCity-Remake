package com.mygdx.game.controller.menu_button_grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerInputMapping;

/**
 * Controller listener for the menu (game) state. Any class that implements the interface
 * IControllerCallbackMenuState can be inserted in the constructor of an instance of this class (for
 * example via this) so that callbacks to controller inputs can provided to this class via the
 * implemented methods of the interface. This class needs to be added as controller listener and
 * removed again on closing of the game state.
 */
public class ControllerCallbackGenericMenuButtonGrid implements ControllerListener {

  private final IControllerCallbackGenericMenuButtonGrid controllerCallbackClass;

  public ControllerCallbackGenericMenuButtonGrid(
      IControllerCallbackGenericMenuButtonGrid controllerCallbackClass) {
    Gdx.app.debug("controller_callback_menu_state:constructor",
        MainGame.getCurrentTimeStampLogString());
    this.controllerCallbackClass = controllerCallbackClass;
  }

  @Override
  public void connected(Controller controller) {
    Gdx.app.debug("controller_callback_menu_state:connected",
        MainGame.getCurrentTimeStampLogString() + "controller connected with the id \"" + controller
            .getName() + "\"");
  }

  @Override
  public void disconnected(Controller controller) {
    Gdx.app.debug("controller_callback_menu_state:disconnected",
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
    Gdx.app.debug("controller_callback_menu_state:axisMoved",
        MainGame.getCurrentTimeStampLogString() + "controller axis moved \"" + axisCode
            + "\" with the value " + value);

    // TODO Left, Right, Up, Down input handling is missing
    // final boolean isXAxis = axisCode == ControllerWiki.AXIS_LEFT_X || axisCode == ControllerWiki.AXIS_RIGHT_X;
    // controllerMenuCallbackInterface.controllerCallbackStickMoved(isXAxis, value);
    return false;
  }

  private void buttonPressed(Controller controller, int buttonId, boolean pressed) {
    Gdx.app.debug("controller_callback_menu_state:buttonPressed",
        MainGame.getCurrentTimeStampLogString() + "controller button" + (pressed ? "" : " not")
            + " pressed \"" + buttonId + "\"");

    if (pressed) {
      switch (ControllerInputMapping.getControllerButton(controller, buttonId)) {
        case BUTTON_A:
          controllerCallbackClass.controllerCallbackClickMenuButton();
          break;
        case BUTTON_B:
        case BUTTON_BACK:
          controllerCallbackClass.controllerCallbackClickBackButton();
          break;
        case BUTTON_START:
          controllerCallbackClass.controllerCallbackClickStartMenuButton();
          break;
        case BUTTON_LB:
        case BUTTON_RB:
          controllerCallbackClass.controllerCallbackToggleFullScreen();
          break;
        case BUTTON_UP:
          controllerCallbackClass.controllerCallbackSelectAboveMenuButton();
          break;
        case BUTTON_DOWN:
          controllerCallbackClass.controllerCallbackSelectBelowMenuButton();
          break;
        case BUTTON_LEFT:
          controllerCallbackClass.controllerCallbackSelectLeftMenuButton();
          break;
        case BUTTON_RIGHT:
          controllerCallbackClass.controllerCallbackSelectRightMenuButton();
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
