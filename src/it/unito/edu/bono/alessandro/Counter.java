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

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class Counter {

    private String filePath;
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
                transitionMatrix.increment(oldTag, CustomTag.END);
                tag = CustomTag.START;
            }
            oldTag = tag;
        }
    }

    public String getMostFrequentTag(String word, String defaultTag) {
        String mostFreqTag = "";
        Integer maxValue = 0;
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
