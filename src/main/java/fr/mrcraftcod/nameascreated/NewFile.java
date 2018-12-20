package fr.mrcraftcod.nameascreated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Date;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/08/2017.
 *
 * @author Thomas Couchoud
 * @since 2017-08-12
 */
@SuppressWarnings("WeakerAccess")
public class NewFile{
	private static final Logger LOGGER = LoggerFactory.getLogger(NewFile.class);
	private final String name;
	private final String extension;
	private final File parent;
	private final Date date;
	private final File source;
	
	NewFile(final String name, final String extension, final File parent, final Date fileDate, final File source){
		this.parent = parent;
		this.name = name;
		this.extension = extension.toLowerCase();
		this.date = fileDate;
		this.source = source;
	}
	
	public String getName(final File file){
		if(file == null || (name + extension).equalsIgnoreCase(file.getName())){
			return name + extension;
		}
		if(!new File(file.getParentFile(), name + extension).exists()){
			return name + extension;
		}
		var i = 1;
		while(new File(file.getParentFile(), name + " " + i + extension).exists()){
			i++;
		}
		return name + " " + i + extension;
	}
	
	public void renameTo(final File filePath){
		if(this.getSource().renameTo(filePath)){
			LOGGER.debug("Renamed {} to {}", this, filePath);
		}
		else{
			LOGGER.debug("Renamed {} to {}", this, filePath);
		}
	}
	
	public File getSource(){
		return source;
	}
	
	public Date getDate(){
		return date;
	}
	
	public String getExtension(){
		return extension;
	}
	
	public File getParent(){
		return parent;
	}
}
