package com.mygdx.game.file.generic;

import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.HashMap;

public class CsvFileHandler {

  public static ArrayList<CsvFileRecord> readCsvFile(final FileHandle file) {
    final String[] textLines = file.readString().split("\n");
    if (textLines.length < 1) {
      throw new IllegalArgumentException("A csv file needs to at least have a header");
    }
    final String[] csvFileHeader = textLines[0].split(",");

    ArrayList<CsvFileRecord> records = new ArrayList<>();

    for (int i = 1; i < textLines.length; i++) {
      HashMap<String, String> csvFileRowValues = new HashMap<>();

      final String[] csvFileRow = textLines[i].split(",");
      if (csvFileHeader.length != csvFileRow.length) {
        throw new IllegalArgumentException(
            "The CSV file header has a different length (" + csvFileHeader.length
                + ") compared to the current row (" + csvFileRow.length + ") at line index " + i);
      }
      for (int j = 0; j < csvFileHeader.length; j++) {
        csvFileRowValues.put(csvFileHeader[j].trim(), csvFileRow[j].trim());
      }

      records.add(new CsvFileRecord(csvFileRowValues));
    }

    return records;
  }

}
