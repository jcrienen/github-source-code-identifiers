package com.juulcrienen.githubsourcecodeidentifiers.extractors;

import ai.serenade.treesitter.Languages;

public class LanguageMapper {

    public static long getLanguage(String extension) {
        return switch(extension) {
            case "cs" -> Languages.cSharp();
            case "java" -> Languages.java();
            case "c" -> Languages.c();
            case "cpp" -> Languages.cpp();
            default -> throw new IllegalArgumentException("The provided extension is not (yet) supported!");
        };
    }

}
