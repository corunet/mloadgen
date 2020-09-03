package net.coru.mloadgen.model.json;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Schema {

	String id;

	String name;

	String type;

	List<Field> properties;

	List<Field> descriptions;

	public static class SchemaBuilder {

		List<Field> properties = new ArrayList<>();

		List<Field> descriptions = new ArrayList<>();

		public SchemaBuilder property(Field field) {
			properties.add(field);
			return this;
		}

		public SchemaBuilder properties(List<Field> fieldList) {
			properties.addAll(fieldList);
			return this;
		}

		public SchemaBuilder description(Field field) {
			descriptions.add(field);
			return this;
		}

		public SchemaBuilder descriptions(List<Field> fieldList) {
			descriptions.addAll(fieldList);
			return this;
		}
	}
}
