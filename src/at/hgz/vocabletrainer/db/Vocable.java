package at.hgz.vocabletrainer.db;

public class Vocable {

	private int id;
	private int dictionaryId;
	private String picture;
	private String word;
	
	public Vocable(int id, int dictionaryId, String picture, String word) {
		this.id = id;
		this.dictionaryId = dictionaryId;
		this.picture = picture;
		this.word = word;
	}

	public int getId() {
		return id;
	}

	public int getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(int trainingSetId) {
		this.dictionaryId = trainingSetId;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
