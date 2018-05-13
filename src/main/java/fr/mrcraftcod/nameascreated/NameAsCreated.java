package fr.mrcraftcod.nameascreated;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import com.mashape.unirest.http.JsonNode;
import fr.mrcraftcod.utils.base.Log;
import fr.mrcraftcod.utils.http.RequestHandler;
import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import org.json.JSONObject;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.parse.PointLocationParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 23/01/2017.
 *
 * @author Thomas Couchoud
 * @since 2017-01-23
 */
public class NameAsCreated
{
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
	
	public static void main(String[] args)
	{
		LinkedList<String> argsList = new LinkedList<>();
		argsList.addAll(Arrays.asList(args));
		
		if(argsList.peek().equals("-n"))
		{
			argsList.pop();
			renameCount(argsList);
		}
		else if(argsList.peek().equals("-r"))
		{
			argsList.pop();
			renameDate(listFiles(new File(argsList.peek())));
		}
		else
			renameDate(argsList);
	}
	
	private static LinkedList<String> listFiles(File folder)
	{
		LinkedList<String> files = new LinkedList<>();
		for(File file : folder.listFiles())
		{
			if(file.isDirectory())
				files.addAll(listFiles(file));
			else
				files.add(file.getAbsolutePath());
		}
		return files;
	}
	
	private static void renameCount(LinkedList<String> args)
	{
		int i = 0;
		if(args.peek().equals("-s"))
		{
			args.pop();
			i = Integer.parseInt(args.pop()) - 1;
		}
		List<NewFile> files = args.stream().map(name -> {
			try
			{
				return buildName(new File(name), true);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}).sorted(Comparator.comparing(NewFile::getDate)).collect(Collectors.toList());
		Iterator<NewFile> filesIterator = files.iterator();
		while(filesIterator.hasNext())
		{
			NewFile newFile = filesIterator.next();
			newFile.getSource().renameTo(new File(newFile.getParent(), ++i + newFile.getExtension()));
		}
	}
	
	private static void renameDate(LinkedList<String> args)
	{
		for(String arg : args)
		{
			try
			{
				File f = new File(arg);
				if(f.exists() && f.isFile() && f.getName().contains(".") && !f.getName().startsWith("."))
				{
					try
					{
						File fileTo = new File(f.getParentFile(), buildName(f, true).getName(f));
						if(fileTo.getName().equals(f.getName()))
							continue;
						if(fileTo.exists())
						{
							System.out.println("Couldn't rename file " + f.getAbsolutePath() + " to " + fileTo.getAbsolutePath() + ", file already exists");
							continue;
						}
						if(!f.renameTo(fileTo))
							System.out.println("Failed to rename " + f.getAbsolutePath());
						else
							System.out.println("Renamed " + f.getName() + " to " + fileTo.getName());
					}
					catch(Exception e)
					{
						System.out.println("Error renaming file " + f.getAbsolutePath() + " -> " + e.getMessage());
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static NewFile buildName(File f, boolean log) throws IOException
	{
		String prefix = "";
		String extension = f.getName().substring(f.getName().lastIndexOf('.'));
		String name = f.getName().substring(0, f.getName().lastIndexOf('.'));
		BasicFileAttributes attr = Files.readAttributes(Paths.get(f.toURI()), BasicFileAttributes.class);
		Date date = new Date(attr.lastModifiedTime().toMillis());
		Calendar currentCal = Calendar.getInstance();
		currentCal.setTime(date);
		
		HashMap<Class, Integer> tags = new HashMap<>();
		tags.put(ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		tags.put(Mp4Directory.class, Mp4Directory.TAG_CREATION_TIME);
		tags.put(QuickTimeDirectory.class, QuickTimeDirectory.TAG_CREATION_TIME);
		
		try
		{
			Metadata metadata = ImageMetadataReader.readMetadata(f);
			if(metadata != null)
			{
				try
				{
					for(Class c : tags.keySet())
					{
						if(log)
							System.out.format("Trying %s\n", c.getName());
						Directory directory = metadata.getFirstDirectoryOfType(c);
						if(directory != null)
						{
							Optional<ZoneId> maybeZoneId = Optional.empty();
							try
							{
								for(GpsDirectory gpsDirectory : metadata.getDirectoriesOfType(GpsDirectory.class))
								{
									if(!maybeZoneId.isPresent())
									{
										GeoLocation location = gpsDirectory.getGeoLocation();
										maybeZoneId = Optional.ofNullable(getZoneID(location.getLatitude(), location.getLongitude()));
									}
								}
								for(QuickTimeMetadataDirectory gpsDirectory : metadata.getDirectoriesOfType(QuickTimeMetadataDirectory.class))
								{
									if(!maybeZoneId.isPresent())
									{
										PointLocation location = PointLocationParser.parsePointLocation(gpsDirectory.getString(0x050D));
										maybeZoneId = Optional.ofNullable(getZoneID(location.getLatitude().getDegrees(), location.getLongitude().getDegrees()));
									}
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							ZoneId zoneID = maybeZoneId.orElse(ZoneId.systemDefault());
							TimeZone timeZone = TimeZone.getTimeZone(zoneID);
							Date takenDate = directory.getDate(tags.get(c), timeZone);
							if(takenDate == null)
								continue;
							if(log)
								System.out.println("Matched");
							
							try
							{
								for(FileSystemDirectory fileDirectory : metadata.getDirectoriesOfType(FileSystemDirectory.class))
								{
									if(fileDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE).before(takenDate))
										takenDate = fileDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							
							Calendar parsedCal = Calendar.getInstance(timeZone);
							parsedCal.setTime(takenDate);
							if(parsedCal.get(Calendar.YEAR) < 1970)
								throw new ParseException("Invalid year", 0);
							
							ZonedDateTime dateTime = LocalDateTime.ofEpochSecond(parsedCal.getTimeInMillis() / 1000, 0, zoneID.getRules().getOffset(Instant.now())).atZone(ZoneId.systemDefault());
							return new NewFile(outputDateFormat.format(Date.from(dateTime.toInstant())), extension, f.getParentFile(), takenDate, f);
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		for(SimpleDateFormat sdf : formats)
		{
			try
			{
				if(log)
					System.out.format("Trying format `%s`\n", sdf.toPattern());
				Date cdate = sdf.parse(name);
				
				Calendar parsedCal = Calendar.getInstance();
				
				parsedCal.setTime(cdate);
				if(parsedCal.get(Calendar.YEAR) < 1970)
					throw new ParseException("Invalid year", 0);
				if(parsedCal.get(Calendar.YEAR) == 1970)
					parsedCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));
				
				date = parsedCal.getTime();
				if(log)
					System.out.println("Matched");
				return new NewFile(outputDateFormat.format(date), extension, f.getParentFile(), date, f);
			}
			catch(ParseException ignored)
			{
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(log)
			Log.warning("Unrecognized file format : " + name);
		
		Calendar parsedCal = Calendar.getInstance();
		
		int CAL_YEAR = Calendar.YEAR;
		
		parsedCal.setTime(date);
		if(parsedCal.get(CAL_YEAR) <= 1970)
			parsedCal.set(CAL_YEAR, currentCal.get(CAL_YEAR));
		
		//if(parsedCal.after(currentCal))
		//	parsedCal.set(CAL_YEAR, parsedCal.get(CAL_YEAR) - 1);
		
		return new NewFile(prefix + outputDateFormat.format(parsedCal.getTime()), extension, f.getParentFile(), date, f);
	}
	
	private static ZoneId getZoneID(double latitude, double longitude)
	{
		try
		{
			RequestHandler<JsonNode> result = new JSONGetRequestSender("http://api.geonames.org/timezoneJSON?lat=" + latitude + "&lng=" + longitude + "&username=mrcraftcod").getRequestHandler();
			if(result.getStatus() == 200)
			{
				JsonNode root = result.getRequestResult();
				if(root != null)
				{
					JSONObject rootObj = root.getObject();
					if(rootObj.has("timezoneId"))
						return ZoneId.of(rootObj.getString("timezoneId"));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
