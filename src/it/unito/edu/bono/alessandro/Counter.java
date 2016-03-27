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
package it.unito.edu.bono.alessandro;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class Counter {

    private String filePath;
    private HashMap<String, Integer> tagsCounter = new HashMap<>();
    private SparseMatrix transitionMatrix = new SparseMatrix();
    private SparseMatrix emissionMatrix = new SparseMatrix();

    public Counter(String filePath) {
        this.filePath = filePath;
    }

    public void count() throws IOException {
        String line;
        String oldTag = CustomTag.START;
        String tag;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                tag = temp[1];
                emissionMatrix.increment(tag, word);
                transitionMatrix.increment(oldTag, tag);
            } else { // è finita la frase
                tag = CustomTag.END;
                incrementTagsCounter(tagsCounter, tag);
                transitionMatrix.increment(oldTag, tag);
                tag = CustomTag.START;
            }
            incrementTagsCounter(tagsCounter, tag);
            oldTag = tag;
        }
    }

    public double getEmissionProbability(String tag, String word) {
        return emissionMatrix.get(tag, word) / (double) tagsCounter.get(tag);
    }

    public double getTransitionProbability(String tag1, String tag2) {
        return transitionMatrix.get(tag1, tag2) / (double) tagsCounter.get(tag1);
    }

    public ArrayList<String> getTags() {
        return new ArrayList<>(tagsCounter.keySet());
    }

    private void incrementTagsCounter(HashMap<String, Integer> tagsCounter, String tag) {
        if (!tagsCounter.containsKey(tag)) {
            tagsCounter.put(tag, 1);
        } else {
            Integer oldValue = tagsCounter.get(tag);
            tagsCounter.put(tag, oldValue + 1);
        }
    }

    public String getMostFrequentTag(String word, String defaultTag) {
        String mostFreqTag = "";
        int maxValue = 0;
        for (String tag : emissionMatrix.getRows()) {
            Integer value = emissionMatrix.get(tag, word);
            if (value > maxValue) {
                maxValue = value;
                mostFreqTag = tag;
            }
        }
        return maxValue != 0 ? mostFreqTag : defaultTag;
    }
}
