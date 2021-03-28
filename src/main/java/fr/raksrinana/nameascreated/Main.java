package fr.raksrinana.nameascreated;

import fr.raksrinana.nameascreated.renaming.RenameDate;
import fr.raksrinana.nameascreated.renaming.RenameIncrementing;
import fr.raksrinana.nameascreated.strategy.ByDateRenaming;
import fr.raksrinana.nameascreated.utils.JacksonObjectMapper;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Main{
	/**
	 * Main method.
	 *
	 * @param args .
	 */
	public static void main(String[] args){
		var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(CommandLine.ParameterException e){
			log.error("Failed to parse arguments", e);
			cli.usage(System.out);
			return;
		}
		
		Unirest.config().setObjectMapper(new JacksonObjectMapper())
				.connectTimeout(30000)
				.enableCookieManagement(true)
				.verifySsl(true);
		NewFile.testMode = parameters.isTestMode();
		var files = parameters.getFiles().stream()
				.flatMap(f -> listFiles(f, parameters.isRecursive()).stream())
				.distinct()
				.collect(Collectors.toList());
		switch(parameters.getRunMode()){
			case DATE -> RenameDate.processFiles(new ByDateRenaming(), files);
			case SEQUENCE -> RenameIncrementing.processFiles(parameters.getStartIndex(), new ByDateRenaming(), files);
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
	@NotNull
	private static List<Path> listFiles(@NotNull Path folder, boolean recursive){
		try{
			Stream<Path> paths;
			if(recursive){
				paths = Files.walk(folder);
			}
			else{
				paths = Files.list(folder);
			}
			return paths
					.filter(file -> !Files.isDirectory(file))
					.collect(Collectors.toList());
		}
		catch(IOException e){
			log.error("Failed to list files in folder {}", folder, e);
		}
		return List.of();
	}
}
