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
import it.unito.edu.bono.alessandro.util.SparseCube;
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
public class TrigramsPoSTagger extends PoSTaggerAbstract {

    private final CustomArray unigramTransition = new CustomArray();
    private final SparseMatrix bigramTransition = new SparseMatrix();
    private final SparseCube trigramTransition = new SparseCube();
    private final SparseMatrix emissionMatrix = new SparseMatrix();
    private ArrayList<String> knownWords;
    private int totalToken = 0;
    private double lambda1 = 0;
    private double lambda2 = 0;
    private double lambda3 = 0;

    @Override
    public void train() throws IOException {
        String line;
        String oldOldTag = CustomTag.PRESTART;
        String oldTag = CustomTag.START;
        String tag;
        BufferedReader reader = new BufferedReader(new FileReader(trainingSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String word = temp[0];
                tag = temp[1];
                word = normalizer.normalize(word);
                totalToken++;
                emissionMatrix.increment(tag, word);
                unigramTransition.increment(tag);
                bigramTransition.increment(oldTag, tag);
                trigramTransition.increment(oldOldTag, oldTag, tag);
            } else { // è finita la frase
                tag = CustomTag.END;
                unigramTransition.increment(tag);
                bigramTransition.increment(oldTag, tag);
                trigramTransition.increment(oldOldTag, oldTag, tag);

                oldTag = CustomTag.PRESTART;
                tag = CustomTag.START;
                unigramTransition.increment(tag);
                bigramTransition.increment(oldTag, tag);
                trigramTransition.increment(oldOldTag, oldTag, tag);
            }
            oldOldTag = oldTag;
            oldTag = tag;
        }
        reader.close();
        deletedInterpolation();

        // list of known word for smoothing, we sort them so that search for
        // unknown words will be easier
        knownWords = getWords();
        Collections.sort(knownWords);
        if (smoother != null) {
            smoother.train();
        }
    }

    private void deletedInterpolation() throws IOException {
        String line;
        String oldOldTag = CustomTag.PRESTART;
        String oldTag = CustomTag.START;
        String tag;
        BufferedReader reader = new BufferedReader(new FileReader(trainingSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                tag = temp[1];
                deletedInterpolationImpl(oldOldTag, oldTag, tag);
            } else { // è finita la frase
                tag = CustomTag.END;
                deletedInterpolationImpl(oldOldTag, oldTag, tag);

                oldTag = CustomTag.PRESTART;
                tag = CustomTag.START;
            }
            oldOldTag = oldTag;
            oldTag = tag;
        }

        reader.close();

        double totalLambda = lambda1 + lambda2 + lambda3;

        lambda1 /= totalLambda;
        lambda2 /= totalLambda;
        lambda3 /= totalLambda;
    }

    private void deletedInterpolationImpl(String oldOldTag, String oldTag, String tag) {
        double trigramProb;
        double bigramProb;
        double unigramProb;

        int trigramProbDenominator = bigramTransition.get(oldOldTag, oldTag) - 1;
        if (trigramProbDenominator == 0) {
            trigramProb = 0;
        } else {
            trigramProb = (trigramTransition.get(oldOldTag, oldTag, tag) - 1) / (double) trigramProbDenominator;
        }

        int bigramProbDenominator = unigramTransition.get(oldTag) - 1;
        if (bigramProbDenominator == 0) {
            bigramProb = 0;
        } else {
            bigramProb = (bigramTransition.get(oldTag, tag) - 1) / (double) bigramProbDenominator;
        }

        unigramProb = (unigramTransition.get(tag) - 1) / (double) (totalToken - 1);

        if (trigramProb >= bigramProb && trigramProb >= unigramProb) {
            lambda3 += trigramTransition.get(oldOldTag, oldTag, tag);
        } else if (bigramProb >= trigramProb && bigramProb >= unigramProb) {
            lambda2 += trigramTransition.get(oldOldTag, oldTag, tag);
        } else { // unigramProb is the greatest
            lambda1 += trigramTransition.get(oldOldTag, oldTag, tag);
        }
    }

    private ArrayList<String> getTags() {
        return new ArrayList<>(unigramTransition.getIdexes());
    }

    private ArrayList<String> getWords() {
        return new ArrayList<>(emissionMatrix.getColumns());
    }

    private double getEmissionProbability(String tag, String word) {
        word = normalizer.normalize(word);
        // if (!knownWords.contains(word)) is too much inefficient (~10 sec)
        // we use a binary search, given that we sorted the known word
        // during train()
        if (smoother != null && Collections.binarySearch(knownWords, word) < 0) {
            return smoother.smooth(tag, word);
        }
        return emissionMatrix.get(tag, word) / (double) unigramTransition.get(tag);
    }

    private double getTransitionProbability(String oldOldTag, String oldTag, String tag) {
        double trigramProb;
        double bigramProb;
        double unigramProb;

        int trigramProbDenominator = bigramTransition.get(oldOldTag, oldTag);
        if (trigramProbDenominator == 0) {
            trigramProb = 0;
        } else {
            trigramProb = trigramTransition.get(oldOldTag, oldTag, tag) / (double) trigramProbDenominator;
        }

        int bigramProbDenominator = unigramTransition.get(oldTag);
        if (bigramProbDenominator == 0) {
            bigramProb = 0;
        } else {
            bigramProb = bigramTransition.get(oldTag, tag) / (double) bigramProbDenominator;
        }

        unigramProb = unigramTransition.get(tag) / (double) totalToken;

        return lambda3 * trigramProb + lambda2 * bigramProb + lambda1 * unigramProb;
    }

    @Override
    public ArrayList<Pair<String, String>> tagSentence(ArrayList<String> sentence) {
        ArrayList<String> tags = getTags();
        int tagsSize = tags.size();
        int sentenceSize = sentence.size();
        double[][][] viterbi = new double[tagsSize][tagsSize][sentenceSize];
        int[][][] backpointer = new int[tagsSize][tagsSize][sentenceSize];

        for (int i = 0; i < tagsSize; i++) {
            int startIdx = tags.indexOf(CustomTag.START);
            double transitionProbability = getTransitionProbability(CustomTag.PRESTART, CustomTag.START, tags.get(i));
            double emissionProbability = getEmissionProbability(tags.get(i), sentence.get(0));
            viterbi[startIdx][i][0] = transitionProbability * emissionProbability;
            backpointer[startIdx][i][0] = -1;
        }

        for (int i = 1; i < sentenceSize; i++) {
            for (int j = 0; j < tagsSize; j++) {
                for (int k = 0; k < tagsSize; k++) {
                    int argMax = argMax(viterbi, j, k, i - 1);
                    double transitionProbability = getTransitionProbability(tags.get(argMax), tags.get(j), tags.get(k));
                    double emissionProbability = getEmissionProbability(tags.get(k), sentence.get(i));
                    viterbi[j][k][i] = viterbi[argMax][j][i - 1] * transitionProbability * emissionProbability;
                    backpointer[j][k][i] = argMax;
                }
            }
        }

        // search for the two best tags (i and j) according to
        // the end of the sentence
        int iMax = -1;
        int jMax = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < tagsSize; i++) {
            for (int j = 0; j < tagsSize; j++) {
                double transitionProbability = getTransitionProbability(tags.get(i), tags.get(j), CustomTag.END);
                double currentValue = viterbi[i][j][sentence.size() - 1] * transitionProbability;
                if (currentValue > maxValue) {
                    maxValue = currentValue;
                    iMax = i;
                    jMax = j;
                }
            }
        }

        // start to follow the backpointer while assigning to each word
        // of the sentence the right tag
        ArrayList<Pair<String, String>> output = new ArrayList<>();
        int idxTag0 = iMax;
        int idxTag1 = jMax;
        output.add(new Pair<>(sentence.get(sentenceSize - 1), tags.get(idxTag1)));
        output.add(new Pair<>(sentence.get(sentenceSize - 2), tags.get(idxTag0)));
        for (int j = sentenceSize - 3; j >= 0; j--) {
            String word = sentence.get(j);
            String tag = tags.get(backpointer[idxTag0][idxTag1][j + 2]);
            output.add(new Pair<>(word, tag));
            idxTag1 = idxTag0;
            idxTag0 = backpointer[idxTag0][idxTag1][j + 2];
        }

        Collections.reverse(output);
        return output;
    }

    private int argMax(double[][][] viterbi, int idxTag0, int idxTag1, int idxWord) {
        int argMax = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        ArrayList<String> tags = getTags();
        int tagsSize = tags.size();

        for (int k = 0; k < tagsSize; k++) {
            double transitionProbability = getTransitionProbability(tags.get(k), tags.get(idxTag0), tags.get(idxTag1));
            double currentValue = viterbi[k][idxTag0][idxWord] * transitionProbability;
            if (currentValue > maxValue) {
                argMax = k;
                maxValue = currentValue;
            }
        }

        return argMax;
    }
}
