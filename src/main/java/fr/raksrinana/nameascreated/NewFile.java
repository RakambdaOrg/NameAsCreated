package fr.raksrinana.nameascreated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.nio.file.Path;
import java.time.ZonedDateTime;

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
	private final Path parent;
	private final ZonedDateTime date;
	private final Path source;
	public static boolean testMode = false;
	
	/**
	 * Constructor.
	 *
	 * @param name      The name of the new file.
	 * @param extension The extension of the new file.
	 * @param parent    The parent folder.
	 * @param fileDate  The creation date of the file.
	 * @param source    The source file.
	 */
	public NewFile(final String name, final String extension, final Path parent, final ZonedDateTime fileDate, final Path source){
		this.parent = parent;
		this.name = name;
		if(!extension.startsWith(".")){
			throw new IllegalArgumentException("Extension should start with a dot");
		}
		this.extension = extension.toLowerCase();
		this.date = fileDate;
		this.source = source;
	}
	
	/**
	 * Get the new name of the file to put it in a folder.
	 *
	 * @param file The folder where to put the file in.
	 *
	 * @return The new name.
	 */
	public String getName(final File file){
		if(file == null || (name + extension).equalsIgnoreCase(file.getName())){
			return name + extension;
		}
		if(!new File(file.getParentFile(), name + extension).exists()){
			return name + extension;
		}
		var i = 1;
		while(new File(file.getParentFile(), String.format("%s (%d)%s", name, i, extension)).exists()){
			i++;
		}
		return String.format("%s (%d)%s", name, i, extension);
	}
	
	/**
	 * Rename this file to the given file.
	 *
	 * @param filePath The file to rename it to.
	 */
	public void renameTo(final File filePath){
		if(testMode || this.getSource().toFile().renameTo(filePath)){
			LOGGER.debug("Renamed {} to {}", this, filePath);
		}
		else{
			LOGGER.debug("Renamed {} to {}", this, filePath);
		}
	}
	
	/**
	 * Get the source path.
	 *
	 * @return The source path.
	 */
	public Path getSource(){
		return source;
	}
	
	/**
	 * Get the date of the file.
	 *
	 * @return The creation date.
	 */
	public ZonedDateTime getDate(){
		return date;
	}
	
	/**
	 * Get the extension of this file.
	 *
	 * @return The extension.
	 */
	public String getExtension(){
		return extension;
	}
	
	/**
	 * Get the parent folder.
	 *
	 * @return The parent path.
	 */
	public Path getParent(){
		return parent;
	}
}
