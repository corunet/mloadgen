package net.coru.mloadgen.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class Field {

	String name;
	String type;

	abstract public Field cloneField(String fieldName);

}
