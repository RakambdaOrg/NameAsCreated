/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 23/01/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-01-23
 */
module fr.mrcraftcod.nameascreated {
	requires transitive fr.mrcraftcod.utils.http;
	
	requires metadata.extractor;
	requires pointlocation6709;
	
	requires java.scripting;
	requires org.slf4j;
	requires ch.qos.logback.classic;
	requires unirest.java;
	requires javafx.base;
	requires javafx.controls;
	
	exports fr.mrcraftcod.nameascreated;
	exports fr.mrcraftcod.nameascreated.strategy;
	exports fr.mrcraftcod.nameascreated.extractor;
}