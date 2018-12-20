package fr.mrcraftcod.nameascreated;

import fr.mrcraftcod.nameascreated.renaming.RenamingStrategy;
import fr.mrcraftcod.utils.base.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-12-20.
 *
 * @author Thomas Couchoud
 * @since 2018-12-20
 */
class RenameIncrementingTest{
	private static final RenamingStrategy RENAMING_STRATEGY = f -> {
		final var name = f.getName().substring(0, f.getName().lastIndexOf("."));
		return new NewFile(name, f.getName().substring(f.getName().lastIndexOf(".")), f.getParentFile(), new Date(Integer.parseInt(name)), f);
	};
	private List<File> files;
	
	@BeforeEach
	void setUp(){
		final var classLoader = getClass().getClassLoader();
		files = Arrays.stream(Objects.requireNonNull(new File(Objects.requireNonNull(classLoader.getResource("renameIncrement")).getFile()).listFiles())).map(f -> {
			final var dest = Paths.get("test").resolve(f.getName());
			try{
				dest.getParent().toFile().mkdirs();
				Files.copy(Paths.get(f.toURI()), dest);
			}
			catch(IOException ignored){
			}
			return dest.toFile();
		}).collect(Collectors.toList());
	}
	
	@AfterEach
	void tearDown(){
		FileUtils.forceDelete(new File("test"));
	}
	
	@Test
	void processFiles(){
		assertEquals(4, files.size());
		RenameIncrementing.processFiles(9, RENAMING_STRATEGY, files);
		final var newFiles = Arrays.asList(Objects.requireNonNull(new File("test").listFiles()));
		assertEquals(4, newFiles.size());
		newFiles.forEach(f -> {
			try{
				final var name = Files.readAllLines(Paths.get(f.toURI())).get(0);
				assertEquals(name, f.getName());
			}
			catch(final IOException e){
				fail("Couldn't read file");
			}
		});
	}
}