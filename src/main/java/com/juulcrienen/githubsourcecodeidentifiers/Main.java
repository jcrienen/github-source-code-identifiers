package com.juulcrienen.githubsourcecodeidentifiers;

import com.juulcrienen.githubapiwrapper.GitHubAPIWrapper;
import org.apache.commons.cli.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        long startMillis = System.currentTimeMillis();

        Logger logger = Logger.getLogger("GHSCIE");
        logger.setLevel(Level.INFO);

        CommandLine commandLine = null;

        String propertiesFile;
        boolean verbose;

        Option propertiesOption = Option.builder("p")
                .required(true)
                .longOpt("properties")
                .desc("Properties file")
                .hasArg(true)
                .build();

        Option verboseOption = Option.builder("v")
                .longOpt("verbose")
                .desc("Use to enable verbose mode")
                .build();

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        options.addOption(propertiesOption);
        options.addOption(verboseOption);

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException exception) {
            System.out.println(exception.getMessage());
            formatter.printHelp("github-source-code-extractor", options);
            return;
        }

        propertiesFile = commandLine.getOptionValue("properties");
        verbose = commandLine.hasOption("verbose");

        try {
            GitHubExtractor extractor = new GitHubExtractor(propertiesFile, verbose);
            extractor.extractIdentifiers();

            long endMillis = System.currentTimeMillis();
            GitHubAPIWrapper.info("Total time: " + (endMillis - startMillis) / 1000.0 + " seconds");
        } catch (Exception e) {
            GitHubAPIWrapper.error(e.getMessage());
        }
    }

}
