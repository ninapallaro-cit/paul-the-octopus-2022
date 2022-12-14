package com.ciandt.paul;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the basic Application class. When you run the command line, this is the first class that will be called
 * and that will be responsible for triggering the prediction process.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private Config config;

    /**
     * Init method called by the runtime execution engine
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {

        Options options = new Options();

        Option output = new Option("d", "debug", false, "[OPTIONAL] turn on debug mode (default = off)");
        output.setRequired(false);
        options.addOption(output);

        Option file = new Option("f", "file", false, "[OPTIONAL] generates the CSV file (only for 'predict' command) (default = no file)");
        output.setRequired(false);
        options.addOption(file);

        Option predictor = new Option("p", "predictor", true, "username / login (required for 'upload' command)");
        output.setRequired(false);
        options.addOption(predictor);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("paul.sh", options);
            System.exit(1);
            return;
        }

        Boolean debugEnabled = cmd.hasOption("debug");
        if (debugEnabled) {
            config.setDebug("true");
        }
        Boolean generateFile = cmd.hasOption("file");
        String strPredictor = cmd.getOptionValue("predictor");
        if (strPredictor == null) {
            strPredictor = config.getDefaultPredictor();
        }

        //log the arguments
        if (config.isDebugEnabled()) {
            logger.debug("Application started with " + args.length + " arguments");
            for (int i = 0; i < args.length; i++) {
                logger.debug(">> " + args[i]);
            }
            logger.debug("debug mode = " + debugEnabled);
            logger.debug("generate file = " + generateFile);
            logger.debug("predictor = " + strPredictor);
        }

        //prediction
        try {
            predictionService.predict(generateFile, strPredictor);
        } catch (Exception e) {
            logger.error("Error creating prediction", e);
            System.exit(1);
        }

        logger.info("Process completed successfully!");
        System.exit(0);
    }

}

