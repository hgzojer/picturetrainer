package at.hgz.picturetrainer.zip;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="vocable")
public class XmlVocable {

	@Element
	private String picture;
	
	@Element
	private String word;

	public XmlVocable() {
	}

	public XmlVocable(String picture, String word) {
		this.picture = picture;
		this.word = word;
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
