package at.hgz.picturetrainer.set;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import at.hgz.picturetrainer.db.Vocable;

public class TrainingSet {
	
	public static final int DIRECTION_FORWARD = 1;
	public static final int DIRECTION_BACKWARD = 2;
	public static final int DIRECTION_BIDIRECTIONAL = 3;

	private List<TrainingElem> list;

	public TrainingSet(List<Vocable> vocables, int direction) {
		list = new LinkedList<TrainingElem>();
		if (direction == DIRECTION_FORWARD || direction == DIRECTION_BIDIRECTIONAL) {
			createList1to2(vocables);
		}
		if (direction == DIRECTION_BACKWARD || direction == DIRECTION_BIDIRECTIONAL) {
			createList2to1(vocables);
		}
		Collections.shuffle(list);
	}

	/**
	 * List for language 1 to language 2
	 * 
	 * @param vocables
	 */
	private void createList1to2(List<Vocable> vocables) {
		for (Vocable vocable : vocables) {
			list.add(new TrainingElem(vocable.getPicture(), vocable.getWord(),
					vocables, false));
		}
	}

	/**
	 * List for language 2 to language 1
	 * 
	 * @param vocables
	 */
	private void createList2to1(List<Vocable> vocables) {
		for (Vocable vocable : vocables) {
			list.add(new TrainingElem(vocable.getPicture(), vocable.getWord(),
					vocables, true));
		}
	}

	public List<TrainingElem> getList() {
		return list;
	}

}
