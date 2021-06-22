package com.mygdx.game.preferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.game.MainGame;

/**
 * Class that manages application wide settings/preferences
 */
public class PreferencesManager {

  private static final String PREFERENCE_FULLSCREEN_ACTIVATED_BOOL = "FULLSCREEN_ACTIVATED_BOOL";
  private static final boolean PREFERENCE_FULLSCREEN_ACTIVATED_BOOL_DEFAULT = false;
  private static final String PREFERENCE_MUSIC_ON_BOOL = "MUSIC_ON_BOOL";
  private static final boolean PREFERENCE_MUSIC_ON_BOOL_DEFAULT = true;
  private static final String PREFERENCE_SOUND_EFFECTS_ON_BOOL = "SOUND_EFFECTS_ON_BOOL";
  private static final boolean PREFERENCE_SOUND_EFFECTS_ON_BOOL_DEFAULT = true;
  private static final String PREFERENCE_HIGHSCORE_NAME_STRING_BASE = "HIGHSCORE_NAME_STRING_";
  private static final String PREFERENCE_HIGHSCORE_SCORE_VALUE_BASE = "HIGHSCORE_SCORE_VALUE_";
  private static final String PREFERENCE_HIGHSCORE_LEVEL_VALUE_BASE = "HIGHSCORE_LEVEL_VALUE_";
  private static final String PREFERENCE_HIGHSCORE_LAPS_VALUE_BASE = "HIGHSCORE_LAPS_VALUE_";
  private static final String PREFERENCE_ALREADY_LAUNCHED_BOOL = "ALREADY_LAUNCHED_BOOL";
  private static final String PREFERENCE_LAST_HIGHSCORE_NAME_STRING = "LAST_HIGHSCORE_NAME_STRING";
  private static final String PREFERENCES_ID = "td-racing-ludum-dare-41";
  /**
   * The number of highscore entries saved in prefs
   */
  private static final int NUMBER_HIGHSCORE_ENTRIES = 5;
  /**
   * A Preference instance is a libGDX object that manages preferences with a hash map
   * implementation that holds values that can be accessed via an unique string
   */
  private final Preferences prefs;

  public PreferencesManager() {
    Gdx.app.debug("preferences_manager:constructor", MainGame.getCurrentTimeStampLogString());

    // Get the preferences object and load hash map
    prefs = Gdx.app.getPreferences(PREFERENCES_ID);

    // Setup preferences object with default values in case this is the first launch
    if (!getAlreadyLaunched()) {
      Gdx.app.debug("preferences_manager:constructor",
          MainGame.getCurrentTimeStampLogString() + "first launch detected");
      // Set that the game was already launched (for the next start)
      setAlreadyLaunched(true);
      // Set that the game should be launched in windowed mode
      setFullscreen(PREFERENCE_FULLSCREEN_ACTIVATED_BOOL_DEFAULT);
      // Set user name
      setHighscoreName("NOBODY");
      // Set music to on
      setMusicOn(PREFERENCE_MUSIC_ON_BOOL_DEFAULT);
      // Set sound effects to on
      setSoundEffectsOn(PREFERENCE_SOUND_EFFECTS_ON_BOOL_DEFAULT);
      // Reset highscore list (which creates default entries)
      resetHighscore();
    }
  }

  public boolean getFullscreen() {
    return prefs.getBoolean(PREFERENCE_FULLSCREEN_ACTIVATED_BOOL, PREFERENCE_FULLSCREEN_ACTIVATED_BOOL_DEFAULT);
  }

  public void setFullscreen(final boolean fullscreenActivated) {
    prefs.putBoolean(PREFERENCE_FULLSCREEN_ACTIVATED_BOOL, fullscreenActivated).flush();
  }

  public boolean getAlreadyLaunched() {
    return prefs.getBoolean(PREFERENCE_ALREADY_LAUNCHED_BOOL, false);
  }

  public void setAlreadyLaunched(final boolean alreadyLaunched) {
    prefs.putBoolean(PREFERENCE_ALREADY_LAUNCHED_BOOL, alreadyLaunched).flush();
  }

  public void setHighscoreName(final String name) {
    prefs.putString(PREFERENCE_LAST_HIGHSCORE_NAME_STRING, name).flush();
  }

  public char[] getName() {
    return prefs.getString(PREFERENCE_LAST_HIGHSCORE_NAME_STRING).toCharArray();
  }

  public void checkHighscore() {
    final HighscoreEntry[] entries = retrieveHighscore();
    for (int i = 0; i < entries.length; i++) {
      if (entries[i].getName() == null || entries[i].getName().equals("")) {
        prefs.putString(PREFERENCE_HIGHSCORE_NAME_STRING_BASE + i, "------");
        prefs.putInteger(PREFERENCE_HIGHSCORE_SCORE_VALUE_BASE + i, -1);
        prefs.putInteger(PREFERENCE_HIGHSCORE_LEVEL_VALUE_BASE + i, -1);
        prefs.putInteger(PREFERENCE_HIGHSCORE_LAPS_VALUE_BASE + i, -1);
      }
    }
    prefs.flush();
  }

  /**
   * Clear highscore list by setting every value to 0 and the scorer name to NOBODY
   */
  public void resetHighscore() {
    for (int i = 0; i < NUMBER_HIGHSCORE_ENTRIES; i++) {
      prefs.putString(PREFERENCE_HIGHSCORE_NAME_STRING_BASE + i, "------")
          .putInteger(PREFERENCE_HIGHSCORE_SCORE_VALUE_BASE + i, -1)
          .putInteger(PREFERENCE_HIGHSCORE_LEVEL_VALUE_BASE + i, -1)
          .putInteger(PREFERENCE_HIGHSCORE_LAPS_VALUE_BASE + i, -1);
    }
    prefs.flush();
  }

  /**
   * Reset all preferences
   */
  public void reset() {
    prefs.clear();
  }

  public HighscoreEntry[] retrieveHighscore() {
    final HighscoreEntry[] entries = new HighscoreEntry[NUMBER_HIGHSCORE_ENTRIES];
    for (int i = 0; i < entries.length; i++) {
      Gdx.app.debug("preferences_manager:retrieveHighscore",
          MainGame.getCurrentTimeStampLogString() + "-> Retrieve score #" + i + " (" + prefs
              .getInteger(PREFERENCE_HIGHSCORE_SCORE_VALUE_BASE + i) + ") from \"" + prefs
              .getString(PREFERENCE_HIGHSCORE_NAME_STRING_BASE + i) + "\" [level=" + prefs
              .getInteger(PREFERENCE_HIGHSCORE_LEVEL_VALUE_BASE + i) + ",laps=" + prefs
              .getInteger(PREFERENCE_HIGHSCORE_LAPS_VALUE_BASE + i) + "]");

      entries[i] = new HighscoreEntry(
          prefs.getInteger(PREFERENCE_HIGHSCORE_SCORE_VALUE_BASE + i),
          prefs.getInteger(PREFERENCE_HIGHSCORE_LEVEL_VALUE_BASE + i),
          prefs.getInteger(PREFERENCE_HIGHSCORE_LAPS_VALUE_BASE + i),
          prefs.getString(PREFERENCE_HIGHSCORE_NAME_STRING_BASE + i)
      );
    }
    return entries;
  }

  public boolean getMusicOn() {
    return prefs.getBoolean(PREFERENCE_MUSIC_ON_BOOL, PREFERENCE_MUSIC_ON_BOOL_DEFAULT);
  }

  public void setMusicOn(final boolean musicOn) {
    prefs.putBoolean(PREFERENCE_MUSIC_ON_BOOL, musicOn).flush();
  }

  public boolean getSoundEffectsOn() {
    return prefs.getBoolean(PREFERENCE_SOUND_EFFECTS_ON_BOOL, true);
  }

  public void setSoundEffectsOn(final boolean soundEffectsOn) {
    prefs.putBoolean(PREFERENCE_SOUND_EFFECTS_ON_BOOL, soundEffectsOn).flush();
  }

  public void saveHighscore(String name, int score, int level, int laps) {
    Gdx.app.debug("preferences_manager:saveHighscore",
        MainGame.getCurrentTimeStampLogString() + "save score (" + score + ") from \"" + name
            + "\" [level=" + level + ",laps=" + laps + "]");
    setHighscoreName(name);
    final HighscoreEntry[] entries = retrieveHighscore();
    for (int i = 0; i < entries.length; i++) {
      if (entries[i].getScore() < score) {
        prefs.putString(PREFERENCE_HIGHSCORE_NAME_STRING_BASE + i, name);
        prefs.putInteger(PREFERENCE_HIGHSCORE_SCORE_VALUE_BASE + i, score);
        prefs.putInteger(PREFERENCE_HIGHSCORE_LEVEL_VALUE_BASE + i, level);
        prefs.putInteger(PREFERENCE_HIGHSCORE_LAPS_VALUE_BASE + i, laps);
        for (int j = i + 1; j < entries.length; j++) {
          prefs.putString(PREFERENCE_HIGHSCORE_NAME_STRING_BASE + j, entries[j - 1].getName());
          prefs.putInteger(PREFERENCE_HIGHSCORE_SCORE_VALUE_BASE + j, entries[j - 1].getScore());
          prefs.putInteger(PREFERENCE_HIGHSCORE_LEVEL_VALUE_BASE + j, entries[j - 1].getLevel());
          prefs.putInteger(PREFERENCE_HIGHSCORE_LAPS_VALUE_BASE + j, entries[j - 1].getLaps());
        }
        prefs.flush();
        return;
      }
    }
  }

  public boolean scoreIsInTop5(final int score) {
    for (final HighscoreEntry entry : retrieveHighscore()) {
      Gdx.app.debug("preferences_manager:scoreIsInTop5",
          MainGame.getCurrentTimeStampLogString() + "score (" + score + ") > existing entry ("
              + entry.getScore() + ")");
      if (entry.getScore() < score) {
        return true;
      }
    }
    return false;
  }

  /**
   * Structure for an highscore entry
   */
  public static class HighscoreEntry {

    /**
     * The score value
     */
    private final int score;
    /**
     * The level value
     */
    private final int level;
    /**
     * The laps value
     */
    private final int laps;
    /**
     * The name of the person that set the score
     */
    private final String name;

    /**
     * Constructor for creating a new Highscore Entry
     *
     * @param score The score value
     * @param name  The name of the person that set the score
     */
    HighscoreEntry(final int score, final int level, final int laps, final String name) {
      this.score = score;
      this.level = level;
      this.laps = laps;
      this.name = name;
    }

    public int getScore() {
      return score;
    }

    public int getLevel() {
      return level;
    }

    public int getLaps() {
      return laps;
    }

    public String getName() {
      return name;
    }

    public String toString() {
      return score + " by " + name;
    }
  }

}
