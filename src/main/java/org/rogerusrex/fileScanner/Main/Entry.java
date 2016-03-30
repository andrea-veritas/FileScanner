package org.rogerusrex.fileScanner.Main;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;


/**
 * Created by Roger on 2016/3/30.
 */
public class Entry {
    public static void main(String[] argv){

        Arguements args=new Arguements(argv);

        File root=new File(args.getTargetDirectory());
        if(root.exists()&&root.isDirectory()){

        }

    }

    public static class Arguements{
        public Arguements(String[] argv){
            CmdLineParser parser = new CmdLineParser(this);
            try {
                parser.parseArgument(argv);
            } catch (CmdLineException e) {
                parser.printUsage(System.out);
                System.exit(-1);
            }
        }

        public final static String MODE_CSV="mode.csv";
        public final static String MODE_json="mode.json";

        public String getOutputMode() {
            return outputMode;
        }

        public String getOutputFile() {
            return outputFile;
        }
        public String getTargetDirectory() {
            return targetDirectory;
        }
        @Option(name = "-m", aliases = {"--mode"})
        private String outputMode = MODE_CSV;
        @Option(name="-o")
        private String outputFile;
        @Option(name="-t")
        private String targetDirectory=".";
    }


}
