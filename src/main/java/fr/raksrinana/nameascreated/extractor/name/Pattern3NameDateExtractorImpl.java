package fr.raksrinana.nameascreated.extractor.name;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class Pattern3NameDateExtractorImpl extends DatePatternNameExtractor{
	private static final Pattern NAME_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
			.withLocale(Locale.ENGLISH)
			.withZone(ZoneId.systemDefault());
	
	@Override
	protected DateTimeFormatter getFormatter(){
		return DATE_TIME_FORMATTER;
	}
	
	@Override
	protected Pattern getPattern(){
		return NAME_PATTERN;
	}
}
