package fr.raksrinana.nameascreated.renaming;

import fr.raksrinana.nameascreated.strategy.RenamingStrategy;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class RenameDate{
	/**
	 * Rename files with their creation date.
	 *
	 * @param renamingStrategy The strategy to rename the files.
	 * @param files            The list of files to modify.
	 */
	public static void processFiles(final RenamingStrategy renamingStrategy, final List<Path> files){
		for(final var file : files){
			try{
				if(file.toFile().exists() && file.toFile().isFile() && file.toFile().getName().contains(".") && !file.toFile().getName().startsWith(".")){
					try{
						final var newFile = renamingStrategy.renameFile(file);
						final var fileTo = file.getParent().resolve(newFile.getName(file.toFile()));
						if(fileTo.toFile().getName().equals(file.toFile().getName())){
							continue;
						}
						if(fileTo.toFile().exists()){
							log.warn("Couldn't rename file {} to {}, file already exists", file, fileTo);
							continue;
						}
						newFile.renameTo(fileTo.toFile());
					}
					catch(final Exception e){
						log.error("Error strategy file {}: {}", file, e.getMessage());
					}
				}
			}
			catch(final Exception e){
				log.error("Error processing file {}", file, e);
			}
		}
	}
}
