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

import it.unito.edu.bono.alessandro.util.CustomTag;
import it.unito.edu.bono.alessandro.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class ViterbiPoSTagger extends PoSTaggerAbstract {

    @Override
    public ArrayList<Pair<String, String>> tagPhrase(ArrayList<String> phrase) throws IOException {
        ArrayList<String> tags = counter.getTags();
        double[][] viterbi = new double[tags.size()][phrase.size()];
        int[][] backpointer = new int[tags.size()][phrase.size()];

        for (int i = 0; i < tags.size(); i++) {
            double transitionProbability = counter.getTransitionProbability(CustomTag.START, tags.get(i));
            double emissionProbability = counter.getEmissionProbability(tags.get(i), phrase.get(0));
            viterbi[i][0] = transitionProbability * emissionProbability;
            backpointer[i][0] = -1;
        }

        for (int i = 1; i < phrase.size(); i++) {
            for (int j = 0; j < tags.size(); j++) {
                int argMax = argMax(viterbi, j, i - 1);
                double transitionProbability = counter.getTransitionProbability(tags.get(argMax), tags.get(j));
                double emissionProbability = counter.getEmissionProbability(tags.get(j), phrase.get(i));
                viterbi[j][i] = viterbi[argMax][i - 1] * transitionProbability * emissionProbability;
                backpointer[j][i] = argMax;
            }
        }

        int argMax = argMax(viterbi, tags.indexOf(CustomTag.END), phrase.size() - 1);
        ArrayList<Pair<String, String>> output = new ArrayList<>();
        int idxTag = argMax;
        for (int j = phrase.size() - 1; j >= 0; j--) {
            String word = phrase.get(j);
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
        ArrayList<String> tags = counter.getTags();

        for (int k = 0; k < tags.size(); k++) {
            double transitionProbability = counter.getTransitionProbability(tags.get(k), tags.get(idxTag));
            double currentValue = viterbi[k][idxWord] * transitionProbability;
            if (currentValue > maxValue) {
                argMax = k;
                maxValue = currentValue;
            }
        }

        return argMax;
    }
}
