package net.coru.mloadgen.model.json;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public abstract class Field {

	String name;

	String type;

	Field value;

}
