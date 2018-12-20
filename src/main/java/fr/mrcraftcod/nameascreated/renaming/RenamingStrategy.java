package fr.mrcraftcod.nameascreated.renaming;

import fr.mrcraftcod.nameascreated.NewFile;
import java.io.File;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-12-20.
 *
 * @author Thomas Couchoud
 * @since 2018-12-20
 */
@FunctionalInterface
public interface RenamingStrategy{
	NewFile renameFile(File file);
}
