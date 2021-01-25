package fr.raksrinana.nameascreated;

import fr.raksrinana.nameascreated.renaming.RenameDate;
import fr.raksrinana.nameascreated.renaming.RenameIncrementing;
import fr.raksrinana.nameascreated.strategy.ByDateRenaming;
import fr.raksrinana.nameascreated.utils.JacksonObjectMapper;
import kong.unirest.Unirest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(final CommandLine.ParameterException e){
			log.error("Failed to parse arguments", e);
			cli.usage(System.out);
			return;
		}
		
		Unirest.config().setObjectMapper(new JacksonObjectMapper())
				.connectTimeout(30000)
				.socketTimeout(30000)
				.enableCookieManagement(true)
				.verifySsl(true);
		NewFile.testMode = parameters.isTestMode();
		final var files = parameters.getFiles().stream()
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
	@NonNull
	private static List<Path> listFiles(@NonNull final Path folder, boolean recursive){
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
