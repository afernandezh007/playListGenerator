package com.afernandezh.playlistgenerator.playlistgenerator.gen;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Util class with all the necessary static methods to the Main generator class
 */
@Log4j
public class GeneratorUtils {

    /**
     * Main method to generate the playlist files for each disc folder
     *
     * @param disc
     */
    public static void generateM3U(Path disc) {

        String discName = disc.getFileName().toString();
        Path m3uFile = disc.resolve(discName + ".m3u");
        Optional<String> m3uFileContent = Optional.empty();

        try {
            m3uFileContent = Files
                    .list(disc)
                    .filter(path -> path.getFileName().toString().endsWith(".mp3"))
                    .map(path -> path.getFileName().toString())
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
        return p.getFileName().toString().endsWith(".m3u");
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
                    .anyMatch(path -> path.getFileName().toString().endsWith(".mp3"));
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
            log.info("Deleted m3u file: " + p.getFileName());
        } catch (IOException e) {
            log.error("There was problems while deleting the file " + p.getFileName(), e);
        }
    }



    /**
     * Method to copy the M3U file generated to the playlist folder
     * and update it with the relative paths to the mp3 files
     *
     * @param sourceM3U
     * @param playListFolder
     * @param rootFolder
     */
    public static void relativizePathsAndCopy(Path sourceM3U, Path playListFolder, Path rootFolder) {

        //1.- Copy the same m3u file without any change to the playlistFolder
        copyM3UWithRelative(sourceM3U, playListFolder);

        //2.- Calculate the relative path between playListFolder and source mp3 folder
        Path targetM3U = playListFolder.resolve(sourceM3U.getFileName());
        Integer nParts = rootFolder.getNameCount();
        Path relativeTarget = targetM3U.subpath(nParts, targetM3U.getNameCount() - 1);
        Path relativeSource = sourceM3U.subpath(nParts, sourceM3U.getNameCount() - 1);
        Path relativePathBetween = relativeTarget.relativize(relativeSource);

        //3.- Update the target file with the relative path
        try {
            updateM3UFileWithRelative(targetM3U, relativePathBetween);
        } catch (IOException e) {
            log.error("There was problems while updating the file " + targetM3U.getFileName(), e);
        }
    }

    /**
     * Method to copy a m3u file to a destination folder
     *
     * @param sourceM3U
     * @param playListFolder
     */
    public static void copyM3UWithRelative(Path sourceM3U, Path playListFolder) {

        try {
            Path targetM3U = playListFolder.resolve(sourceM3U.getFileName());
            Files.copy(sourceM3U, targetM3U);
            log.info(targetM3U + " copied!");
        } catch (IOException e) {
            log.error("There was problems while copying the file " + sourceM3U.getFileName(), e);
        }
    }

    /**
     * Update the M3U file with the relative path to the mp3
     *
     * @param sourceM3U
     * @param relative
     * @throws IOException
     */
    private static void updateM3UFileWithRelative(Path sourceM3U, Path relative) throws IOException {

        Optional<String> lines = Files
                .lines(sourceM3U)
                .map(line -> relative.getFileName().toString() + Generator.WINDOWS_SEPARATOR + line)
                .reduce((s1, s2) -> s1 + "\n" + s2);
        log.info(sourceM3U + " updated with relative paths!");
        if (lines.isPresent()) {
            Files.write(sourceM3U, lines.get().getBytes());
        }
    }
}
