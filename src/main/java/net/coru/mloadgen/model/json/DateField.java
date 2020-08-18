package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DateField extends Field {

	String format;

	@Builder
	public DateField(String name, String format) {
		super(name, "DATE");
		this.format = format;
	}
}
