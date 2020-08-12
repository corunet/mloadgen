package net.coru.mloadgen.model.json;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
public class ArrayField extends Field{

	static final String type = "array";

	String name;

	String defaultValue;

	List<Field> value;
}
