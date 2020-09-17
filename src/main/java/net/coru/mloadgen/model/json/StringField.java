package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StringField extends Field {

	@Builder(toBuilder = true)
	public StringField(String name) {
		super(name, "string");
	}

	@Override
	public Field cloneField(String fieldName) {
		return this.toBuilder().name(fieldName).build();
	}
}
