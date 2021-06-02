package com.mygdx.game.controller.generic.one_click;

import com.mygdx.game.controller.IControllerCallbackGenericGlobalButtons;

/**
 * Callback interface for controller inputs in the credit state
 */
public interface IControllerCallbackGenericOneClick extends
    IControllerCallbackGenericGlobalButtons {

  /**
   * Click any button (besides the full screen toggle button)
   */
  void controllerCallbackClickAnyButton();

}
