package com.api.framework;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class utils {
  // Method to get json schema factory
  public static JsonSchemaFactory getJsonSchemaFactory() {
    return JsonSchemaFactory.newBuilder()
        .setValidationConfiguration(
            ValidationConfiguration.newBuilder().setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
        .freeze();
  }
}
