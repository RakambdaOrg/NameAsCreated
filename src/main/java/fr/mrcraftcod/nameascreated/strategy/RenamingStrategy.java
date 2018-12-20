package fr.mrcraftcod.nameascreated.strategy;

import fr.mrcraftcod.nameascreated.NewFile;
import java.nio.file.Path;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-12-20.
 *
 * @author Thomas Couchoud
 * @since 2018-12-20
 */
@FunctionalInterface
public interface RenamingStrategy{
	/**
	 * Handle the strategy of a file.
	 *
	 * @param path The path of the file to rename.
	 *
	 * @return The infos about how to rename this file.
	 * @throws Exception If something went wrong.
	 */
	NewFile renameFile(Path path) throws Exception;
}
