import java.io.*;
import java.util.*;

// Expense Class
class Expense implements Serializable {
    private double amount;
    private String category;
    private String date;

    public Expense(double amount, String category, String date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String toString() {
        return amount + " - " + category + " - " + date;
    }
}

// User Class
class User implements Serializable {
    private String username;
    private ArrayList<Expense> expenses;

    public User(String username) {
        this.username = username;
        this.expenses = new ArrayList<>();
    }

    public void addExpense(Expense e) {
        expenses.add(e);
    }

    public void showExpenses() {
        for (Expense e : expenses) {
            System.out.println(e);
        }
    }
}

// Main Class
public class pennypoint {
    static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) {
        User u = new User("test");
        u.addExpense(new Expense(100, "Food", "2026-04-18"));
        u.showExpenses();
    }
}
