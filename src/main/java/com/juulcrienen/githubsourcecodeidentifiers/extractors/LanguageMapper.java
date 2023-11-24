package com.juulcrienen.githubsourcecodeidentifiers.extractors;

import ai.serenade.treesitter.Languages;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LanguageMapper {

    public static long getLanguage(String language) {
        return switch(language) {
            case "csharp" -> Languages.cSharp();
            case "java" -> Languages.java();
            case "c" -> Languages.c();
            case "c++", "cpp" -> Languages.cpp();
            case "python" -> Languages.python();
            default -> throw new IllegalArgumentException("The language " + language + " is not (yet) supported!");
        };
    }

    public static List<String> getExtensions(String language, Properties properties) {
        return Arrays.asList(properties.getProperty("extensions." + language).split(","));
    }

    public static long getLanguageByExtension(String extension, Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if(!((String) entry.getKey()).startsWith("extensions")) continue;
            if(Arrays.asList(((String) entry.getValue()).split(",")).contains((extension))) return getLanguage(((String) entry.getKey()).substring(11));
        }
        throw new IllegalArgumentException("The provided extension does not have a language!");
    }

}
