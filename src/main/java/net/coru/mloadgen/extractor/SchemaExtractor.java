/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.extractor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.Schema;

public interface SchemaExtractor {

	List<FieldValueMapping> flatPropertiesList(Schema parserSchema);

	List<Schema> schemaTypesList(String schemaType, File schemaFile) throws IOException;

	List<FieldValueMapping> processSchema(Schema schema);
}
