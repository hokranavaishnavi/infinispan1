package org.example.dao;



import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entity.Employee;

import java.util.List;
import java.util.Scanner;

public class Employeedao {
    private EntityManagerFactory entityManagerFactory;
    private Scanner scanner = new Scanner(System.in);
    public Employeedao() {


        entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
    }

    public void start() {
        while (true) {
            System.out.println("\n-------MENU-------");
            System.out.println("1.Add Employee");
            System.out.println("2.View Employee by id");
            System.out.println("3.View all Employees");
            System.out.println("4.Update Employee by id");
            System.out.println("5.Delete Employee");
            System.out.println("6.View Employee by Name");
            System.out.println("7.View Employee by Mail");
            System.out.println("8.Exit");
            System.out.println("Enter the choice: ");
            String input = scanner.next();
            // Read as string
            // Validate input is numeric (1–9)
            if (!input.matches("[1-9]")) {
                System.out.println("Invalid Input! Please enter a number from 1 to 9.");
                continue;
            }
            int choice = Integer.parseInt(input); // Safe conversion now!
            try {
                switch (choice) {
                    case 1:
                        addEmployee();
                        break;
                    case 2:
                        viewEmployeeById();
                        break;
                    case 3:
                        viewAllEmployees();
                        break;
                    case 4:
                        updateEmployee();
                        break;
                    case 5:
                        deleteEmployee();
                        break;
                    case 6:
                        viewEmployeeByName();
                        break;
                    case 7:
                        viewEmployeeByMail();
                        break;
                    case 8:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (EmployeeNotFoundException ex) {
                System.out.println("ERROR: " + ex.getMessage());

                // continues loop without closing program
            }
        }
    }

    private void addEmployee() {
        System.out.println("Enter employee name");
        String name = scanner.next();
        String phone;
        while (true) {
            System.out.println("Enter employee phone number");
            phone = scanner.next();
            if (isValidPhone(phone)) {
                break;
            } else {
                System.out.println(" Invalid phone number. Please enter exactly 10 digits.");
            }
        }
        System.out.println("Enter employee mail");
        String mail = scanner.next();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Employee employee1 = new Employee(name, phone, mail);
        entityManager.persist(employee1);
        transaction.commit();
        entityManager.close();
        System.out.println("Employee added!! ");
    }

    private void viewEmployeeById() {
        System.out.println("Enter employee id");
        int id = scanner.nextInt();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = entityManager.find(Employee.class, id);
        if (employee == null) {
            entityManager.close();
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found!");
        }
        //1st Level
        Employee employee1 = entityManager.find(Employee.class, id);         //
        System.out.println(employee1);
        entityManager.close();

        //2nd level
        EntityManager em2 = entityManagerFactory.createEntityManager();
        Employee employee2 = em2.find(Employee.class, id);

        // If cache enabled → no query fired
        System.out.println(employee2);
        em2.close();
    }

    private void viewEmployeeByName() {
        System.out.println("Enter employee name");
        String name = scanner.next();
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            List<Employee> list = em.createQuery("SELECT e FROM Employee e WHERE e.employee_name = :name", Employee.class).setParameter("name", name).getResultList();
            if (list.isEmpty()) {
                throw new EmployeeNotFoundException("Employee with name " + name + " not found!");
            }
            // Print all matching employees
            list.forEach(System.out::println);
        } finally {
            em.close();
        }
    }

    private void viewEmployeeByMail() {
        System.out.println("Enter employee mail");
        String mail = scanner.next();
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            List<Employee> list = em.createQuery("SELECT e FROM Employee e WHERE e.employee_email = :mail", Employee.class).setParameter("mail", mail).getResultList();
            if (list.isEmpty()) {
                throw new EmployeeNotFoundException("Employee with mail " + mail + " not found!");
            }
            // Print all matching employees
            list.forEach(System.out::println);
        } finally {
            em.close();
        }
    }

    private void viewAllEmployees() {
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Employee> list = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
        if (list.isEmpty()) {
            System.out.println("No Employees Found!");
        } else {
            System.out.println("\n---- Employee List ----");
            list.forEach(System.out::println);
        }
        em.close();
    }

    private void updateEmployee() {
        System.out.print("Enter Employee ID to Update: ");
        int id = scanner.nextInt();
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Employee emp = em.find(Employee.class, id);
        if (emp == null) {
            System.out.println("Employee Not Found!");
            em.close();
            return;
        }
        System.out.print("Enter new Name: ");
        emp.setEmployee_name(scanner.next());
        System.out.print("Enter new Email: ");
        emp.setEmployee_email(scanner.next());
        String phone;
        while (true) {
            System.out.println("Enter employee phone number");
            phone = scanner.next();
            if (isValidPhone(phone)) {
                break;
            } else {
                System.out.println(" Invalid phone number. Please enter exactly 10 digits.");
            }
        }
        em.merge(emp);
        tx.commit();
        em.close();
        System.out.println("Employee Updated Successfully!");
    }

    private void deleteEmployee() {
        System.out.print("Enter Employee ID to Delete: ");
        int id = scanner.nextInt();
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Employee emp = em.find(Employee.class, id);
        if (emp == null) {
            System.out.println("Employee not found!");
            em.close();
            return;
        }
        em.remove(emp);
        tx.commit();
        em.close();
        System.out.println("Employee Deleted Successfully!");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}