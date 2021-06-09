package com.mygdx.game.entities.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.entities.ZombieCallbackInterface;
import com.mygdx.game.entities.ZombieOptions;
import com.mygdx.game.world.Map;

public class ZombieLincoln extends Zombie {

  public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("lincoln");
  public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("lincoln_dead");
  public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame.getGameZombieFilePath("blood");
  private static final float DAMAGE = 40;
  private static final float HEALTH = 3000;
  private static final float MONEY = 10000;
  private static final float SPEED = 2f;
  private static final float SCORE = 1000;
  private static final String ENEMY_NAME = "Lincoln";

  private static final ZombieOptions zombieOptions = getZombieOptions();

  public ZombieLincoln(final Vector2 position, final World world, final AssetManager assetManager,
      final Map map, final float spawnTimeStamp, final ZombieCallbackInterface callbackInterface,
      final String extra) {
    super(ENEMY_NAME + extra, position, DAMAGE, HEALTH, MONEY, SCORE, spawnTimeStamp, SPEED,
        world, assetManager, ASSET_ID_TEXTURE_ALIVE, ASSET_ID_TEXTURE_DEAD, ASSET_ID_TEXTURE_DAMAGE,
        map, callbackInterface, zombieOptions);
  }

  private static ZombieOptions getZombieOptions() {
    final ZombieOptions zombieOptions = new ZombieOptions();
    zombieOptions.showHealthBar = true;
    return zombieOptions;
  }

  @Override
  protected void disposeZombieResources() {
    // Nothing to dispose
  }
}
