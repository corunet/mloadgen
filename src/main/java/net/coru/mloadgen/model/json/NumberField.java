package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NumberField extends Field {

	String defaultValue;

	@Builder
	public NumberField(String name, String defaultValue) {
		super(name, "number");
		this.defaultValue = defaultValue;
	}
}
