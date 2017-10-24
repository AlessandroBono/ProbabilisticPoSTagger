# ProbabilisticPoSTagger
A Java implementation of different probabilistic [part-of-speech (PoS) tagging](https://en.wikipedia.org/wiki/Part-of-speech_tagging) techniques.

## PoS tagging techniques
The PoS tagging techniques implemented are the following:

* **BaseLinePoSTagger**
Simple PoS tagger that assigns to each word the most common tag of that word.

* **ViterbiPoSTagger**
The [Viterbi algorithm](https://en.wikipedia.org/wiki/Viterbi_algorithm) is a [dynamic programming technique](https://en.wikipedia.org/wiki/Dynamic_programming) that assumes a [Hidden Markov Model (HMM)](https://en.wikipedia.org/wiki/Hidden_Markov_model) in which the unobserved states are the correct sequence of part-of-speech tags while the visible output, dependent on the state, are the words that compose the sentence. This is an implementation of the Viterbi algorithm that uses [bigrams](https://en.wikipedia.org/wiki/Bigram), therefore it assumes that each state transition depends only on the previous state.

* **TrigramsPoSTagger**
This is an implementation of the Viterbi algorithm that uses [trigrams](https://en.wikipedia.org/wiki/Trigram) instead of bigrams, therefore assumes that state transition depends on the previous two states. This implementation may suffer from sparseness issues, which are mitigated thanks to the deleted interpolation algorithm.

## Normalizing techniques
Furthermore, in order to improve the overall tagging performance, the following normalizing techniques have been implemented:

* **CapitalizeNormalizer**
Each word is capitalized. For example, both `Chair` and `chair` become `CHAIR`.

* **LemmaNormalizer**
Each word is reduced to its lemma. For example, `go`, `goes`, `going`, `went`, and `gone` become `go`.

## Smoothing techniques
Finally, since datasets are not able to represent all the possible situations, different smoothing techniques are present in order to being able to estimate the probability that an unknown words can be assigned to a given tag. Some of the implemented smoothing techniques are the following:

* **OneOverNSmoother**
Given that the training dataset contains `nTag` tags. This smoother assigns a probability of `1 / nTags` to each tag.

* **NounSmoother**
This smoother take advantage of the fact that the `NOUN` tag is the most common tag. Given that the training dataset contains `nTag` tags. It assigns a probability of `(nTags - 1) / nTags` to the `NOUN` tag, `1 / nTags` otherwise.

* **MorphItSmoother**
This smoother is able to give a more accurate probability by looking at how much frequent a tag is for a given word in the [Morph-it](http://sslmitdev-online.sslmit.unibo.it/linguistics/morph-it.php) resource.

