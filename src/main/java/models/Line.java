package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by danielchu on 12/9/18.
 */
public enum Line {
  COL_A, COL_B, COL_C, ROW_1, ROW_2, ROW_3, DIAG_L, DIAG_R;

  private static Map<Integer, Line> COL_MAP = new HashMap<>();
  private static Map<Integer, Line> ROW_MAP = new HashMap<>();

  static {
    COL_MAP.put(0, COL_A);
    COL_MAP.put(1, COL_B);
    COL_MAP.put(2, COL_C);
    ROW_MAP.put(0, ROW_1);
    ROW_MAP.put(1, ROW_2);
    ROW_MAP.put(2, ROW_3);
  }

  public static List<Line> getLinesToUpdate(int col, int row) {
    List<Line> lines = new ArrayList<>();
    if (col == row) {
      lines.add(DIAG_L);
    }
    if (col + row == 2) {
      lines.add(DIAG_R);
    }
    lines.add(COL_MAP.get(col));
    lines.add(ROW_MAP.get(row));
    return lines;
  }
}
