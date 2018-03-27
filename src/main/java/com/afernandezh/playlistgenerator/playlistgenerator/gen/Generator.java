package com.afernandezh.playlistgenerator.playlistgenerator.gen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * https://musicbrainz.org/doc/Cover_Art_Archive/API#Example_3
 */
@Data
@AllArgsConstructor
@Log4j
public class Generator {

    private static final String DELIMITER = "-";

    private String rootFolder;
    private String targetPlayListFolder;

    public void generatePlayLists() throws IOException {

        // Normalize the paths
        rootFolder = rootFolder.replace("\\", "\\\\");
        targetPlayListFolder = targetPlayListFolder.replace("\\", "\\\\");

        //0.- Check if playlist folder exists or not to create it
        Path playListFolder = Paths.get(targetPlayListFolder);
        if (!Files.exists(playListFolder)) {
            Files.createDirectory(playListFolder);
        }

        //1.- Delete all m3u
        Files.walk(Paths.get(rootFolder))
                .filter(GeneratorUtils::isM3UFile)
                .forEach(GeneratorUtils::deleteFile);

        //2.- Generate m3u in each mp3 folder
        Files.walk(Paths.get(rootFolder))
                .filter(Files::isDirectory)
                .filter(GeneratorUtils::isMP3Folder)
                .forEach(GeneratorUtils::generateM3U);

        //3.- Copy generated files to targetPlayListFolder
        Files.walk(Paths.get(rootFolder))
                .filter(GeneratorUtils::isM3UFile)
                .forEach(path -> GeneratorUtils.copyM3UWithRelative(path, targetPlayListFolder));

        //4.- Generate the structure
        //...
    }
}
