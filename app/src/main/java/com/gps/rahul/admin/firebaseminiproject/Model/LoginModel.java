package com.gps.rahul.admin.firebaseminiproject.Model;

public class LoginModel {
    private String Name;
    private String Password;
    private String phone;
    private String IsStaff;
    private String secureCode;

    public LoginModel() {
    }

    public LoginModel(String name, String password,String secureCode) {
        Name = name;
        Password = password;
        IsStaff="false";
        this.secureCode=secureCode;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
