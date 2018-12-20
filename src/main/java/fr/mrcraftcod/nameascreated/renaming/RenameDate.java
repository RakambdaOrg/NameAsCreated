package fr.mrcraftcod.nameascreated.renaming;

import fr.mrcraftcod.nameascreated.strategy.RenamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-12-20.
 *
 * @author Thomas Couchoud
 * @since 2018-12-20
 */
public class RenameDate{
	private static final Logger LOGGER = LoggerFactory.getLogger(RenameDate.class);
	
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
							LOGGER.warn("Couldn't rename file {} to {}, file already exists", file, fileTo);
							continue;
						}
						newFile.renameTo(fileTo.toFile());
					}
					catch(final Exception e){
						LOGGER.error("Error strategy file {}", file, e.getMessage());
					}
				}
			}
			catch(final Exception e){
				LOGGER.error("Error processing file {}", file, e);
			}
		}
	}
}
