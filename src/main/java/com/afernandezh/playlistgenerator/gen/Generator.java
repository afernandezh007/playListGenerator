package com.afernandezh.playlistgenerator.gen;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Generator {

    private String rootPath;
    private String targetPlayListPath;

}
