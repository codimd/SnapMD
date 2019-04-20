package de.claudiuscoenen.snapmd.api.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.claudiuscoenen.snapmd.model.Pad;

public class History {

	private List<Pad> history;

	public List<Pad> getHistory() {
		if (history == null) {
			return Collections.emptyList();
		}
		history.sort(Comparator.comparingLong(Pad::getTime).reversed());
		return history;
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
