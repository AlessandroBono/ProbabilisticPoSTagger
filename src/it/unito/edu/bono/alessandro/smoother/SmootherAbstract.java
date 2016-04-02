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
import it.unito.edu.bono.alessandro.util.Counter;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public abstract class SmootherAbstract implements Smoother {

    protected Counter counter = null;
    protected Normalizer normalizer = null;

    @Override
    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void setNormaizer(Normalizer normalizer) {
        this.normalizer = normalizer;
    }

    @Override
    public abstract double smooth(String tag, String word);
}
