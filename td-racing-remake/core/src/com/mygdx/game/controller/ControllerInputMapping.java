package com.mygdx.game.controller;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.mygdx.game.HtmlPlatformInfo;
import com.mygdx.game.MainGame;

/**
 * Class that helps mapping the input of different controllers onto a generic set of buttons
 * provided by an enum
 */
public class ControllerInputMapping {

  private static ControllerInputMappingButtons getControllerButtonDesktopXbox(int buttonCode) {
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
    return ControllerInputMappingButtons.UNKNOWN;
  }

  private static ControllerInputMappingButtons getControllerButtonHtmlXbox(int buttonCode) {
    final HtmlPlatformInfo platformInfo = MainGame.getPlatformInfo();
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
      return ControllerInputMappingButtons.BUTTON_LB;
    }
    if (buttonCode == 5) {
      return ControllerInputMappingButtons.BUTTON_RB;
    }
    if (platformInfo != null && platformInfo.isFirefox) {
      if (buttonCode == 6) {
        return ControllerInputMappingButtons.BUTTON_BACK;
      }
      if (buttonCode == 7) {
        return ControllerInputMappingButtons.BUTTON_START;
      }
    } else {
      // (Chrome)
      if (buttonCode == 6) {
        return ControllerInputMappingButtons.BUTTON_LT_HTML_COMPATIBILITY;
      }
      if (buttonCode == 7) {
        return ControllerInputMappingButtons.BUTTON_RT_HTML_COMPATIBILITY;
      }
      if (buttonCode == 8) {
        return ControllerInputMappingButtons.BUTTON_BACK;
      }
      if (buttonCode == 9) {
        return ControllerInputMappingButtons.BUTTON_START;
      }
    }
    if (buttonCode == 12) {
      return ControllerInputMappingButtons.BUTTON_UP;
    }
    if (buttonCode == 13) {
      return ControllerInputMappingButtons.BUTTON_DOWN;
    }
    if (buttonCode == 14) {
      return ControllerInputMappingButtons.BUTTON_LEFT;
    }
    if (buttonCode == 15) {
      return ControllerInputMappingButtons.BUTTON_RIGHT;
    }
    return ControllerInputMappingButtons.UNKNOWN;
  }

  public static ControllerInputMappingButtons getControllerButton(Controller controller,
      int buttonCode) {
    switch (controller.getName()) {
      case "Microsoft Controller (STANDARD GAMEPAD Vendor: 045e Product: 02ea)":
      case "©Microsoft Corporation Controller (STANDARD GAMEPAD Vendor: 045e Product: 028e)":
        return getControllerButtonHtmlXbox(buttonCode);
      case "Xbox One Wireless Controller (Model 1708)":
      case "X360 Controller":
        return getControllerButtonDesktopXbox(buttonCode);
      default:
        if (Gdx.app.getType() == ApplicationType.Desktop) {
          Gdx.app.debug("controller_input_mapping:getControllerButton",
              MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                  .getName() + "\" on desktop -> use Xbox desktop default bindings");
          return getControllerButtonDesktopXbox(buttonCode);
        } else if (Gdx.app.getType() == ApplicationType.WebGL) {
          Gdx.app.debug("controller_input_mapping:getControllerButton",
              MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                  .getName() + "\" on html -> use Xbox html default bindings");
          return getControllerButtonHtmlXbox(buttonCode);
        } else if (Gdx.app.getType() == ApplicationType.Android) {
          Gdx.app.debug("controller_input_mapping:getControllerButton",
              MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                  .getName() + "\" on android -> use Xbox desktop default bindings");
          return getControllerButtonDesktopXbox(buttonCode);
        }
        Gdx.app.error("controller_input_mapping:getControllerButton",
            MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                .getName() + "\" on unknown platform");
    }
    return ControllerInputMappingButtons.UNKNOWN;
  }

  private static ControllerInputMappingAxes getControllerAxisDesktopXbox(int axisCode) {
    if (axisCode == 0) {
      return ControllerInputMappingAxes.AXIS_LEFT_PAD_HORIZONTAL;
    }
    if (axisCode == 1) {
      return ControllerInputMappingAxes.AXIS_LEFT_PAD_VERTICAL;
    }
    if (axisCode == 2) {
      return ControllerInputMappingAxes.AXIS_RIGHT_PAD_HORIZONTAL;
    }
    if (axisCode == 3) {
      return ControllerInputMappingAxes.AXIS_RIGHT_PAD_VERTICAL;
    }
    if (axisCode == 4) {
      return ControllerInputMappingAxes.AXIS_LT;
    }
    if (axisCode == 5) {
      return ControllerInputMappingAxes.AXIS_RT;
    }
    return ControllerInputMappingAxes.UNKNOWN;
  }

  private static ControllerInputMappingAxes getControllerAxisHtmlXbox(int axisCode) {
    final HtmlPlatformInfo platformInfo = MainGame.getPlatformInfo();

    if (axisCode == 0) {
      return ControllerInputMappingAxes.AXIS_LEFT_PAD_HORIZONTAL;
    }
    if (axisCode == 1) {
      return ControllerInputMappingAxes.AXIS_LEFT_PAD_VERTICAL;
    }
    if (platformInfo != null && platformInfo.isFirefox) {
      if (axisCode == 2) {
        return ControllerInputMappingAxes.AXIS_LT;
      }
    } else {
      if (axisCode == 2) {
        return ControllerInputMappingAxes.AXIS_RIGHT_PAD_HORIZONTAL;
      }
    }
    if (axisCode == 3) {
      return ControllerInputMappingAxes.AXIS_RIGHT_PAD_VERTICAL;
    }
    if (platformInfo != null && platformInfo.isFirefox) {
      if (axisCode == 4) {
        return ControllerInputMappingAxes.AXIS_RIGHT_PAD_HORIZONTAL;
      }
    }

    // Both axis lt and rt are not recognized on the XBox One and 360 controller via the chrome web browser
    if (platformInfo != null && platformInfo.isFirefox) {
      if (axisCode == 5) {
        return ControllerInputMappingAxes.AXIS_RT;
      }
      if (axisCode == 6) {
        return ControllerInputMappingAxes.AXIS_BOTTOM_LEFT_PAD_VERTICAL_HTML_COMPATIBILITY;
      }
      if (axisCode == 7) {
        return ControllerInputMappingAxes.AXIS_BOTTOM_LEFT_PAD_HORIZONTAL_HTML_COMPATIBILITY;
      }
    }
    return ControllerInputMappingAxes.UNKNOWN;
  }

  public static ControllerInputMappingAxes getControllerAxis(Controller controller, int axisCode) {
    switch (controller.getName()) {
      case "Microsoft Controller (STANDARD GAMEPAD Vendor: 045e Product: 02ea)":
      case "©Microsoft Corporation Controller (STANDARD GAMEPAD Vendor: 045e Product: 028e)":
      case "045e-028e-Microsoft X-Box 360 pad":
      case "045e-02ea-Microsoft X-Box One S pad":
        return getControllerAxisHtmlXbox(axisCode);
      case "Xbox One Wireless Controller (Model 1708)":
      case "X360 Controller":
        return getControllerAxisDesktopXbox(axisCode);
      default:
        if (Gdx.app.getType() == ApplicationType.Desktop) {
          Gdx.app.debug("controller_input_mapping:getControllerAxis",
              MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                  .getName() + "\" on desktop -> use Xbox desktop default bindings");
          return getControllerAxisDesktopXbox(axisCode);
        } else if (Gdx.app.getType() == ApplicationType.WebGL) {
          Gdx.app.debug("controller_input_mapping:getControllerAxis",
              MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                  .getName() + "\" on html -> use Xbox html default bindings");
          return getControllerAxisHtmlXbox(axisCode);
        } else if (Gdx.app.getType() == ApplicationType.Android) {
          Gdx.app.debug("controller_input_mapping:getControllerAxis",
              MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                  .getName() + "\" on android -> use Xbox desktop default bindings");
          return getControllerAxisDesktopXbox(axisCode);
        }
        Gdx.app.error("controller_input_mapping:getControllerAxis",
            MainGame.getCurrentTimeStampLogString() + "unknown controller name \"" + controller
                .getName() + "\" on unknown platform");
    }
    return ControllerInputMappingAxes.UNKNOWN;
  }
}
