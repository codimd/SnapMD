package de.claudiuscoenen.hackmdsnapshot.api.model;


import java.util.Collections;
import java.util.List;

import de.claudiuscoenen.hackmdsnapshot.model.Pad;

public class History {

	private List<Pad> history;

	public List<Pad> getHistory() {
		return history == null ? Collections.emptyList() : history;
	}

	public void setHistory(List<Pad> history) {
		this.history = history;
	}

	@Override
	public String toString() {
		return "History{" +
				"history=" + history +
				'}';
	}
}
