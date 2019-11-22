package fr.raksrinana.nameascreated.strategy;

import fr.raksrinana.nameascreated.NewFile;
import java.nio.file.Path;

@FunctionalInterface
public interface RenamingStrategy{
	/**
	 * Handle the strategy of a file.
	 *
	 * @param path The path of the file to rename.
	 *
	 * @return The infos about how to rename this file.
	 *
	 * @throws Exception If something went wrong.
	 */
	NewFile renameFile(Path path) throws Exception;
}
