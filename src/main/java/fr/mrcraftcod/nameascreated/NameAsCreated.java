package fr.mrcraftcod.nameascreated;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.xmp.XmpDirectory;
import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.parse.PointLocationParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 23/01/2017.
 *
 * @author Thomas Couchoud
 * @since 2017-01-23
 */
public class NameAsCreated{
	private static final Logger LOGGER = LoggerFactory.getLogger(NameAsCreated.class);
	private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	private static final SimpleDateFormat[] formats = {
			new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.ENGLISH),
			new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.ENGLISH),
			new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
			new SimpleDateFormat("'Screen Shot' yyyy-MM-dd 'at' HH.mm.ss", Locale.ENGLISH),
			new SimpleDateFormat("'Photo' MMM dd, hh mm ss aaa", Locale.ENGLISH),
			new SimpleDateFormat("'Photo' dd-MM-yyyy, hh mm ss", Locale.ENGLISH),
			new SimpleDateFormat("'Photo' dd-MM-yyyy hh mm ss", Locale.ENGLISH),
			new SimpleDateFormat("'Video' MMM dd, hh mm ss aaa", Locale.ENGLISH),
			new SimpleDateFormat("'Video' dd-MM-yyy, hh mm ss", Locale.ENGLISH),
			new SimpleDateFormat("'Video' dd-MM-yyy hh mm ss", Locale.ENGLISH),
			new SimpleDateFormat("dd MMM yyy, hh:mm:ss", Locale.ENGLISH)
	};
	private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}\\.\\d{2}\\.\\d{2}).*");
	
	public static void main(final String[] args){
		final var argsList = new LinkedList<>(Arrays.asList(args));
		
		switch(Objects.requireNonNull(argsList.peek())){
			case "-n":
				argsList.pop();
				renameCount(argsList);
				break;
			case "-r":
				argsList.pop();
				renameDate(listFiles(new File(Objects.requireNonNull(argsList.peek()))));
				break;
			default:
				renameDate(argsList);
				break;
		}
	}
	
	private static LinkedList<String> listFiles(final File folder){
		final var files = new LinkedList<String>();
		for(final var file : Objects.requireNonNull(folder.listFiles())){
			if(file.isDirectory()){
				files.addAll(listFiles(file));
			}
			else{
				files.add(file.getAbsolutePath());
			}
		}
		return files;
	}
	
	private static void renameCount(final LinkedList<String> args){
		var i = 0;
		if(Objects.requireNonNull(args.peek()).equals("-s")){
			args.pop();
			i = Integer.parseInt(args.pop()) - 1;
		}
		final var files = args.stream().map(name -> {
			try{
				return buildName(new File(name));
			}
			catch(IOException e){
				LOGGER.error("Error building name", e);
			}
			return null;
		}).filter(Objects::nonNull).sorted(Comparator.comparing(NewFile::getDate)).collect(Collectors.toList());
		for(final var newFile : files){
			//noinspection ResultOfMethodCallIgnored
			newFile.getSource().renameTo(new File(newFile.getParent(), ++i + newFile.getExtension()));
		}
	}
	
	private static void renameDate(final LinkedList<String> args){
		for(final var arg : args){
			try{
				final var f = new File(arg);
				if(f.exists() && f.isFile() && f.getName().contains(".") && !f.getName().startsWith(".")){
					try{
						final var fileTo = new File(f.getParentFile(), buildName(f).getName(f));
						if(fileTo.getName().equals(f.getName())){
							continue;
						}
						if(fileTo.exists()){
							LOGGER.warn("Couldn't rename file {} to {}, file already exists", f.getAbsolutePath(), fileTo.getAbsolutePath());
							continue;
						}
						if(!f.renameTo(fileTo)){
							LOGGER.error("Failed to rename {}", f.getAbsolutePath());
						}
						else{
							LOGGER.info("Renamed {} to {}", f.getName(), fileTo.getName());
						}
					}
					catch(final Exception e){
						LOGGER.error("Error renaming file {}", f.getAbsolutePath(), e.getMessage());
					}
				}
			}
			catch(final Exception e){
				LOGGER.error("Error processing file {}", arg, e);
			}
		}
	}
	
	@SuppressWarnings({
			"Duplicates",
			"WeakerAccess"
	})
	public static NewFile buildName(final File f) throws IOException{
		final var prefix = "";
		final var extension = f.getName().substring(f.getName().lastIndexOf('.'));
		final var name = f.getName().substring(0, f.getName().lastIndexOf('.'));
		final var attr = Files.readAttributes(Paths.get(f.toURI()), BasicFileAttributes.class);
		var date = new Date(attr.lastModifiedTime().toMillis());
		final var currentCal = Calendar.getInstance();
		currentCal.setTime(date);
		
		final var tags = new HashMap<Class<? extends Directory>, DateExtractor<? extends Directory>>();
		tags.put(ExifSubIFDDirectory.class, new SimpleDateExtractor<ExifSubIFDDirectory>(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
		tags.put(Mp4Directory.class, new SimpleDateExtractor<Mp4Directory>(Mp4Directory.TAG_CREATION_TIME));
		tags.put(QuickTimeDirectory.class, new SimpleDateExtractor<QuickTimeDirectory>(QuickTimeDirectory.TAG_CREATION_TIME));
		tags.put(XmpDirectory.class, new XmpDateExtractor());
		
		try{
			final var metadata = ImageMetadataReader.readMetadata(f);
			if(metadata != null){
				try{
					Optional<ZoneId> maybeZoneId = Optional.empty();
					try{
						for(final var gpsDirectory : metadata.getDirectoriesOfType(GpsDirectory.class)){
							if(maybeZoneId.isEmpty()){
								final var location = gpsDirectory.getGeoLocation();
								maybeZoneId = Optional.ofNullable(getZoneID(location.getLatitude(), location.getLongitude()));
							}
						}
						for(final var quickTimeMetadataDirectory : metadata.getDirectoriesOfType(QuickTimeMetadataDirectory.class)){
							if(maybeZoneId.isEmpty()){
								final var location = PointLocationParser.parsePointLocation(quickTimeMetadataDirectory.getString(0x050D));
								maybeZoneId = Optional.ofNullable(getZoneID(location.getLatitude().getDegrees(), location.getLongitude().getDegrees()));
							}
						}
						for(final var xmpDirectory : metadata.getDirectoriesOfType(XmpDirectory.class)){
							if(maybeZoneId.isEmpty()){
								final var xmpValues = xmpDirectory.getXmpProperties();
								if(xmpValues.containsKey("exif:GPSLatitude") && xmpValues.containsKey("exif:GPSLongitude")){
									final var lat = getAngle(xmpValues.get("exif:GPSLatitude"));
									final var lon = getAngle(xmpValues.get("exif:GPSLongitude"));
									if(lat != null && lon != null){
										final var location = new PointLocation(new Latitude(lat), new Longitude(lon));
										maybeZoneId = Optional.ofNullable(getZoneID(location.getLatitude().getDegrees(), location.getLongitude().getDegrees()));
									}
								}
							}
						}
					}
					catch(final Exception e){
						LOGGER.warn("Error getting GPS infos", e);
					}
					final var zoneID = maybeZoneId.orElse(ZoneId.systemDefault());
					final var timeZone = TimeZone.getTimeZone(zoneID);
					
					for(final var c : tags.keySet()){
						LOGGER.debug("Trying {}", c.getName());
						final var directory = metadata.getFirstDirectoryOfType(c);
						if(directory != null){
							var takenDate = tags.get(c).parse(directory, timeZone);//directory.getDate(tags.get(c), timeZone);
							if(takenDate == null){
								continue;
							}
							LOGGER.info("Matched directory {} for {}{}", directory, name, extension);
							
							try{
								for(final var fileDirectory : metadata.getDirectoriesOfType(FileSystemDirectory.class)){
									if(fileDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE).before(takenDate)){
										takenDate = fileDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
									}
								}
							}
							catch(final Exception e){
								LOGGER.warn("Error getting taken date", e);
							}
							
							final var parsedCal = Calendar.getInstance(timeZone);
							parsedCal.setTime(takenDate);
							if(parsedCal.get(Calendar.YEAR) < 1970){
								throw new ParseException("Invalid year", 0);
							}
							
							final var dateTime = parsedCal.getTime();
							return new NewFile(outputDateFormat.format(Date.from(dateTime.toInstant())), extension, f.getParentFile(), takenDate, f);
						}
					}
				}
				catch(final Exception e){
					LOGGER.error("Error processing directories for {}{}", name, extension, e);
				}
			}
		}
		catch(final Exception e){
			LOGGER.error("Error processing metadata for {}{}", name, extension, e);
		}
		
		for(final var sdf : formats){
			try{
				LOGGER.debug("Trying format `{}`", sdf.toPattern());
				final var cdate = sdf.parse(name);
				
				final var parsedCal = Calendar.getInstance();
				
				parsedCal.setTime(cdate);
				if(parsedCal.get(Calendar.YEAR) < 1970){
					throw new ParseException("Invalid year", 0);
				}
				if(parsedCal.get(Calendar.YEAR) == 1970){
					parsedCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));
				}
				
				date = parsedCal.getTime();
				LOGGER.debug("Matched date format for {}{}", name, extension);
				return new NewFile(outputDateFormat.format(date), extension, f.getParentFile(), date, f);
			}
			catch(final ParseException ignored){
			}
			catch(final Exception e){
				LOGGER.error("Error using format {}", sdf, e);
			}
		}
		
		LOGGER.info("Trying pattern: {}", DATE_PATTERN);
		final var matcher = DATE_PATTERN.matcher(name);
		if(matcher.matches()){
			try{
				LOGGER.debug("Pattern matched");
				final var cdate = outputDateFormat.parse(name);
				final var parsedCal = Calendar.getInstance();
				
				parsedCal.setTime(cdate);
				if(parsedCal.get(Calendar.YEAR) < 1970){
					throw new ParseException("Invalid year", 0);
				}
				if(parsedCal.get(Calendar.YEAR) == 1970){
					parsedCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));
				}
				
				date = parsedCal.getTime();
				LOGGER.debug("Matched date format for {}{}", name, extension);
				return new NewFile(outputDateFormat.format(date), extension, f.getParentFile(), date, f);
			}
			catch(final ParseException ignored){
			}
			catch(final Exception e){
				LOGGER.error("Error using format", e);
			}
		}
		
		LOGGER.warn("Unrecognized date format : {}{}, using file last modified time", name, extension);
		
		final var parsedCal = Calendar.getInstance();
		
		final var CAL_YEAR = Calendar.YEAR;
		
		parsedCal.setTime(date);
		if(parsedCal.get(CAL_YEAR) <= 1970){
			parsedCal.set(CAL_YEAR, currentCal.get(CAL_YEAR));
		}
		
		//if(parsedCal.after(currentCal))
		//	parsedCal.set(CAL_YEAR, parsedCal.get(CAL_YEAR) - 1);
		
		return new NewFile(prefix + outputDateFormat.format(parsedCal.getTime()), extension, f.getParentFile(), date, f);
	}
	
	private static Angle getAngle(final String s){
		final var pattern = Pattern.compile("(\\d{1,3}),(\\d{1,2})\\.(\\d+)([NESW])");
		final var matcher = pattern.matcher(s);
		if(matcher.matches()){
			var angle = Integer.parseInt(matcher.group(1)) + (Integer.parseInt(matcher.group(2)) / 60.0) + (Double.parseDouble("0." + matcher.group(3)) / 60.0);
			angle *= getMultiplicator(matcher.group(4));
			return Angle.fromDegrees(angle);
		}
		return null;
	}
	
	private static double getMultiplicator(final String group){
		switch(group){
			case "W":
			case "S":
				return -1;
			default:
				return 1;
		}
	}
	
	private static ZoneId getZoneID(final double latitude, final double longitude){
		try{
			final var result = new JSONGetRequestSender("http://api.geonames.org/timezoneJSON?lat=" + latitude + "&lng=" + longitude + "&username=mrcraftcod").getRequestHandler();
			if(result.getStatus() == 200){
				final var root = result.getRequestResult();
				if(root != null){
					final var rootObj = root.getObject();
					if(rootObj.has("timezoneId")){
						return ZoneId.of(rootObj.getString("timezoneId"));
					}
				}
			}
		}
		catch(final Exception e){
			LOGGER.error("Error getting zoneID for coordinates {};{}", latitude, longitude, e);
		}
		return null;
	}
}
