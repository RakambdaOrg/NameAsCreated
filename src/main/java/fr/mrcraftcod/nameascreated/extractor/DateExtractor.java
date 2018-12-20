package fr.mrcraftcod.nameascreated.extractor;

import com.drew.metadata.Directory;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @param <T> The directory type.
 *
 * @author Thomas Couchoud
 * @since 2018-09-30
 */
public interface DateExtractor<T extends Directory>{
	/**
	 * Get the date from the given {@link Directory}.
	 *
	 * @param directory The directory to get from.
	 * @param tz        The timezone of the date.
	 *
	 * @return The date or null if not found.
	 */
	ZonedDateTime parse(Directory directory, TimeZone tz);
	
	/**
	 * Get the class of the directory.
	 *
	 * @return The directory's class.
	 */
	Class<T> getKlass();
}
