package com.mygdx.game.helper;

/**
 * Menu Button mockup for menu navigation implementation with multiple menu buttons in a grid ([][])
 */
public interface IHelperMenuButton {

  /**
   * Get the button ID
   *
   * @return The ID of the button
   */
  String getId();
  /**
   * Get if the button is currently selected
   *
   * @return True if currently selected
   */
  boolean isSelected();
  /**
   * Set if the button is currently selected or not
   */
  void setSelected(boolean selected);

}
