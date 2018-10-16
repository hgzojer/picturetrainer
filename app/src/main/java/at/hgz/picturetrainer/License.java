package at.hgz.picturetrainer;

public class License {

	private String moduleName;
	
	private String author;
	
	private String licenseText;

	public License(String moduleName, String author, String licenseText) {
		this.moduleName = moduleName;
		this.author = author;
		this.licenseText = licenseText;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		if (getAuthor() == null) {
			return getModuleName();
		}
		if ("*".equals(getAuthor())) {
			return getModuleName() + " (authors: see homepage)";
		}
		return getModuleName() + " (by " + getAuthor() + ")";
	}

	public String getLicenseText() {
		return licenseText;
	}
	
}
