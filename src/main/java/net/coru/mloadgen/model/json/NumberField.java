package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@EqualsAndHashCode(callSuper = true)
public class NumberField extends Field {

	String defaultValue;

	@Builder
	public NumberField(String name, String defaultValue, Number value) {
		super(name, "NUMBER");
		this.defaultValue = defaultValue;
	}
}
