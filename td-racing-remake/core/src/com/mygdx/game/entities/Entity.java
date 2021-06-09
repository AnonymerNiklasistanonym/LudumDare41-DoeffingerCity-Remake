package com.mygdx.game.entities;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

/**
 * Blueprint for a game entity which needs to be able to dispose loaded resources and be removed
 * from the Box2D world
 */
public abstract class Entity implements Disposable {

  /**
   * The name of the entity
   */
  protected final String name;
  /**
   * The Box2D world that the entity lives in
   */
  protected final World world;

  public Entity(final String name, final World world) {
    this.name = name;
    this.world = world;
  }

  /**
   * Remove the connection to the Box2D world
   */
  public abstract void removeFromWorld();

}
