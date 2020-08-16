package net.coru.mloadgen.model.json;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ObjectField extends Field{

	List<Field> properties;

	@Builder
	public ObjectField(String name, List<Field> properties) {
		super(name, "OBJECT");
		this.properties = properties;
	}

	public static class ObjectFieldBuilder {
		List<Field> properties = new ArrayList<>();

		public ObjectFieldBuilder properties(List<Field> fieldList) {
			properties.addAll(fieldList);
			return this;
		}

		public ObjectFieldBuilder property(Field field) {
			properties.add(field);
			return this;
		}
	}
}
