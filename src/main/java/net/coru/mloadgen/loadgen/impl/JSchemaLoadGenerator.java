/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.loadgen.impl;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.coru.mloadgen.loadgen.BaseLoadGenerator;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.exception.MLoadGenException;
import net.coru.mloadgen.processor.JsonSchemaProcessor;

@Slf4j
public class JSchemaLoadGenerator implements BaseLoadGenerator {

  private final JsonSchemaProcessor avroSchemaProcessor;

  public JSchemaLoadGenerator() {
    avroSchemaProcessor = new JsonSchemaProcessor();
  }

  public void setUpGenerator(Map<String, String> originals, String avroSchemaName, List<FieldValueMapping> fieldExprMappings) {
    try {
      this.avroSchemaProcessor.processSchema(fieldExprMappings);
    } catch (Exception exc){
      log.error("Please make sure that properties data type and expression function return type are compatible with each other", exc);
      throw new MLoadGenException(exc);
    }
  }

  public void setUpGenerator(String schema, List<FieldValueMapping> fieldExprMappings) {
    try {
      this.avroSchemaProcessor.processSchema(fieldExprMappings);
    } catch (Exception exc){
      log.error("Please make sure that properties data type and expression function return type are compatible with each other", exc);
      throw new MLoadGenException(exc);
    }
  }

  public String nextMessage() {
    return avroSchemaProcessor.next();
  }

}
