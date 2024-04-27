// 231RDB118 Dmytro Svyrydenko
// 231RDC028 Ņikita Hess
// 231RDB090 Iļja Grabovskis
// 231RDB383 Jaroslavs Zaharenkovs

import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String choiseStr;
		String sourceFile, resultFile, firstFile, secondFile;
		
		loop: while (true) {
			
			choiseStr = sc.next();
								
			switch (choiseStr) {
			case "comp":
				System.out.print("source file name: ");
				sourceFile = sc.next();
				System.out.print("archive name: ");
				resultFile = sc.next();
				comp(sourceFile, resultFile);
                break;
			case "decomp":
				System.out.print("archive name: ");
				sourceFile = sc.next();
				System.out.print("file name: ");
				resultFile = sc.next();
				decomp(sourceFile, resultFile);
                break;
			case "size":
				System.out.print("file name: ");
				sourceFile = sc.next();
				size(sourceFile);
				break;
			case "equal":
				System.out.print("first file name: ");
				firstFile = sc.next();
				System.out.print("second file name: ");
				secondFile = sc.next();
				System.out.println(equal(firstFile, secondFile));
				break;
			case "about":
				about();
				break;
			case "exit":
				break loop;
			}
		}

		sc.close();
	}

	public static void comp(String sourceFile, String resultFile) {
		LZ77.compress(sourceFile, resultFile);
	}

	public static void decomp(String sourceFile, String resultFile) {
		LZ77.decompress(sourceFile, resultFile);
	}
	
	public static void size(String sourceFile) {
		try {
			FileInputStream f = new FileInputStream(sourceFile);
			System.out.println("size: " + f.available());
			f.close();
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	public static boolean equal(String firstFile, String secondFile) {
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
				for (int i=0; i<k1; i++) {
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
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}
	
	public static void about() {
		System.out.println("231RDB118 Dmytro Svyrydenko");
		System.out.println("231RDC028 Ņikita Hess");
		System.out.println("231RDB090 Iļja Grabovskis");
		System.out.println("231RDB383 Jaroslavs Zaharenkovs");
	}
}

class LZ77 {

    public static void compress(String sourceFile, String resultFile) {
		StringBuilder compressedData = new StringBuilder();
		int windowSize = 256;
		int searchBufferStart = 0;
	
		String data = readFile(sourceFile);
	
		for (int i = 0; i < data.length(); i++) {
			int matchLength = 0;
			int matchDistance = 0;
	
			for (int j = searchBufferStart; j < i && j < i - matchDistance; j++) {
				int currentMatchLength = 0;
				while (i + currentMatchLength < data.length() && data.charAt(i + currentMatchLength) == data.charAt(j + currentMatchLength)) {
					currentMatchLength++;
				}
	
				if (currentMatchLength > matchLength) {
					matchLength = currentMatchLength;
					matchDistance = i - j;
				}
			}
	
			searchBufferStart = Math.max(searchBufferStart, i - windowSize + 1);
	
			if (matchLength > 0) {
				compressedData.append("(" + matchDistance + "," + matchLength + ")");
				i += matchLength - 1;
			} else {

				if (data.charAt(i) == '(') {
					compressedData.append("090909");
				} else {
					compressedData.append(data.charAt(i));
				}
			}
		}
	
		writeStringToFile(compressedData.toString(), resultFile);
	}

    private static String readFile(String filePath) {
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				content.append(line).append("\n");
			}
			if (content.length() > 0 && content.charAt(content.length() - 1) == '\n') {
				content.deleteCharAt(content.length() - 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content.toString();
	}
	

    private static void writeStringToFile(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public static void decompress(String sourceFile, String resultFile) {
		StringBuilder decompressedData = new StringBuilder();
		String data = readFile(sourceFile);
	
		int i = 0;
		while (i < data.length()) {
			
			if (i <= data.length() - 6 && data.substring(i, i + 6).equals("090909")) {

				decompressedData.append('(');
				i += 6; 
			} else if (data.charAt(i) != '(') {
				decompressedData.append(data.charAt(i));
				i++;
			} else {

				int closingIndex = data.indexOf(')', i);
				if (closingIndex == -1) {

					System.err.println("Invalid compressed data format");
					return;
				}
	
				String substring = data.substring(i + 1, closingIndex);
				String[] parts = substring.split(",");
				int distance = Integer.parseInt(parts[0]);
				int length = Integer.parseInt(parts[1]);
	
				int startPos = decompressedData.length() - distance;
				for (int j = 0; j < length; j++) {
					char ch = decompressedData.charAt(startPos + j);
					decompressedData.append(ch);
				}
	
				i = closingIndex + 1;
			}
		}
	
		writeStringToFile(decompressedData.toString(), resultFile);
	}
	
	
}