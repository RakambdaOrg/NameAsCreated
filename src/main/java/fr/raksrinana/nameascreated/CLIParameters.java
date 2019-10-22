package fr.raksrinana.nameascreated;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 01/09/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-09-01
 */
@SuppressWarnings("FieldMayBeFinal")
public class CLIParameters{
	@Parameter(description = "The files or folders to process", listConverter = PathConverter.class)
	private List<Path> files = new ArrayList<>();
	@Parameter(names = {
			"-m",
			"--mode"
	}, description = "The mode to use to rename")
	private RunMode runMode = RunMode.DATE;
	@Parameter(names = {
			"-r",
			"--recursive"
	}, description = "Set the status of the recursion to list files", arity = 1)
	private boolean recursive = true;
	@Parameter(names = {
			"-t",
			"--test"
	}, description = "Run the program in test move (no operations on the files will be executed)")
	private boolean testMode = false;
	@Parameter(names = {
			"--start-index"
	}, description = "The start index when renaming a sequence")
	private int startIndex = 1;
	@Parameter(names = {
			"-h",
			"--help"
	}, help = true)
	private boolean help = false;
	
	public CLIParameters(){
	}
	
	public List<Path> getFiles(){
		return files;
	}
	
	public RunMode getRunMode(){
		return runMode;
	}
	
	public int getStartIndex(){
		return startIndex;
	}
	
	public boolean isRecursive(){
		return recursive;
	}
	
	public boolean isTestMode(){
		return testMode;
	}
}
