package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BooleanField extends Field {

	@Builder
	public BooleanField(String name) {
		super(name, "boolean");
	}

}
