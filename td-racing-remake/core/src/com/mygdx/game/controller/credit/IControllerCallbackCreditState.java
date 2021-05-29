package com.mygdx.game.controller.credit;

/**
 * Callback interface for controller inputs in the credit state
 */
public interface IControllerCallbackCreditState {

  /**
   * Click any button (besides the full screen toggle button)
   */
  void controllerCallbackClickAnyButton();

  /**
   * Toggle full screen
   */
  void controllerCallbackToggleFullScreen();

}
