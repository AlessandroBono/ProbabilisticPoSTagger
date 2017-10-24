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
package it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.postagger;

import it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.normalizer.Normalizer;
import it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.smoother.Smoother;
import it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.util.Pair;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public abstract class PoSTaggerAbstract implements PoSTagger {

    protected String trainingSetPath;
    protected String devSetPath;
    protected Normalizer normalizer = (String word) -> word;
    protected Smoother smoother = null;

    @Override
    public void setDevSet(String devSetPath) {
        this.devSetPath = devSetPath;
        if (smoother != null) {
            smoother.setDevSet(devSetPath);
        }
    }

    @Override
    public void setTrainingSet(String trainingSetPath) {
        this.trainingSetPath = trainingSetPath;
        if (smoother != null) {
            smoother.setTrainingSet(trainingSetPath);
        }
    }

    @Override
    public void setNormalizer(Normalizer normalizer) {
        this.normalizer = normalizer;
        if (smoother != null) {
            smoother.setNormalizer(normalizer);
        }
    }

    @Override
    public void setSmoother(Smoother smoother) {
        this.smoother = smoother;
        if (devSetPath != null) {
            smoother.setDevSet(devSetPath);
        }
        if (trainingSetPath != null) {
            smoother.setTrainingSet(trainingSetPath);
        }
        if (normalizer != null) {
            smoother.setNormalizer(normalizer);
        }
    }

    @Override
    public abstract void train() throws IOException;

    @Override
    public abstract ArrayList<Pair<String, String>> tagSentence(ArrayList<String> sentence);

}
