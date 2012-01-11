package nlp.spellcheck.spell;

import java.util.List;

import nlp.spellcheck.util.Counter;
import nlp.spellcheck.util.StringUtils;

public class KGramWithEditDistancesSpellingCorrector extends KGramSpellingCorrector {
  
  public List<String> corrections(String word) {
	  List<String> corrections = super.corrections(word);
	    Counter<String> sortedWords = new Counter<String>(); 
	    for(String suggestion : corrections) {
	      Double position = StringUtils.levenshtein(suggestion, word);
	      sortedWords.setCount(suggestion, 1 / position);
	    }
	    return sortedWords.topK(CORRECTION_COUNT);
  }
}