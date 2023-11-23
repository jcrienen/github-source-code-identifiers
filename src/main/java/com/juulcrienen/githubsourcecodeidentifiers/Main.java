package com.juulcrienen.githubsourcecodeidentifiers;

import org.apache.commons.cli.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("GHSCIE");
        logger.setLevel(Level.INFO);

        CommandLine commandLine = null;

        String inputFilePath;
        String outputFolderPath;
        String libraryInputFilePath;
        String[] extension;
        String username;
        String token;
        boolean verbose;

        Option inputOption = Option.builder("i")
                .required(true)
                .longOpt("input")
                .desc("Input file path")
                .hasArg(true)
                .build();
        Option outputOption = Option.builder("o")
                .required(true)
                .longOpt("output")
                .desc("Output folder location")
                .hasArg(true)
                .build();
        Option libraryOption = Option.builder("l")
                .required(true)
                .longOpt("library")
                .desc("Library file location")
                .hasArg(true)
                .build();
        Option extensionOption = Option.builder("e")
                .required(true)
                .longOpt("extension")
                .desc("File extension(s)")
                .hasArg(true)
                .build();

        Option verboseOption = Option.builder("v")
                .longOpt("verbose")
                .desc("Use to enable verbose mode")
                .build();

        Option usernameOption = Option.builder("u")
                .longOpt("username")
                .desc("Username for accessing the GitHub API")
                .hasArg(true)
                .build();

        Option tokenOption = Option.builder("t")
                .longOpt("token")
                .desc("Token for accessing the GitHub API")
                .hasArg(true)
                .build();

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        options.addOption(inputOption);
        options.addOption(outputOption);
        options.addOption(libraryOption);
        options.addOption(extensionOption);
        options.addOption(verboseOption);
        options.addOption(usernameOption);
        options.addOption(tokenOption);

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException exception) {
            System.out.println(exception.getMessage());
            formatter.printHelp("github-source-code-extractor", options);
            return;
        }

        inputFilePath = commandLine.getOptionValue("input");
        outputFolderPath = commandLine.getOptionValue("output");
        libraryInputFilePath = commandLine.getOptionValue("library");
        extension = commandLine.getOptionValues("extension");
        username = commandLine.getOptionValue("username");
        token = commandLine.getOptionValue("token");
        verbose = commandLine.hasOption("verbose");

        GitHubExtractor extractor;
        if(commandLine.hasOption("username") && commandLine.hasOption("token")) {
            extractor = new GitHubExtractor(libraryInputFilePath, username, token, verbose);
        } else extractor = new GitHubExtractor(libraryInputFilePath, verbose);

        extractor.extractIdentifiers(inputFilePath, outputFolderPath, extension);
    }

}
