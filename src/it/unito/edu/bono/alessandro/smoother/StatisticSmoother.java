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

import it.unito.edu.bono.alessandro.util.Counter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class StatisticSmoother extends SmootherAbstract {

    private String devSetPath = "";
    private HashMap<String, Integer> unknownCounter = new HashMap<>();

    public StatisticSmoother(String devSetPath) {
        this.devSetPath = devSetPath;
    }

    @Override
    public void setCounter(Counter counter) {
        try {
            super.setCounter(counter);
            count();
        } catch (IOException ex) {
            Logger.getLogger(StatisticSmoother.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void count() throws IOException {
        ArrayList<String> knownWords = counter.getWords();
        if (knownWords.isEmpty()) {
            throw new IllegalStateException("Allenare il PoSTagger prima di assegnarli uno Smoother");
        }
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(devSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                String tag = temp[1];
                if (normalizer != null) {
                    word = normalizer.normalize(word);
                }
                if (!knownWords.contains(word)) {
                    incrementUnkownCounter(unknownCounter, tag);
                }
            }
        }
    }

    private void incrementUnkownCounter(HashMap<String, Integer> unknownCounter, String tag) {
        if (!unknownCounter.containsKey(tag)) {
            unknownCounter.put(tag, 1);
        } else {
            Integer oldValue = unknownCounter.get(tag);
            unknownCounter.put(tag, oldValue + 1);
        }
    }

    @Override
    public double smooth(String tag, String word) {
        return unknownCounter.getOrDefault(tag, 1) / (double) unknownCounter.size();
    }
}
