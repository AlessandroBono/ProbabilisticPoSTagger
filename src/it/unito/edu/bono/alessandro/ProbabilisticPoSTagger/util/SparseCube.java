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
package it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.util;

import java.util.HashMap;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class SparseCube {

    private final HashMap<String, HashMap<String, HashMap<String, Integer>>> cube;

    public SparseCube() {
        cube = new HashMap<>();
    }

    public Integer get(String coord0, String coord1, String coord2) {
        if (!cube.containsKey(coord0)) {
            return 0;
        }
        if (!cube.get(coord0).containsKey(coord1)) {
            return 0;
        }
        if (!cube.get(coord0).get(coord1).containsKey(coord2)) {
            return 0;
        }
        return cube.get(coord0).get(coord1).get(coord2);
    }

    public void set(String coord0, String coord1, String coord2, Integer value) {
        if (!cube.containsKey(coord0)) {
            cube.put(coord0, new HashMap<>());
        }
        if (!cube.get(coord0).containsKey(coord1)) {
            cube.get(coord0).put(coord1, new HashMap<>());
        }
        cube.get(coord0).get(coord1).put(coord2, value);
    }

    public void increment(String coord0, String coord1, String coord2) {
        int oldValue = get(coord0, coord1, coord2);
        set(coord0, coord1, coord2, oldValue + 1);
    }
}
