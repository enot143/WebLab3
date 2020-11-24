package ru.arina.maxim.beans;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@ManagedBean(name = "dateBean")
@ApplicationScoped
public class DateBean implements Serializable {

    public String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss z").format(new Date());
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());

    }
}