package fr.raksrinana.nameascreated.strategy;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.xmp.XmpDirectory;
import fr.raksrinana.nameascreated.NewFile;
import fr.raksrinana.nameascreated.extractor.DateExtractor;
import fr.raksrinana.nameascreated.extractor.SimpleDateExtractor;
import fr.raksrinana.nameascreated.extractor.XmpDateExtractor;
import fr.raksrinana.utils.http.requestssenders.get.JSONGetRequestSender;
import lombok.extern.slf4j.Slf4j;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.parse.PointLocationParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class ByDateRenaming implements RenamingStrategy{
	private final DateTimeFormatter outputDateFormat;
	private final List<DateTimeFormatter> parsingFormats;
	private final ArrayList<DateExtractor<?>> dateExtractors;
	
	/**
	 * Constructor.
	 * <p>
	 * The output format will be `yyyy-MM-dd HH.mm.ss`.
	 */
	public ByDateRenaming(){
		parsingFormats = new ArrayList<>();
		parsingFormats.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("'Screen Shot' yyyy-MM-dd 'at' HH.mm.ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("'Photo' MMM dd, hh mm ss a", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("'Photo' dd-MM-yyyy, HH mm ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("'Photo' dd-MM-yyyy HH mm ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("'Video' MMM dd, hh mm ss a", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("'Video' dd-MM-yyy, HH mm ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("'Video' dd-MM-yyy HH mm ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		parsingFormats.add(DateTimeFormatter.ofPattern("dd MMM yyy, HH:mm:ss", Locale.ENGLISH).withZone(ZoneId.systemDefault()));
		dateExtractors = new ArrayList<>();
		dateExtractors.add(new SimpleDateExtractor<>(QuickTimeMetadataDirectory.class, QuickTimeMetadataDirectory.TAG_CREATION_DATE));
		dateExtractors.add(new SimpleDateExtractor<>(QuickTimeDirectory.class, QuickTimeDirectory.TAG_CREATION_TIME));
		dateExtractors.add(new SimpleDateExtractor<>(Mp4Directory.class, Mp4Directory.TAG_CREATION_TIME));
		dateExtractors.add(new SimpleDateExtractor<>(ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
		dateExtractors.add(new XmpDateExtractor());
		outputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss", Locale.ENGLISH).withZone(ZoneId.systemDefault());
	}
	
	/**
	 * Constructor.
	 *
	 * @param parsingFormats   The formatters to parse dates.
	 * @param dateExtractors   The extractors to extract data from the dictionaries of the file.
	 * @param outputDateFormat The output format.
	 */
	public ByDateRenaming(final List<DateTimeFormatter> parsingFormats, final ArrayList<DateExtractor<?>> dateExtractors, final DateTimeFormatter outputDateFormat){
		this.parsingFormats = parsingFormats;
		this.dateExtractors = dateExtractors;
		this.outputDateFormat = outputDateFormat;
	}
	
	@Override
	public NewFile renameFile(final Path path) throws IOException{
		final var file = path.toFile();
		final var prefix = "";
		final var extension = file.getName().substring(file.getName().lastIndexOf('.'));
		final var name = file.getName().substring(0, file.getName().lastIndexOf('.'));
		final var attr = Files.readAttributes(path, BasicFileAttributes.class);
		final var createdDate = Instant.ofEpochMilli(attr.lastModifiedTime().toMillis()).atZone(ZoneId.systemDefault());
		return processMetadata(path, name, extension).orElseGet(() -> {
			for(final var dateTimeFormatter : parsingFormats){
				try{
					log.debug("Trying format `{}`", dateTimeFormatter);
					final var date = ZonedDateTime.parse(name, dateTimeFormatter);
					if(date.getYear() < 1970){
						throw new ParseException("Invalid year", 0);
					}
					log.info("Matched date format for {}{}", name, extension);
					return new NewFile(outputDateFormat.format(date), extension, path.getParent(), date, path);
				}
				catch(final ParseException e){
					log.warn("Invalid year with used format for file {}", path);
				}
				catch(final DateTimeParseException ignored){
				}
				catch(final Exception e){
					log.error("Error using format {} => {}", dateTimeFormatter, e.getMessage());
				}
			}
			log.warn("Unrecognized date format : {}{}, using file last modified time", name, extension);
			var theDate = createdDate;
			if(createdDate.getYear() <= 1970){
				theDate = createdDate.withYear(LocalDateTime.now().getYear());
			}
			return new NewFile(prefix + outputDateFormat.format(theDate), extension, path.getParent(), theDate, path);
		});
	}
	
	/**
	 * Process the metadata of a file to attempt to extract it's creation date.
	 *
	 * @param path      The path of the file.
	 * @param name      The name of the file.
	 * @param extension The extension of the file.
	 *
	 * @return A potential newFile object.
	 */
	private Optional<NewFile> processMetadata(Path path, String name, String extension){
		try{
			final var metadata = ImageMetadataReader.readMetadata(path.toFile());
			if(Objects.nonNull(metadata)){
				final var zoneID = getZoneIdFromMetadata(metadata).orElse(ZoneId.systemDefault());
				final var timeZone = TimeZone.getTimeZone(zoneID);
				for(final var dataExtractor : dateExtractors){
					for(var directory : metadata.getDirectoriesOfType(dataExtractor.getKlass())){
						try{
							log.debug("Trying {}", dataExtractor.getKlass().getName());
							if(Objects.nonNull(directory)){
								var takenDate = dataExtractor.parse(directory, timeZone);
								if(Objects.nonNull(takenDate)){
									log.info("Matched directory {} for {}{}", directory, name, extension);
									// try{
									// 	for(final var fileDirectory : metadata.getDirectoriesOfType(FileSystemDirectory.class)){
									// 		final var fileDate = fileDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE).toInstant().atZone(ZoneId.systemDefault());
									// 		if(fileDate.isBefore(takenDate)){
									// 			takenDate = fileDate;
									// 		}
									// 	}
									// }
									// catch(final Exception e){
									// 	log.warn("Error getting taken date", e);
									// }
									if(takenDate.getYear() < 1970){
										throw new ParseException("Invalid year", 0);
									}
									return Optional.of(new NewFile(outputDateFormat.format(takenDate), extension, path.getParent(), takenDate, path));
								}
							}
						}
						catch(final ParseException e){
							log.warn("Invalid year with directory {} for file {}", dataExtractor.getKlass().getName(), path);
						}
						catch(final Exception e){
							log.error("Error processing directory {} for {}{} => {}", dataExtractor.getKlass().getName(), name, extension, e.getMessage());
						}
					}
				}
			}
		}
		catch(final Exception e){
			log.error("Error processing metadata for {}{} => {}", name, extension, e.getMessage());
		}
		return Optional.empty();
	}
	
	/**
	 * Get a potential zoneID from the geolocation present in the metadata of the file
	 *
	 * @param metadata The metadata.
	 *
	 * @return The potential zoneID found.
	 */
	private Optional<ZoneId> getZoneIdFromMetadata(final Metadata metadata){
		try{
			for(final var gpsDirectory : metadata.getDirectoriesOfType(GpsDirectory.class)){
				final var location = gpsDirectory.getGeoLocation();
				final var zoneId = getZoneID(location.getLatitude(), location.getLongitude());
				if(Objects.nonNull(zoneId)){
					return Optional.of(zoneId);
				}
			}
			for(final var quickTimeMetadataDirectory : metadata.getDirectoriesOfType(QuickTimeMetadataDirectory.class)){
				final var repr = quickTimeMetadataDirectory.getString(0x050D);
				if(Objects.nonNull(repr) && !repr.isBlank()){
					final var location = PointLocationParser.parsePointLocation(repr);
					final var zoneId = getZoneID(location.getLatitude().getDegrees(), location.getLongitude().getDegrees());
					if(Objects.nonNull(zoneId)){
						return Optional.of(zoneId);
					}
				}
			}
			for(final var xmpDirectory : metadata.getDirectoriesOfType(XmpDirectory.class)){
				final var xmpValues = xmpDirectory.getXmpProperties();
				if(xmpValues.containsKey("exif:GPSLatitude") && xmpValues.containsKey("exif:GPSLongitude")){
					final var lat = getAngle(xmpValues.get("exif:GPSLatitude"));
					final var lon = getAngle(xmpValues.get("exif:GPSLongitude"));
					if(lat != null && lon != null){
						final var location = new PointLocation(new Latitude(lat), new Longitude(lon));
						final var zoneId = getZoneID(location.getLatitude().getDegrees(), location.getLongitude().getDegrees());
						if(Objects.nonNull(zoneId)){
							return Optional.of(zoneId);
						}
					}
				}
			}
		}
		catch(final Exception e){
			log.warn("Error getting GPS infos", e);
		}
		return Optional.empty();
	}
	
	/**
	 * Get the zoneID of a geolocation.
	 *
	 * @param latitude  The latitude.
	 * @param longitude The longitude.
	 *
	 * @return The zoneID corresponding to this location.
	 */
	private static ZoneId getZoneID(final double latitude, final double longitude){
		try{
			final var result = new JSONGetRequestSender("http://api.geonames.org/timezoneJSON?lat=" + latitude + "&lng=" + longitude + "&username=mrcraftcod").getRequestHandler();
			if(result.getStatus() == 200){
				final var root = result.getRequestResult();
				if(root != null){
					if(root.getObject().has("timezoneId")){
						return ZoneId.of(root.getObject().getString("timezoneId"));
					}
				}
			}
		}
		catch(final Exception e){
			log.error("Error getting zoneID for coordinates {};{}", latitude, longitude, e);
		}
		return null;
	}
	
	/**
	 * Convert a N/E/S/W coordinate to an angle one.
	 *
	 * @param s The coordinate to convert.
	 *
	 * @return The angle.
	 */
	private static Angle getAngle(final String s){
		final var pattern = Pattern.compile("(\\d{1,3}),(\\d{1,2})\\.(\\d+)([NESW])");
		final var matcher = pattern.matcher(s);
		if(matcher.matches()){
			var angle = Integer.parseInt(matcher.group(1)) + (Integer.parseInt(matcher.group(2)) / 60.0) + (Double.parseDouble("0." + matcher.group(3)) / 60.0);
			angle *= getMultiplicand(matcher.group(4));
			return Angle.fromDegrees(angle);
		}
		return null;
	}
	
	/**
	 * Get the sign of the angle depending of it's position (N/E/S/W).
	 *
	 * @param group The group (N/E/S/W).
	 *
	 * @return The sign of the angle.
	 */
	private static double getMultiplicand(final String group){
		switch(group){
			case "W":
			case "S":
				return -1;
			default:
				return 1;
		}
	}
}
