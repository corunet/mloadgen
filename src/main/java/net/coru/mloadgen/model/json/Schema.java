package net.coru.mloadgen.model.json;

import java.util.List;
import lombok.Value;

@Value
public class Schema {

	String id;

	String name;

	List<Field> fields;
}
