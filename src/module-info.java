
module Jchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.bmazurkiewicz01.client;
    opens com.bmazurkiewicz01.client.controller;
    opens com.bmazurkiewicz01.client.model;
    opens com.bmazurkiewicz01.client.view;
}