package net.coru.mloadgen.extractor;

import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.Schema;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface SchemaExtractor {

	List<FieldValueMapping> flatPropertiesList(Schema parserSchema);

	List<Schema> schemaTypesList(File schemaFile) throws IOException;

	List<FieldValueMapping> processSchema(Schema schema);
}
