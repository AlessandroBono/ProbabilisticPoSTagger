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

import it.unito.edu.bono.alessandro.normalizer.Normalizer;
import it.unito.edu.bono.alessandro.smoother.Smoother;
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

    private String filePath = null;
    private Normalizer normalizer = null;
    private Smoother smoother = null;
    private HashMap<String, Integer> tagsCounter = new HashMap<>();
    private SparseMatrix transitionMatrix = new SparseMatrix();
    private SparseMatrix emissionMatrix = new SparseMatrix();
    private boolean logarithmProbability = false;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setNormalizer(Normalizer normalizer) {
        this.normalizer = normalizer;
    }

    public void setSmoother(Smoother smoother) {
        this.smoother = smoother;
        this.smoother.setCounter(this);
    }

    public void activateLogProbability() {
        logarithmProbability = true;
    }

    public void deactivateLogProbability() {
        logarithmProbability = false;
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
                if (normalizer != null) {
                    word = normalizer.normalize(word);
                }
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
        if (normalizer != null) {
            word = normalizer.normalize(word);
        }
        int emissionCount = emissionMatrix.get(tag, word);
        if (emissionCount == 0) {
            if (smoother != null) {
                double smoothed = smoother.smooth(word);
                return logarithmProbability ? Math.log(smoothed) : smoothed;
            } else {
                return logarithmProbability ? Math.log(Double.MIN_VALUE) : 0;
            }
        }
        if (logarithmProbability) {
            return Math.log(emissionCount) - Math.log(tagsCounter.get(tag));
        }
        return emissionCount / tagsCounter.get(tag);
    }

    public double getTransitionProbability(String tag1, String tag2) {
        double transitionCounter = transitionMatrix.get(tag1, tag2);
        if (transitionCounter == 0) {
            return logarithmProbability ? Math.log(Double.MIN_VALUE) : 0;
        }
        if (logarithmProbability) {
            return Math.log(transitionCounter) - Math.log(tagsCounter.get(tag1));
        }
        return transitionCounter / tagsCounter.get(tag1);
    }

    public ArrayList<String> getTags() {
        return new ArrayList<>(tagsCounter.keySet());
    }

    public ArrayList<String> getWords() {
        return new ArrayList<>(emissionMatrix.getColumns());
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
        if (normalizer != null) {
            word = normalizer.normalize(word);
        }
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
