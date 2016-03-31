package org.rogerusrex.fileScanner.Main;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import javax.xml.transform.Result;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * Created by Roger on 2016/3/30.
 */
public class Entry {
    private static class ResultBean{
        public ResultBean(String filePath, String hash, Long length) {
            this.filePath = filePath;
            this.hash = hash;
            this.length = length;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getHash() {
            return hash;
        }

        public Long getLength() {
            return length;
        }

        String filePath;
        String hash;
        Long length;
    }
    private static class Message<T>{
        public Message(T content, MessageType type) {
            this.content = content;
            this.type = type;
        }

        public T getContent() {
            return content;
        }

        public MessageType getType() {
            return type;
        }

        private T content;
        private MessageType type;
    }
    private static enum  MessageType{
        CONTENT,END
    }
    public static void main(String[] argv) {

        Arguements args = new Arguements(argv);

        File root = new File(args.getTargetDirectory());
        Stack<File> stack = new Stack<File>();
        final BlockingQueue<Message<File>> fileQueue=new LinkedBlockingQueue<>();
        final BlockingQueue<Message<ResultBean>> resultQueue=new LinkedBlockingQueue<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message<ResultBean> resultBeanMessage=null;
                try {
                    while((resultBeanMessage=resultQueue.poll(1000L,TimeUnit.MILLISECONDS))!=null){
                        if(resultBeanMessage.getType().equals(MessageType.END)){
                            return;
                        }
                        System.out.println(String.format("%s,%s,%s",resultBeanMessage.getContent().getFilePath(),resultBeanMessage.getContent().getHash(),resultBeanMessage.getContent().getLength().toString()));

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message<File> msg=null;
                try {
                    while((msg=fileQueue.poll(1000L, TimeUnit.MILLISECONDS))!=null){

                        if(msg.getType().equals(MessageType.END)){
                            resultQueue.offer(new Message<ResultBean>(new ResultBean(null,null,null),MessageType.END));
                            return;
                        }

                        File f=msg.getContent();
                        if(f.exists()&&f.canRead()) {
                            try {
                                MessageDigest md5=MessageDigest.getInstance("MD5");
                                byte[] digest;
                                FileInputStream fis = new FileInputStream(f);
                                FileChannel channel = fis.getChannel();
                                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY,0,f.length());
                                md5.update(buffer);
                                byte[] binary=md5.digest();
                                String b64=Base64.getEncoder().encodeToString(binary);

                                Message<ResultBean> resultBeanMessage = new Message<ResultBean>(new ResultBean(f.getAbsolutePath(),b64,f.length()),MessageType.CONTENT);
                                resultQueue.offer(resultBeanMessage);


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }).start();
        if (root.exists() && root.isDirectory()) {
            stack.push(root);
            File node=null;
            while(stack.isEmpty()==false){
                node=stack.pop();
                if(node.isDirectory()){
                    for(File f:node.listFiles()){
                        stack.push(f);
                    }
                }else{
                    fileQueue.offer(new Message(node,MessageType.CONTENT));
                }
            }
            fileQueue.offer(new Message<File>(null,MessageType.END));
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
