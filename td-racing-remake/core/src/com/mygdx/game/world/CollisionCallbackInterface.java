package com.mygdx.game.world;

import com.mygdx.game.entities.Car;
import com.mygdx.game.world.Checkpoint;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.world.FinishLine;
import com.mygdx.game.entities.towers.FlameTowerFire;

public interface CollisionCallbackInterface {

	void collisionCallbackCarEnemy(final Car car, final Zombie zombie);

	void collisionCallbackCarCheckpoint(final Car car, final Checkpoint checkpoint);

	void collisionCallbackCarFinishLine(final Car car, final FinishLine finishLine);

	void collisionCallbackFlameEnemy(final Zombie zombie, final FlameTowerFire flameTowerFire);

}
