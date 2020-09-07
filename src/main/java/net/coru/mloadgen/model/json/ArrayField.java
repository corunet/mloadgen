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

	List<Field> value;

	int minItems;

	boolean uniqueItems;

	@Builder(toBuilder = true)
	public ArrayField(String name, List<Field> value, int minItems, boolean uniqueItems) {
		super(name, "array");
		this.value = value;
		this.minItems = minItems;
		this.uniqueItems = uniqueItems;
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
