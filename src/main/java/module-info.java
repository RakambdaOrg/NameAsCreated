open module fr.raksrinana.nameascreated {
	requires unirest.java;
	requires metadata.extractor;
	requires pointlocation6709;
	requires org.slf4j;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	requires static lombok;
	requires static org.jetbrains.annotations;
	
	exports fr.raksrinana.nameascreated;
	exports fr.raksrinana.nameascreated.strategy;
	exports fr.raksrinana.nameascreated.extractor.media;
	exports fr.raksrinana.nameascreated.extractor.name;
}