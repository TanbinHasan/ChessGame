package com.chess.model;

public enum Mode {
	MANUAL_ONLY("Player vs Player");
	private final String value;
	Mode(String value) {
		this.value = value;
	}
	// ---------------------------------- GENERIC GETTER ----------------------------------
	public String get() {
		return value;
	}
}
