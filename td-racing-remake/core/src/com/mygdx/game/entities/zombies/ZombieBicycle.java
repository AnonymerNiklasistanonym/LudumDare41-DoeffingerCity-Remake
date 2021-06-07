package com.mygdx.game.entities.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.entities.ZombieOptions;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.world.Map;
import com.mygdx.game.world.Node;

public class ZombieBicycle extends Zombie {

  public static final String ASSET_ID_TEXTURE_ALIVE = MainGame.getGameZombieFilePath("bicycle");
  public static final String ASSET_ID_TEXTURE_DEAD = MainGame.getGameZombieFilePath("bicycle_dead");
  public static final String ASSET_ID_TEXTURE_DAMAGE = MainGame.getGameZombieFilePath("blood");
  private static final float DAMAGE = 2;
  private static final float HEALTH = 15;
  private static final float MONEY = 2;
  private static final float SPEED = 15;
  private static final float SCORE = 20;
  private static final float DENSITY = 5f;
  private static final String ENEMY_NAME = "Bicycle";

  private static final ZombieOptions zombieOptions = getZombieOptions();

  public ZombieBicycle(final Vector2 position, final World world, final AssetManager assetManager,
      final Map map, final float spawnTimeStamp) {
    super(ENEMY_NAME, position, DAMAGE, HEALTH, MONEY, SCORE, spawnTimeStamp, SPEED, world,
        assetManager, ASSET_ID_TEXTURE_ALIVE, ASSET_ID_TEXTURE_DEAD, ASSET_ID_TEXTURE_DAMAGE, map,
        zombieOptions);
  }

  private static ZombieOptions getZombieOptions() {
    final ZombieOptions zombieOptions = new ZombieOptions();
    zombieOptions.showHealthBar = true;
    return zombieOptions;
  }

  @Override
  protected FixtureDef createFixture() {
    final PolygonShape zBox = new PolygonShape();
    zBox.setAsBox(textureAlive.getWidth() * PlayState.PIXEL_TO_METER * 0.4f,
        textureAlive.getHeight() * PlayState.PIXEL_TO_METER * 0.4f);
    final FixtureDef fdef = new FixtureDef();
    fdef.shape = zBox;
    fdef.density = DENSITY;
    // fdef.isSensor=true;
    fdef.filter.categoryBits = PlayState.PLAYER_BOX;
    return fdef;
  }

  @Override
  protected void disposeZombieResources() {
    // Nothing to dispose
  }

  @Override
  protected Array<Node> findPath() {
    return map.getRandomMotorPath();
  }
}
