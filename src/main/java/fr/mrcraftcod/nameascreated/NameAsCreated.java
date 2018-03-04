package fr.mrcraftcod.nameascreated;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import fr.mrcraftcod.utils.base.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	private static SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	private static SimpleDateFormat[] formats = {
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
		
		try
		{
			if(log)
				System.out.format("Trying EXIF\n");
			Metadata metadata = ImageMetadataReader.readMetadata(f);
			ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			Date takenDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			if(log)
				System.out.println("Matched");
			return new NewFile(outputDateFormat.format(date), extension, f.getParentFile(), takenDate, f);
		}
		catch(ImageProcessingException e)
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
}
