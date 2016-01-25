package com.lucime.promfy;

public class UserDetails {
    String date_of_birth;
    String email;
    String phone;
    String username;

    public UserDetails() {
        this.username = BuildConfig.FLAVOR;
        this.email = BuildConfig.FLAVOR;
        this.phone = BuildConfig.FLAVOR;
        this.date_of_birth = BuildConfig.FLAVOR;
    }

    public UserDetails(String u, String e, String p, String dob) {
        this.username = u;
        this.email = e;
        this.phone = p;
        this.date_of_birth = dob;
    }
}
