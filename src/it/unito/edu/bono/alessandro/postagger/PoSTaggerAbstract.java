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

import it.unito.edu.bono.alessandro.normalizer.Normalizer;
import it.unito.edu.bono.alessandro.util.Counter;
import it.unito.edu.bono.alessandro.util.Pair;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public abstract class PoSTaggerAbstract implements PoSTagger {

    protected String devSetPath;
    protected Counter counter = new Counter();

    @Override
    public void setTraningSet(String trainingSetPath) {
        counter.setFilePath(trainingSetPath);
    }

    @Override
    public void setdDevSet(String devSetPath) {
        this.devSetPath = devSetPath;
    }

    @Override
    public void setNormalizer(Normalizer normalizer) {
        counter.setNormalizer(normalizer);
    }

    @Override
    public void train() throws IOException {
        counter.count();
    }

    @Override
    public abstract ArrayList<Pair<String, String>> tagPhrase(ArrayList<String> phrase) throws IOException;

}
