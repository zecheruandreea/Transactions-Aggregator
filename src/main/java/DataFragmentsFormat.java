public class DataFragmentsFormat {
    private String firstDay;
    private String firstDate;
    private String secondDay;
    private String secondDate;

    public String getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(String firstDay) {
        this.firstDay = firstDay;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getSecondDay() {
        return secondDay;
    }

    public void setSecondDay(String secondDay) {
        this.secondDay = secondDay;
    }

    public String getSecondDate() {
        return secondDate;
    }

    public void setSecondDate(String secondDate) {
        this.secondDate = secondDate;
    }

    public DataFragmentsFormat(String firstDay, String firstDate, String secondDay, String secondDate){
        this.firstDay = firstDay;
        this.firstDate = firstDate;
        this.secondDay = secondDay;
        this.secondDate = secondDate;
    }

    @Override
    public String toString() {
        return firstDay + " " + firstDate + "-" + secondDay + " " + secondDate;
    }
}
