package com.mygdx.game.entities.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.entities.ZombieCallbackInterface;
import com.mygdx.game.entities.ZombieOptions;
import com.mygdx.game.world.Map;

public class ZombieFat extends Zombie {

  public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("fat");
  public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("fat_dead");
  public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame.getGameZombieFilePath("blood");
  private static final float DAMAGE = 5;
  private static final float HEALTH = 100;
  private static final float MONEY = 4;
  private static final float SPEED = 1f;
  private static final float SCORE = 40;
  private static final String ENEMY_NAME = "Fat";

  private static final ZombieOptions zombieOptions = getZombieOptions();

  public ZombieFat(final Vector2 position, final World world, final AssetManager assetManager,
      final Map map, final float spawnTimeStamp, final ZombieCallbackInterface callbackInterface) {
    super(ENEMY_NAME, position, DAMAGE, HEALTH, MONEY, SCORE, spawnTimeStamp, SPEED, world,
        assetManager, ASSET_ID_TEXTURE_ALIVE, ASSET_ID_TEXTURE_DEAD, ASSET_ID_TEXTURE_DAMAGE, map,
        callbackInterface, zombieOptions);
  }

  private static ZombieOptions getZombieOptions() {
    final ZombieOptions zombieOptions = new ZombieOptions();
    zombieOptions.showHealthBar = true;
    zombieOptions.density = 4;
    return zombieOptions;
  }

  @Override
  protected void disposeZombieResources() {
    // Nothing to dispose
  }
}
