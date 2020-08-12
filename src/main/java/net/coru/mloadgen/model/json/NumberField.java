package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
public class NumberField extends Field {

	static final String type = "number";

	String name;

	String defaultValue;

	Number value;
}
