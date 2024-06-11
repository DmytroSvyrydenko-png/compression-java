// 231RDB118 Dmytro Svyrydenko
// 231RDC028 Ņikita Hess
// 231RDB090 Iļja Grabovskis
// 231RDB383 Jaroslavs Zaharenkovs

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.*;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);

        HashMap<String, String> latvianDictionary = new HashMap<>();
        latvianDictionary.put("Ā", "A");
        latvianDictionary.put("ā", "a");
        latvianDictionary.put("Č", "C");
        latvianDictionary.put("č", "c");
        latvianDictionary.put("Ē", "E");
        latvianDictionary.put("ē", "e");
        latvianDictionary.put("Ģ", "G");
        latvianDictionary.put("ģ", "g");
        latvianDictionary.put("Ī", "I");
        latvianDictionary.put("ī", "i");
        latvianDictionary.put("Ķ", "K");
        latvianDictionary.put("ķ", "k");
        latvianDictionary.put("Ļ", "L");
        latvianDictionary.put("ļ", "l");
        latvianDictionary.put("Ņ", "N");
        latvianDictionary.put("ņ", "n");
        latvianDictionary.put("Š", "S");
        latvianDictionary.put("š", "s");
        latvianDictionary.put("Ū", "U");
        latvianDictionary.put("ū", "u");
        latvianDictionary.put("Ž", "Z");
        latvianDictionary.put("ž", "z");

        while (true) {
            System.out.println("Command (comp/decomp/size/equal/about/exit):");
            String command = scanner.nextLine().trim().toLowerCase();

            if (command.equals("comp")) {
                System.out.println("Name:");
                String inputFilePath = scanner.nextLine();

                System.out.println("Name new:");
                String outputFilePath = scanner.nextLine();

                try {
                    String input = main.readFile(inputFilePath);

                    ByteArrayOutputStream compressedStream = main.lzw_compress(input);

                    main.writeCompressedFile(outputFilePath, compressedStream.toByteArray());

                    System.out.println("Compression successful!");
                } catch (IOException e) {
                    System.out.println("Error reading/writing file: " + e.getMessage());
                }
            } else if (command.equals("decomp")) {
                System.out.println("Enter the full path to the file for decompression:");
                String inputFilePath = scanner.nextLine();

                System.out.println("Enter the name for the new file with unpacked data:");
                String outputFilePath = scanner.nextLine();

                try {
                    byte[] compressed = main.readCompressedFile(inputFilePath);

                    String extracted = main.lzw_extract(compressed, latvianDictionary);

                    main.writeFile(outputFilePath, extracted);

                    System.out.println("Decompression successful!");
                } catch (IOException e) {
                    System.out.println("Error reading/writing file: " + e.getMessage());
                }
            } else if (command.equals("size")) {
                System.out.print("File name: ");
                String sourceFile = scanner.nextLine();
                main.size(sourceFile);
            } else if (command.equals("equal")) {
                System.out.print("First file name: ");
                String firstFile = scanner.nextLine();
                System.out.print("Second file name: ");
                String secondFile = scanner.nextLine();
                System.out.println(main.equal(firstFile, secondFile));
            } else if (command.equals("about")) {
                main.about();
            } else if (command.equals("exit")) {
                System.out.println("Program terminated.");
                break;
            } else {
                System.out.println("Invalid command, please try again.");
            }
        }
        scanner.close();
    }

    public String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int c;
            while ((c = br.read()) != -1) {
                content.append((char) c);
            }
        }
        return content.toString();
    }

    public void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(content);
        }
    }

    public void writeCompressedFile(String filePath, byte[] compressedData) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(compressedData);
        }
    }

    public ByteArrayOutputStream lzw_compress(String input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArrayList<String> temp_out = new ArrayList<>();
        String[] data = input.split("");
        for (String character : data) {
            temp_out.add(character);
        }
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
            for (String outchar : temp_out) {
                gzipOutputStream.write(outchar.getBytes());
            }
            gzipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return out;
    }

    public byte[] readCompressedFile(String filePath) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
        return out.toByteArray();
    }

    public String lzw_extract(byte[] input, HashMap<String, String> dictionary) {
        StringBuilder out = new StringBuilder();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(input);
            GZIPInputStream gzipInputStream = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int len;
            String phrase = "";
            while ((len = gzipInputStream.read(buffer)) != -1) {
                for (int i = 0; i < len; i++) {
                    char currentChar = (char) buffer[i];
                    String currentStr = String.valueOf(currentChar);
                    if (dictionary.containsKey(phrase + currentStr)) {
                        phrase += currentStr;
                    } else {
                        out.append(dictionary.getOrDefault(phrase, phrase));
                        phrase = currentStr;
                    }
                }
            }
            out.append(dictionary.getOrDefault(phrase, phrase));
            gzipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
    

    public void size(String sourceFile) {
        try {
            FileInputStream f = new FileInputStream(sourceFile);
            System.out.println("Size: " + f.available());
            f.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean equal(String firstFile, String secondFile) {
        try {
            FileInputStream f1 = new FileInputStream(firstFile);
            FileInputStream f2 = new FileInputStream(secondFile);
            int k1, k2;
            byte[] buf1 = new byte[1000];
            byte[] buf2 = new byte[1000];
            do {
                k1 = f1.read(buf1);
                k2 = f2.read(buf2);
                if (k1 != k2) {
                    f1.close();
                    f2.close();
                    return false;
                }
                for (int i = 0; i < k1; i++) {
                    if (buf1[i] != buf2[i]) {
                        f1.close();
                        f2.close();
                        return false;
                    }

                }
            } while (!(k1 == -1 && k2 == -1));
            f1.close();
            f2.close();
            return true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public void about() {
        System.out.println("231RDB118 Dmytro Svyrydenko");
        System.out.println("231RDC028 Ņikita Hess");
        System.out.println("231RDB090 Iļja Grabovskis");
        System.out.println("231RDB383 Jaroslavs Zaharenkovs");
    }
}