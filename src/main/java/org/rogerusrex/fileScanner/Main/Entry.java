package org.rogerusrex.fileScanner.Main;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.Stack;


/**
 * Created by Roger on 2016/3/30.
 */
public class Entry {
    public static void main(String[] argv) {

        Arguements args = new Arguements(argv);

        File root = new File(args.getTargetDirectory());
        Stack<File> stack = new Stack<File>();
        if (root.exists() && root.isDirectory()) {
            stack.push(root);
            File node=null;
            while((node=stack.pop())!=null){
                if(node.isDirectory()){
                    for(File f:node.listFiles()){
                        stack.push(f);
                    }
                }else{
                    String fileName=node.getAbsolutePath();
                    Long size=FileUtils.sizeOf(node);
                    FileUtils.checksum(node,);
                }
            }
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
