import java.io.*;
import java.util.*;

public class FolderParser {

    public static Map<Integer, TransactionsInfo> parseCsvFile(String filePath) {
        BufferedReader bufferedReader = null;
        TransactionsInfo info;
        int rowIndex = 1;
        Map<Integer, TransactionsInfo> transactions = new HashMap<>();

        try{
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;

            while((line = bufferedReader.readLine()) != null){
                String[] row = line.split("[,;]");

                if(line.startsWith("Sinteza")){
                    break;
                }

                if(rowIndex > 7 && !line.isEmpty()){
                    for(int i = 0; i < row.length; i += 9){
                        String data = row[i];
                        String type = row[i + 2];
                        double quantity = Double.parseDouble(row[i + 3]);
                        double price = Double.parseDouble(row[i + 4]);
                        info = new TransactionsInfo(data, type, quantity, price);
                        transactions.put(rowIndex, info);
                    }
                }
                rowIndex++;
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }finally {
            try{
                assert bufferedReader != null;
                bufferedReader.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return transactions;
    }

    public static Map<String, Map<Integer, TransactionsInfo>> parseCsvFolder(String folderPath){
        //transactions stored by row
        Map<Integer, TransactionsInfo> transactions;
        //transactions stored by day
        Map<String, Map<Integer, TransactionsInfo>> transactionsPerDay = new LinkedHashMap<>();

        try{
            File sourceFolder = new File(folderPath);
            String fileExt = "";

            for(File sourceFile: Objects.requireNonNull(sourceFolder.listFiles())){
                String fileName = sourceFile.getName();
                String filePath = folderPath + "\\" + fileName;

                transactions = FolderParser.parseCsvFile(filePath);
                fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

                if(fileExt.equalsIgnoreCase("csv")){
                    transactionsPerDay.put(fileName, transactions);
                }else{
                    System.out.println("Filename extension not supported!");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return transactionsPerDay;
    }

    //functie care tine tranzactiile sub forma unei date unice si fiecare data contine un array cu cantitatea aflata
    //pe nr par si pretul pe nr impar
    public static Map<?,?>[] sortTransactionByUniqueData(String folderPath){
        Map<String, Map<Integer, TransactionsInfo>> transactionsPerDay;
        transactionsPerDay = FolderParser.parseCsvFolder(folderPath);

        Map<String, List<Double>> uniqueTransactionsSell = new TreeMap<>();
        Map<String, List<Double>> uniqueTransactionsBuy = new TreeMap<>();

        for(String name : transactionsPerDay.keySet()){

            for(Integer i : transactionsPerDay.get(name).keySet()) {
                List<Double> values = new ArrayList<>();
                String data = transactionsPerDay.get(name).get(i).getData();
                Double quantity = Math.abs(transactionsPerDay.get(name).get(i).getQuantity());
                Double price = transactionsPerDay.get(name).get(i).getPrice();
                String type = transactionsPerDay.get(name).get(i).getType();

                if(type.equals("Vanzare")) {
                    if(!uniqueTransactionsSell.containsKey(data)) {
                        values.add(quantity);
                        values.add(price);
                        uniqueTransactionsSell.put(data, values);
                    }else{
                        uniqueTransactionsSell.get(data).add(quantity);
                        uniqueTransactionsSell.get(data).add(price);
                    }
                }

                if(type.equals("Cumparare")){
                    if(!uniqueTransactionsBuy.containsKey(data)){
                        values.add(quantity);
                        values.add(price);
                        uniqueTransactionsBuy.put(data, values);
                    } else {
                        uniqueTransactionsBuy.get(data).add(quantity);
                        uniqueTransactionsBuy.get(data).add(price);
                    }
                }
            }
        }

        Map<?, ?>[] sellBuyTransactions = new Map<?, ?>[2];
        sellBuyTransactions[0] = uniqueTransactionsSell;
        sellBuyTransactions[1] = uniqueTransactionsBuy;

        return sellBuyTransactions;
    }


}
