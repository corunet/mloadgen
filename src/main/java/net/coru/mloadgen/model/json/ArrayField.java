package net.coru.mloadgen.model.json;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;
import lombok.ToString;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ArrayField extends Field{

	List<Field> value;

	@Builder
	public ArrayField(String name, List<Field> value) {
		super(name, "ARRAY");
		this.value = value;
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
