package fr.mrcraftcod.nameascreated;

import fr.mrcraftcod.nameascreated.renaming.ByDateRenaming;
import java.io.File;
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
				RenameIncrementing.processFiles(startIndex, new ByDateRenaming(), argsList.stream().map(File::new).collect(Collectors.toList()));
				break;
			case "-t":
				argsList.pop();
				NameAsCreated.testMode = true;
				NameAsCreated.renameDate(argsList);
				break;
			case "-r":
				argsList.pop();
				NameAsCreated.renameDate(listFiles(new File(Objects.requireNonNull(argsList.peek()))));
				break;
			case "-rt":
				argsList.pop();
				NameAsCreated.testMode = true;
				NameAsCreated.renameDate(listFiles(new File(Objects.requireNonNull(argsList.peek()))));
				break;
			default:
				NameAsCreated.renameDate(argsList);
				break;
		}
	}
	
	/**
	 * List all the files in a directory.
	 *
	 * @param folder The folder to get the files from.
	 *
	 * @return A list of file paths.
	 */
	private static LinkedList<String> listFiles(final File folder){
		final var files = new LinkedList<String>();
		for(final var file : Objects.requireNonNull(folder.listFiles())){
			if(file.isDirectory()){
				files.addAll(listFiles(file));
			}
			else{
				files.add(file.getAbsolutePath());
			}
		}
		return files;
	}
}
