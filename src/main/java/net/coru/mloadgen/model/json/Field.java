package net.coru.mloadgen.model.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class Field {

	private String name;
	private String type;

	public Field(String name, String type) {
		this.name = name;
		this.type = type;
	}
}
