package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
public class StringField extends Field {

	static final String type = "string";

	String name;

	String defaultValue;

	String value;
}
