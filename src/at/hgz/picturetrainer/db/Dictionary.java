package at.hgz.picturetrainer.db;

public class Dictionary {

	private int id;
	private byte[] picture;
	private String name;
	
	public Dictionary(int id, byte[] picture, String name) {
		this.id = id;
		this.picture = picture;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
