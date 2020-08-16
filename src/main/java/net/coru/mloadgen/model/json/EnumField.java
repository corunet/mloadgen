package net.coru.mloadgen.model.json;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@EqualsAndHashCode(callSuper = true)
public class EnumField extends Field {

	String defaultValue;

	@Singular
	List<String> enumValues;

	@Builder
	public EnumField(String name, String defaultValue, List<String> enumValues) {
		super(name, "ENUM");
		this.defaultValue = defaultValue;
		this.enumValues = enumValues;
	}
}
