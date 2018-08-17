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
	requires slf4j.api;
	requires json;
	requires unirest.java;
	
	exports fr.mrcraftcod.nameascreated;
}