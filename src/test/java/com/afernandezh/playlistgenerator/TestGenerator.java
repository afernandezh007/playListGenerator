package com.afernandezh.playlistgenerator;


import com.afernandezh.playlistgenerator.playlistgenerator.gen.Generator;
import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@Log4j
public class TestGenerator {

    @Test
    public void test() {

        ClassLoader classLoader = getClass().getClassLoader();
        String rootPath = null;
        try {
            rootPath = Paths.get(classLoader.getResource("root").toURI()).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String playListPath = Paths.get(rootPath).resolve("00_playlists").toFile().toString();
        String playListClassifiedPath = Paths.get(rootPath).resolve("00_playlists_classified").toFile().toString();


        log.info("=====================================================");
        log.info("ROOT PATH: " + rootPath);
        log.info("=====================================================");

        Generator gen = new Generator(rootPath, playListPath, playListClassifiedPath);

        try {
            gen.generatePlayLists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
