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

import it.unito.edu.bono.alessandro.util.CustomArray;
import it.unito.edu.bono.alessandro.util.SparseMatrix;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alessandro Bono <alessandro.bono@edu.unito.it>
 */
public class MorphItSmoother extends SmootherAbstract {

    private final List<String> knownTags = new ArrayList<>();
    private final SparseMatrix morphIt = new SparseMatrix();

    @Override
    public double smooth(String tag, String word) {
        int nTags = knownTags.size();
        CustomArray tagsCount = new CustomArray();
        int totalCount = 0;

        if (morphIt.getRows().contains(word)) {
            for (String pos : knownTags) {
                int count = morphIt.get(word, pos);
                totalCount += count;
                tagsCount.increment(pos, count);
            }
            return tagsCount.get(tag) / (double) totalCount;
        } else {
            // verifico se è un numero
            if (tag.equals("NUM")) {
                if (word.matches("('|-)?\\d+((.|,)\\d+)?")) {
                    return 1;
                } else {
                    return 0;
                }
            }

            // ricadiamo nel caso in cui assumiamo che NOUN è il tag più comune
            if (tag.equals("NOUN")) {
                return (nTags - 1) / (double) nTags;
            } else {
                return 1 / (double) (nTags * (nTags - 1));
            }
        }
    }

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

        reader = new BufferedReader(new FileReader("data/morph-it_048.txt"));
        while ((line = reader.readLine()) != null) {
            // in morphit ogni riga è così composta: <parola, lemma, tag>
            String[] temp = line.split("\t");
            if (temp.length == 3) {
                String word = temp[0];
                String tag = temp[2];
                word = normalizer.normalize(word);
                tag = simplifyTag(tag);
                morphIt.increment(word, tag);
            }
        }
        reader.close();
    }

    private String simplifyTag(String tag) {
        // i PoS di MorphIt sono più complessi, qui li semplifichiamo
        if (tag.contains(":")) {
            tag = tag.substring(0, tag.indexOf(":"));
        }
        if (tag.contains("NUM")) {
            tag = "NUM";
        } else if (tag.contains("-")) {
            tag = tag.substring(0, tag.indexOf("-"));
        }

        // MorphIt usa dei tag diversi, qua cerchiamo di allinearli
        // con quelli UD
        switch (tag) {
            case "NOUN":
                tag = "NOUN";
                break;
            case "NPR":
                tag = "PROPN";
                break;
            case "VER":
            case "MOD":
            case "CAU":
            case "ASP":
                tag = "VERB";
                break;
            case "ADJ":
                tag = "ADJ";
                break;
            case "CON":
                tag = "CONJ";
                break;
            case "PRO":
                tag = "PRON";
                break;
            case "ADV":
                tag = "ADV";
                break;
            case "PRE":
                tag = "ADP";
                break;
            case "DET":
                tag = "DET";
                break;
            case "PON":
            case "SENT":
                tag = "PUNCT";
                break;
            case "NUM":
                tag = "NUM";
                break;
            case "AUX":
                tag = "AUX";
                break;
            case "INT":
                tag = "INTJ";
                break;
            case "SYM":
            case "SMI":
            case "ABL":
                tag = "SYM";
                break;
            case "ART":
            case "ARTPRE":
            case "TALE":
                tag = "DET";
                break;
            case "WH":
                tag = "SCONJ";
            default:
        }
        return tag;
    }

}
