package fr.raksrinana.nameascreated.extractor.media;

import com.drew.metadata.Directory;
import com.drew.metadata.xmp.XmpDirectory;
import lombok.NonNull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

public class XmpMediaDateExtractor implements MediaDateExtractor<XmpDirectory>{
	private final List<String> keys = List.of("xmp:CreateDate", "photoshop:DateCreated");
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	@NonNull
	@Override
	public Optional<ZonedDateTime> parse(@NonNull final Directory directory, @NonNull final TimeZone tz){
		final var xmpDirectory = (XmpDirectory) directory;
		final var values = xmpDirectory.getXmpProperties();
		for(final var key : keys){
			if(values.containsKey(key)){
				try{
					return Optional.ofNullable(values.get(key))
							.map(date -> ZonedDateTime.parse(date, dateTimeFormatter.withZone(tz.toZoneId())));
				}
				catch(final Exception ignored){
				}
			}
		}
		return Optional.empty();
	}
	
	@NonNull
	@Override
	public Class<XmpDirectory> getKlass(){
		return XmpDirectory.class;
	}
}
