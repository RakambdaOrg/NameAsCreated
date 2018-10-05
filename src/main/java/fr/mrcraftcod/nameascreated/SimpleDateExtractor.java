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
public class SimpleDateExtractor<T extends Directory> implements DateExtractor<T>
{
	private final int tag;
	
	public SimpleDateExtractor(int tag)
	{
		this.tag = tag;
	}
	
	@Override
	public Date parse(Directory directory, TimeZone tz)
	{
		return directory.getDate(this.tag, tz);
	}
}
