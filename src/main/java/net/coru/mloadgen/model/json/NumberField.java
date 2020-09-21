package net.coru.mloadgen.model.json;

import java.util.Objects;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
public class NumberField extends Field {

	String defaultValue;

	Number minimum;

	Number maximum;

	Number exclusiveMinimum;

	Number exclusiveMaximum;

	Number multipleOf;

	@Builder(toBuilder = true)
	public NumberField(String name, String defaultValue, Number minimum, Number maximum, Number exclusiveMinimum,
			Number exclusiveMaximum, Number multipleOf) {
		super(name, "number");
		this.defaultValue = defaultValue;
		this.maximum = maximum;
		this.minimum = minimum;
		this.exclusiveMinimum = exclusiveMinimum;
		this.exclusiveMaximum = exclusiveMaximum;
		this.multipleOf = multipleOf;
	}

	@Override
	public Field cloneField(String fieldName) {
		return this.toBuilder().name(fieldName).build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof NumberField)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		NumberField that = (NumberField) o;
		return Objects.equals(defaultValue, that.defaultValue) &&
				compare(minimum, that.minimum) &&
				compare(maximum, that.maximum) &&
				compare(exclusiveMinimum, that.exclusiveMinimum) &&
				compare(exclusiveMaximum, that.exclusiveMaximum) &&
				compare(multipleOf, that.multipleOf) &&
				Objects.equals(super.getName(), ((NumberField) o).getName()) &&
				Objects.equals(super.getType(), ((NumberField) o).getType());
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), defaultValue, minimum, maximum, exclusiveMinimum, exclusiveMaximum, multipleOf);
	}

	public boolean compare(Number a, Number b){
			return a.toString().equalsIgnoreCase(b.toString());
	}
}
