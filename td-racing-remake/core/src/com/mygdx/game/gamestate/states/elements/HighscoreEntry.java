package com.mygdx.game.gamestate.states.elements;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame;

public class HighscoreEntry implements Disposable {

  public static final String ASSET_MANAGER_ID_FONT = MainGame
      .getGameFontFilePath("cornerstone_upper_case_big");
  public static final String ASSET_MANAGER_ID_TEXTURE = MainGame.getGameButtonFilePath("highscore");
  private static final float fontScale = 0.5f;

  public static Texture texture;
  public static BitmapFont fontText;
  private final Sprite spriteEntry;
  private final String name;
  private final int place, score, level;
  private final float fontXNumber, fontYNumber, fontXName, fontYName, fontXScore, fontYScore, fontXLevel, fontYLevel;
  private final String textPlace, textName, textScore, textLevel;

  public HighscoreEntry(final int place, final int score, final int level, final String name,
      final AssetManager assetManager,
      final float xPosition, final float yPosition) {
    this.place = place;
    this.score = score;
    this.level = level;
    this.name = name;

    fontText = assetManager.get(ASSET_MANAGER_ID_FONT, BitmapFont.class);
    fontText.setUseIntegerPositions(false);
    texture = assetManager.get(ASSET_MANAGER_ID_TEXTURE, Texture.class);
    spriteEntry = new Sprite(texture);
    spriteEntry.setSize((float) texture.getWidth() / 2, (float) texture.getHeight() / 2);
    spriteEntry
        .setPosition(xPosition - spriteEntry.getWidth() / 2,
            yPosition - spriteEntry.getHeight() / 2);
    final float yPositionText = yPosition + spriteEntry.getHeight() / 5 * 2;
    fontXNumber = xPosition - spriteEntry.getWidth() / 2 + 20;
    fontYNumber = yPositionText;
    fontXName = fontXNumber + (float) MainGame.GAME_WIDTH / 15;
    fontYName = yPositionText;
    fontXScore = fontXName + (float) MainGame.GAME_WIDTH / 4.5f;
    fontYScore = yPositionText;
    fontXLevel = fontXScore + (float) MainGame.GAME_WIDTH / 3.75f;
    fontYLevel = yPositionText;

    textPlace = "" + place + ".";
    textName = name;
    textScore = "" + (name.equals("------") ? "-" : score);
    textLevel = "(LEVEL " + (name.equals("------") ? "-" : (level + 1)) + ")";
  }

  public void draw(final SpriteBatch spriteBatch) {
    spriteEntry.draw(spriteBatch);
    fontText.getData().setScale(fontScale);
    fontText.draw(spriteBatch, textPlace, fontXNumber, fontYNumber);
    fontText.draw(spriteBatch, textName, fontXName, fontYName);
    // TODO Make font scale different when score exceeds a high value
    fontText.draw(spriteBatch, textScore, fontXScore, fontYScore);
    fontText.getData().setScale(fontScale / 3);
    fontText.draw(spriteBatch, textLevel, fontXLevel, fontYLevel);
  }

  @Override
  public void dispose() {
    // Dispose loaded assets
    spriteEntry.getTexture().dispose();
    fontText.dispose();
  }

}
