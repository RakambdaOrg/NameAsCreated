package fr.mrcraftcod.nameascreated;

import java.io.File;
import java.util.Date;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/08/2017.
 *
 * @author Thomas Couchoud
 * @since 2017-08-12
 */
public class NewFile
{
	private final String name;
	private final String extension;
	private final File parent;
	private final Date date;
	private final File source;
	
	NewFile(String name, String extension, File parent, Date filedate, File source)
	{
		this.parent = parent;
		this.name = name;
		this.extension = extension.toLowerCase();
		this.date = filedate;
		this.source = source;
	}
	
	public String getName(File file)
	{
		if(file == null || (name + extension).equalsIgnoreCase(file.getName()))
			return name + extension;
		if(!new File(file.getParentFile(), name + extension).exists())
			return name + extension;
		int i = 1;
		while(new File(file.getParentFile(), name + " " + i + extension).exists())
			i++;
		return name + " " + i + extension;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public String getExtension()
	{
		return extension;
	}
	
	public File getFile()
	{
		return new File(parent, getName(null));
	}
	
	public File getParent()
	{
		return parent;
	}
	
	public File getSource()
	{
		return source;
	}
}
