package com.mygdx.game.controller.create_highscore_entry;

import com.mygdx.game.controller.IControllerCallbackGenericGlobalButtons;
import com.mygdx.game.controller.generic.menu_button_grid.NextMenuButtonDirection;

/**
 * Callback interface for controller inputs in the menu state
 */
public interface IControllerCallbackCreateHighscoreEntryState extends
    IControllerCallbackGenericGlobalButtons {

  /**
   * Select a character entry
   *
   * @param direction The direction in which the next character entry was selected
   */
  void controllerCallbackSelectCharacterEntry(NextCharacterEntryDirection direction);

  /**
   * Change a character in the currently selected character entry
   *
   * @param direction The direction in which the character was changed
   */
  void controllerCallbackChangeCharacterEntry(ChangeCharacterDirection direction);

  /**
   * Accept the highscore entry and go to the next state
   */
  void controllerCallbackClickSelect();

  /**
   * Go back because a back button was pressed
   */
  void controllerCallbackClickBackButton();
}
