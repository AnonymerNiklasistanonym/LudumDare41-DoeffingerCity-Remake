package com.mygdx.game.gamestate.states.elements;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame;

public class HighscoreEntry implements Disposable {

  public static final String ASSET_MANAGER_ID_FONT = MainGame.getGameFontFilePath("cornerstone_upper_case_big");
  public static final String ASSET_MANAGER_ID_TEXTURE = MainGame.getGameButtonFilePath("highscore");
  private static final float fontScale = 0.5f;

  public static Texture texture;
  public static BitmapFont fontText;
  private final Sprite spriteEntry;
  private final String name;
  private final int place, score;
  private final float fontXNumber, fontYNumber, fontXName, fontYName, fontXScore, fontYScore;

  public HighscoreEntry(final int place, final int score, final String name,
      final AssetManager assetManager,
      final float xPosition, final float yPosition) {
    this.place = place;
    this.score = score;
    this.name = name;

    fontText = assetManager.get(ASSET_MANAGER_ID_FONT, BitmapFont.class);
    fontText.setUseIntegerPositions(false);
    texture = assetManager.get(ASSET_MANAGER_ID_TEXTURE, Texture.class);
    spriteEntry = new Sprite(texture);
    spriteEntry.setSize((float) texture.getWidth() / 2, (float) texture.getHeight() / 2);
    spriteEntry
        .setPosition(xPosition - spriteEntry.getWidth() / 2,
            yPosition - spriteEntry.getHeight() / 2);
    fontXNumber = xPosition - spriteEntry.getWidth() / 2 + 20;
    fontYNumber = yPosition + spriteEntry.getHeight() / 5 * 2;
    fontXName = xPosition - spriteEntry.getWidth() / 9 * 3 - 10;
    fontYName = yPosition + spriteEntry.getHeight() / 5 * 2;
    fontXScore = xPosition + spriteEntry.getWidth() / 9;
    fontYScore = yPosition + spriteEntry.getHeight() / 5 * 2;
  }

  public void draw(final SpriteBatch spriteBatch) {
    spriteEntry.draw(spriteBatch);
    fontText.getData().setScale(fontScale);
    fontText.draw(spriteBatch, "" + place, fontXNumber, fontYNumber);
    fontText.draw(spriteBatch, name, fontXName, fontYName);
    fontText.draw(spriteBatch, "" + score, fontXScore, fontYScore);
  }

  @Override
  public void dispose() {
    // Dispose loaded assets
    spriteEntry.getTexture().dispose();
    fontText.dispose();
  }

}
