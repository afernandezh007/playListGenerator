package com.afernandezh.playlistgenerator.playlistgenerator.gen;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

        //1.- Calculate the relative path between playListFolder and source mp3 folder
        Integer nParts = rootFolder.getNameCount();
        Path sourceFolder = sourceM3U.getParent();
        Path relativeSourceFolder = sourceFolder.subpath(nParts, sourceFolder.getNameCount());
        Path relativeTargetFolder = playListFolder.subpath(nParts, playListFolder.getNameCount());

        //2.- Copy the same m3u file without any change to the playlistFolder
        Path targetM3U = copyM3UWithRelative(sourceM3U, playListFolder, relativeSourceFolder);
        Path relativePathBetween = relativeTargetFolder.relativize(relativeSourceFolder);

        //3.- Update the target file with the relative path
        try {
            updateM3UFileWithRelative(targetM3U, relativePathBetween);
        } catch (IOException e) {
            log.error("There was problems while updating the file " + targetM3U.getFileName(), e);
        }
    }

    /**
     * Method to copy a m3u file to a destination folder
     * @param sourceM3U
     * @param playListFolder
     * @param relativeSourceFolder
     */
    public static Path copyM3UWithRelative(Path sourceM3U, Path playListFolder, Path relativeSourceFolder) {

        Path returnValue = null;
        try {
            //We must append all the relative path to the m3u name to sort correctly in the main playlist folder

            //1.- Transform the relative to a prefix String like s1 - s2 - s3
            List<Path> parts = new ArrayList<>();
            relativeSourceFolder.iterator().forEachRemaining(parts::add);

            //remove last because it's the disc name and its implicit in the file name, so we cannot repeat it
            parts.remove(parts.size()-1);

            Optional<String> pathInStringCustom = parts.stream()
                    .map(p -> p.getFileName().toString())
                    .reduce((p1, p2) -> p1 + Generator.DELIMITER + p2);

            if (pathInStringCustom.isPresent()) {
                String prefixForM3U = pathInStringCustom.get();

                Path targetM3U = playListFolder.resolve(prefixForM3U + Generator.DELIMITER + sourceM3U.getFileName());
                Files.copy(sourceM3U, targetM3U);
                log.info(targetM3U + " copied!");
                returnValue = targetM3U;
            }
        } catch (IOException e) {
            log.error("There was problems while copying the file " + sourceM3U.getFileName(), e);
        }
        return returnValue;
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
