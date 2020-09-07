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
@Builder(toBuilder = true)
public abstract class Field {

	private String name;
	private String type;

	public Field cloneField(String fieldName) {
		return this.toBuilder().name(fieldName).build();
	}

}
