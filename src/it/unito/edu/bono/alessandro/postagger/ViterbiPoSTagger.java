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
package it.unito.edu.bono.alessandro.postagger;

import it.unito.edu.bono.alessandro.util.CustomArray;
import it.unito.edu.bono.alessandro.util.CustomTag;
import it.unito.edu.bono.alessandro.util.Pair;
import it.unito.edu.bono.alessandro.util.SparseMatrix;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class ViterbiPoSTagger extends PoSTaggerAbstract {

    private final CustomArray tagsCounter = new CustomArray();
    private final SparseMatrix transitionMatrix = new SparseMatrix();
    private final SparseMatrix emissionMatrix = new SparseMatrix();
    private ArrayList<String> knownWords;
    private boolean logarithmProbability = true;

    public ViterbiPoSTagger() {
        this(true);
    }

    public ViterbiPoSTagger(boolean logarithmProbability) {
        this.logarithmProbability = logarithmProbability;
    }

    @Override
    public void train() throws IOException {
        String line;
        String oldTag = CustomTag.START;
        String tag;
        BufferedReader reader = new BufferedReader(new FileReader(trainingSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                tag = temp[1];
                word = normalizer.normalize(word);
                emissionMatrix.increment(tag, word);
                transitionMatrix.increment(oldTag, tag);
            } else { // è finita la frase
                tag = CustomTag.END;
                tagsCounter.increment(tag);
                transitionMatrix.increment(oldTag, tag);
                tag = CustomTag.START;
            }
            tagsCounter.increment(tag);
            oldTag = tag;
        }
        reader.close();

        // list of known word for smoothing, we sort them so that search for
        // unknown words will be easier
        knownWords = getWords();
        Collections.sort(knownWords);
        if (smoother != null) {
            smoother.train();
        }
    }

    private double getEmissionProbability(String tag, String word) {
        word = normalizer.normalize(word);
        // if (!knownWords.contains(word)) is too much inefficient (~10 sec)
        // we use a binary search, given that we sorted the known word
        // during train()
        if (smoother != null && Collections.binarySearch(knownWords, word) < 0) {
            double smoothed = smoother.smooth(tag, word);
            if (logarithmProbability && smoothed == 0) {
                return Math.log(Double.MIN_VALUE);
            }
            return logarithmProbability ? Math.log(smoothed) : smoothed;
        }
        int emissionCount = emissionMatrix.get(tag, word);
        if (logarithmProbability) {
            if (emissionCount == 0) {
                return Math.log(Double.MIN_VALUE);
            }
            return Math.log(emissionCount) - Math.log(tagsCounter.get(tag));
        }
        return emissionCount / (double) tagsCounter.get(tag);
    }

    private double getTransitionProbability(String tag1, String tag2) {
        double transitionCounter = transitionMatrix.get(tag1, tag2);
        if (transitionCounter == 0) {
            return logarithmProbability ? Math.log(Double.MIN_VALUE) : 0;
        }
        if (logarithmProbability) {
            return Math.log(transitionCounter) - Math.log(tagsCounter.get(tag1));
        }
        return transitionCounter / (double) tagsCounter.get(tag1);
    }

    private ArrayList<String> getTags() {
        return new ArrayList<>(tagsCounter.getIdexes());
    }

    private ArrayList<String> getWords() {
        return new ArrayList<>(emissionMatrix.getColumns());
    }

    @Override
    public ArrayList<Pair<String, String>> tagSentence(ArrayList<String> sentence) {
        ArrayList<String> tags = getTags();
        double[][] viterbi = new double[tags.size()][sentence.size()];
        int[][] backpointer = new int[tags.size()][sentence.size()];

        for (int i = 0; i < tags.size(); i++) {
            double transitionProbability = getTransitionProbability(CustomTag.START, tags.get(i));
            double emissionProbability = getEmissionProbability(tags.get(i), sentence.get(0));
            if (logarithmProbability) {
                viterbi[i][0] = transitionProbability + emissionProbability;
            } else {
                viterbi[i][0] = transitionProbability * emissionProbability;
            }
            backpointer[i][0] = -1;
        }

        for (int i = 1; i < sentence.size(); i++) {
            for (int j = 0; j < tags.size(); j++) {
                int argMax = argMax(viterbi, j, i - 1);
                double transitionProbability = getTransitionProbability(tags.get(argMax), tags.get(j));
                double emissionProbability = getEmissionProbability(tags.get(j), sentence.get(i));
                if (logarithmProbability) {
                    viterbi[j][i] = viterbi[argMax][i - 1] + transitionProbability + emissionProbability;
                } else {
                    viterbi[j][i] = viterbi[argMax][i - 1] * transitionProbability * emissionProbability;
                }
                backpointer[j][i] = argMax;
            }
        }

        int argMax = argMax(viterbi, tags.indexOf(CustomTag.END), sentence.size() - 1);
        ArrayList<Pair<String, String>> output = new ArrayList<>();
        int idxTag = argMax;
        for (int j = sentence.size() - 1; j >= 0; j--) {
            String word = sentence.get(j);
            String tag = tags.get(idxTag);
            output.add(new Pair<>(word, tag));
            idxTag = backpointer[idxTag][j];
        }

        Collections.reverse(output);
        return output;
    }

    private int argMax(double[][] viterbi, int idxTag, int idxWord) {
        int argMax = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        ArrayList<String> tags = getTags();

        for (int k = 0; k < tags.size(); k++) {
            double transitionProbability = getTransitionProbability(tags.get(k), tags.get(idxTag));
            double currentValue;
            if (logarithmProbability) {
                currentValue = viterbi[k][idxWord] + transitionProbability;
            } else {
                currentValue = viterbi[k][idxWord] * transitionProbability;
            }
            if (currentValue > maxValue) {
                argMax = k;
                maxValue = currentValue;
            }
        }

        return argMax;
    }
}
