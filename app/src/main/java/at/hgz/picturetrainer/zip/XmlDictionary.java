package at.hgz.picturetrainer.zip;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="dictionary")
public class XmlDictionary {
	
	@Element
	private String picture;
	
	@Element
	private String name;
	
	@ElementList
	private ArrayList<XmlVocable> vocables;

	public XmlDictionary() {
	}

	public XmlDictionary(String picture, String name,
			ArrayList<XmlVocable> vocables) {
		this.picture = picture;
		this.name = name;
		this.vocables = vocables;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<XmlVocable> getVocables() {
		return vocables;
	}

	public void setVocables(ArrayList<XmlVocable> vocables) {
		this.vocables = vocables;
	}
	
}
