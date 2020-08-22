package fr.raksrinana.nameascreated;

import lombok.Getter;
import lombok.NoArgsConstructor;
import picocli.CommandLine;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@CommandLine.Command(name = "nameascreated", mixinStandardHelpOptions = true)
public class CLIParameters{
	@CommandLine.Parameters(description = "The files or folders to process")
	private List<Path> files = new ArrayList<>();
	@CommandLine.Option(names = {
			"-m",
			"--mode"
	}, description = "The mode to use to rename")
	private RunMode runMode = RunMode.DATE;
	@CommandLine.Option(names = {
			"-r",
			"--recursive"
	}, description = "Set the status of the recursion to list files", arity = "1")
	private boolean recursive = true;
	@CommandLine.Option(names = {
			"-t",
			"--test"
	}, description = "Run the program in test move (no operations on the files will be executed)")
	private boolean testMode = false;
	@CommandLine.Option(names = {
			"--start-index"
	}, description = "The start index when renaming a sequence")
	private int startIndex = 1;
}
