package com.mygdx.game.file;

import java.util.HashMap;

public class CsvFileRecord {

  final HashMap<String, String> values;

  public CsvFileRecord(final HashMap<String, String> values) {
    this.values = values;
  }

  public String get(final String columnName) {
    if (!values.containsKey(columnName)) {
      throw new IllegalArgumentException("The column name \"" + columnName + "\" was not found");
    }
    return values.get(columnName);
  }

}
