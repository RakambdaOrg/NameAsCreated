package fr.raksrinana.nameascreated.utils;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import kong.unirest.GenericType;
import kong.unirest.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Type;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

public class JacksonObjectMapper implements ObjectMapper{
	private final com.fasterxml.jackson.databind.ObjectMapper mapper;
	
	public JacksonObjectMapper(){
		JsonFactoryBuilder factoryBuilder = new JsonFactoryBuilder();
		factoryBuilder.enable(JsonReadFeature.ALLOW_TRAILING_COMMA);
		mapper = new com.fasterxml.jackson.databind.ObjectMapper(factoryBuilder.build());
		mapper.setVisibility(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(ANY)
				.withGetterVisibility(NONE)
				.withSetterVisibility(NONE)
				.withCreatorVisibility(NONE));
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
	}
	
	public <T> T readValue(String value, Class<T> valueType){
		try{
			return mapper.readValue(value, valueType);
		}
		catch(IOException var4){
			throw new RuntimeException(var4);
		}
	}
	
	public <T> T readValue(String value, GenericType<T> genericType){
		try{
			return mapper.readValue(value, new TypeReference<T>(){
				public Type getType(){
					return genericType.getType();
				}
			});
		}
		catch(IOException var4){
			throw new RuntimeException(var4);
		}
	}
	
	public String writeValue(Object value){
		try{
			return mapper.writeValueAsString(value);
		}
		catch(JsonProcessingException var3){
			throw new RuntimeException(var3);
		}
	}
}
