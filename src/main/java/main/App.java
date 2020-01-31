package main;


import java.io.IOException;


public class App {

    static {
            try {
                if (System.getProperty("os.arch").equals("amd64")){
                    NativeUtils.loadLibraryFromJar("/jacob-1.18-x64.dll"); // during runtime. .DLL within .JAR
                } else
                {
                    NativeUtils.loadLibraryFromJar("/jacob-1.18-x86.dll");
                }

            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }

    }
    public static void main(String[] args) throws IOException {

        DirectoryChoose.main(args);

    }

}
