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

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public abstract class PoSTaggerAbstract {

    protected String trainingSetPath;
    protected String devSetPath;
    protected Counter counter;

    public void setTraningSet(String traningSetPath) {
        this.trainingSetPath = traningSetPath;
    }

    public void setdDevSet(String devSetPath) {
        this.devSetPath = devSetPath;
    }

    public void train() throws IOException {
        counter = new Counter(trainingSetPath);
        counter.count();
    }

    public abstract ArrayList<Pair<String, String>> tagPhrase(ArrayList<String> phrase) throws IOException;

}
