package fr.raksrinana.nameascreated.renaming;

import fr.raksrinana.nameascreated.NewFile;
import fr.raksrinana.nameascreated.strategy.RenamingStrategy;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class RenameIncrementing{
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
				log.error("Error building name", e);
			}
			return null;
		}).filter(Objects::nonNull).sorted(Comparator.comparing(NewFile::getDate)).collect(Collectors.toList());
		for(final var newFile : newFiles){
			newFile.renameTo(newFile.getParent().resolve(i++ + newFile.getExtension()).toFile());
		}
	}
}
