import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Formatter {

    // formatul datelor 04:00
    public static List<String> splitDataInHourAndMinute(List<String> date){
        // map care contine pentru fiecare data la care incep tranzactiile
        // ora si minutele
        List<String> dataSplitInHoursMinutes = new ArrayList<>();

        // am extras data la care incep tranzactiile => am luat ora si minutele
        for(String h:date){
            String[] hourMinuteSplit = h.split(":");
            String hour = hourMinuteSplit[0];
            String minutes = hourMinuteSplit[1];

            //poz para ora, poz impara minutele
            dataSplitInHoursMinutes.add(hour);
            dataSplitInHoursMinutes.add(minutes);
        }

        return dataSplitInHoursMinutes;
    }

    public static Map<String, DataFragmentsFormat> formIncreaseInterval(Map<String, DataFragmentsFormat> dataParts){

        Map<String, DataFragmentsFormat> dataTransformedTransactions = new LinkedHashMap<>();

        for(String initialInterval:dataParts.keySet()) {
            String increasedData = addOneHourToInterval(initialInterval);
            String[] parts = increasedData.split("-");
            String firstPart = parts[0];
            String secondPart = parts[1];

            String firstDay = firstPart.substring(0, 8);
            String firstDate;

            if(dataParts.get(initialInterval).getFirstDate().contains("A")){
                firstDate = firstPart.substring(9) + "A";
            } else{
                firstDate = firstPart.substring(9);
            }

            if(dataParts.get(initialInterval).getFirstDate().contains("B")){
                firstDate = firstPart.substring(9) + "B";
            } else{
                firstDate = firstPart.substring(9);
            }

            String secondDay = secondPart.substring(0, 8);
            String secondDate;

            if(dataParts.get(initialInterval).getSecondDate().contains("A")){
                secondDate = secondPart.substring(9) + "A";
            }else{
                secondDate = secondPart.substring(9);
            }

            DataFragmentsFormat increasedInterval = new DataFragmentsFormat(firstDay, firstDate, secondDay, secondDate);
            dataTransformedTransactions.put(initialInterval, increasedInterval);
        }

        return dataTransformedTransactions;
    }

    public static String addOneHourToInterval(String interval){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the time zone to UTC

        String[] intervalParts = interval.split("-");

        try {
            Date startDate = dateFormat.parse(intervalParts[0]);
            Date endDate = dateFormat.parse(intervalParts[1]);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTime(startDate);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            Date updatedStartDate = calendar.getTime();

            calendar.setTime(endDate);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            Date updatedEndDate = calendar.getTime();

            return dateFormat.format(updatedStartDate) + "-" + dateFormat.format(updatedEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return interval; // Return the original interval in case of an error
        }

    }

    public static List<String> generateIntervalsForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1); // Month is 0-based in Calendar, so we subtract 1

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");

        // Get the last day of the month
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<String> dateGeneratorForMonth = new ArrayList<>();

        for(int day = 1; day <= lastDay; day++) {
            // Start from 00:00 for each day
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            String startInterval = sdf.format(calendar.getTime());

            List<String> dateGeneratorPerDay = new ArrayList<>();

            // Generate intervals for the day until 23:45
            while(calendar.get(Calendar.HOUR_OF_DAY) != 23 || calendar.get(Calendar.MINUTE) != 45) {
                calendar.add(Calendar.MINUTE, 15);
                String endInterval = sdf.format(calendar.getTime());
                dateGeneratorPerDay.add(startInterval + "-" + endInterval);

                startInterval = endInterval;
            }

            // Add the last interval for the day (23:45-00:00 of the next day)
            calendar.add(Calendar.MINUTE, 15);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            String endInterval = sdf.format(calendar.getTime());
            dateGeneratorPerDay.add(startInterval + "-" + endInterval);
            dateGeneratorForMonth.addAll(dateGeneratorPerDay);
        }

        return dateGeneratorForMonth;
    }

    public static Map<String, List<String>> splitIntervalsInQuarters(List<String> firstHoursMinutes, List<String> secondHoursMinutes,
                                                                     Map<String, DataFragmentsFormat> dataTransformedTransactions) throws ParseException {

        Map<String, List<String>> initialIntervalSplitInQuarters = new LinkedHashMap<>();

        int j = 0;
        for(String data:dataTransformedTransactions.keySet()){
            if(firstHoursMinutes.get(j + 1).equals("00") && secondHoursMinutes.get(j + 1).equals("00")){
                String firstDay = dataTransformedTransactions.get(data).getFirstDay();
                String firstDate = dataTransformedTransactions.get(data).getFirstDate();
                String secondDay = dataTransformedTransactions.get(data).getSecondDay();
                String secondDate = dataTransformedTransactions.get(data).getSecondDate();
                String startTimestamp = firstDay + " " + firstDate;
                String endTimestamp = secondDay + " " + secondDate;

                List<String> quartersIntervals = Formatter.splitIntervalIntoFour(startTimestamp, endTimestamp);
                initialIntervalSplitInQuarters.put(String.valueOf(dataTransformedTransactions.get(data)), quartersIntervals);
            }

           if(firstHoursMinutes.get(j + 1).contains("B") && secondHoursMinutes.get(j + 1).equals("00")){
               String firstDay = dataTransformedTransactions.get(data).getFirstDay();
               String firstDate = dataTransformedTransactions.get(data).getFirstDate();
               String secondDay = dataTransformedTransactions.get(data).getSecondDay();
               String secondDate = dataTransformedTransactions.get(data).getSecondDate();

               String startTimestamp = firstDay + " " + firstDate;
               String endTimestamp = secondDay + " " + secondDate;

               List<String> quartersIntervals = Formatter.splitIntervalIntoFour(startTimestamp, endTimestamp);
               initialIntervalSplitInQuarters.put(String.valueOf(dataTransformedTransactions.get(data)), quartersIntervals);

               List<String> quartersIntervalsB = new ArrayList<>();
               for(String quarter:quartersIntervals){
                   String[] parts = quarter.split(" ");
                   // Extract the time part from the first element of the array
                   String timeRange = parts[1];
                   String[] timeRangeParts = timeRange.split("-");
                   // Extract the first hour from the time range
                   String firstDayQ = parts[0];
                   String firstDateQ = timeRangeParts[0]; // 03:00
                   String secondDayQ = timeRangeParts[1];
                   String secondDateQ = parts[2]; // 04:00

                   quartersIntervalsB.add(firstDayQ + " " + firstDateQ + "B" + "-" + secondDayQ + " " + secondDateQ + "B");

               }
               initialIntervalSplitInQuarters.get(String.valueOf(dataTransformedTransactions.get(data))).addAll(quartersIntervalsB);
           }

           if(firstHoursMinutes.get(j + 1).equals("00") && secondHoursMinutes.get(j + 1).contains("B")){
                String firstDay = dataTransformedTransactions.get(data).getFirstDay();
                String firstDate = dataTransformedTransactions.get(data).getFirstDate();
                String secondDay = dataTransformedTransactions.get(data).getSecondDay();
                String secondDate = dataTransformedTransactions.get(data).getSecondDate();

                String startTimestamp = firstDay + " " + firstDate;
                String endTimestamp = secondDay + " " + secondDate;

                List<String> quartersIntervals = Formatter.splitIntervalIntoFour(startTimestamp, endTimestamp);
                initialIntervalSplitInQuarters.put(String.valueOf(dataTransformedTransactions.get(data)), quartersIntervals);

                List<String> quartersIntervalsB = new ArrayList<>();
                for(String quarter:quartersIntervals){
                    String[] parts = quarter.split(" ");
                    // Extract the time part from the first element of the array
                    String timeRange = parts[1];
                    String[] timeRangeParts = timeRange.split("-");
                    // Extract the first hour from the time range
                    String firstDayQ = parts[0];
                    String firstDateQ = timeRangeParts[0]; // 03:00
                    String secondDayQ = timeRangeParts[1];
                    String secondDateQ = parts[2]; // 04:00

                    quartersIntervalsB.add(firstDayQ + " " + firstDateQ + "B" + "-" + secondDayQ + " " + secondDateQ + "B");

                }
                initialIntervalSplitInQuarters.put(String.valueOf(dataTransformedTransactions.get(data)), quartersIntervalsB);
            }

           j+=2;
        }

        return initialIntervalSplitInQuarters;
    }

    public static List<String> splitIntervalIntoFour(String startTimestamp, String endTimestamp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
        Date startDate = sdf.parse(startTimestamp);
        Date endDate = sdf.parse(endTimestamp);

        // Calculate the quarter duration in milliseconds (15 minutes)
        long quarterDurationMillis = 15 * 60 * 1000;

        // Calculate the number of quarters in the interval
        long intervalDurationMillis = endDate.getTime() - startDate.getTime();
        int numQuarters = (int) (intervalDurationMillis / quarterDurationMillis);

        // Split the interval into four intervals
        Date currentQuarter = startDate;
        List<String> quartersIntervals = new ArrayList<>();

        for(int i = 0; i < 4; i++) {
            Date nextQuarter = new Date(currentQuarter.getTime() + quarterDurationMillis);
            quartersIntervals.add(sdf.format(currentQuarter) + "-" + sdf.format(nextQuarter));
            currentQuarter = nextQuarter;
        }

        return quartersIntervals;
    }

    public static Map<String, List<Double>> multiplyQuantityQuartersWithFour(Map<String, List<Double>> uniqueTransactions,
                                                                             List<String> firstHoursMinutes, List<String> secondHoursMinutes,
                                                                             Map<String, DataFragmentsFormat> dataTransformedTransactions,
                                                                             Map<String, List<String>> splitIntervalsForSell){
        Map<String, List<Double>> transactionsTransformed = new LinkedHashMap<>();

        int j = 0;
        for(String data:uniqueTransactions.keySet()){
            //System.out.println("initial data -> " + data);
            List<Double> values = new ArrayList<>();

            for(int i = 0; i < uniqueTransactions.get(data).size() - 1; i += 2){
                Double quantity;
                if(firstHoursMinutes.get(j + 1).equals("15") || firstHoursMinutes.get(j + 1).equals("30") ||
                        firstHoursMinutes.get(j + 1).equals("45") ||
                        (firstHoursMinutes.get(j + 1).equals("00") && secondHoursMinutes.get(j + 1).equals("15"))){
                    quantity = 4 * uniqueTransactions.get(data).get(i);
                }else{
                    quantity = uniqueTransactions.get(data).get(i);
                }
                Double price = uniqueTransactions.get(data).get(i + 1);
                values.add(quantity);
                values.add(price);
            }
            String increasedInterval = String.valueOf(dataTransformedTransactions.get(data));

            List<String> quarters = splitIntervalsForSell.getOrDefault(increasedInterval, Collections.singletonList(increasedInterval));

            if(!data.contains("B")) {
                // doar acest for era inainte sa verific si cazurile cu datul orei inainte
                for(String quarter : quarters) {
                    List<Double> quarterValues = new ArrayList<>(values); // Create a copy of values for this quarter
                    if (transactionsTransformed.containsKey(quarter)) {
                        transactionsTransformed.get(quarter).addAll(quarterValues);
                    } else {
                        transactionsTransformed.put(quarter, quarterValues);
                    }
                }
            }else{
                for(int i = 0; i < quarters.size(); i++){
                    if(i > 3) {
                        List<Double> quarterValues = new ArrayList<>(values); // Create a copy of values for this quarter

                        if (transactionsTransformed.containsKey(quarters.get(i))) {
                            transactionsTransformed.get(quarters.get(i)).addAll(quarterValues);
                        } else {
                            transactionsTransformed.put(quarters.get(i), quarterValues);
                        }
                    } else {
                        List<Double> zeroValues = new ArrayList<>();
                        for(Double v:values) {
                            zeroValues.add(0.0);
                        }

                        transactionsTransformed.put(quarters.get(i), zeroValues);
                    }
                }
            }

            j+=2;
        }

        return transactionsTransformed;
    }


}
