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

	String defaultValue;

	@Builder
	public StringField(String name, String defaultValue) {
		super(name, "STRING");
		this.defaultValue = defaultValue;
	}
}
