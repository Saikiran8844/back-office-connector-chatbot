package com.chatbotservices.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDto {
    private Long id;
    private String userName;
    private String name;
    private String email;
    private String country;
    private String city;
    private String course;
    private int year;
    private String college;
    private int semester;
   

	@JsonIgnore
    private List<UserSubscriptionDto> subscriptions;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
    public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public List<UserSubscriptionDto> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<UserSubscriptionDto> subscriptions) {
        this.subscriptions = subscriptions;
    }

	
}

