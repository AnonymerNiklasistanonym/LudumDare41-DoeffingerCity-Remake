package com.mygdx.game.gamestate.states.elements;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameStateManager;

public class HighscoreSelectCharacterDisplay {

  public static final String ASSET_MANAGER_ID_CHARACTER_FONT = MainGame
      .getGameFontFilePath("cornerstone_big");
  private static final float BUTTON_INPUT_ANIMATION_TIME_IN_S = (float) 1 / 10;
  private static final float fontScaleCharacter = 1;
  private final Vector2 position;
  private final float x1, x2, x3, y1up, y1down, y23up, y23down;
  private char selectedCharacter;
  private HighscoreSelectCharacterDisplayInputState state;
  private final BitmapFont fontCharacter;
  private float elapsedAnimationTimeInS = 0;

  public HighscoreSelectCharacterDisplay(final char defaultCharacter,
      final AssetManager assetManager, final float xPosition, final float yPosition,
      final HighscoreSelectCharacterDisplayInputState state) {
    selectedCharacter = defaultCharacter;
    this.state = state;

    fontCharacter = assetManager.get(ASSET_MANAGER_ID_CHARACTER_FONT);
    fontCharacter.getData().setScale(fontScaleCharacter);
    position = GameStateManager
        .calculateCenteredTextPosition(fontCharacter, "" + selectedCharacter, xPosition * 2,
            yPosition * 2);

    x1 = position.x + 30f;
    x2 = x1 - 35f;
    x3 = x1 + 35f;
    y1up = position.y + 70f;
    y1down = position.y - 150f;
    y23up = y1up - 20f;
    y23down = y1down + 20f;
  }

  public void update(final float deltaTime) {
    // Calculate how long the button should be highlighted if down or up was pressed
    if (state == HighscoreSelectCharacterDisplayInputState.DOWN
        || state == HighscoreSelectCharacterDisplayInputState.UP) {
      elapsedAnimationTimeInS += deltaTime;
      if (elapsedAnimationTimeInS > BUTTON_INPUT_ANIMATION_TIME_IN_S) {
        state = HighscoreSelectCharacterDisplayInputState.ACTIVE;
        elapsedAnimationTimeInS = 0;
      }
    }
  }

  public void setUpDownInput(final HighscoreSelectCharacterDisplayInputState state) {
    this.state = state;
  }

  public char getCurrentSelectedCharacter() {
    return selectedCharacter;
  }

  public void setNewCharacter(final char selectedCharacter) {
    this.selectedCharacter = selectedCharacter;
  }

  public void draw(final SpriteBatch spriteBatch) {
    fontCharacter.draw(spriteBatch, "" + selectedCharacter, position.x, position.y);
  }

  public void drawUpDownInput(ShapeRenderer shapeRenderer) {
    if (state != HighscoreSelectCharacterDisplayInputState.NOT_ACTIVE) {
      shapeRenderer.setColor((state == HighscoreSelectCharacterDisplayInputState.UP) ? 0 : 1,
          (state == HighscoreSelectCharacterDisplayInputState.UP) ? 0 : 1, 1, 1);
      shapeRenderer.triangle(x1, y1up, x2, y23up, x3, y23up);
      shapeRenderer.setColor((state == HighscoreSelectCharacterDisplayInputState.DOWN) ? 0 : 1,
          (state == HighscoreSelectCharacterDisplayInputState.DOWN) ? 0 : 1, 1, 1);
      shapeRenderer.triangle(x1, y1down, x2, y23down, x3, y23down);
    }
  }

}
