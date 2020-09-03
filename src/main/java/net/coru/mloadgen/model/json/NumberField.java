package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NumberField extends Field {

	String defaultValue;

	Number minimum;

	Number maximum;

	@Builder
	public NumberField(String name, String defaultValue, Number minimum, Number maximum) {
		super(name, "number");
		this.defaultValue = defaultValue;
		this.maximum = maximum;
		this.minimum = minimum;
	}
}
