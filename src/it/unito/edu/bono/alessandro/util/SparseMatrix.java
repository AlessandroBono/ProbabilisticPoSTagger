/*
 * Copyright (C) 2016 Alessandro Bono <alessandro.bono@edu.unito.it>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unito.edu.bono.alessandro.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class SparseMatrix {

    private HashMap<String, HashMap<String, Integer>> matrix;

    public SparseMatrix() {
        matrix = new HashMap<>();
    }

    public Integer get(String row, String col) {
        if (!matrix.containsKey(row)) {
            return 0;
        }
        if (!matrix.get(row).containsKey(col)) {
            return 0;
        }
        return matrix.get(row).get(col);
    }

    public void set(String row, String col, Integer value) {
        if (!matrix.containsKey(row)) {
            matrix.put(row, new HashMap<>());
        }
        matrix.get(row).put(col, value);
    }

    public void increment(String row, String col) {
        int oldValue = get(row, col);
        set(row, col, oldValue + 1);
    }

    public Set<String> getRows() {
        return matrix.keySet();
    }

    public Set<String> getColumns() {
        Set<String> cols = new HashSet<>();
        Set<String> rows = matrix.keySet();
        for (String row : rows) {
            cols.addAll(matrix.get(row).keySet());
        }
        return cols;
    }

    @Override
    public String toString() {
        String output = "";
        for (String si : matrix.keySet()) {
            for (String sj : matrix.get(si).keySet()) {
                output += si + " " + sj + " " + matrix.get(si).get(sj) + "\n";
            }
        }
        return output;
    }
}
