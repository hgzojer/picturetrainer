package at.hgz.picturetrainer.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import at.hgz.picturetrainer.db.Dictionary;
import at.hgz.picturetrainer.db.Vocable;

public final class ZipUtil {
	
	private static final String DICTIONARY_XML = "dictionary.xml";
	private static ZipUtil instance;
	
	private static class ZipElem {
		public String filename;
		public byte[] data;
	}
	
	public static ZipUtil getInstance() {
		if (instance == null) {
			instance = new ZipUtil();
		}
		return instance;
	}

	public byte[] marshall(Dictionary dictionary, List<Vocable> vocables) {
		List<ZipElem> zipElems = new ArrayList<ZipElem>();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		try {
			Serializer serializer = new Persister(new Format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
			XmlDictionary xmlDictionary = new XmlDictionary();
			xmlDictionary.setName(dictionary.getName());
			ZipElem dictionaryPicture = new ZipElem();
			dictionaryPicture.filename = "IMG_"+ timeStamp;
			dictionaryPicture.data = dictionary.getPicture();
			zipElems.add(dictionaryPicture);
			xmlDictionary.setPicture(dictionaryPicture.filename);
			xmlDictionary.setName(dictionary.getName());
			ArrayList<XmlVocable> xmlVocables = new ArrayList<XmlVocable>();
			xmlDictionary.setVocables(xmlVocables);
			int i = 1;
			for (Vocable vocable : vocables) {
				ZipElem picture = new ZipElem();
				picture.filename = "IMG_"+ timeStamp + "_" + i;
				picture.data = vocable.getPicture();
				zipElems.add(picture);
				xmlVocables.add(new XmlVocable(picture.filename, vocable.getWord()));
				i++;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			serializer.write(xmlDictionary, out);
			ZipElem main = new ZipElem();
			main.filename = DICTIONARY_XML;
			main.data = out.toByteArray();
			zipElems.add(0, main);
			ByteArrayOutputStream zOut = new ByteArrayOutputStream();
			ZipOutputStream zipOut = new ZipOutputStream(zOut);
			for (ZipElem zipElem : zipElems) {
				ZipEntry zipEntry = new ZipEntry(zipElem.filename);
				//zipEntry.setSize(zipElem.data.length);
				zipOut.putNextEntry(zipEntry);
				zipOut.write(zipElem.data, 0, zipElem.data.length);
				zipOut.closeEntry();
			}
			zipOut.flush();
			zipOut.close();
			return zOut.toByteArray();
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	public static class Entity {
		private Dictionary dictionary;
		private List<Vocable> vocables;
		public Entity(Dictionary dictionary, List<Vocable> vocables) {
			this.dictionary = dictionary;
			this.vocables = vocables;
		}
		public Dictionary getDictionary() {
			return dictionary;
		}
		public List<Vocable> getVocables() {
			return vocables;
		}
	}
	
	public Entity unmarshall(byte[] zipBytes) {
		Map<String, ZipElem> zipElems = new HashMap<String, ZipElem>();
		try {
			ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(zipBytes));
			ZipEntry zipEntry = zipIn.getNextEntry();
			while (zipEntry != null) {
				ZipElem zipElem = new ZipElem();
				zipElem.filename = zipEntry.getName();
				zipElem.data = IOUtils.toByteArray(zipIn);
				zipElems.put(zipElem.filename, zipElem);
				zipEntry = zipIn.getNextEntry();
			}
			Serializer serializer = new Persister();
			byte[] dictionaryBytes = zipElems.get(DICTIONARY_XML).data;
			ByteArrayInputStream in = new ByteArrayInputStream(dictionaryBytes);
			XmlDictionary xmlDictionary = serializer.read(XmlDictionary.class, in);
			byte[] dictionaryPictureBytes;
			String name;
			if (xmlDictionary != null) {
				String picture = xmlDictionary.getPicture();
				dictionaryPictureBytes = zipElems.get(picture).data;
				name = xmlDictionary.getName();
			} else {
				dictionaryPictureBytes = null;
				name = null;
			}
			Dictionary dictionary = new Dictionary(-1, dictionaryPictureBytes, name);
			List<Vocable> vocables = new ArrayList<Vocable>();
			if (xmlDictionary != null && xmlDictionary.getVocables() != null) {
				for (XmlVocable xmlVocable : xmlDictionary.getVocables()) {
					if (xmlVocable != null) {
						String picture = xmlVocable.getPicture();
						byte[] pictureBytes = zipElems.get(picture).data;
						vocables.add(new Vocable(-1, -1, pictureBytes, xmlVocable.getWord()));
					}
				}
			}
			return new Entity(dictionary, vocables);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
