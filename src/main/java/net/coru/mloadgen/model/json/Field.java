package net.coru.mloadgen.model.json;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class Field {

	private String name;
	private String type;

}
