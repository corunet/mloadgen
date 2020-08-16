package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class BooleanField extends Field {

	String defaultValue;

	@Builder
	public BooleanField(String name, String defaultValue) {
		super(name, "BOOLEAN");
		this.defaultValue = defaultValue;
	}
}
