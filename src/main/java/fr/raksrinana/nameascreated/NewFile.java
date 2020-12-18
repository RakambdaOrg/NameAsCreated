package fr.raksrinana.nameascreated;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
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
	public NewFile(@NonNull final String name, @NonNull final String extension, @NonNull final Path parent, @NonNull final ZonedDateTime fileDate, @NonNull final Path source){
		this.parent = parent;
		this.name = name;
		this.extension = extension.toLowerCase();
		this.date = fileDate;
		this.source = source;
	}
	
	/**
	 * Get the new name of the file to put it in a folder.
	 *
	 * @param directory The folder where to put the file in.
	 *
	 * @return The new name.
	 */
	@NonNull
	public String getName(final Path directory){
		if(directory == null || (name + extension).equalsIgnoreCase(directory.getFileName().toString())){
			return name + extension;
		}
		if(!directory.resolve(name + extension).toFile().exists()){
			return name + extension;
		}
		var i = 1;
		while(directory.resolve(String.format("%s (%d)%s", name, i, extension)).toFile().exists()){
			i++;
		}
		return String.format("%s (%d)%s", name, i, extension);
	}
	
	/**
	 * Rename this file to the given file.
	 *
	 * @param target The file to rename it to.
	 */
	public void moveTo(@NonNull final Path target){
		if(!testMode){
			try{
				Files.move(this.getSource(), target);
				log.debug("Renamed {} to {}", this, target);
			}
			catch(IOException e){
				log.error("Failed to move {} to {}", this, target, e);
			}
		}
		else{
			log.debug("Renamed {} to {}", this, target);
		}
	}
}
