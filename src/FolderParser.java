import java.io.*;
import java.util.*;


public class FolderParser {

    public static Map<Integer, TransactionsInfo> parseCsvFile(String filePath) {
        BufferedReader bufferedReader = null;
        TransactionsInfo info;
        List<String> columnsNames = new ArrayList<>();
        int rowIndex = 1;
        Map<Integer, TransactionsInfo> transactions = new HashMap<>();

        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                String[] row = line.split(",");

                for(String s:row){
                    System.out.println("Fiecare linie split: " + s);
                }

                if(line.startsWith("Sinteza")){
                    break;
                }

                if(rowIndex == 7) {
                    for(String index : row) {
                        if(index.startsWith("Nume contract")) {
                            columnsNames.add(index);
                        } else if(index.startsWith("Cantitate")) {
                            columnsNames.add(index);
                        } else if(index.startsWith("Pret")) {
                            columnsNames.add(index);
                        }
                    }
                } else if(rowIndex > 7) {
                    for(int i = 0; i < row.length; i += 9) {
                        String data = row[0];
                        double quantity = Double.parseDouble(row[3]);
                        double price = Double.parseDouble(row[4]);
                        info = new TransactionsInfo(data, quantity, price);
                        transactions.put(rowIndex, info);
                    }
                }
                rowIndex++;
                System.out.println();
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
