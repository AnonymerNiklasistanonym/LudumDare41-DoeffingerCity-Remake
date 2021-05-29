package com.mygdx.game.controller;

import com.badlogic.gdx.controllers.Controller;

/**
 * Class that helps mapping the input of different controllers onto a generic set of buttons
 * provided by an enum
 * <p>
 * TODO Add axis input
 */
public class ControllerInputMapping {

  public static ControllerInputMappingButtons getControllerButton(Controller controller,
      int buttonCode) {
    switch (controller.getName()) {
      case "Xbox One Wireless Controller (Model 1708)":
      case "X360 Controller":
      default:
        if (buttonCode == 0) {
          return ControllerInputMappingButtons.BUTTON_A;
        }
        if (buttonCode == 1) {
          return ControllerInputMappingButtons.BUTTON_B;
        }
        if (buttonCode == 2) {
          return ControllerInputMappingButtons.BUTTON_X;
        }
        if (buttonCode == 3) {
          return ControllerInputMappingButtons.BUTTON_Y;
        }
        if (buttonCode == 4) {
          return ControllerInputMappingButtons.BUTTON_BACK;
        }
        if (buttonCode == 6) {
          return ControllerInputMappingButtons.BUTTON_START;
        }
        if (buttonCode == 9) {
          return ControllerInputMappingButtons.BUTTON_LB;
        }
        if (buttonCode == 10) {
          return ControllerInputMappingButtons.BUTTON_RB;
        }
        if (buttonCode == 11) {
          return ControllerInputMappingButtons.BUTTON_UP;
        }
        if (buttonCode == 12) {
          return ControllerInputMappingButtons.BUTTON_DOWN;
        }
        if (buttonCode == 13) {
          return ControllerInputMappingButtons.BUTTON_LEFT;
        }
        if (buttonCode == 14) {
          return ControllerInputMappingButtons.BUTTON_RIGHT;
        }
    }
    return ControllerInputMappingButtons.UNKNOWN;
  }
}
