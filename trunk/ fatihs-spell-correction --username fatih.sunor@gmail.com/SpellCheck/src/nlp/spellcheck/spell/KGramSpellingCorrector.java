package nlp.spellcheck.spell;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nlp.spellcheck.util.Counter;
import nlp.spellcheck.util.IOUtils;
import nlp.spellcheck.util.StringUtils;

public class KGramSpellingCorrector implements SpellingCorrector {
	protected static final int CORRECTION_COUNT = 10;
	private Map<String, List<String>> invertedIndex = new HashMap<String, List<String>>();

	/** Initializes spelling corrector by indexing kgrams in words from a file */
	public KGramSpellingCorrector(){
		File path = new File("/afs/ir/class/cs276/pe1-2011/big.txt.gz");
		// File path = new File("C:/big.txt.gz");
		Set<String> words = new HashSet<String>();
		Counter<String> wordCount = new Counter<String>();
		int numTotalWords = 0;
		for (String line : IOUtils.readLines(IOUtils.openFile(path))){
			for (String word : StringUtils.tokenize(line)){
    			wordCount.incrementCount(word);
            	numTotalWords++;
            	if(!words.contains(word)){
            		words.add(word);
            		List<String> kgrams = getGrams(word, 2);
            		addWordToIndex(word, kgrams);
            	}
            }
        }
	}

	private List<String> getGrams(String word, int k) {
		List<String> kGrams = new ArrayList<String>();
		String start = "$" + word.substring(0, k - 1);
		kGrams.add(start);
		for(int wordIndex = 0; wordIndex < word.length() - k + 1; wordIndex++) {
			kGrams.add(word.substring(wordIndex, wordIndex + k));
		}
		String end = word.substring(word.length() - k + 1) + "$";
		kGrams.add(end);
		return kGrams;
	}
	
	private void addWordToIndex(String word, List<String> kgrams) {
		for(String kgram : kgrams) {
			List<String> wordList = invertedIndex.get(kgram);
			if(wordList != null) {
				wordList.add(word);
			}else{
				List<String> newWordList = new ArrayList<String>();
				newWordList.add(word);
				invertedIndex.put(kgram, newWordList);
			}
		}
	}
	
	public List<String> corrections(String word){
		List<String> kgrams = getGrams(word, 2);
		Counter<String> counter = new Counter<String>();
		for(String kgram : kgrams){
			List<String> words = invertedIndex.get(kgram);
			if(words != null){
				for(String wordIterator : words){
					counter.incrementCount(wordIterator);
				}
			}
		}
		for(Entry<String, Double> e : counter.entrySet()){
			String temporaryWord = (String) e.getKey();
			Double temporaryCount = (Double) e.getValue();
			double unionSize = word.length() + temporaryWord.length() - temporaryCount;
			counter.setCount(temporaryWord, temporaryCount / unionSize);
	    }
	    return counter.topK(CORRECTION_COUNT);
	}
}
