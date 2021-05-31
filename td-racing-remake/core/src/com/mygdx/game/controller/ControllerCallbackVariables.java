package com.mygdx.game.controller;

/**
 * Controller callback variables
 */
public abstract class ControllerCallbackVariables {
  /**
   * Tracker if any controller key was pressed (besides the full screen, music and sound effect button)
   */
  protected boolean controllerAnyKeyWasPressed = false;
  /**
   * Tracker if a controller down key was pressed
   */
  protected boolean controllerDownKeyWasPressed = false;
  /**
   * Tracker if a controller up key was pressed
   */
  protected boolean controllerUpKeyWasPressed = false;
  /**
   * Tracker if a controller left key was pressed
   */
  protected boolean controllerLeftKeyWasPressed = false;
  /**
   * Tracker if a controller right key was pressed
   */
  protected boolean controllerRightKeyWasPressed = false;
  /**
   * Tracker if a controller selection key was pressed
   */
  protected boolean controllerSelectKeyWasPressed = false;
  /**
   * Tracker if a controller start key was pressed
   */
  protected boolean controllerStartKeyWasPressed = false;
  /**
   * Tracker if a controller back key was pressed
   */
  protected boolean controllerBackKeyWasPressed = false;
  /**
   * Tracker if a controller full screen toggle key was pressed
   */
  protected boolean controllerToggleFullScreenPressed = false;
  /**
   * Tracker if a controller music toggle key was pressed
   */
  protected boolean controllerToggleMusicPressed = false;
  /**
   * Tracker if a controller sound effects toggle key was pressed
   */
  protected boolean controllerToggleSoundEffectsPressed = false;
}
