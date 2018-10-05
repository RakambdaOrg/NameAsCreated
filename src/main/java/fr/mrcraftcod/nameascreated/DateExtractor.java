package fr.mrcraftcod.nameascreated;

import com.drew.metadata.Directory;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-09-30
 */
public interface DateExtractor<T extends Directory>{
	Date parse(Directory directory, TimeZone tz);
	
	Class<T> getKlass();
}
