package net.coru.mloadgen.model.json;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
public class EnumField extends Field {

	static final String type = "enum";

	String name;

	String defaultValue;

	List<String> enumValues;

	String value;
}
