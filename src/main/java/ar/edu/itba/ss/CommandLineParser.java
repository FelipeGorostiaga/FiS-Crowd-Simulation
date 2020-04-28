package ar.edu.itba.ss;

import org.apache.commons.cli.*;

import static java.lang.System.exit;

class CommandLineParser {

    static double FPS = 1000;
    static int N = 100;
    static double speed = 0.8; // m/s


    private static Options createOptions(){
        Options options = new Options();
        options.addOption("h", "help", false, "Shows this screen.");
        options.addOption("n", "particles", true, "Number of pedestrians.");
        options.addOption("s", "desiredSpeed", true, "Desired speed of the pedestrians.");
        options.addOption("fps", "fps", true, "Time step for the animation.");
        return options;
    }

    public static void parseOptions(String[] args){
        Options options = createOptions();
        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();

        try{
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("h")){
                help(options);
            }
            if (cmd.hasOption("fps")) {
                FPS = Double.parseDouble(cmd.getOptionValue("fps"));
            }
            if (cmd.hasOption("n")) {
                N = Integer.parseInt(cmd.getOptionValue("p"));
            }
            if (cmd.hasOption("s")) {
                speed = Double.parseDouble(cmd.getOptionValue("s"));
            }
        }catch (Exception e){
            System.out.println("Argument not recognized.");
            help(options);
        }
    }

    private static void help(Options options){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Crowd simulation - Exit!", options);
        exit(0);
    }
























}
