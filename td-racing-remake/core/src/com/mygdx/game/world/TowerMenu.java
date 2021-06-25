package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Tower;
import com.mygdx.game.entities.Zombie;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.entities.towers.FlameTower;
import com.mygdx.game.entities.towers.LaserTower;
import com.mygdx.game.entities.towers.CannonTower;
import com.mygdx.game.entities.towers.SniperTower;
import java.util.Arrays;

public class TowerMenu implements Disposable {

	private static final float SCALE_FACTOR = 1;

	private static final Vector2 start = new Vector2(30, 0);

	private final Sprite[] sprites;
	private final World world;
	private final ScoreBoard scoreboard;

	private Tower buildingtower;
	private boolean[] towerUnlocked;
	private boolean[] towerSelected;

	public TowerMenu(final AssetManager assetManager, final String textureTowerCannonButton,
			final String textureTowerLaserButton, final String textureTowerFlameButton,
			final String textureTowerSniperButton, final World world, final ScoreBoard scoreboard) {
		this.world = world;
		this.scoreboard = scoreboard;
		Texture cannonButton = assetManager.get(textureTowerCannonButton);
		Texture laserButton = assetManager.get(textureTowerLaserButton);
		Texture flameButton = assetManager.get(textureTowerFlameButton);
		Texture sniperButton = assetManager.get(textureTowerSniperButton);
		this.sprites = new Sprite[] { new Sprite(cannonButton), new Sprite(laserButton), new Sprite(flameButton),
				new Sprite(sniperButton) };
		this.towerSelected = new boolean[sprites.length];
		this.towerUnlocked = new boolean[sprites.length];

		Vector2 spriteSize = new Vector2();
		for (final Sprite sprite : sprites) {
			spriteSize.set(sprite.getWidth(), sprite.getHeight()).scl(PlayState.PIXEL_TO_METER).scl(SCALE_FACTOR);
			sprite.setSize(spriteSize.x, spriteSize.y);
			sprite.setOriginCenter();
		}

		float versatz = sprites[0].getWidth();
		float x = start.x;

		for (final Sprite sprite : sprites) {
			sprite.setPosition(x, start.y);
			x += versatz;
		}
	}

	public void draw(final SpriteBatch batch) {
		for (final Sprite sprite : sprites)
			sprite.draw(batch);
	}

	public void selectTower(int i, final Vector3 mousePos, final Array<Zombie> enemies,
			final AssetManager assetManager) {
		boolean unselect = !towerUnlocked[i];

		if (towerSelected[i])
			unselect = true;

		Arrays.fill(towerSelected, false);

		if (!unselect) {
			if (canAfford(i))
				towerSelected[i] = true;
		} else {
			if (buildingtower != null && buildingtower.body != null)
				world.destroyBody(buildingtower.body);
			if (buildingtower != null)
				buildingtower = null;
			return;
		}

		if (buildingtower != null && buildingtower.body != null) {
			world.destroyBody(buildingtower.body);
			buildingtower = null;
		} else if (buildingtower != null)
			buildingtower = null;

		if (towerSelected[i] && towerUnlocked[i]) {
			buildingtower = getTower(i, mousePos, enemies, assetManager);
			buildingtower.activateRange(true);
		}
	}

	public Tower getTower(final int tower, final Vector3 mousePos, final Array<Zombie> enemies,
			final AssetManager assetManager) {
		final Vector2 mousePos2 = new Vector2(mousePos.x, mousePos.y);
		switch (tower) {
		case 0:
			return new CannonTower(mousePos2.cpy(), enemies, world, assetManager);
		case 1:
			return new LaserTower(mousePos2.cpy(), enemies, world, assetManager);
		case 2:
			return new FlameTower(mousePos2.cpy(), enemies, world, assetManager);
		case 3:
			return new SniperTower(mousePos2.cpy(), enemies, world, assetManager);
		}
		Gdx.app.error("towerMenu:getTower", MainGame.getCurrentTimeStampLogString() + "not found correct Tower at id=" + tower);
		return null;
	}

	public void update() {
		for (int i = 0; i < towerUnlocked.length; i++) {
			sprites[i].setColor(1, 1, 1, 0);
			if (towerUnlocked[i]) {
				sprites[i].setColor(1, 1, 1, 0.5f);
				if (canAfford(i))
					sprites[i].setColor(1, 1, 1, 1f);
				if (towerSelected[i] && towerUnlocked[i])
					sprites[i].setColor(0.25f, 1, 0.25f, 1);
			}
		}
	}

	public Tower getCurrentTower() {
		return buildingtower;
	}

	private boolean canAfford(int i) {
		int price = 0;
		switch (i) {
		case 0:
			price = CannonTower.COST;
			break;
		case 1:
			price = LaserTower.COST;
			break;
		case 2:
			price = FlameTower.COST;
			break;
		case 3:
			price = SniperTower.COST;
			break;
		}
		return (scoreboard.getMoney() >= price);
	}

	public void unselectAll() {
		for (int i = 0; i < towerSelected.length; i++)
			towerSelected[i] = false;
		buildingtower = null;
	}

	public boolean contains(final float xPos, final float yPos) {
		float towerMenuWidth = 0;
		for (int i = 0; i < towerUnlocked.length; i++)
			towerMenuWidth += (towerUnlocked[i]) ? sprites[i].getWidth() : 0;
		return (xPos >= start.x && xPos <= start.x + towerMenuWidth)
				&& (yPos >= start.y && yPos <= start.y + ((sprites.length > 0) ? sprites[0].getHeight() : 0));
	}

	@Override
	public void dispose() {
		for (final Sprite sprite : sprites)
			sprite.getTexture().dispose();
	}

	public void unlockTower(final int i) {
		unlockTower(i, true);
	}

	public void unlockTower(final int i, final boolean unLock) {
		towerUnlocked[i] = unLock;
	}

	public Vector2 getStart() {
		return start;
	}

}
