package io.victoralbertos.mockery.internal.samples;

import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import io.victoralbertos.mockery.internal.Mockeries;
import io.victoralbertos.mockery.internal.Model;
import java.util.List;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;

@Bypass
public interface OptionalParams {
  @Valid(STRING)
  List<String> method(@Valid(STRING) String s1,
      @DTO(Mockeries.MockModel.class) List<Model> models,
      @Optional Integer i1);
}
