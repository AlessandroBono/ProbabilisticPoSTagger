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
package it.unito.edu.bono.alessandro.smoother;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class StatisticSmoother extends SmootherAbstract {

    private final HashMap<String, Integer> unknownCounter = new HashMap<>();
    private int totalUnknown = 0;

    private void incrementUnkownCounter(HashMap<String, Integer> unknownCounter, String tag) {
        if (!unknownCounter.containsKey(tag)) {
            unknownCounter.put(tag, 1);
        } else {
            Integer oldValue = unknownCounter.get(tag);
            unknownCounter.put(tag, oldValue + 1);
        }
    }

    @Override
    public void train() throws IOException {
        // learn known words in the training set
        List<String> knownWords = new ArrayList<>();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(trainingSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                word = normalizer.normalize(word);
                knownWords.add(word);
            }
        }
        reader.close();

        // find words that aren't present in the training set while present in
        // the dev set
        reader = new BufferedReader(new FileReader(devSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                String tag = temp[1];
                word = normalizer.normalize(word);
                if (!knownWords.contains(word)) {
                    incrementUnkownCounter(unknownCounter, tag);
                    totalUnknown++;
                }
            }
        }
        reader.close();
    }

    @Override
    public double smooth(String tag, String word) {
        return unknownCounter.getOrDefault(tag, 1) / (double) totalUnknown;
    }

}
