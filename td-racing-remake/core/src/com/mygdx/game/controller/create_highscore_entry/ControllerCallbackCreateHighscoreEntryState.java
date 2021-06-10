package com.mygdx.game.controller.create_highscore_entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerInputMapping;
import java.util.Date;

public class ControllerCallbackCreateHighscoreEntryState implements ControllerListener {

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
  private final IControllerCallbackCreateHighscoreEntryState controllerCallbackClass;
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


  public ControllerCallbackCreateHighscoreEntryState(
      IControllerCallbackCreateHighscoreEntryState controllerCallbackClass) {
    Gdx.app.debug("controller_callback_create_highscore_entry_state:constructor",
        MainGame.getCurrentTimeStampLogString());
    this.controllerCallbackClass = controllerCallbackClass;
  }

  @Override
  public void connected(Controller controller) {
    Gdx.app.debug("controller_callback_create_highscore_entry_state:connected",
        MainGame.getCurrentTimeStampLogString() + "controller connected with the id \"" + controller
            .getName() + "\"");
  }

  @Override
  public void disconnected(Controller controller) {
    Gdx.app.debug("controller_callback_create_highscore_entry_state:disconnected",
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
      Gdx.app.debug("controller_callback_create_highscore_entry_state:axisMoved",
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
          Gdx.app.debug("controller_callback_create_highscore_entry_state:axisMoved",
              MainGame.getCurrentTimeStampLogString() + "(horizontal) time difference "
                  + timeDifferenceSinceLastLeftRightInput + "ms < "
                  + THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS + "ms");
          if (timeDifferenceSinceLastLeftRightInput > THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS) {
            lastTimeAxisHorizontalInputCallback = new Date().getTime();
            controllerCallbackClass.controllerCallbackSelectCharacterEntry(value > 0 ? NextCharacterEntryDirection.RIGHT : NextCharacterEntryDirection.LEFT);
          }
          break;
        case AXIS_LEFT_PAD_VERTICAL:
        case AXIS_RIGHT_PAD_VERTICAL:
        case AXIS_BOTTOM_LEFT_PAD_VERTICAL_HTML_COMPATIBILITY:
          // Reduce spamming of one axis input by only allowing one input for a certain time
          final long timeDifferenceSinceLastUpDownInput =
              new Date().getTime() - lastTimeAxisVerticalInputCallback;
          Gdx.app.debug("controller_callback_create_highscore_entry_state:axisMoved",
              MainGame.getCurrentTimeStampLogString() + "(vertical) time difference "
                  + timeDifferenceSinceLastUpDownInput + "ms < "
                  + THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS + "ms");
          if (timeDifferenceSinceLastUpDownInput > THRESHOLD_BETWEEN_AXIS_INPUTS_IN_MS) {
            lastTimeAxisVerticalInputCallback = new Date().getTime();
            controllerCallbackClass.controllerCallbackChangeCharacterEntry(value < 0 ? ChangeCharacterDirection.UPWARDS : ChangeCharacterDirection.DOWNWARDS);
          }
          break;
        default:
          // not important
      }
    }
    return false;
  }

  private void buttonPressed(Controller controller, int buttonId, boolean pressed) {
    Gdx.app.debug("controller_callback_create_highscore_entry_state:buttonPressed",
        MainGame.getCurrentTimeStampLogString() + "controller button" + (pressed ? "" : " not")
            + " pressed \"" + buttonId + "\"");

    if (pressed) {
      switch (ControllerInputMapping.getControllerButton(controller, buttonId)) {
        case BUTTON_A:
        case BUTTON_START:
          controllerCallbackClass.controllerCallbackClickSelect();
          break;
        case BUTTON_B:
        case BUTTON_BACK:
          controllerCallbackClass.controllerCallbackClickBackButton();
          break;
        case BUTTON_LB:
        case BUTTON_RB:
          controllerCallbackClass.controllerCallbackToggleFullScreen();
          break;
        case BUTTON_UP:
          controllerCallbackClass.controllerCallbackChangeCharacterEntry(ChangeCharacterDirection.UPWARDS);
          break;
        case BUTTON_DOWN:
          controllerCallbackClass.controllerCallbackChangeCharacterEntry(ChangeCharacterDirection.DOWNWARDS);
          break;
        case BUTTON_LEFT:
          controllerCallbackClass.controllerCallbackSelectCharacterEntry(NextCharacterEntryDirection.LEFT);
          break;
        case BUTTON_RIGHT:
          controllerCallbackClass.controllerCallbackSelectCharacterEntry(NextCharacterEntryDirection.RIGHT);
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
