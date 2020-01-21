package fr.raksrinana.nameascreated.extractor;

import com.drew.metadata.Directory;
import lombok.Getter;
import lombok.NonNull;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

public class SimpleDateExtractor<T extends Directory> implements DateExtractor<T>{
	private final int tag;
	@Getter
	private final Class<T> klass;
	
	/**
	 * Constructor.
	 *
	 * @param klass The class of the directory.
	 * @param tag   The tag to fetch the date from.
	 */
	public SimpleDateExtractor(@NonNull final Class<T> klass, final int tag){
		this.klass = klass;
		this.tag = tag;
	}
	
	@NonNull
	@Override
	public Optional<ZonedDateTime> parse(@NonNull final Directory directory, @NonNull final TimeZone tz){
		return Optional.ofNullable(directory.getDate(this.tag, tz)).map(Date::toInstant).map(date -> date.atZone(tz.toZoneId()));
	}
}
