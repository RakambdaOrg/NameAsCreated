module fr.raksrinana.nameascreated {
	requires transitive fr.raksrinana.utils.http;
	requires unirest.java;
	requires metadata.extractor;
	requires pointlocation6709;
	requires org.slf4j;
	requires ch.qos.logback.classic;
	requires jcommander;
	requires static lombok;
	exports fr.raksrinana.nameascreated;
	exports fr.raksrinana.nameascreated.strategy;
	exports fr.raksrinana.nameascreated.extractor;
}