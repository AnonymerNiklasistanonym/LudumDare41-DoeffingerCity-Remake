package com.mygdx.game.unsorted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.game.MainGame;

/**
 * Class that manages application wide settings/preferences
 */
public class PreferencesManager {

	/**
	 * Structure for an highscore entry
	 */
	public static class HighscoreEntry {

		/**
		 * The score value
		 */
		private final int score;
		/**
		 * The name of the person that set the score
		 */
		private final String name;

		public int getScore() {
			return score;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return score + " by " + name;
		}

		/**
		 * Constructor for creating a new Highscore Entry
		 * @param score The score value
		 * @param name The name of the person that set the score
		 */
		HighscoreEntry(final int score, final String name) {
			this.score = score;
			this.name = name;
		}
	}

	private static final String MUSIC = "MUSIC";
	private static final String SOUND_EFFECTS = "SOUND_EFFECTS";
	private static final String HIGHSCORE_NAME = "HIGHSCORE_NAME";
	private static final String HIGHSCORE_SCORE = "HIGHSCORE_SCORE";
	private static final String PREFERENCES_NAME = "td-racing-ludum-dare-41";
	private static final String ALREADY_LAUNCHED = "ALREADY_LAUNCHED";
	private static final String LAST_NAME = "LAST_NAME";

	private static final int NUMBER_HIGHSCORE_ENTRIES = 5;

	/**
	 * A Preference instance is a libGDX object that manages preferences with a hash map implementation that holds values that can be accessed via an unique string
	 */
	private final Preferences prefs;

	public PreferencesManager() {
		Gdx.app.debug("preferences_manager:constructor", MainGame.getCurrentTimeStampLogString());

		// Get the preferences object and load hash map
		prefs = Gdx.app.getPreferences(PREFERENCES_NAME);

		reset();

		// Setup preferences object with default values in case this is the first launch
		if (!getAlreadyLaunched()) {
			Gdx.app.debug("preferences_manager:constructor", MainGame.getCurrentTimeStampLogString() + "first launch detected");
			// Set that the game was already launched (for the next start)
			setAlreadyLaunched(true);
			// Set user name
			setHighscoreName("NOBODY");
			// Set music to on
			setMusicOn(true);
			// Set sound effects to on
			setSoundEffectsOn(true);
			// Reset highscore list (which creates default entries)
			resetHighscore();
		}
	}

	public boolean getAlreadyLaunched() {
		return prefs.getBoolean(ALREADY_LAUNCHED, false);
	}

	public void setAlreadyLaunched(final boolean alreadyLaunched) {
		prefs.putBoolean(ALREADY_LAUNCHED, alreadyLaunched).flush();
	}

	public void setHighscoreName(final String name) {
		prefs.putString(LAST_NAME, name).flush();
	}

	public char[] getName() {
		return prefs.getString(LAST_NAME).toCharArray();
	}

	public void checkHighscore() {
		final HighscoreEntry[] entries = retrieveHighscore();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getName() == null || entries[i].getName().equals(""))
				prefs.putString(HIGHSCORE_NAME + i, "NOBODY");
			if (entries[i].getScore() < 0)
				prefs.putInteger(HIGHSCORE_SCORE + i, 0);
		}
		prefs.flush();
	}

	/**
	 * Clear highscore list by setting every value to 0 and the scorer name to NOBODY
	 */
	public void resetHighscore() {
		for (int i = 0; i < NUMBER_HIGHSCORE_ENTRIES; i++)
			prefs.putString(HIGHSCORE_NAME + i, "NOBODY").putInteger(HIGHSCORE_SCORE + i, 0);
		prefs.flush();
	}

	public void saveHighscore(String[] names, int[] scores) {
		for (int i = 0; i < NUMBER_HIGHSCORE_ENTRIES; i++)
			prefs.putString(HIGHSCORE_NAME + i, names[i]).putInteger(HIGHSCORE_SCORE + i, scores[i]);
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
		for (int i = 0; i < entries.length; i++)
			entries[i] = new HighscoreEntry(prefs.getInteger(HIGHSCORE_SCORE + i), prefs.getString(HIGHSCORE_NAME + i));
		return entries;
	}

	public void setMusicOn(final boolean musicOn) {
		prefs.putBoolean(MUSIC, musicOn).flush();
	}

	public void setSoundEffectsOn(final boolean soundEffectsOn) {
		prefs.putBoolean(SOUND_EFFECTS, soundEffectsOn).flush();
	}

	public boolean getMusicOn() {
		return prefs.getBoolean(SOUND_EFFECTS, true);
	}

	public boolean getSoundEfectsOn() {
		return prefs.getBoolean(MUSIC, true);
	}

	public void saveHighscore(String name, int score) {
		setHighscoreName(name);
		final HighscoreEntry[] entries = retrieveHighscore();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getScore() < score) {
				prefs.putString(HIGHSCORE_NAME + i, name);
				prefs.putInteger(HIGHSCORE_SCORE + i, score);
				for (int j = i + 1; j < entries.length; j++) {
					prefs.putString(HIGHSCORE_NAME + j, entries[j - 1].getName());
					prefs.putInteger(HIGHSCORE_SCORE + j, entries[j - 1].getScore());
				}
				prefs.flush();
				return;
			}
		}
	}

	public boolean scoreIsInTop5(final int score) {
		for (final HighscoreEntry entry : retrieveHighscore()) {
			if (entry.getScore() < score)
				return true;
		}
		return false;
	}

}
