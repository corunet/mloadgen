package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MapField extends Field{

	Field mapType;

	@Builder(toBuilder = true)
	public MapField(String name, Field mapType) {
		super(name, "map");
		this.mapType = mapType;
	}

	@Override
	public Field cloneField(String fieldName) {
		return this.toBuilder().name(fieldName).build();
	}
}
