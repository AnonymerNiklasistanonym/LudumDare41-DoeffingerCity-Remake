package com.mygdx.game.helper.menu;

import com.mygdx.game.helper.HelperUtil;

public class HelperMenu {
  public static String selectNextButton(final IHelperMenuButton[][] menuButtons, final HelperMenuButtonNavigation key, final String lastId) {
    switch (key) {
      case DOWN:
        for (int i = 0; i < menuButtons.length; i++) {
          for (int j = 0; j < menuButtons[i].length; j++) {
            // If the menu button that is selected was found
            if (menuButtons[i][j].isSelected()) {
              // Deselect it
              menuButtons[i][j].setSelected(false);
              // And select the button in the row below (or if its the last row the row top above)
              final int menuButtonRowBelowIndex = HelperUtil
                  .moduloWithPositiveReturnValues(i + 1, menuButtons.length);
              // Check if in this row there is the previously selected button id
              if (lastId != null) {
                for (int k = 0; k < menuButtons[menuButtonRowBelowIndex].length; k++) {
                  if (menuButtons[menuButtonRowBelowIndex][k].getId().equals(lastId)) {
                    menuButtons[menuButtonRowBelowIndex][k].setSelected(true);
                    return menuButtons[i][j].getId();
                  }
                }
              }
              // If not or if the ID was null just follow the default algorithm
              final int menuButtonIndexInRowBelow = HelperUtil
                  .moduloWithPositiveReturnValues(j, menuButtons[menuButtonRowBelowIndex].length);
              menuButtons[menuButtonRowBelowIndex][menuButtonIndexInRowBelow].setSelected(true);
              return menuButtons[i][j].getId();
            }
          }
        }
      case UP:
        for (int i = 0; i < menuButtons.length; i++) {
          for (int j = 0; j < menuButtons[i].length; j++) {
            // If the menu button that is selected was found
            if (menuButtons[i][j].isSelected()) {
              // Deselect it
              menuButtons[i][j].setSelected(false);
              // And select the button in the row above (or if its the first row the row down below)
              final int menuButtonRowAboveIndex = HelperUtil
                  .moduloWithPositiveReturnValues(i - 1, menuButtons.length);
              // Check if in this row there is the previously selected button id
              if (lastId != null) {
                for (int k = 0; k < menuButtons[menuButtonRowAboveIndex].length; k++) {
                  if (menuButtons[menuButtonRowAboveIndex][k].getId().equals(lastId)) {
                    menuButtons[menuButtonRowAboveIndex][k].setSelected(true);
                    return menuButtons[i][j].getId();
                  }
                }
              }
              // If not or if the ID was null just follow the default algorithm
              final int menuButtonIndexInRowAbove = HelperUtil
                  .moduloWithPositiveReturnValues(j, menuButtons[menuButtonRowAboveIndex].length);
              menuButtons[menuButtonRowAboveIndex][menuButtonIndexInRowAbove].setSelected(true);
              return menuButtons[i][j].getId();
            }
          }
        }
      case LEFT:
        for (int i = 0; i < menuButtons.length; i++) {
          for (int j = 0; j < menuButtons[i].length; j++) {
            // If the menu button that is selected was found
            if (menuButtons[i][j].isSelected()) {
              // Deselect it
              menuButtons[i][j].setSelected(false);
              // Now either select the previous button in the same row or if it was the first select
              // the last button in the row above
              if (j != 0) {
                menuButtons[i][j - 1].setSelected(true);
              } else {
                final int menuButtonRowAboveIndex = HelperUtil
                    .moduloWithPositiveReturnValues(i - 1, menuButtons.length);
                menuButtons[menuButtonRowAboveIndex][menuButtons[menuButtonRowAboveIndex].length - 1].setSelected(true);
              }
              return menuButtons[i][j].getId();
            }
          }
        }
      case RIGHT:
        for (int i = 0; i < menuButtons.length; i++) {
          for (int j = 0; j < menuButtons[i].length; j++) {
            // If the menu button that is selected was found
            if (menuButtons[i][j].isSelected()) {
              // Deselect it
              menuButtons[i][j].setSelected(false);
              // Now either select the next button in the same row or if it was the last select the
              // first button in the row below
              if (j != menuButtons[i].length - 1) {
                menuButtons[i][j + 1].setSelected(true);
              } else {
                final int menuButtonRowBelowIndex = HelperUtil
                    .moduloWithPositiveReturnValues(i + 1, menuButtons.length);
                menuButtons[menuButtonRowBelowIndex][0].setSelected(true);
              }
              return menuButtons[i][j].getId();
            }
          }
        }
    }
    return null;
  }
}
