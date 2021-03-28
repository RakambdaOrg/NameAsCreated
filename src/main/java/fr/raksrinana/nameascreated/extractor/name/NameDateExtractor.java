package fr.raksrinana.nameascreated.extractor.name;

import com.drew.metadata.Directory;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.TimeZone;

public interface NameDateExtractor{
	/**
	 * Get the date from the filename
	 *
	 * @param name The filename
	 *
	 * @return The date if found.
	 */
	@NotNull Optional<ZonedDateTime> parse(@NotNull String name);
}
