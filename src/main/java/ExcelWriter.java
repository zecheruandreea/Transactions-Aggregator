import jdk.swing.interop.SwingInterOpUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ExcelWriter {
    public static void createHeader(String folderPath, Sheet sheetName){
        Map<String, List<Double>> uniqueTransactionsSell = (Map<String, List<Double>>) FolderParser.sortTransactionByUniqueData(folderPath)[0];
        Map<String, List<Double>> uniqueTransactionsBuy = (Map<String, List<Double>>) FolderParser.sortTransactionByUniqueData(folderPath)[1];;
        XSSFRow header = (XSSFRow) sheetName.createRow(0);
        header.createCell(0).setCellValue("Interval");

        if(sheetName.getSheetName().equals("Vanzare")){
            for(String data : uniqueTransactionsSell.keySet()){
                for(int i = 1; i < uniqueTransactionsSell.get(data).size(); i += 2){
                    header.createCell(i).setCellValue("Cantitate");
                    header.createCell(i + 1).setCellValue("Pret");
                }
            }
        }

        if(sheetName.getSheetName().equals("Cumparare")){
            for(String data : uniqueTransactionsBuy.keySet()){
                for(int i = 1; i < uniqueTransactionsBuy.get(data).size(); i += 2){
                    header.createCell(i).setCellValue("Cantitate");
                    header.createCell(i + 1).setCellValue("Pret");
                }
            }
        }
    }

    public static void setRowsValues(String folderPath, Sheet sheetName, int year, int month) throws ParseException {

        // am modificat intervalele, adica le am crescut cu o ora
        Map<String, List<Double>> uniqueTransactionsSell =
                (Map<String, List<Double>>) ExcelWriter.completeIncreasedFinalTransactionsWithZeros(folderPath, year, month)[0];
        Map<String, List<Double>> uniqueTransactionsBuy =
                (Map<String, List<Double>>) ExcelWriter.completeIncreasedFinalTransactionsWithZeros(folderPath, year, month)[1];

        try{
            int rowNo = 1;

            if(sheetName.getSheetName().equalsIgnoreCase("Vanzare")) {
                for(String data:uniqueTransactionsSell.keySet()){
                    XSSFRow row = (XSSFRow) sheetName.createRow(rowNo++);
                    row.createCell(0).setCellValue(data);

                    for(int j = 0; j < uniqueTransactionsSell.get(data).size() - 1; j += 2){
                        row.createCell(j + 1).setCellValue(Math.abs(uniqueTransactionsSell.get(data).get(j)));
                        row.createCell(j + 2).setCellValue(uniqueTransactionsSell.get(data).get(j + 1));
                    }
                }
            }else if(sheetName.getSheetName().equalsIgnoreCase("Cumparare")){
                for(String data:uniqueTransactionsBuy.keySet()){
                    XSSFRow row = (XSSFRow) sheetName.createRow(rowNo++);
                    row.createCell(0).setCellValue(data);

                    for(int j = 0; j < uniqueTransactionsBuy.get(data).size() - 1; j += 2){
                        row.createCell(j + 1).setCellValue(Math.abs(uniqueTransactionsBuy.get(data).get(j)));
                        row.createCell(j + 2).setCellValue(uniqueTransactionsBuy.get(data).get(j + 1));
                    }
                }
            }
        }catch(NullPointerException nullPointerException){
            nullPointerException.printStackTrace();
        }
    }

    public static void write(String folderPath, int year, int month, String outputFileName){

         try{
            Workbook workbook = new XSSFWorkbook();
            Sheet sheetCumparare = workbook.createSheet("Cumparare");
            Sheet sheetVanzare = workbook.createSheet("Vanzare");

            ExcelWriter.createHeader(folderPath,sheetCumparare);
            ExcelWriter.createHeader(folderPath, sheetVanzare);

            ExcelWriter.setRowsValues(folderPath, sheetCumparare, year, month);
            ExcelWriter.setRowsValues(folderPath, sheetVanzare, year, month);

            FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);

            workbook.write(fileOutputStream);
            fileOutputStream.close();
            System.out.println("Excel written succesfully");
        }catch(IOException | ParseException ioException){
            ioException.printStackTrace();
        }

    }

    public static Map<?, ?>[] completeIncreasedFinalTransactionsWithZeros(String folderPath, int year, int month) throws ParseException {
        Map<String, List<Double>> uniqueTransactionsSell = (Map<String, List<Double>>) FolderParser.sortTransactionByUniqueData(folderPath)[0];
        Map<String, List<Double>> uniqueTransactionsBuy = (Map<String, List<Double>>) FolderParser.sortTransactionByUniqueData(folderPath)[1];

        //exemplu: 20230228 03:00-20230228 04:00 -> firstDate = 03:00, secondDate = 04:00
        List<String> firstDatesSell = new ArrayList<>();
        List<String> secondDatesSell = new ArrayList<>();
        // map care contine perechi (interval, interval spart in bucati cu ziua si ora)
        Map<String, DataFragmentsFormat> dataPartsSell = new LinkedHashMap<>();

        // am impartit intervalul de date in bucati cu ziua si ora
        for(String date:uniqueTransactionsSell.keySet()){
            String[] parts = date.split(" ");
            // Extract the time part from the first element of the array
            String timeRange = parts[1];
            String[] timeRangeParts = timeRange.split("-");
            // Extract the first hour from the time range
            String firstDay = parts[0];
            String firstDate = timeRangeParts[0]; // 03:00
            String secondDay = timeRangeParts[1];
            String secondDate = parts[2]; // 04:00

            DataFragmentsFormat dataFragmentsFormat = new DataFragmentsFormat(firstDay, firstDate, secondDay, secondDate);

            firstDatesSell.add(firstDate);
            secondDatesSell.add(secondDate);
            dataPartsSell.put(date, dataFragmentsFormat);
        }

        List<String> firstDatesBuy = new ArrayList<>();
        List<String> secondDatesBuy = new ArrayList<>();
        // map care contine perechi (interval, interval spart in bucati cu ziua si ora)
        Map<String, DataFragmentsFormat> dataPartsBuy = new LinkedHashMap<>();

        // am impartit intervalul de date in bucati cu ziua si ora
        for(String date:uniqueTransactionsBuy.keySet()){
            String[] parts = date.split(" ");
            // Extract the time part from the first element of the array
            String timeRange = parts[1];
            String[] timeRangeParts = timeRange.split("-");
            // Extract the first hour from the time range
            String firstDay = parts[0];
            String firstDate = timeRangeParts[0]; // 03:00
            String secondDay = timeRangeParts[1];
            String secondDate = parts[2]; // 04:00

            DataFragmentsFormat dataFragmentsFormat = new DataFragmentsFormat(firstDay, firstDate, secondDay, secondDate);

            firstDatesBuy.add(firstDate);
            secondDatesBuy.add(secondDate);
            dataPartsBuy.put(date, dataFragmentsFormat);
        }

        // map care contine pentru fiecare data la care incep tranzactiile
        // ora si minutele
        List<String> firstHoursMinutesSell = Formatter.splitDataInHourAndMinute(firstDatesSell);
        List<String> secondHoursMinutesSell = Formatter.splitDataInHourAndMinute(secondDatesSell);

        List<String> firstHoursMinutesBuy = Formatter.splitDataInHourAndMinute(firstDatesBuy);
        List<String> secondHoursMinutesBuy = Formatter.splitDataInHourAndMinute(secondDatesBuy);

        // map care contine intervalul initial(data din tabelele initiale) si data transformata
        // dupa fusul orar de care avem nevoie(+1)
        Map<String, DataFragmentsFormat> dataTransformedForSellTransactions =
                Formatter.formIncreaseInterval(dataPartsSell);

        Map<String, DataFragmentsFormat> dataTransformedForBuyTransactions =
                Formatter.formIncreaseInterval(dataPartsBuy);

        Map<String, List<String>> splitIntervalsForSell = Formatter.splitIntervalsInQuarters(firstHoursMinutesSell,
                secondHoursMinutesSell, dataTransformedForSellTransactions);

        Map<String, List<String>> splitIntervalsForBuy = Formatter.splitIntervalsInQuarters(firstHoursMinutesBuy,
                secondHoursMinutesBuy, dataTransformedForBuyTransactions);

        Map<String, List<Double>> transactionsTransformedForSell = Formatter.multiplyQuantityQuartersWithFour(
                uniqueTransactionsSell,firstHoursMinutesSell, secondHoursMinutesSell,dataTransformedForSellTransactions,
                splitIntervalsForSell);

        Map<String, List<Double>> transactionsTransformedForBuy = Formatter.multiplyQuantityQuartersWithFour(
                uniqueTransactionsBuy, firstHoursMinutesBuy, secondHoursMinutesBuy,dataTransformedForBuyTransactions,
                splitIntervalsForBuy);

        List<String> dateGenerator = Formatter.generateIntervalsForMonth(year, month);
        Map<String, List<Double>> finalTransactionsForSell = new LinkedHashMap<>();
        Map<String, List<Double>> finalTransactionsForBuy = new LinkedHashMap<>();

        for(String data: splitIntervalsForSell.keySet()){
            if(splitIntervalsForSell.get(data).size() > 4){
                int indexToInsertForSell = dateGenerator.indexOf(splitIntervalsForSell.get(data).get(3));

                dateGenerator.remove(indexToInsertForSell);
                dateGenerator.add(indexToInsertForSell, splitIntervalsForSell.get(data).get(4));
                dateGenerator.add(indexToInsertForSell + 1, splitIntervalsForSell.get(data).get(5));
                dateGenerator.add(indexToInsertForSell + 2, splitIntervalsForSell.get(data).get(6));
                dateGenerator.add(indexToInsertForSell + 3, splitIntervalsForSell.get(data).get(7));
            }
        }

        int maxSellValues = ExcelWriter.getMaxNrValues(uniqueTransactionsSell);
        int maxBuyValues = ExcelWriter.getMaxNrValues(uniqueTransactionsBuy);

        for(String generator : dateGenerator) {
            if (transactionsTransformedForSell.containsKey(generator)) {
                finalTransactionsForSell.put(generator, transactionsTransformedForSell.get(generator));

                for (int i = finalTransactionsForSell.get(generator).size(); i < maxSellValues; i++) {
                    finalTransactionsForSell.get(generator).add(0.0);
                }
            } else {
                List<Double> values = new ArrayList<>();
                for (int i = 0; i < maxSellValues; i++) {
                    values.add(0.0);
                }
                finalTransactionsForSell.put(generator, values);
            }

            if (transactionsTransformedForBuy.containsKey(generator)) {
                finalTransactionsForBuy.put(generator, transactionsTransformedForBuy.get(generator));

                for (int i = finalTransactionsForBuy.get(generator).size(); i < maxBuyValues; i++) {
                    finalTransactionsForBuy.get(generator).add(0.0);
                }
            } else {
                List<Double> values = new ArrayList<>();
                for (int i = 0; i < maxBuyValues; i++) {
                    values.add(0.0);
                }
                finalTransactionsForBuy.put(generator, values);
            }
        }

        Map<?, ?>[] sellBuyTransformedTransactions = new Map<?, ?>[2];
        sellBuyTransformedTransactions[0] = finalTransactionsForSell;
        sellBuyTransformedTransactions[1] = finalTransactionsForBuy;

        return sellBuyTransformedTransactions;
    }

    public static int getMaxNrValues(Map<String, List<Double>> uniqueTransactions){
        int max = 0;
        for(String interval:uniqueTransactions.keySet()){
            int noValues = 0;
            for(Double value:uniqueTransactions.get(interval)){
                noValues++;
            }

            if(noValues > max){
                max = noValues;
            }
        }

        return max;
    }


}
