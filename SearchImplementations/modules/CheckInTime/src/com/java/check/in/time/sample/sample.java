package com.java.check.in.time.sample;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class sample {

	public static void main(String[] args) throws IOException {

		File[] files = new File("C:\\temp\\foldername")
				.listFiles(obj -> obj.isFile() && obj.getName().endsWith(".csv"));

		List<Path> paths = new ArrayList<Path>();
		for (File file : files) {
			System.out.println(file.getPath());
			file.getPath();
			paths.add(Paths.get(file.getPath()));
		}

		// List<Path> paths = Arrays.asList(Paths.get("c:/temp/file1.csv"),
		// Paths.get("c:/temp/file2.csv"));
		List<String> mergedLines = new ArrayList<>();
		for (Path p : paths) {
			List<String> lines = Files.readAllLines(p, Charset.forName("UTF-8"));
			if (!lines.isEmpty()) {
				if (mergedLines.isEmpty()) {
					mergedLines.add(lines.get(0)); // add header only once
				}
				mergedLines.addAll(lines.subList(1, lines.size()));
			}
		}
		Path target = Paths.get("c:/temp/merged.csv");
		Files.write(target, mergedLines, Charset.forName("UTF-8"));
	}

}
