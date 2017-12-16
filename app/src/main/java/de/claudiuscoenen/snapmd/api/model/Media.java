package de.claudiuscoenen.snapmd.api.model;


@SuppressWarnings("unused")
public class Media {

	private String link;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "Media{" +
				"link='" + link + '\'' +
				'}';
	}
}
