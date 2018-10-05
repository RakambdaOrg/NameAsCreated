package fr.mrcraftcod.nameascreated;

import com.drew.metadata.Directory;
import com.drew.metadata.xmp.XmpDirectory;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-09-30
 */
public class XmpDateExtractor implements DateExtractor<XmpDirectory>
{
	private final List<String> keys = List.of("xmp:CreateDate", "photoshop:DateCreated");
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss");
	
	@Override
	public Date parse(Directory directory, TimeZone tz)
	{
		XmpDirectory xmpDirectory = (XmpDirectory) directory;
		Map<String, String> values = xmpDirectory.getXmpProperties();
		for(String key : keys)
		{
			if(values.containsKey(key))
			{
				try
				{
					return sdf.parse(ZonedDateTime.ofInstant(sdf.parse(values.get(key)).toInstant(), ZoneOffset.systemDefault()).withZoneSameInstant(tz.toZoneId()).format(df));
				}
				catch(Exception e)
				{
				}
			}
		}
		return null;
	}
}
