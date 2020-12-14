package sindh.agriculureextension.fadiary.form;

public class VisitModel {

    public VisitModel() {
    }

    private int ID;
    private long Time;
    private String Farmer;
    private String Question;
    private String Suggestion;
    private double LAT;
    private double LANG;
    private String Image;
    private int Status;
    private String Address;
    private String LocationName;
    private String FarmerPhone;

    public String getFarmerPhone() {
        return FarmerPhone;
    }

    public void setFarmerPhone(String farmerPhone) {
        FarmerPhone = farmerPhone;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFarmer() {
        return Farmer;
    }

    public void setFarmer(String farmer) {
        Farmer = farmer;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getSuggestion() {
        return Suggestion;
    }

    public void setSuggestion(String suggestion) {
        Suggestion = suggestion;
    }

    public double getLAT() {
        return LAT;
    }

    public void setLAT(double LAT) {
        this.LAT = LAT;
    }

    public double getLANG() {
        return LANG;
    }

    public void setLANG(double LANG) {
        this.LANG = LANG;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    @Override
    public String toString() {
        return "VisitModel{" +
                "ID=" + ID +
                ", Farmer='" + Farmer + '\'' +
                ", Question='" + Question + '\'' +
                ", Suggestion='" + Suggestion + '\'' +
                ", LAT=" + LAT +
                ", LANG=" + LANG +
                ", Image='" + Image + '\'' +
                ", Status=" + Status +
                ", Address='" + Address + '\'' +
                ", LocationName='" + LocationName + '\'' +
                ", FarmerPhone='" + FarmerPhone + '\'' +
                '}';
    }
}
