package com.mygdx.game.controller.generic.menu_button_grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerInputMapping;
import java.util.Date;

/**
 * Controller listener for any menu lik (game) state. Any class that implements the interface
 * IControllerCallbackGenericMenuButtonGrid can be inserted in the constructor of an instance of
 * this class (for example via this) so that callbacks to controller inputs can provided to this
 * class via the implemented methods of the interface. This class needs to be added as controller
 * listener and removed again on closing of the game state.
 */
public class ControllerCallbackGenericMenuButtonGrid implements ControllerListener {

  /**
   * The threshold for recognizing an axis input (this is used so that the constant spam of low
   * values when the user only slightly touches an axis is ignored)
   */
  private static final float THRESHOLD_CONTROLLER_AXIS_INPUT = 0.5f;
  /**
   * The threshold for an "ignore inputs after a registered input" time span so that menu inputs are
   * not spammed (otherwise a simple axis up input quickly creates 5 UP callbacks but we only want
   * to register one)
   */
  private static final float THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS = 300;
  /**
   * Class that implements the controller callbacks
   */
  private final IControllerCallbackGenericMenuButtonGrid controllerCallbackClass;
  /**
   * Used to save a timestamp (new Date().getTime()) of when the last time a vertical axis input was
   * made
   */
  private long lastTimeAxisVerticalInputCallback = 0;
  /**
   * Used to save a timestamp (new Date().getTime()) of when the last time a horizontal axis input
   * was made
   */
  private long lastTimeAxisHorizontalInputCallback = 0;


  public ControllerCallbackGenericMenuButtonGrid(
      IControllerCallbackGenericMenuButtonGrid controllerCallbackClass) {
    Gdx.app.debug("controller_callback_generic_menu_button_grid:constructor",
        MainGame.getCurrentTimeStampLogString());
    this.controllerCallbackClass = controllerCallbackClass;
  }

  @Override
  public void connected(Controller controller) {
    Gdx.app.debug("controller_callback_generic_menu_button_grid:connected",
        MainGame.getCurrentTimeStampLogString() + "controller connected with the id \"" + controller
            .getName() + "\"");
  }

  @Override
  public void disconnected(Controller controller) {
    Gdx.app.debug("controller_callback_generic_menu_button_grid:disconnected",
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
    // Be sure to only register inputs that are above a certain threshold
    if (Math.abs(value) > THRESHOLD_CONTROLLER_AXIS_INPUT) {
      Gdx.app.debug("controller_callback_generic_menu_button_grid:axisMoved",
          MainGame.getCurrentTimeStampLogString() + "controller axis moved " + axisCode + " ("
              + ControllerInputMapping.getControllerAxis(controller, axisCode).name()
              + ") with the a value " + value + "that is higher than the threshold "
              + THRESHOLD_CONTROLLER_AXIS_INPUT);
      switch (ControllerInputMapping.getControllerAxis(controller, axisCode)) {
        case AXIS_LEFT_PAD_HORIZONTAL:
        case AXIS_RIGHT_PAD_HORIZONTAL:
        case AXIS_BOTTOM_LEFT_PAD_HORIZONTAL_HTML_COMPATIBILITY:
          // Reduce spamming of one axis input by only allowing one input for a certain time
          final long timeDifferenceSinceLastLeftRightInput =
              new Date().getTime() - lastTimeAxisHorizontalInputCallback;
          Gdx.app.debug("controller_callback_generic_menu_button_grid:axisMoved",
              MainGame.getCurrentTimeStampLogString() + "(horizontal) time difference "
                  + timeDifferenceSinceLastLeftRightInput + "ms < "
                  + THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS + "ms");
          if (timeDifferenceSinceLastLeftRightInput > THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS) {
            lastTimeAxisHorizontalInputCallback = new Date().getTime();
            if (value > 0) {
              controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.RIGHT);
            } else {
              controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.LEFT);
            }
          }
          break;
        case AXIS_LEFT_PAD_VERTICAL:
        case AXIS_RIGHT_PAD_VERTICAL:
        case AXIS_BOTTOM_LEFT_PAD_VERTICAL_HTML_COMPATIBILITY:
          // Reduce spamming of one axis input by only allowing one input for a certain time
          final long timeDifferenceSinceLastUpDownInput =
              new Date().getTime() - lastTimeAxisVerticalInputCallback;
          Gdx.app.debug("controller_callback_generic_menu_button_grid:axisMoved",
              MainGame.getCurrentTimeStampLogString() + "(vertical) time difference "
                  + timeDifferenceSinceLastUpDownInput + "ms < "
                  + THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS + "ms");
          if (timeDifferenceSinceLastUpDownInput > THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS) {
            lastTimeAxisVerticalInputCallback = new Date().getTime();
            if (value > 0) {
              controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.BELOW);
            } else {
              controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.ABOVE);
            }
          }
          break;
        default:
          // not important
      }
    }
    return false;
  }

  private void buttonPressed(Controller controller, int buttonId, boolean pressed) {
    Gdx.app.debug("controller_callback_generic_menu_button_grid:buttonPressed",
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
          controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.ABOVE);
          break;
        case BUTTON_DOWN:
          controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.BELOW);
          break;
        case BUTTON_LEFT:
          controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.LEFT);
          break;
        case BUTTON_RIGHT:
          controllerCallbackClass.controllerCallbackSelectMenuButton(NextMenuButtonDirection.RIGHT);
          break;
        case BUTTON_X:
          controllerCallbackClass.controllerCallbackToggleMusic();
          break;
        case BUTTON_Y:
          controllerCallbackClass.controllerCallbackToggleSoundEffects();
          break;
        default:
          // not important
      }
    }
  }

}
