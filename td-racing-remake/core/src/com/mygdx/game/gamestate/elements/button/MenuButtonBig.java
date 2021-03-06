package com.mygdx.game.gamestate.elements.button;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.MainGame;

public class MenuButtonBig extends MenuButton {

  public static final String ASSET_MANAGER_ID_FONT = MainGame
      .getGameFontFilePath("cornerstone_big");
  public static final String ASSET_MANAGER_ID_TEXTURE_DEFAULT = MainGame
      .getGameButtonFilePath("menu_not_active");
  public static final String ASSET_MANAGER_ID_TEXTURE_SELECTED = MainGame
      .getGameButtonFilePath("menu_active");

  private static final float fontScale = 1;

  public MenuButtonBig(final String id, final String text, final AssetManager assetManager,
      final float xPosition, final float yPosition, final boolean activated) {
    super(id, text, assetManager, ASSET_MANAGER_ID_FONT, fontScale,
        ASSET_MANAGER_ID_TEXTURE_DEFAULT, ASSET_MANAGER_ID_TEXTURE_SELECTED, xPosition, yPosition,
        activated);
  }

  public MenuButtonBig(final String id, final String text, final AssetManager assetManager,
      final float xPosition, final float yPosition) {
    this(id, text, assetManager, xPosition, yPosition, false);
  }

  @Override
  public void dispose() {
    super.disposeMedia();
  }

}
