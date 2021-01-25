open module fr.raksrinana.nameascreated {
	requires unirest.java;
	requires metadata.extractor;
	requires pointlocation6709;
	requires org.slf4j;
	requires ch.qos.logback.classic;
	requires info.picocli;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	requires com.google.gson;
	requires static lombok;
	
	exports fr.raksrinana.nameascreated;
	exports fr.raksrinana.nameascreated.strategy;
	exports fr.raksrinana.nameascreated.extractor;
}