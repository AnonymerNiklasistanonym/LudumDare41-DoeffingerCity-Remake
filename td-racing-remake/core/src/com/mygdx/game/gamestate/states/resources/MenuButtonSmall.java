package com.mygdx.game.gamestate.states.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.game.MainGame;

public class MenuButtonSmall extends MenuButton {

	public MenuButtonSmall(String id, String text, Texture textureDefault, Texture textureSelected, float xPosition, float yPosition, boolean activated) {
		super(id, text, new BitmapFont(Gdx.files.internal(MainGame.getGameFontFilePath("cornerstone_big"))), 0.8f, textureDefault, textureSelected, xPosition, yPosition, activated);
	}

	public MenuButtonSmall(String id, String text, Texture textureDefault, Texture textureSelected, float xPosition, float yPosition) {
		super(id, text, new BitmapFont(Gdx.files.internal(MainGame.getGameFontFilePath("cornerstone_big"))), 0.8f, textureDefault, textureSelected, xPosition, yPosition);
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}
}
