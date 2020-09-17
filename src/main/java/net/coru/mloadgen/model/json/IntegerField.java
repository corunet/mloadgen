package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IntegerField extends Field {

	String defaultValue;

	int minimum;

	int maximum;

	@Builder(toBuilder = true)
	public IntegerField(String name, String defaultValue, int minimum, int maximum) {
		super(name, "number");
		this.defaultValue = defaultValue;
		this.maximum = maximum;
		this.minimum = minimum;
	}

	@Override
	public Field cloneField(String fieldName) {
		return this.toBuilder().name(fieldName).build();
	}
}
