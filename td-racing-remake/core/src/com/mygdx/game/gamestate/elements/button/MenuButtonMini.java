package com.mygdx.game.gamestate.elements.button;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.MainGame;

public class MenuButtonMini extends MenuButton {

  public static final String ASSET_MANAGER_ID_FONT = MainGame
      .getGameFontFilePath("cornerstone_big");
  public static final String ASSET_MANAGER_ID_TEXTURE_DEFAULT = MainGame
      .getGameButtonFilePath("menu_not_active_mini");
  public static final String ASSET_MANAGER_ID_TEXTURE_SELECTED = MainGame
      .getGameButtonFilePath("menu_active_mini");

  private static final float FONT_SCALE = 0.4f;

  public MenuButtonMini(final String id, final String text, final AssetManager assetManager,
      final float xPosition, final float yPosition, final boolean activated) {
    super(id, text, assetManager, ASSET_MANAGER_ID_FONT, FONT_SCALE,
        ASSET_MANAGER_ID_TEXTURE_DEFAULT, ASSET_MANAGER_ID_TEXTURE_SELECTED, xPosition, yPosition,
        activated);
  }

  public MenuButtonMini(final String id, final String text, final AssetManager assetManager,
      final float xPosition, final float yPosition) {
    this(id, text, assetManager, xPosition, yPosition, false);
  }

  @Override
  protected void disposeButtonResources() {
    // Nothing to dispose
  }
}
