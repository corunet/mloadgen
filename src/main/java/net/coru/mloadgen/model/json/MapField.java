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

	@Builder
	public MapField(String name, Field mapType) {
		super(name, "MAP");
		this.mapType = mapType;
	}
}
