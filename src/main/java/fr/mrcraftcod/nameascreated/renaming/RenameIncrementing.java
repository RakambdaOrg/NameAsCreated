package fr.mrcraftcod.nameascreated.renaming;

import fr.mrcraftcod.nameascreated.NewFile;
import fr.mrcraftcod.nameascreated.strategy.RenamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-12-20.
 *
 * @author Thomas Couchoud
 * @since 2018-12-20
 */
public class RenameIncrementing{
	private static final Logger LOGGER = LoggerFactory.getLogger(RenameIncrementing.class);
	
	/**
	 * Rename files with an incrementing number.
	 *
	 * @param startIndex       The starting index of the number.
	 * @param renamingStrategy The strategy to rename the files.
	 * @param files            The list of files to modify.
	 */
	public static void processFiles(final int startIndex, final RenamingStrategy renamingStrategy, final List<Path> files){
		var i = startIndex;
		final var newFiles = files.stream().map(name -> {
			try{
				return renamingStrategy.renameFile(name);
			}
			catch(Exception e){
				LOGGER.error("Error building name", e);
			}
			return null;
		}).filter(Objects::nonNull).sorted(Comparator.comparing(NewFile::getDate)).collect(Collectors.toList());
		for(final var newFile : newFiles){
			newFile.renameTo(newFile.getParent().resolve(++i + newFile.getExtension()).toFile());
		}
	}
}
