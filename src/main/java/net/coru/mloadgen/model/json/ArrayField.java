package net.coru.mloadgen.model.json;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.ToString;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ArrayField extends Field{

	List<Field> values;

	int minItems;

	boolean uniqueItems;

	@Builder(toBuilder = true)
	public ArrayField(String name, List<Field> values, int minItems, boolean uniqueItems) {
		super(name, "array");
		this.values = values;
		this.minItems = minItems;
		this.uniqueItems = uniqueItems;
	}

	@Override
	public Field cloneField(String fieldName) {
		return this.toBuilder().name(fieldName).build();
	}

	public static class ArrayFieldBuilder {

		private List<Field> value = new ArrayList<>();

		public ArrayFieldBuilder values(List<Field> values) {
			value.addAll(values);
			return this;
		}

		public ArrayFieldBuilder value(Field value) {
			this.value.add(value);
			return this;
		}
	}
}
