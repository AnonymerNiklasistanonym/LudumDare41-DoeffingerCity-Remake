package com.mygdx.game.gamestate.states.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.helper.IHelperMenuButton;

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
	 * Indicator if the menu button is selected
	 */
	private boolean selected;

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
	 * The ID of the button to identify it when pressed or not
	 */
	private final String id;
	/**
	 * The position of the text
	 */
	private final Vector2 textPosition;

	/**
	 * Super constructor for creating a new menu button
	 *
	 * @param id The ID of the button to identify it when pressed or not
	 * @param text The button text
	 * @param font The button text font
	 * @param fontScale The menu button text font scale
	 * @param textureDefault The default texture when the button is not selected
	 * @param textureSelected The texture when the button is selected
	 * @param xPosition X position for rendering
	 * @param yPosition Y position for rendering
	 * @param selected Indicator if the button is currently selected
	 */
	public MenuButton(final String id, final String text, final BitmapFont font, final float fontScale, final Texture textureDefault, final Texture textureSelected, final float xPosition, final float yPosition,
			final boolean selected) {
		this.id = id;
		this.text = text;
		this.font = font;

		this.textureSelected = textureSelected;
		this.textureDefault = textureDefault;

		this.selected = selected;

		// Create new button sprite with the given information
		this.button = new Sprite(this.selected ? textureSelected : textureDefault);
		this.button.setSize(textureSelected.getWidth(), textureSelected.getHeight());
		this.button.setPosition(xPosition - this.button.getWidth() / 2, yPosition - this.button.getHeight() / 2);

		// Calculate the text position and set the font scale
		this.font.getData().setScale(fontScale);
		this.textPosition = GameStateManager.calculateCenteredTextPosition(this.font, this.text, xPosition * 2, yPosition * 2);
	}

	/**
	 * Overload constructor so that it can be called without the button being selected
	 *
	 * @param id The ID of the button to identify it when pressed or not
	 * @param text The button text
	 * @param font The button text font
	 * @param fontScale The menu button text font scale
	 * @param textureDefault The default texture when the button is not selected
	 * @param textureSelected The texture when the button is selected
	 * @param xPosition X position for rendering
	 * @param yPosition Y position for rendering
	 */
	public MenuButton(final String id, final String text, final BitmapFont font, final float fontScale, final Texture textureDefault, final Texture textureSelected, final float xPosition, final float yPosition) {
		this(id, text, font, fontScale, textureSelected, textureDefault, xPosition, yPosition, false);
	}

	/**
	 * Set if the button is currently selected or not
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
		this.button.setTexture(this.selected ? this.textureSelected : this.textureDefault);
	}

	/**
	 * Get if the button is currently selected
	 *
	 * @return True if currently selected
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * Add the button draw call to a given libGDX sprite batch
	 *
	 * @param spriteBatch libGDX sprite batch to which the draw call should be added
	 */
	public void draw(final SpriteBatch spriteBatch) {
		// Draw the button (texture)
		if (this.selected) {
			this.button.setAlpha(1.0f);
		} else {
			this.button.setAlpha(0.25f);
		}
		this.button.draw(spriteBatch);

		// Draw the text on top of the button
		this.font.draw(spriteBatch, this.text, this.textPosition.x, this.textPosition.y);
	}

	/**
	 * Get if the given cursor position is on the button
	 *
	 * @param cursorPosition The current cursor position on the canvas
	 * @return True if cursor position is on the button
	 */
	public boolean contains(final Vector3 cursorPosition) {
		return (cursorPosition.x > this.button.getX() && cursorPosition.x < this.button.getX() + this.button.getWidth())
				&& (cursorPosition.y > this.button.getY() && cursorPosition.y < this.button.getY() + this.button.getHeight());
	}

	/**
	 * Get the button ID
	 *
	 * @return The ID of the button
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Dispose textures and fonts
	 * TODO Asset manager migration later? (https://github.com/libgdx/libgdx/wiki/Managing-your-assets)
	 */
	public void disposeMedia() {
		this.textureSelected.dispose();
		this.textureDefault.dispose();
		this.font.dispose();
	}

}
