package de.claudiuscoenen.snapmd.model;


import android.util.Log;

import java.util.List;

public class Pad {

	private String id;
	private String text;
	private Long time;
	private List<String> tags;

	public Pad() {
	}

	public Pad(String id, String text) {
		this.id = id;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setTime(String time) {
		Log.w("Pad", "invalid JSON entry for " + this.getId());
		this.time = Long.parseLong(time, 10);
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Pad{" +
				"id='" + id + '\'' +
				", text='" + text + '\'' +
				", time=" + time +
				", tags=" + tags +
				'}';
	}
}
