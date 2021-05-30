package com.mygdx.game.controller.one_click;

/**
 * Callback interface for controller inputs in the credit state
 */
public interface IControllerCallbackGenericOneClick {

  /**
   * Click any button (besides the full screen toggle button)
   */
  void controllerCallbackClickAnyButton();

  /**
   * Toggle full screen
   */
  void controllerCallbackToggleFullScreen();

}
