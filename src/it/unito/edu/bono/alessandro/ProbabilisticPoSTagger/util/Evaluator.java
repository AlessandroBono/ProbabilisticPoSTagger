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

import it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.postagger.PoSTagger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class Evaluator {

    private int total = 0;
    private int correct = 0;
    private long testTime = 0;
    private final SparseMatrix coupleErrors = new SparseMatrix();
    private String testSetPath;
    private PoSTagger posTagger;

    public void setPoSTagger(PoSTagger tagger) {
        this.posTagger = tagger;
    }

    public void setTestSet(String testSetPath) {
        this.testSetPath = testSetPath;
    }

    public void evaluate() throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(testSetPath));
        ArrayList<String> sentence = new ArrayList<>();
        ArrayList<Pair<String, String>> correctTags = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                String tag = temp[1];
                sentence.add(word);
                correctTags.add(new Pair(word, tag));
            } else {
                long startTime = System.nanoTime();
                ArrayList<Pair<String, String>> resultTags = posTagger.tagSentence(sentence);
                long endTime = System.nanoTime();
                testTime += endTime - startTime;
                checkResultPerformance(resultTags, correctTags);
                sentence = new ArrayList<>();
                correctTags = new ArrayList<>();
            }
        }
        printResults();
    }

    private void checkResultPerformance(ArrayList<Pair<String, String>> resultTags, ArrayList<Pair<String, String>> correctTags) {
        if (resultTags.size() != correctTags.size()) {
            throw new IllegalArgumentException("Test set con dimensioni diverse");
        }
        for (int i = 0; i < resultTags.size(); i++) {
            Pair<String, String> resultPair = resultTags.get(i);
            Pair<String, String> correctPair = correctTags.get(i);
            if (!resultPair.getFirst().equals(correctPair.getFirst())) {
                throw new IllegalArgumentException("Test set non correttamente allineati");
            }
            String resultTag = resultPair.getSecond();
            String correctTag = correctPair.getSecond();
            if (resultTag.equals(correctTag)) {
                correct++;
            } else {
                coupleErrors.increment(resultTag, correctTag);
            }
            total++;
        }
    }

    private void printResults() {
        int maxValue = Integer.MIN_VALUE;
        String maxTag0 = null;
        String maxTag1 = null;
        for (String tag0 : coupleErrors.getRows()) {
            for (String tag1 : coupleErrors.getColumns()) {
                int nErrors = coupleErrors.get(tag0, tag1);
                if (nErrors > maxValue) {
                    maxValue = nErrors;
                    maxTag0 = tag0;
                    maxTag1 = tag1;
                }
            }
        }

        System.out.println("### " + posTagger.getClass().getName() + " ###");
        System.out.println("Corretti: " + correct + "/" + total);
        System.out.println("Percentuale: " + correct / (float) total);
        System.out.println("Tempo Impiegato: " + testTime / 1000000000.0 + " secondi");
        System.out.println("Errore più comune: " + maxTag0 + "-" + maxTag1 + " " + maxValue / (float) (total - correct));
    }
}
