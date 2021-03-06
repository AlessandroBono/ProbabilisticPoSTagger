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
package it.unito.edu.bono.alessandro.ProbabilisticPoSTagger.smoother;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class OneOverNSmoother extends SmootherAbstract {

    private final List<String> knownTags = new ArrayList<>();

    @Override
    public void train() throws IOException {
        // count how many tags are present
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(trainingSetPath));
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] temp = line.split("\t");
                String tag = temp[1];
                if (!knownTags.contains(tag)) {
                    knownTags.add(tag);
                }
            }
        }
        reader.close();
    }

    @Override
    public double smooth(String tag, String word) {
        return 1 / (double) knownTags.size();
    }

}
