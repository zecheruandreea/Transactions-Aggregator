import java.io.*;
import java.util.*;


public class FolderParser {

    public static Map<Integer, TransactionsInfo> parseCsvFile(String filePath) {
        BufferedReader bufferedReader = null;
        TransactionsInfo info;
        int rowIndex = 1;
        Map<Integer, TransactionsInfo> transactions = new HashMap<>();

        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                String[] row = line.split(",");

                if(line.startsWith("Sinteza")){
                    break;
                }

                if(rowIndex > 7) {
                    for(int i = 0; i < row.length; i += 9) {
                        String data = row[i];
                        double quantity = Double.parseDouble(row[i + 3]);
                        double price = Double.parseDouble(row[i + 4]);
                        info = new TransactionsInfo(data, quantity, price);
                        transactions.put(rowIndex, info);
                    }
                }
                rowIndex++;
            }
        } catch(IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return transactions;
    }

    public static void parseCsvFolder(String folderPath){
        Map<Integer, TransactionsInfo> transactions;

        try{
            File sourceFolder = new File(folderPath);
            String fileExt = "";

            for(File sourceFile: Objects.requireNonNull(sourceFolder.listFiles())){
                String fileName = sourceFile.getName();
                System.out.println(fileName);
                String filePath = folderPath + "\\" + fileName;

                transactions = FolderParser.parseCsvFile(filePath);
                fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

                if(fileExt.equalsIgnoreCase("csv")){

                    FolderParser.printTableFormat(transactions, filePath);

                    //System.out.println("We have read " + fileName + " successfully");
                }else{
                    System.out.println("Filename extension not supported!");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void printTableFormat(Map<Integer, TransactionsInfo> transactions, String filePath){
        transactions = FolderParser.parseCsvFile(filePath);

        Formatter tableFormat = new Formatter();
        tableFormat.format("%15s %30s %15s\n", "Interval", "Cantitate", "Pret");

        for(Integer j: transactions.keySet()){
            tableFormat.format("%14s %14s %17s\n", transactions.get(j).getData(), transactions.get(j).getQuantity(),
                    transactions.get(j).getPrice());
        }

        System.out.println(tableFormat);
    }

}
