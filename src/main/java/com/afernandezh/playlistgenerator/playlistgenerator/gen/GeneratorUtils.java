package com.afernandezh.playlistgenerator.playlistgenerator.gen;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Log4j
public class GeneratorUtils {

    /**
     * Main method to generate the playlist files for each disc folder
     *
     * @param disc
     */
    public static void generateM3U(Path disc) {

        String discName = disc.toFile().getName();
        Path m3uFile = Paths.get(disc.toFile().toString(), discName + ".m3u");
        Optional<String> m3uFileContent = Optional.empty();

        try {
            m3uFileContent = Files
                    .list(disc)
                    .filter(path -> path.toFile().toString().endsWith(".mp3"))
                    .map(path -> path.toFile().getName())
                    .reduce((s1, s2) -> s1 + "\n" + s2);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (m3uFileContent.isPresent()) {
            try {
                Files.write(m3uFile, m3uFileContent.get().getBytes());
                log.info(m3uFile + " written!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if param path is a m3u file
     *
     * @param p
     * @return
     */
    public static boolean isM3UFile(Path p) {
        return p.toFile().toString().endsWith(".m3u");
    }

    /**
     * Check if current dir is a folder with some mp3
     *
     * @param disc
     * @return
     */
    public static boolean isMP3Folder(Path disc) {
        boolean returnValue = false;
        try {
            returnValue = Files
                    .list(disc)
                    .anyMatch(path -> path.toFile().toString().endsWith(".mp3"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
     * Delete file catching the exception
     *
     * @param p
     */
    public static void deleteFile(Path p) {
        try {
            Files.delete(p);
            log.info("Deleted m3u file: " + p.toFile().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to copy a m3u file to a destination folder
     * @param m3ufile
     * @param targetFolder
     */
    public static void copyM3UWithRelative(Path m3ufile, String targetFolder) {

        Path playListFolder = Paths.get(targetFolder);
        try {

            Path target = playListFolder.resolve(m3ufile.getFileName());
            Files.copy(m3ufile, target);
            log.info(target + " copied!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
