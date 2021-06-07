package com.mygdx.game.level;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.world.Map;
import com.mygdx.game.entities.zombies.ZombieBicycle;
import com.mygdx.game.entities.zombies.ZombieFat;
import com.mygdx.game.entities.zombies.ZombieLincoln;
import com.mygdx.game.entities.zombies.ZombieSmall;
import com.mygdx.game.entities.zombies.ZombieSpider;

public class Wave {

	private final Array<ZombieWave> zombieWaves;

	public Wave() {
		this.zombieWaves = new Array<ZombieWave>();
	}

	public void addNewZombieWave(final ZombieWave zombieWave) {
		this.zombieWaves.add(zombieWave);
	}

	public void check(final int i) {
		System.out.println(">>> Wave #" + (i + 1));
		for (int j = 0; j < zombieWaves.size; j++)
			zombieWaves.get(j).check(j);
	}

	public Array<Zombie> createEnemies(final Vector2 entryPosition, final World world,
			final AssetManager assetManager, final Map map, final float currentTime) {

		final Array<Zombie> allEnemies = new Array<Zombie>();

		for (int wave = 0; wave < zombieWaves.size; wave++) {
			int counter = 0;

			for (int j = 0; j < zombieWaves.get(wave).getSpiderZombieNumber(); j++) {
				allEnemies.add(
						new ZombieSpider(entryPosition, world, assetManager, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getSpiderTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getBycicleZombieNumber(); j++) {
				allEnemies.add(
						new ZombieBicycle(entryPosition, world, assetManager, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getBycicleTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getFatZombieNumber(); j++) {
				allEnemies.add(new ZombieFat(entryPosition, world, assetManager, map, currentTime
						+ zombieWaves.get(wave).getEntryTime() + counter++ * zombieWaves.get(wave).getFatTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getSmallZombieNumber(); j++) {
				allEnemies.add(
						new ZombieSmall(entryPosition, world, assetManager, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getSmallTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getLincolnZombieNumber(); j++) {
				allEnemies.add(
						new ZombieLincoln(entryPosition, world, assetManager, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getLincolnTimeDelta()));
			}

		}

		return allEnemies;
	}

	public Array<ZombieWave> getZombieWaves() {
		return this.zombieWaves;
	}
}
