package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainGame extends ApplicationAdapter {
    /**
     * Height of the game screen (the window)
     */
    public final static int GAME_HEIGHT = 720;
    /**
     * Width of the game screen (the window)
     */
    public final static int GAME_WIDTH = 1280;
    /**
     * Name of the game
     */
    public final static String GAME_NAME = "TnT (Tracks `n Towers)";
    /**
     * The provided icon sizes
     */
    public final static int[] GAME_ICON_SIZES = {16, 32, 64};
    SpriteBatch batch;
    Texture img;

    /**
     * Get the filepath of a game icon given its size
     *
     * @param IconSize The size of the game icon
     */
    public static String getGameIconFilePath(int IconSize) {
        return "icon/icon_" + IconSize + ".png";
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
