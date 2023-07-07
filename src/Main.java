import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        String filePath = "C:\\Users\\zeche\\Desktop\\Practica\\TransactionsAggregator" +
                "\\src\\ENGIE_februarie_2023\\Settlement-note_GDFE_2023-02-01-RON.csv";
        Map<Integer, TransactionsInfo> transactions = new HashMap<>();
        transactions = FolderParser.parseCsvFile(filePath);

        FolderParser.printTableFormat(transactions, filePath);

//        Map<Integer, TransactionsInfo> transactions = new HashMap<>();
//        String folderPath = "C:\\Users\\zeche\\Desktop\\Practica\\TransactionsAggregator" +
//                "\\src\\ENGIE_februarie_2023";
//
//        try{
//            File sourceFolder = new File(folderPath);
//            String fileExt = "";
//
//            for(File sourceFile: Objects.requireNonNull(sourceFolder.listFiles())){
//                String fileName = sourceFile.getName();
//                System.out.println(fileName);
//                String filePath = "C:\\Users\\zeche\\Desktop\\Practica\\TransactionsAggregator" +
//                        "\\src\\ENGIE_februarie_2023\\" + fileName;
//
//                transactions = FolderParser.parseCsvFile(filePath);
//
//                fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
//
//                    if(fileExt.equalsIgnoreCase("csv")){
//
//                        //FolderParser.printTableFormat(transactions, filePath);
//
//                        System.out.println("We have read " + fileName + " successfully");
//                    }else{
//                        System.out.println("Filename extension not supported!");
//                    }
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }

    }
}
