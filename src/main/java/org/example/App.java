package org.example;


import org.example.dao.Employeedao;


public class App {
    public static void main(String[] args) {
        Employeedao service = new Employeedao();
        service.start();
    }
}
