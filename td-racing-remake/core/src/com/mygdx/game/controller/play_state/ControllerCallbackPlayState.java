package com.mygdx.game.controller.play_state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.controller.ControllerInputMapping;
import java.util.Date;

public class ControllerCallbackPlayState implements ControllerListener {

  /**
   * TODO Define this in MainGame if it works
   */
  private static final float PIXEL_TO_METER = 0.05f;
  /**
   * Class that implements the controller callbacks
   */
  private final IControllerCallbackPlayState controllerCallbackClass;
  private static final float THRESHOLD_CONTROLLER_ACCELERATE_CAR_AXIS_INPUT = 0.3f;
  private static final float THRESHOLD_CONTROLLER_STEER_CAR_AXIS_INPUT = 0.3f;
  private static final float CONTROLLER_CURSOR_SPEED_MODIFIER = 0.25f;

  private final Vector2 cursorPositionPlaceTower = new Vector2();
  private float steerCarLeftRight;
  private final Vector2 steerCarForwardsBackwards = new Vector2();
  private long timeStampControllerCursorPositionWasChanged = 0;

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
    if (Math.abs(steerCarLeftRight) > THRESHOLD_CONTROLLER_STEER_CAR_AXIS_INPUT) {
      return (steerCarLeftRight < 0) ? SteerCarLeftRight.LEFT : SteerCarLeftRight.RIGHT;
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

  public Vector3 getControllerCursorPositionPlaceTower() {
    /*
    if (combinedCursor.x < 0) {
      combinedCursor.x = 0;
    } else if (combinedCursor.x >= MainGame.GAME_WIDTH * PIXEL_TO_METER) {
      combinedCursor.x = MainGame.GAME_WIDTH * PIXEL_TO_METER;
    }

    if (combinedCursor.y < 0) {
      combinedCursor.y = 0;
    } else if (combinedCursor.y >= MainGame.GAME_HEIGHT * PIXEL_TO_METER) {
      combinedCursor.y = MainGame.GAME_HEIGHT * PIXEL_TO_METER;
    }*/

    return new Vector3(cursorPositionPlaceTower, 0);
  }

  @Override
  public boolean axisMoved(Controller controller, int axisCode, float value) {
    Gdx.app.debug("controller_callback_play_state:axisMoved",
        MainGame.getCurrentTimeStampLogString() + "controller axis moved " + axisCode + " ("
            + ControllerInputMapping.getControllerAxis(controller, axisCode).name()
            + ") with the a value " + value);
    Vector3 currentMouseCursor, combinedCursor;
    Vector2 newControllerCursor;
    switch (ControllerInputMapping.getControllerAxis(controller, axisCode)) {
      case AXIS_LT:
        steerCarForwardsBackwards.set(value, steerCarForwardsBackwards.y);
        break;
      case AXIS_RT:
        steerCarForwardsBackwards.set(steerCarForwardsBackwards.x, value);
        break;
      case AXIS_LEFT_PAD_HORIZONTAL:
        steerCarLeftRight = value;
        break;
      case AXIS_LEFT_PAD_VERTICAL:
        // Ignore input
        break;
      case AXIS_RIGHT_PAD_HORIZONTAL:
        newControllerCursor = cursorPositionPlaceTower.cpy().mulAdd(new Vector2(value, 0), CONTROLLER_CURSOR_SPEED_MODIFIER);
        if ((newControllerCursor.x > 0) && (newControllerCursor.x <= MainGame.GAME_WIDTH * PIXEL_TO_METER)) {
          Gdx.app.debug("controller_callback_play_state:axisMoved",
              MainGame.getCurrentTimeStampLogString() + "update x direction to " + newControllerCursor.x);
            timeStampControllerCursorPositionWasChanged = new Date().getTime();
          Gdx.app.debug("test", MainGame.getCurrentTimeStampLogString() + "controller timestamp was changed");
          cursorPositionPlaceTower.set(newControllerCursor);
        }
        break;
      case AXIS_RIGHT_PAD_VERTICAL:
        newControllerCursor = cursorPositionPlaceTower.cpy().mulAdd(new Vector2(0, -value), CONTROLLER_CURSOR_SPEED_MODIFIER);
        if ((newControllerCursor.y > 0) && (newControllerCursor.y <= MainGame.GAME_HEIGHT * PIXEL_TO_METER)) {
          Gdx.app.debug("controller_callback_play_state:axisMoved",
              MainGame.getCurrentTimeStampLogString() + "update y direction to " + newControllerCursor.y);
          timeStampControllerCursorPositionWasChanged = new Date().getTime();
          Gdx.app.debug("test", MainGame.getCurrentTimeStampLogString() + "controller timestamp was changed");
          cursorPositionPlaceTower.set(newControllerCursor);
        }
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

  private static final float DEBUG_RENDER_BAR_WIDTH = 10;

  public void drawDebugInput(final ShapeRenderer shapeRenderer) {
    // Draw backgrounds
    shapeRenderer.setColor(0, 0, 0, 1);
    // > left/right
    shapeRenderer.rect((float) 26, 22, DEBUG_RENDER_BAR_WIDTH, 1);
    // > forwards/backwards
    shapeRenderer.rect((float) 26, 20, DEBUG_RENDER_BAR_WIDTH, 1);

    // Draw values
    // > left/right
    shapeRenderer.setColor(0, 1, 0, 1);
    shapeRenderer.rect(26, 22, DEBUG_RENDER_BAR_WIDTH / 2  + (steerCarLeftRight * DEBUG_RENDER_BAR_WIDTH / 2), 1);
    // > forwards/backwards
    // >> Draw the separate input value sources
    shapeRenderer.setColor(1, 1, 0, 1);
    shapeRenderer.rect(26 + 5, 20, (steerCarForwardsBackwards.x * DEBUG_RENDER_BAR_WIDTH / 2), 0.5f);
    shapeRenderer.rect(26, 20, (steerCarForwardsBackwards.y * DEBUG_RENDER_BAR_WIDTH / 2), 0.5f);
    // >> Draw the collective input value source
    shapeRenderer.setColor(0, 1, 0, 1);
    shapeRenderer.rect(26, 20.5f, DEBUG_RENDER_BAR_WIDTH / 2 + ((steerCarForwardsBackwards.x - steerCarForwardsBackwards.y) * DEBUG_RENDER_BAR_WIDTH / 2), 0.5f);
  }

  public long getTimeStampControllerCursorPositionWasChanged() {
    return timeStampControllerCursorPositionWasChanged;
  }
}
