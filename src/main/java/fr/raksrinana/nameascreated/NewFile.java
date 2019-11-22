package fr.raksrinana.nameascreated;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.nio.file.Path;
import java.time.ZonedDateTime;

@Slf4j
public class NewFile{
	private final String name;
	@Getter
	private final String extension;
	@Getter
	private final Path parent;
	@Getter
	private final ZonedDateTime date;
	@Getter
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
			log.debug("Renamed {} to {}", this, filePath);
		}
		else{
			log.debug("Renamed {} to {}", this, filePath);
		}
	}
}
