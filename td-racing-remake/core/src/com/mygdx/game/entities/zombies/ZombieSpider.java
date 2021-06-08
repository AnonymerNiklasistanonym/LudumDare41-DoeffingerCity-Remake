package com.mygdx.game.entities.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.entities.ZombieCallbackInterface;
import com.mygdx.game.entities.ZombieOptions;
import com.mygdx.game.world.Map;

public class ZombieSpider extends Zombie {

  public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("spider");
  public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("spider_dead");
  public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame
      .getGameZombieFilePath("blood_green");
  private static final float DAMAGE = 1;
  private static final float HEALTH = 2;
  private static final float MONEY = 10;
  private static final float SPEED = 10;
  private static final float SCORE = 50;
  private static final String ENEMY_NAME = "Spider";

  private static final ZombieOptions zombieOptions = new ZombieOptions();

  public ZombieSpider(final Vector2 position, final World world, final AssetManager assetManager,
      final Map map, final float spawnTimeStamp, final ZombieCallbackInterface callbackInterface) {
    super(ENEMY_NAME, position, DAMAGE, HEALTH, MONEY, SCORE, spawnTimeStamp, SPEED, world,
        assetManager, ASSET_ID_TEXTURE_ALIVE, ASSET_ID_TEXTURE_DEAD, ASSET_ID_TEXTURE_DAMAGE, map,
        callbackInterface, zombieOptions);
  }

  @Override
  protected void disposeZombieResources() {
    // Nothing to dispose
  }
}
