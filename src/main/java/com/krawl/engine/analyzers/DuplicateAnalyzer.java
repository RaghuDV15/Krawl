package com.krawl.engine.analyzers;

import static com.krawl.KrawlApplication.krawlConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krawl.data.model.Vertex;

// TODO: Auto-generated Javadoc
/**
 * The Class DuplicateAnalyzer.
 */
@Component
public class DuplicateAnalyzer implements Runnable {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void run() {
		LOGGER.info("New Duplicate Analyser Thread Started");
		ObjectMapper mapper = new ObjectMapper();
		List<Vertex> vertices = new ArrayList<Vertex>();
		while (true) {
			try {
				Files.walkFileTree(Paths.get(krawlConfig.getIndexPath()), EnumSet.of(FileVisitOption.FOLLOW_LINKS),
						Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {

							@Override
							public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
								vertices.stream().collect(Collectors.groupingBy(Vertex::getUrl)).values().stream()
										.filter(sameUrl -> sameUrl.size() > 1).forEach(v -> {
											LOGGER.warn("Duplicate Krawl" + ((Vertex) v).getUrl());
										});
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
									throws IOException {
								vertices.clear();
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
								vertices.add(mapper.readValue((new FileInputStream(file.toString())), Vertex.class));
								return FileVisitResult.CONTINUE;
							}
						});
				Thread.sleep(60000);
			} catch (Exception e) {
			}
		}
	}
}