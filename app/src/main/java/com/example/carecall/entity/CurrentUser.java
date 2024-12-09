package com.example.carecall.entity;

import java.io.Serializable;
import java.util.List;

public class CurrentUser implements Serializable {
    public int id;
    public String uniqieId;
    public String name = "Aditya";
    public String mobile = "7011341103";
    public String passwd = "";
    public String email = "ranurag378@gmail.com";
    public String address = "RZ-75 A Gali Np.8, Durga Park, N>D-45";
    public String gender = "Male";
    public String dateOfBirth = "25-05-2001";
    public String biography = "N/A";
    public String pictureUrl;
    public String fcmToken;
    private List<MedicalHistory> medicalHistory;

    private List<DoctorData> appointments;

    private List<DoctorData> favouriteDoctors;

    private List<Notification> notifications;

    private boolean isPremiumUser;
}
