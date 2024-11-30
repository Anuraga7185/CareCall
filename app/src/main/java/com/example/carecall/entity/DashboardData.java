package com.example.carecall.entity;

import java.io.Serializable;
import java.util.List;

public class DashboardData implements Serializable {
    public List<CategoryData> Category;
    public List<DoctorData> Doctors;
    public String id;

}
