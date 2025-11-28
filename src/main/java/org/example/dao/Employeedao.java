package org.example.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;


import java.util.List;
import java.util.Scanner;


import org.example.dao.EmployeeNotFoundException;
import org.example.entity.Employee;

public class Employeedao {
    private EntityManagerFactory entityManagerFactory;
    private Scanner scanner = new Scanner(System.in);

    public Employeedao() {
        entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
    }

    public void start() {
        while (true) {
            System.out.println("\n=========== MENU ===========");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employee by ID");
            System.out.println("3. View Employee by Name");
            System.out.println("4. View Employee by Email");
            System.out.println("5. View Employee by Phone Number");
            System.out.println("6. View all Employees");
            System.out.println("7. Update Employee");
            System.out.println("8. Delete Employee");
            System.out.println("9. Exit");
            System.out.print("Enter your Choice: ");
            String input = scanner.next();

            // Read as string
            // Validate input is numeric (1–9)
            if (!input.matches("[1-9]")) {
                System.out.println("Invalid Input! Please enter a number from 1 to 9.");
                continue;
            }
            int choice = Integer.parseInt(input);
            // Safe conversion now!
            try {
                switch (choice) {
                    case 1 -> addEmployee();
                    case 2 -> viewEmployeeByID();
                    case 3 -> viewEmployeeByName();
                    case 4 -> viewEmployeeByEmail();
                    case 5 -> viewEmployeeByPhone();
                    case 6 -> viewAllEmployees();
                    case 7 -> updateEmployee();
                    case 8 -> deleteEmployee();
                    case 9 -> {
                        entityManagerFactory.close();
                        System.out.println("Application Closed Successfully!");
                        return;
                    }
                    default -> System.out.println("Invalid Choice!");
                }
            } catch (EmployeeNotFoundException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected Error: " + e.getMessage());
            }
        }
    }

    private void addEmployee() {
        System.out.print("Enter Employee Name: ");
        String name = scanner.next();
        System.out.print("Enter Employee Email: ");
        String email = scanner.next();
        String phone;
        while (true) {
            System.out.print("Enter 10-digit Employee Phone Number: ");
            phone = scanner.next();
            if (phone.matches("\\d{10}")) break;
            System.out.println("Invalid! Phone number must be exactly 10 digits.");
        }
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Employee employee = new Employee(name, email, phone);
        em.persist(employee);
        tx.commit();
        em.close();
        System.out.println("Employee Successfully Added: " + employee);
    }

    private void viewEmployeeByID() {
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();
        EntityManager em = entityManagerFactory.createEntityManager();

        // First find → Loaded from DB (and stored in first-level cache)
        Employee emp = em.find(Employee.class, id);
        if (emp == null) {
            em.close();
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found!");
        }

        // Second find in same EntityManager → Loaded from 1st level cache (but NOT printed)
        em.find(Employee.class, id);
        em.close();

        // Cache still retained for 2nd level (if enabled)
        // New EntityManager → Should load from 2nd level cache (if enabled)
        EntityManager em2 = entityManagerFactory.createEntityManager();
        Employee emp2 = em2.find(Employee.class, id);
        em2.close();
        // Print only one final result
        System.out.println("\n---- Employee Details ----");
        System.out.println(emp2);
    }

    private void viewEmployeeByName() {
        System.out.print("Enter Employee Name: ");
        String name = scanner.next();
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Employee> list1 = em.createQuery("SELECT e FROM Employee e WHERE e.employee_name = :name", Employee.class).setParameter("name", name).setHint("org.hibernate.cacheable", true).getResultList();
        em.close();
        if (list1.isEmpty()) {
            throw new EmployeeNotFoundException("No Employee found with Name: " + name);
        }
        // 2nd lookup → Must hit cache (no SQL)
        EntityManager em2 = entityManagerFactory.createEntityManager();
        List<Employee> list2 = em2.createQuery("SELECT e FROM Employee e WHERE e.employee_name = :name", Employee.class).setParameter("name", name).setHint("org.hibernate.cacheable", true).getResultList();
        em2.close();
        System.out.println("\n---- Employees ----");
        list2.forEach(System.out::println);
    }

    private void viewEmployeeByEmail() {
        System.out.print("Enter Employee Email: ");
        String email = scanner.next();
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Employee> result1 = em.createQuery("SELECT e FROM Employee e WHERE e.employee_email = :email", Employee.class).setParameter("email", email).setHint("org.hibernate.cacheable", true).getResultList();
        em.close();
        if (result1.isEmpty()) {
            throw new EmployeeNotFoundException("No Employee found with Email: " + email);
        }
        EntityManager em2 = entityManagerFactory.createEntityManager();
        List<Employee> result2 = em2.createQuery("SELECT e FROM Employee e WHERE e.employee_email = :email", Employee.class).setParameter("email", email).setHint("org.hibernate.cacheable", true).getResultList();
        em2.close();
        System.out.println("\n---- Employee Details ----");
        System.out.println(result2.get(0));
    }

    private void viewEmployeeByPhone() {
        System.out.print("Enter Employee Phone Number: ");
        String phone = scanner.next();
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Employee> result1 = em.createQuery("SELECT e FROM Employee e WHERE e.employee_phonenumber = :phone", Employee.class).setParameter("phone", phone).setHint("org.hibernate.cacheable", true).getResultList();
        em.close();
        if (result1.isEmpty()) {
            throw new EmployeeNotFoundException("No Employee found with Phone Number: " + phone);
        }
        EntityManager em2 = entityManagerFactory.createEntityManager();
        List<Employee> result2 = em2.createQuery("SELECT e FROM Employee e WHERE e.employee_phonenumber = :phone", Employee.class).setParameter("phone", phone).setHint("org.hibernate.cacheable", true).getResultList();
        em2.close();
        System.out.println("\n---- Employee Details ----");
        System.out.println(result2.get(0));
    }

    private void viewAllEmployees() {
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Employee> list = em.createQuery("SELECT e FROM Employee e", Employee.class).setHint("jakarta.persistence.cache.storeMode", "USE").setHint("jakarta.persistence.cache.retrieveMode", "USE").setHint("org.hibernate.cacheable", true)
                // Important for query cache
                .getResultList();
        em.close();
        if (list.isEmpty()) {
            throw new EmployeeNotFoundException("No Employees exist in the database!");
        }
        System.out.println("\n---- All Employees ----");
        list.forEach(System.out::println);
    }

    private void updateEmployee() {
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Employee emp = em.find(Employee.class, id);
        if (emp == null) {
            em.close();
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found!");
        }
        System.out.print("Enter New Name: ");
        emp.setEmployee_name(scanner.next());
        System.out.print("Enter New Email: ");
        emp.setEmployee_email(scanner.next());
        String phone;
        while (true) {
            System.out.print("Enter New 10-Digit Phone: ");
            phone = scanner.next();
            if (phone.matches("\\d{10}")) break;
            System.out.println("Invalid phone number!");
        }
        emp.setEmployee_phonenumber(phone);
        em.merge(emp);
        tx.commit();
        em.close();
        System.out.println("Employee Updated Successfully!");
    }

    private void deleteEmployee() {
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Employee emp = em.find(Employee.class, id);
        if (emp == null) {
            em.close();
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found!");
        }
        em.remove(emp);
        tx.commit();
        em.close();
        System.out.println("Employee Deleted Successfully!");
    }
}