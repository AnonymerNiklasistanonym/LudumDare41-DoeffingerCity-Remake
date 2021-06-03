package com.mygdx.game.gamestate.states.elements;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.helper.menu.IHelperMenuButton;

/**
 * Abstract class for menu buttons
 */
public abstract class MenuButton implements Disposable, IHelperMenuButton {

  /**
   * The texture displayed when the button is selected
   */
  private final Texture textureSelected;
  /**
   * The default texture displayed when not selected
   */
  private final Texture textureDefault;
  /**
   * libGDX sprite for the button
   */
  private final Sprite button;
  /**
   * The menu button text
   */
  private final String text;
  /**
   * The menu button text font
   */
  private final BitmapFont font;
  /**
   * The menu button text font scale
   */
  private final float fontScale;
  /**
   * The ID of the button to identify it when pressed or not
   */
  private final String id;
  /**
   * The position of the text
   */
  private final Vector2 textPosition;
  /**
   * Indicator if the menu button is selected
   */
  private boolean selected;

  /**
   * Super constructor for creating a new menu button
   *
   * @param id                            The ID of the button to identify it when pressed or not
   * @param text                          The button text
   * @param assetManager                  Asset manager that contains the font and texture
   *                                      resources
   * @param assetManagerIdFont            The asset manager ID for the text font
   * @param fontScale                     The menu button text font scale
   * @param assetManagerIdTextureDefault  The asset manager ID for the texture of a button
   * @param assetManagerIdTextureSelected The asset manager ID for the texture of a selected button
   * @param xPosition                     X position for rendering
   * @param yPosition                     Y position for rendering
   * @param selected                      Indicator if the button is currently selected
   */
  public MenuButton(final String id, final String text, final AssetManager assetManager,
      final String assetManagerIdFont, final float fontScale,
      final String assetManagerIdTextureDefault, final String assetManagerIdTextureSelected,
      final float xPosition, final float yPosition, final boolean selected) {
    this.id = id;
    this.text = text;
    this.selected = selected;
    this.fontScale = fontScale;

    // Get assets
    font = assetManager.get(assetManagerIdFont);
    textureDefault = assetManager.get(assetManagerIdTextureDefault);
    textureSelected = assetManager.get(assetManagerIdTextureSelected);

    // Create new button sprite with the given information
    button = new Sprite(this.selected ? textureSelected : textureDefault);
    button.setSize(textureSelected.getWidth(), textureSelected.getHeight());
    button.setPosition(xPosition - button.getWidth() / 2,
        yPosition - button.getHeight() / 2);

    // Calculate the text position and set the font scale
    font.getData().setScale(fontScale);
    textPosition = GameStateManager
        .calculateCenteredTextPosition(font, this.text, xPosition * 2, yPosition * 2);
  }

  /**
   * Get if the button is currently selected
   *
   * @return True if currently selected
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * Set if the button is currently selected or not
   */
  public void setSelected(final boolean selected) {
    this.selected = selected;
    button.setTexture(this.selected ? textureSelected : textureDefault);
  }

  /**
   * Add the button draw call to a given libGDX sprite batch
   *
   * @param spriteBatch libGDX sprite batch to which the draw call should be added
   */
  public void draw(final SpriteBatch spriteBatch) {
    // Draw the button (texture)
    if (selected) {
      button.setAlpha(1.0f);
    } else {
      button.setAlpha(0.25f);
    }
    button.draw(spriteBatch);

    // Draw the text on top of the button
    font.getData().setScale(fontScale);
    font.draw(spriteBatch, text, textPosition.x, textPosition.y);
  }

  /**
   * Get if the given cursor position is on the button
   *
   * @param cursorPosition The current cursor position on the canvas
   * @return True if cursor position is on the button
   */
  public boolean contains(final Vector3 cursorPosition) {
    return (cursorPosition.x > button.getX()
        && cursorPosition.x < button.getX() + button.getWidth())
        && (cursorPosition.y > button.getY()
        && cursorPosition.y < button.getY() + button.getHeight());
  }

  /**
   * Get the button ID
   *
   * @return The ID of the button
   */
  public String getId() {
    return id;
  }

  /**
   * Dispose textures and fonts TODO Asset manager migration later? (https://github.com/libgdx/libgdx/wiki/Managing-your-assets)
   */
  public void disposeMedia() {
    // Dispose loaded assets
    textureSelected.dispose();
    textureDefault.dispose();
    font.dispose();
  }

}
