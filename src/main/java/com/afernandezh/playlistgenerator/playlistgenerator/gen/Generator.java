package com.afernandezh.playlistgenerator.playlistgenerator.gen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * https://musicbrainz.org/doc/Cover_Art_Archive/API#Example_3
 */
@Data
@RequiredArgsConstructor
@Log4j
public class Generator {

    //---------------------------------------------------
    // Constants
    public static final String DELIMITER = "-";
    public static final String WINDOWS_SEPARATOR = "\\";

    //---------------------------------------------------
    // Private fieds used like param in constructor
    @NonNull
    private String rootFolder;
    @NonNull
    private String targetPlayListFolder;
    @NonNull
    private String targetPlayListClassifiedFolder;

    //---------------------------------------------------
    //Internal fields
    private Path rootPath;
    private Path playListPath;
    private Path playListClasiffiedPath;

    /**
     * main method to generate the playlists
     * @throws IOException
     */
    public void generatePlayLists() throws IOException {

        //0.- Check if playlist folder exists or not to create it
        normalizeAndCreateFolders();

        //1.- Delete all m3u
        Files.walk(rootPath)
                .filter(GeneratorUtils::isM3UFile)
                .forEach(GeneratorUtils::deleteFile);

        //2.- Generate m3u in each mp3 folder
        Files.walk(rootPath)
                .filter(Files::isDirectory)
                .filter(GeneratorUtils::isMP3Folder)
                .forEach(GeneratorUtils::generateM3U);

        //3.- Copy generated files to targetPlayListFolder and update them with relative paths to the MP3
        Files.walk(rootPath)
                .filter(GeneratorUtils::isM3UFile)
                .forEach(path -> GeneratorUtils.relativizePathsAndCopy(path, playListPath, rootPath));

        //===============================================================================================
        //4.- Generate the structure

//        //4.1.- we classify the m3u files for artist
//        Map<String, List<String>> collect = Files
//                .list(Paths.get(targetPlayListFolder))
//                .map(path -> path.getFileName().toString())
//                .collect(Collectors.groupingBy(GeneratorUtils::getArtist));
//
//        //4.2.- Next we must create the folders for each artist and modify the m3u
//        for (String key : collect.keySet()) {
//
//            Path artistFolder = Paths.get(targetPlayListFolder, key);
//            if (!Files.exists(artistFolder)) {
//                Files.createDirectory(artistFolder);
//            }
//        }
    }

    /**
     * Method to normalize the paths and create the initial folders if necessary to store the playlists
     * @throws IOException
     */
    private void normalizeAndCreateFolders() throws IOException {

        rootPath = Paths.get(rootFolder).normalize();
        playListPath = Paths.get(targetPlayListFolder).normalize();
        playListClasiffiedPath = Paths.get(targetPlayListClassifiedFolder).normalize();

        // Check if playlist folders exist or not to create them
        if (!Files.exists(playListPath)) {
            Files.createDirectory(playListPath);
        }

        if (!Files.exists(playListClasiffiedPath)) {
            Files.createDirectory(playListClasiffiedPath);
        }
    }


}
