package com.mygdx.game.controller.play_state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerInputMapping;
import com.mygdx.game.gamestate.states.elements.HighscoreSelectCharacterDisplayInputState;
import jdk.tools.jmod.Main;

public class ControllerCallbackPlayState implements ControllerListener {

  /**
   * Class that implements the controller callbacks
   */
  private final IControllerCallbackPlayState controllerCallbackClass;
  private static final float THRESHOLD_CONTROLLER_ACCELERATE_CAR_AXIS_INPUT = 0.3f;
  private static final float THRESHOLD_CONTROLLER_STEER_CAR_AXIS_INPUT = 0.3f;

  private final float steerCar = 0;
  private final Vector3 padPlaceTowerPosition = new Vector3();
  private final Vector2 steerCarLeftRight = new Vector2();
  private final Vector2 steerCarForwardsBackwards = new Vector2();

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

  public SteerCarLeftRight getSteerCarLeftRight() {
    float leftRightValue = steerCarLeftRight.x;
    if (Math.abs(leftRightValue) > THRESHOLD_CONTROLLER_STEER_CAR_AXIS_INPUT) {
      return (leftRightValue < 0) ? SteerCarLeftRight.LEFT : SteerCarLeftRight.RIGHT;
    }
    return SteerCarLeftRight.NOTHING;
  }

  public SteerCarForwardsBackwards getSteerCarForwardsBackwards() {
    float forwardsBackwardsValue = - steerCarForwardsBackwards.y + steerCarForwardsBackwards.x;
    if (Math.abs(forwardsBackwardsValue) > THRESHOLD_CONTROLLER_ACCELERATE_CAR_AXIS_INPUT) {
      return (forwardsBackwardsValue < 0) ? SteerCarForwardsBackwards.FORWARDS : SteerCarForwardsBackwards.BACKWARDS;
    }
    return SteerCarForwardsBackwards.NOTHING;
  }

  @Override
  public boolean axisMoved(Controller controller, int axisCode, float value) {
    Gdx.app.debug("controller_callback_play_state:axisMoved",
        MainGame.getCurrentTimeStampLogString() + "controller axis moved " + axisCode + " ("
            + ControllerInputMapping.getControllerAxis(controller, axisCode).name()
            + ") with the a value " + value);
    switch (ControllerInputMapping.getControllerAxis(controller, axisCode)) {
      case AXIS_LT:
        steerCarForwardsBackwards.set(value, steerCarForwardsBackwards.y);
        break;
      case AXIS_RT:
        steerCarForwardsBackwards.set(steerCarForwardsBackwards.x, value);
        break;
      case AXIS_LEFT_PAD_HORIZONTAL:
        steerCarLeftRight.set(value, steerCarLeftRight.y);
        break;
      case AXIS_LEFT_PAD_VERTICAL:
        // Ignore input
        break;
      case AXIS_RIGHT_PAD_HORIZONTAL:
      case AXIS_RIGHT_PAD_VERTICAL:
        // TODO controllerCallbackClass.controllerCallbackPlaceTowerCursorPositionChanged();
        break;
      default:
        // not important
    }
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

  public void drawDebugInput(final ShapeRenderer shapeRenderer) {
    // Draw backgrounds
    shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
    shapeRenderer.rect((float) 26, 20, 10, 1);
    shapeRenderer.setColor(0, 0, 0, 1);
    shapeRenderer.rect((float) 26, 22, 10, 1);
    // Draw values
    shapeRenderer.setColor(1, 1, 0, 1);
    shapeRenderer.rect(26 + 5, 20, (steerCarForwardsBackwards.x * 5), 0.5f);
    shapeRenderer.rect(26, 20, (steerCarForwardsBackwards.y * 5), 0.5f);
    shapeRenderer.setColor(0, 1, 0, 1);
    shapeRenderer.rect(26, 20.5f, 10f / 2  + ((steerCarForwardsBackwards.x - steerCarForwardsBackwards.y) * 5), 0.5f);
    shapeRenderer.rect(26, 22, 10f / 2  + (steerCarLeftRight.x * 5), 1);
  }

}
