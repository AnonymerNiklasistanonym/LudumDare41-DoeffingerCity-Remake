package com.mygdx.game.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;

public class LevelInfoCsvFile {

  public final int levelNumber;
  public final String mapName;
  public final int moneyPerLap;
  public final int timeBonus;

  public final Vector2 carStartPosition;
  public final Vector2 enemySpawnPosition;
  public final Vector2 finishLinePosition;
  public final Vector2 healthBarPosition;
  public final Vector2 pitStopPosition;

  public final HashMap<Integer, Vector2> checkpointPositions;
  public final HashMap<Integer, Boolean> towerUnlocked;

  public LevelInfoCsvFile(int levelNumber, String mapName,
      int moneyPerLap, int timeBonus, Vector2 carStartPosition, Vector2 enemySpawnPosition,
      Vector2 finishLinePosition, Vector2 healthBarPosition,
      Vector2 pitStopPosition,
      HashMap<Integer, Vector2> checkpointPositions,
      HashMap<Integer, Boolean> towerUnlocked) {
    this.levelNumber = levelNumber;
    this.mapName = mapName;
    this.moneyPerLap = moneyPerLap;
    this.timeBonus = timeBonus;
    this.carStartPosition = carStartPosition;
    this.enemySpawnPosition = enemySpawnPosition;
    this.finishLinePosition = finishLinePosition;
    this.healthBarPosition = healthBarPosition;
    this.pitStopPosition = pitStopPosition;
    this.checkpointPositions = checkpointPositions;
    this.towerUnlocked = towerUnlocked;
  }

  public static HashMap<Integer, LevelInfoCsvFile> readLevelInfoFromCsvFile(FileHandle fileHandle) {
    ArrayList<CsvFileRecord> csvFileRecords = CsvFileHandler.readCsvFile(fileHandle);

    HashMap<Integer, LevelInfoCsvFile> levelInfo = new HashMap<>();
    for (CsvFileRecord record : csvFileRecords) {

      final int levelNumber = Integer.parseInt(record.get("Level"));

      int indexCheckpoint = 1;
      HashMap<Integer, Vector2> checkpointPositions = new HashMap<>();
      while (indexCheckpoint != -1) {
        try {
          Vector2 checkpointPosition = new Vector2(
              Float.parseFloat(record.get("Checkpoint " + indexCheckpoint + " position x")),
              Float.parseFloat(record.get("Checkpoint " + indexCheckpoint + " position y")));
          checkpointPositions.put(indexCheckpoint, checkpointPosition);
          indexCheckpoint++;
        } catch (IllegalArgumentException exception) {
          indexCheckpoint = -1;
        }
      }

      int indexTowerUnlocked = 1;
      HashMap<Integer, Boolean> towerUnlocked = new HashMap<>();
      while (indexTowerUnlocked != -1) {
        try {
          boolean singleTowerUnlocked = record.get("Tower " + indexTowerUnlocked + " unlocked")
              .equals("TRUE");
          towerUnlocked.put(indexTowerUnlocked, singleTowerUnlocked);
          indexTowerUnlocked++;
        } catch (IllegalArgumentException exception) {
          indexTowerUnlocked = -1;
        }
      }

      levelInfo.put(levelNumber, new LevelInfoCsvFile(
          levelNumber,
          record.get("Map name"),
          Integer.parseInt(record.get("Money per lap")),
          Integer.parseInt(record.get("Time Bonus")),
          new Vector2(Float.parseFloat(record.get("Car start position x")),
              Float.parseFloat(record.get("Car start position y"))),
          new Vector2(Float.parseFloat(record.get("Enemy spawn position x")),
              Float.parseFloat(record.get("Enemy spawn position y"))),
          new Vector2(Float.parseFloat(record.get("Finish line position x")),
              Float.parseFloat(record.get("Finish line position y"))),
          new Vector2(Float.parseFloat(record.get("Health bar position x")),
              Float.parseFloat(record.get("Health bar position y"))),
          new Vector2(Float.parseFloat(record.get("Pit stop position x")),
              Float.parseFloat(record.get("Pit stop position y"))),
          checkpointPositions,
          towerUnlocked
      ));
    }
    return levelInfo;
  }

  @Override
  public String toString() {
    return "LevelInfoCsvFile{" +
        "levelNumber=" + levelNumber +
        ", mapName='" + mapName + '\'' +
        ", moneyPerLap=" + moneyPerLap +
        ", timeBonus=" + timeBonus +
        ", carStartPosition=" + carStartPosition +
        ", enemySpawnPosition=" + enemySpawnPosition +
        ", finishLinePosition=" + finishLinePosition +
        ", healthBarPosition=" + healthBarPosition +
        ", pitStopPosition=" + pitStopPosition +
        ", checkpointPositions=" + checkpointPositions +
        ", towerUnlocked=" + towerUnlocked +
        '}';
  }

}
