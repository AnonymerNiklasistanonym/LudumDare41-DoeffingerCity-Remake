package com.mygdx.game.entities.towers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Tower;
import com.mygdx.game.entities.TowerOptions;
import com.mygdx.game.entities.Zombie;

public class SniperTower extends Tower {

  public static final String ASSET_ID_TEXTURE_BOTTOM = MainGame
      .getGameTowerFilePath("sniper_bottom");
  public static final String ASSET_ID_TEXTURE_UPPER = MainGame.getGameTowerFilePath("sniper_upper");
  public static final String ASSET_ID_TEXTURE_FIRING = MainGame
      .getGameTowerFilePath("sniper_firing");
  public static final String ASSET_ID_SOUND_SHOOT = MainGame.getGameSoundFilePath("tower_sniper");
  public static final int COST = 400;
  public static final int RANGE = 25;
  private static final String TOWER_NAME = "Sniper";
  private static final float POWER_SHOOT = 40;
  private static final float SPEED_SHOOT = 5;
  private static final float SPEED_TURN = 30;

  private static final TowerOptions TOWER_OPTIONS = getTowerOptions();

  public SniperTower(final Vector2 position, final Array<Zombie> zombies, final World world,
      final AssetManager assetManager) {
    super(TOWER_NAME, position, COST, RANGE, POWER_SHOOT, SPEED_SHOOT, SPEED_TURN,
        assetManager, ASSET_ID_TEXTURE_BOTTOM, ASSET_ID_TEXTURE_UPPER,
        ASSET_ID_TEXTURE_FIRING, ASSET_ID_SOUND_SHOOT, world, zombies, TOWER_OPTIONS);
  }

  private static TowerOptions getTowerOptions() {
    final TowerOptions towerOptions = new TowerOptions();
    towerOptions.rangeColor = new Color(0.5f, 0.1f, 0.7f, 0.3f);
    towerOptions.firingSpriteTime = 0.5f;
    towerOptions.volumeSoundShoot = 0.25f;
    return towerOptions;
  }

  @Override
  protected void drawProjectile(final ShapeRenderer shapeRenderer) {
    shapeRenderer.setColor(Color.ORANGE);
    shapeRenderer.rectLine(center, shotposition, 0.2f);
  }

  @Override
  protected void disposeTowerResources() {
    // Nothing to dispose
  }

  @Override
  protected void drawProjectile(final SpriteBatch spriteBatch) {
    // No sprite or else to draw
  }
}