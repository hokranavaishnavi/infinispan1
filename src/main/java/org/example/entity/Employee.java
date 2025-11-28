package org.example.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Employee")
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer employee_id;
    String employee_name;
    String employee_phone;
    String employee_email;

    public Employee(String employee_name, String employee_phone, String employee_email) {
        this.employee_name = employee_name;
        this.employee_phone = employee_phone;
        this.employee_email = employee_email;
    }

    public Integer getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(Integer employee_id) {
        this.employee_id = employee_id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getEmployee_phone() {
        return employee_phone;
    }

    public void setEmployee_phone(String employee_phone) {
        this.employee_phone = employee_phone;
    }

    public String getEmployee_email() {
        return employee_email;
    }

    public void setEmployee_email(String employee_email) {
        this.employee_email = employee_email;
    }

    public Employee() {
    }

    @Override
    public String toString() {
        return "Employee{" + "employee_id=" + employee_id + ", employee_name='" + employee_name + '\'' + ", employee_phone='" + employee_phone + '\'' + ", employee_email='" + employee_email + '\'' + '}';
    }

    public void setEmployee_phonenumber(String phone) {
        this.employee_phone =employee_phone;
    }
}