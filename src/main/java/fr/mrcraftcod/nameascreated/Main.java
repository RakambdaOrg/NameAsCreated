package fr.mrcraftcod.nameascreated;

import fr.mrcraftcod.nameascreated.renaming.RenameDate;
import fr.mrcraftcod.nameascreated.renaming.RenameIncrementing;
import fr.mrcraftcod.nameascreated.strategy.ByDateRenaming;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-12-20.
 *
 * @author Thomas Couchoud
 * @since 2018-12-20
 */
public class Main{
	/**
	 * Main method.
	 *
	 * @param args
	 */
	public static void main(final String[] args){
		final var argsList = new LinkedList<>(Arrays.asList(args));
		
		switch(Objects.requireNonNull(argsList.peek())){
			case "-n":
				argsList.pop();
				var startIndex = 0;
				if(Objects.requireNonNull(argsList.peek()).equals("-s")){
					argsList.pop();
					startIndex = Integer.parseInt(argsList.pop()) - 1;
				}
				RenameIncrementing.processFiles(startIndex, new ByDateRenaming(), argsList.stream().map(Paths::get).collect(Collectors.toList()));
				break;
			case "-t":
				argsList.pop();
				NewFile.testMode = true;
				RenameDate.processFiles(new ByDateRenaming(), argsList.stream().map(Paths::get).collect(Collectors.toList()));
				break;
			case "-r":
				argsList.pop();
				RenameDate.processFiles(new ByDateRenaming(), listFiles(Paths.get(Objects.requireNonNull(argsList.peek()))));
				break;
			case "-rt":
				argsList.pop();
				NewFile.testMode = true;
				RenameDate.processFiles(new ByDateRenaming(), listFiles(Paths.get(Objects.requireNonNull(argsList.peek()))));
				break;
			default:
				RenameDate.processFiles(new ByDateRenaming(), argsList.stream().map(Paths::get).collect(Collectors.toList()));
				break;
		}
	}
	
	/**
	 * List all the files in a directory.
	 *
	 * @param folder The folder to get the files from.
	 *
	 * @return A list of paths.
	 */
	private static LinkedList<Path> listFiles(final Path folder){
		final var files = new LinkedList<Path>();
		for(final var file : Objects.requireNonNull(folder.toFile().listFiles())){
			if(file.isDirectory()){
				files.addAll(listFiles(folder.resolve(file.getName())));
			}
			else{
				files.add(folder.resolve(file.getName()));
			}
		}
		return files;
	}
}
