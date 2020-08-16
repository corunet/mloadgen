package net.coru.mloadgen.model.json;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@EqualsAndHashCode(callSuper = true)
public class ArrayField extends Field{

	@Singular
	List<Field> value;

	@Builder
	public ArrayField(String name, List<Field> value) {
		super(name, "ARRAY");
		this.value = value;
	}
}
