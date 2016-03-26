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
    private SparseMatrix transitionCounter = new SparseMatrix();
    private SparseMatrix emissionCounter = new SparseMatrix();

    public Counter(String filePath) {
        this.filePath = filePath;
    }

    public void count() throws IOException {
        String line;
        String oldTag = "START";
        String tag;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                tag = temp[1];
                emissionCounter.increment(tag, word);
                transitionCounter.increment(oldTag, tag);
            } else { // è finita la frase
                transitionCounter.increment(oldTag, "END");
                tag = "START";
            }
            oldTag = tag;
        }
    }

    public SparseMatrix getEmissionMatrix() {
        return emissionCounter;
    }
}
