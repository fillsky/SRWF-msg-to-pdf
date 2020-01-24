package main;


import java.io.IOException;


public class App {

    static {
            try {
                NativeUtils.loadLibraryFromJar("/jacob-1.18-x64.dll"); // during runtime. .DLL within .JAR
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }

    }
    public static void main(String[] args) throws IOException {

        DirectoryChoose.main(args);

    }

}
