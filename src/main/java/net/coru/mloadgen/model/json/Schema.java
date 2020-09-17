package net.coru.mloadgen.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


import static java.util.Arrays.asList;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Schema {

	String id;

	String name;

	String type;

	List<String> requiredFields;

	List<Field> properties;

	List<Field> descriptions;

	public static class SchemaBuilder {

		List<Field> properties = new ArrayList<>();

		List<Field> descriptions = new ArrayList<>();

		List<String> requireFields = new ArrayList<>();

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

		public SchemaBuilder descriptions(Collection<Field> fieldList) {
			descriptions.addAll(fieldList);
			return this;
		}

		public SchemaBuilder requiredField(String field) {
			requireFields.add(field);
			return this;
		}

		public SchemaBuilder requiredFields(List<String> fields) {
			requireFields.addAll(fields);
			return this;
		}

		public SchemaBuilder requiredFields(String[] fields) {
			requireFields.addAll(asList(fields));
			return this;
		}
	}
}
