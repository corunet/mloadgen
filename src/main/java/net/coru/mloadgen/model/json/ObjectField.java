package net.coru.mloadgen.model.json;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
public class ObjectField extends Field{

	static final String type = "object";

	String name;

	List<Field> value;
}
