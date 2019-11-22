package fr.raksrinana.nameascreated.extractor;

import com.drew.metadata.Directory;
import com.drew.metadata.xmp.XmpDirectory;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

public class XmpDateExtractor implements DateExtractor<XmpDirectory>{
	private final List<String> keys = List.of("xmp:CreateDate", "photoshop:DateCreated");
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	@Override
	public ZonedDateTime parse(final Directory directory, final TimeZone tz){
		final var xmpDirectory = (XmpDirectory) directory;
		final var values = xmpDirectory.getXmpProperties();
		for(final var key : keys)
		{
			if(values.containsKey(key))
			{
				try
				{
					return Optional.ofNullable(values.get(key)).map(date -> ZonedDateTime.parse(date, dateTimeFormatter.withZone(tz.toZoneId()))).orElse(null);
				}
				catch(final Exception ignored)
				{
				}
			}
		}
		return null;
	}
	
	@Override
	public Class<XmpDirectory> getKlass(){
		return XmpDirectory.class;
	}
}