package net.coru.mloadgen.model.json;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StringField extends Field {

	@Override
	public List<Field> getProperties() {
		return Collections.singletonList(this);
	}

	@Builder(toBuilder = true)
	public StringField(String name) {
		super(name, "string");
	}

	@Override
	public Field cloneField(String fieldName) {
		return this.toBuilder().name(fieldName).build();
	}
}
