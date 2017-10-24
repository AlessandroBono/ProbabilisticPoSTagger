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
package it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.normalizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class LemmaNormalizer implements Normalizer {

    private final HashMap<String, String> lemmas = new HashMap<>();

    public void Normalizer() throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader("data/morph-it_048.txt"));
        while ((line = reader.readLine()) != null) {
            // in morphit ogni riga è così composta: <parola, lemma, tag>
            String[] temp = line.split("\t");
            if (temp.length == 3) {
                String word = temp[0];
                String lemma = temp[1];
                lemmas.put(word, lemma);
            }
        }
        reader.close();
    }

    @Override
    public String normalize(String word) {
        String retVal = lemmas.get(word);
        return retVal != null ? retVal : word;
    }

}
