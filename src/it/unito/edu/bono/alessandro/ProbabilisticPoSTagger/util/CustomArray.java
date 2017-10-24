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
import java.util.Set;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class CustomArray {

    private final HashMap<String, Integer> array = new HashMap<>();

    public void set(String idx, int value) {
        array.put(idx, value);
    }

    public int get(String idx) {
        if (!array.containsKey(idx)) {
            return 0;
        }
        return array.get(idx);
    }

    public Set<String> getIdexes() {
        return array.keySet();
    }

    public void increment(String idx) {
        increment(idx, 1);
    }

    public void increment(String idx, int value) {
        int oldValue = get(idx);
        set(idx, oldValue + value);
    }
}
