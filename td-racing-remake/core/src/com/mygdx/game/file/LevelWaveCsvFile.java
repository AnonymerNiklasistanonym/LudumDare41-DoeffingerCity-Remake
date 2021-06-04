package com.mygdx.game.file;

import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.file.generic.CsvFileHandler;
import com.mygdx.game.file.generic.CsvFileRecord;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class LevelWaveCsvFile {

  public enum ZombieType {
    SMALL,
    FAT,
    BICYCLE,
    SPIDER,
    LINCOLN
  }

  public static class ZombieSpawn {
    public final int count;
    public final float timeDelta;
    public final float timeAfterWaveStarted;

    ZombieSpawn(final int count, final float timeDelta, final float timeAfterWaveStarted) {
      this.count = count;
      this.timeDelta = timeDelta;
      this.timeAfterWaveStarted = timeAfterWaveStarted;
    }

    @Override
    public String toString() {
      return "ZombieSpawn{" +
          "count=" + count +
          ", timeDelta=" + timeDelta +
          ", timeAfterWaveStarted=" + timeAfterWaveStarted +
          '}';
    }
  }

  public final int waveNumber;
  public final HashMap<ZombieType, ArrayList<ZombieSpawn>> zombieSpawns;

  public LevelWaveCsvFile(int waveNumber, HashMap<ZombieType, ArrayList<ZombieSpawn>> zombieSpawns) {
    this.waveNumber = waveNumber;
    this.zombieSpawns = zombieSpawns;
  }

  private static ArrayList<ZombieSpawn> getZombieSpawns(final CsvFileRecord record, final String zombieName, final float timeAfterWaveStarted) {
    ArrayList<ZombieSpawn> zombieSpawns = new ArrayList<>();
    final int zombieCount = Integer.parseInt(record.get(zombieName + " Zombie"));
    final float zombieTimeDelta = Float.parseFloat(record.get(zombieName + " time delta"));

    // Only add zombie spawn to the collection if it contains zombies
    if (zombieCount > 0) {
      zombieSpawns.add(new ZombieSpawn(zombieCount, zombieTimeDelta, timeAfterWaveStarted));
    }

    return zombieSpawns;
  }

  public static ArrayList<LevelWaveCsvFile> readCsvFile(FileHandle fileHandle) {
    ArrayList<CsvFileRecord> csvFileRecords = CsvFileHandler.readCsvFile(fileHandle);

    HashMap<Integer, LevelWaveCsvFile> levelWave = new HashMap<>();
    for (CsvFileRecord record : csvFileRecords) {

      final int waveNumber = Integer.parseInt(record.get("Wave"));
      final float timeAfterWaveStarted = Float.parseFloat(record.get("Time after wave started"));

      HashMap<ZombieType, ArrayList<ZombieSpawn>> zombieSpawns = new HashMap<>();
      ArrayList<ZombieSpawn> smallZombieSpawns = new ArrayList<>(
          getZombieSpawns(record, "Small", timeAfterWaveStarted));
      zombieSpawns.put(ZombieType.SMALL, smallZombieSpawns);
      ArrayList<ZombieSpawn> fatZombieSpawns = new ArrayList<>(
          getZombieSpawns(record, "Fat", timeAfterWaveStarted));
      zombieSpawns.put(ZombieType.FAT, fatZombieSpawns);
      ArrayList<ZombieSpawn> bicycleZombieSpawns = new ArrayList<>(
          getZombieSpawns(record, "Bicycle", timeAfterWaveStarted));
      zombieSpawns.put(ZombieType.BICYCLE, bicycleZombieSpawns);
      ArrayList<ZombieSpawn> spiderZombieSpawns = new ArrayList<>(
          getZombieSpawns(record, "Spider", timeAfterWaveStarted));
      zombieSpawns.put(ZombieType.SPIDER, spiderZombieSpawns);
      ArrayList<ZombieSpawn> lincolnZombieSpawns = new ArrayList<>(
          getZombieSpawns(record, "Lincoln", timeAfterWaveStarted));
      zombieSpawns.put(ZombieType.LINCOLN, lincolnZombieSpawns);

      // If there is already an entry for a wave number merge the lists
      if (levelWave.containsKey(waveNumber)) {
        LevelWaveCsvFile levelWaveCsvFile = levelWave.get(waveNumber);
        levelWaveCsvFile.zombieSpawns.get(ZombieType.SMALL).addAll(smallZombieSpawns);
        levelWaveCsvFile.zombieSpawns.get(ZombieType.FAT).addAll(fatZombieSpawns);
        levelWaveCsvFile.zombieSpawns.get(ZombieType.BICYCLE).addAll(bicycleZombieSpawns);
        levelWaveCsvFile.zombieSpawns.get(ZombieType.SPIDER).addAll(spiderZombieSpawns);
        levelWaveCsvFile.zombieSpawns.get(ZombieType.LINCOLN).addAll(lincolnZombieSpawns);
      } else {
        levelWave.put(waveNumber, new LevelWaveCsvFile(
            waveNumber,
            zombieSpawns
        ));
      }
    }
    ArrayList<LevelWaveCsvFile> levelWaveArrayList = new ArrayList<>(levelWave.size());
    for (int i = 0; i < levelWave.size(); i++) {
      levelWaveArrayList.add(levelWave.get(i + 1));
    }
    return levelWaveArrayList;
  }

  @Override
  public String toString() {
    return "LevelWaveCsvFile{" +
        "waveNumber=" + waveNumber +
        ", zombieSpawns=" + zombieSpawns +
        '}';
  }
}
