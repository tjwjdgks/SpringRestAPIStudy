package seo.study.springrestapi.commons;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;
// json -> object  deserialization
// object -> json  serialization
@JsonComponent // ObjectMapper에 등록하게 해주는 것
public class ErroresSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeFieldName("errors");
        gen.writeStartArray();
        errors.getFieldErrors().stream().forEach(e->{
            try {
                gen.writeStartObject();
                gen.writeStringField("field",e.getField());
                gen.writeStringField("objectName",e.getObjectName());
                gen.writeStringField("code",e.getCode());
                gen.writeStringField("defaultMessage",e.getDefaultMessage());
                Object rejectValue = e.getRejectedValue();
                if(rejectValue != null)
                    gen.writeStringField("rejectValue",rejectValue.toString());
                gen.writeEndObject();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        errors.getGlobalErrors().stream().forEach(e->{
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName",e.getObjectName());
                gen.writeStringField("code",e.getCode());
                gen.writeStringField("defaultMessage",e.getDefaultMessage());
                gen.writeEndObject();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        gen.writeEndArray();
    }
}
