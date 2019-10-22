/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 23/01/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-01-23
 */
module fr.raksrinana.nameascreated {
	requires transitive fr.mrcraftcod.utils.http;
	requires metadata.extractor;
	requires pointlocation6709;
	requires org.slf4j;
	requires ch.qos.logback.classic;
	requires jcommander;
	exports fr.raksrinana.nameascreated;
	exports fr.raksrinana.nameascreated.strategy;
	exports fr.raksrinana.nameascreated.extractor;
}