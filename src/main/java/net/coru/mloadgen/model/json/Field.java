package net.coru.mloadgen.model.json;

import lombok.Getter;

@Getter
public abstract class Field {

	private String name;
	private String type;

	public Field(String name, String type) {
		this.name = name;
		this.type = type;
	}
}
