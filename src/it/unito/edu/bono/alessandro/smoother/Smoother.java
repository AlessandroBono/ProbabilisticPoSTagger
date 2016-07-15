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
package it.unito.edu.bono.alessandro.smoother;

import it.unito.edu.bono.alessandro.normalizer.Normalizer;
import java.io.IOException;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public interface Smoother {

    public void setTrainingSet(String trainingSetPath);

    public void setDevSet(String DevSetPath);

    public void train() throws IOException;

    public double smooth(String tag, String word);

    public void setNormalizer(Normalizer normalizer);

}
