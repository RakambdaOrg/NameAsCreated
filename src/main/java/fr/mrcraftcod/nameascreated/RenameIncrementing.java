package fr.mrcraftcod.nameascreated;

import fr.mrcraftcod.nameascreated.renaming.RenamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
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
	 * @param startIndex The starting index of the number.
	 * @param files      The list of files to modify.
	 */
	public static void processFiles(final int startIndex, final RenamingStrategy renamingStrategy, final List<File> files){
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
			final var newFilePath = new File(newFile.getParent(), ++i + newFile.getExtension());
			newFile.renameTo(newFilePath);
		}
	}
}
