package ru.oksei.Albumify.Models;


public class Person {
    private int id;
    private String email, fio, nickname, password, repassword;

    public Person() {

    }

    public Person(String email, String fio, String nickname, String password, String repassword) {
        id = 0;
        this.email = email;
        this.fio = fio;
        this.nickname = nickname;
        this.password = password;
        this.repassword = repassword;
    }
    public Person(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
