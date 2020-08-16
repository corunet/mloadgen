package net.coru.mloadgen.model.json;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.AllArgsConstructor;

@Value
@Builder
@AllArgsConstructor
public class Schema {

	String id;

	String name;

	List<Field> fields;

	public static class SchemaBuilder {
		List<Field> fields = new ArrayList<>();

		public SchemaBuilder field(Field field) {
			fields.add(field);
			return this;
		}

		public SchemaBuilder fields(List<Field> fieldList) {
			fields.addAll(fieldList);
			return this;
		}
	}
}
