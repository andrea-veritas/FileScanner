package org.rogerusrex.fileScanner.Main;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;


/**
 * Created by Roger on 2016/3/30.
 */
public class Entry {
    public static void main(String[] argv) {

        Arguements args = new Arguements(argv);

        File root = new File(args.getTargetDirectory());
        if (root.exists() && root.isDirectory()) {

        }

    }

    public static class Arguements {
        public final static String MODE_CSV = "mode.csv";
        public final static String MODE_JSON = "mode.json";
        public final static String HASH_MD5 = "hash.md5";
        public final static String HASH_sha1 = "hash.sha1";
        @Option(name = "-m", aliases = {"--mode"}, required = false)
        private String outputMode = MODE_CSV;
        @Option(name = "-o", required = false)
        private String outputFile;
        @Option(name = "-t", required = false)
        private String targetDirectory = ".";
        @Option(name = "-h", required = false)
        private String hashMethod = HASH_MD5;

        public Arguements(String[] argv) {
            CmdLineParser parser = new CmdLineParser(this);
            try {
                parser.parseArgument(argv);
            } catch (CmdLineException e) {
                parser.printUsage(System.out);
                System.exit(-1);
            }
        }

        public String getOutputMode() {
            return outputMode;
        }

        public String getOutputFile() {
            return outputFile;
        }

        public String getTargetDirectory() {
            return targetDirectory;
        }
    }


}
