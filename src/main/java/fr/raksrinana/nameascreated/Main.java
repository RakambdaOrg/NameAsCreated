package fr.raksrinana.nameascreated;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.raksrinana.nameascreated.renaming.RenameDate;
import fr.raksrinana.nameascreated.renaming.RenameIncrementing;
import fr.raksrinana.nameascreated.strategy.ByDateRenaming;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class Main{
	/**
	 * Main method.
	 *
	 * @param args .
	 */
	public static void main(final String[] args){
		final var parameters = new CLIParameters();
		try{
			JCommander.newBuilder().addObject(parameters).build().parse(args);
		}
		catch(final ParameterException e){
			log.error("Failed to parse arguments", e);
			e.usage();
			return;
		}
		NewFile.testMode = parameters.isTestMode();
		final var files = parameters.getFiles().stream().flatMap(f -> listFiles(f, parameters.isRecursive()).stream()).distinct().collect(Collectors.toList());
		switch(parameters.getRunMode()){
			case DATE:
				RenameDate.processFiles(new ByDateRenaming(), files);
				break;
			case SEQUENCE:
				RenameIncrementing.processFiles(parameters.getStartIndex(), new ByDateRenaming(), files);
				break;
		}
	}
	
	/**
	 * List all the files in a directory.
	 *
	 * @param folder    The folder to get the files from.
	 * @param recursive If we should list folders recursively.
	 *
	 * @return A list of paths.
	 */
	private static List<Path> listFiles(final Path folder, boolean recursive){
		final var files = new LinkedList<Path>();
		for(final var file : Objects.requireNonNull(folder.toFile().listFiles())){
			if(file.isDirectory()){
				if(recursive){
					files.addAll(listFiles(folder.resolve(file.getName()), true));
				}
			}
			else{
				files.add(folder.resolve(file.getName()));
			}
		}
		return files;
	}
}
