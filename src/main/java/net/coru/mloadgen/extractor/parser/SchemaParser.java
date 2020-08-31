package net.coru.mloadgen.extractor.parser;

import net.coru.mloadgen.model.json.Schema;

public interface SchemaParser {

	Schema parse(String jsonSchema);
}
