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

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class Evaluator {

    protected String testSetPath;
    protected String devSetPath;
    private PoSTaggerAbstract posTagger;

    public void setPoSTagger(PoSTaggerAbstract tagger) {
        this.posTagger = tagger;
    }

    public void setTestSet(String testSetPath) {
        this.testSetPath = testSetPath;
    }

    public void setdDevSet(String devSetPath) {
        this.devSetPath = devSetPath;
    }

    public void evaluate() throws IOException {
        posTagger.setTestSet(testSetPath);
        posTagger.startTagging();
        ArrayList<Pair<String, String>> result = posTagger.getResult();
        ArrayList<Pair<String, String>> correctTags = getCorrectTags();
        if (result.size() != correctTags.size()) {
            throw new IllegalArgumentException("Test set con dimensioni diverse");
        }
        Integer total = 0;
        Integer correct = 0;
        for (int i = 0; i < result.size(); i++) {
            Pair<String, String> resultPair = result.get(i);
            Pair<String, String> correctPair = correctTags.get(i);
            if (!resultPair.getFirst().equals(correctPair.getFirst())) {
                throw new IllegalArgumentException("Test set non correttamente allineati");
            }
            if (resultPair.getSecond().equals(correctPair.getSecond())) {
                correct++;
            }
            total++;
        }
        System.out.println("Totali: " + total + " " + "Corretti: " + correct);
    }

    public ArrayList<Pair<String, String>> getCorrectTags() throws IOException {
        String line;
        ArrayList<Pair<String, String>> correct = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(testSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                String tag = temp[1];
                correct.add(new Pair<>(word, tag));
            }
        }
        return correct;
    }
}
