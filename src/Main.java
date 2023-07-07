import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        //Testare citire si afisare dintr-un singur fisier
//        String filePath = "C:\\Users\\zeche\\Desktop\\Practica\\TransactionsAggregator" +
//                "\\src\\ENGIE_februarie_2023\\Settlement-note_GDFE_2023-02-01-RON.csv";
//        Map<Integer, TransactionsInfo> transactions = new HashMap<>();
//        transactions = FolderParser.parseCsvFile(filePath);
//
//        FolderParser.printTableFormat(transactions, filePath);

        String folderPath = "C:\\Users\\zeche\\Desktop\\Practica\\TransactionsAggregator" +
                "\\src\\ENGIE_februarie_2023";
        FolderParser.parseCsvFolder(folderPath);


    }
}
