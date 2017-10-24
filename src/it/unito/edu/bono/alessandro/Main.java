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

import it.unito.edu.bono.alessandro.normalizer.LemmaNormalizer;
import it.unito.edu.bono.alessandro.postagger.PoSTagger;
import it.unito.edu.bono.alessandro.postagger.TrigramsPoSTagger;
import it.unito.edu.bono.alessandro.smoother.MorphItSmoother;
import it.unito.edu.bono.alessandro.util.Evaluator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList sentence = new ArrayList<>();
        sentence.add("io");
        sentence.add("mangio");
        sentence.add("la");
        sentence.add("mela");
        try {
            PoSTagger posTagger = new TrigramsPoSTagger();
            posTagger.setNormalizer(new LemmaNormalizer());
            posTagger.setSmoother(new MorphItSmoother());
            posTagger.setTrainingSet("data/ud12_for_POS_TAGGING-160229-train.txt");
            posTagger.setDevSet("data/ud12_for_POS_TAGGING-160229-dev.txt");
            long startTime = System.nanoTime();
            posTagger.train();
            long endTime = System.nanoTime();
            Evaluator evaluator = new Evaluator();
            evaluator.setPoSTagger(posTagger);
            evaluator.setTestSet("data/ud12_for_POS_TAGGING-160229-test.txt");
            evaluator.evaluate();
            System.out.println(posTagger.tagSentence(sentence));
            System.out.println("Training time: " + (endTime - startTime) / 1000000000.0);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
