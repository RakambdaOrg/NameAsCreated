package fr.raksrinana.nameascreated;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CLIParameters{
	@Parameter(description = "The files or folders to process", listConverter = PathConverter.class)
	@Getter
	private List<Path> files = new ArrayList<>();
	@Parameter(names = {
			"-m",
			"--mode"
	}, description = "The mode to use to rename")
	@Getter
	private RunMode runMode = RunMode.DATE;
	@Parameter(names = {
			"-r",
			"--recursive"
	}, description = "Set the status of the recursion to list files", arity = 1)
	@Getter
	private boolean recursive = true;
	@Parameter(names = {
			"-t",
			"--test"
	}, description = "Run the program in test move (no operations on the files will be executed)")
	@Getter
	private boolean testMode = false;
	@Parameter(names = {
			"--start-index"
	}, description = "The start index when renaming a sequence")
	@Getter
	private int startIndex = 1;
	@Parameter(names = {
			"-h",
			"--help"
	}, help = true)
	private boolean help = false;
}
