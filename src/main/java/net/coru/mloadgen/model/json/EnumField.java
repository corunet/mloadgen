package net.coru.mloadgen.model.json;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EnumField extends Field {

	String defaultValue;

	@Singular
	List<String> enumValues;

	@Builder
	public EnumField(String name, String defaultValue, List<String> enumValues) {
		super(name, "enum");
		this.defaultValue = defaultValue;
		this.enumValues = enumValues;
	}
}
