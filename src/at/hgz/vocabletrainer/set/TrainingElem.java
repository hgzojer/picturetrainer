package at.hgz.picturetrainer.set;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import at.hgz.picturetrainer.db.Vocable;

public class TrainingElem {

	private String picture;
	private String word;
	private String language1;
	private String language2;
	
	private List<Vocable> vocables;
	private boolean flipVocables;
	
	/**
	 * 
	 * @param picture
	 * @param word
	 * @param language1
	 * @param language2
	 * @param vocables
	 * @param flipVocables false if DIRECTION_FORWARD, true if DIRECTION_BACKWARD
	 */
	public TrainingElem(String picture, String word, String language1,
			String language2, List<Vocable> vocables, boolean flipVocables) {
		this.picture = picture;
		this.word = word;
		this.language1 = language1;
		this.language2 = language2;
		this.vocables = vocables;
		this.flipVocables = flipVocables;
	}

	public String getPicture() {
		return picture;
	}
	
	public String getWord() {
		return word;
	}
	
	public String[] getAlternatives() {
		List<Vocable> pool = new LinkedList<Vocable>(vocables);
		if (flipVocables) {
			for (Iterator<Vocable> it = pool.iterator(); it.hasNext(); ) {
				Vocable vocable = it.next();
				if (vocable.getPicture() == word && vocable.getWord() == picture) {
					it.remove();
				}
			}
			if (pool.size() < 2) {
				pool.add(new Vocable(-1, -1, word + "a", picture + "a"));
				pool.add(new Vocable(-1, -1, word + "o", picture + "o"));
			}
			Vocable alt1 = pool.remove((int) (Math.random() * pool.size()));
			Vocable alt2 = pool.remove((int) (Math.random() * pool.size()));
			return new String[] { alt1.getPicture(), alt2.getPicture() };
		} else {
			for (Iterator<Vocable> it = pool.iterator(); it.hasNext(); ) {
				Vocable vocable = it.next();
				if (vocable.getPicture() == picture && vocable.getWord() == word) {
					it.remove();
				}
			}
			if (pool.size() < 2) {
				pool.add(new Vocable(-1, -1, picture + "a", word + "a"));
				pool.add(new Vocable(-1, -1, picture + "o", word + "o"));
			}
			Vocable alt1 = pool.remove((int) (Math.random() * pool.size()));
			Vocable alt2 = pool.remove((int) (Math.random() * pool.size()));
			return new String[] { alt1.getWord(), alt2.getWord() };
		}
	}

	public String getLanguage1() {
		return language1;
	}

	public String getLanguage2() {
		return language2;
	}
	
}
