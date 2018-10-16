package at.hgz.picturetrainer.set;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import at.hgz.picturetrainer.ImportActivity;
import at.hgz.picturetrainer.db.Vocable;
import at.hgz.picturetrainer.img.PictureUtil;

public class TrainingElem {

	private byte[] picture;
	private String word;
	
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
	public TrainingElem(byte[] picture, String word, List<Vocable> vocables, boolean flipVocables) {
		this.picture = picture;
		this.word = word;
		this.vocables = vocables;
		this.flipVocables = flipVocables;
	}

	public byte[] getPicture() {
		return picture;
	}
	
	public String getWord() {
		return word;
	}
	
	public TrainingElem[] getAlternatives() {
		List<Vocable> pool = new LinkedList<Vocable>(vocables);
		for (Iterator<Vocable> it = pool.iterator(); it.hasNext(); ) {
			Vocable vocable = it.next();
			if (vocable.getPicture() == picture && vocable.getWord() == word) {
				it.remove();
			}
		}
		if (pool.size() < 2) {
		    PictureUtil util = PictureUtil.getInstance(null);
			pool.add(new Vocable(-1, -1, util.getResourcePicture("cherry"), word + "a"));
			pool.add(new Vocable(-1, -1, util.getResourcePicture("apple"), word + "o"));
		}
		Vocable alt1 = pool.remove((int) (Math.random() * pool.size()));
		Vocable alt2 = pool.remove((int) (Math.random() * pool.size()));
		TrainingElem alt1elem = new TrainingElem(alt1.getPicture(), alt1.getWord(), vocables, flipVocables);
		TrainingElem alt2elem = new TrainingElem(alt2.getPicture(), alt2.getWord(), vocables, flipVocables);
		return new TrainingElem[] { alt1elem, alt2elem };
	}

	public boolean isFlipVocables() {
		return flipVocables;
	}
	
}
