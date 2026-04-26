// ------------- IMPORTS -------------
import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;
import java.util.*;
import java.io.*;

//-------------------- Expense Class --------------------
class Expense implements Serializable {
 private double amount;
 private String category;
 private String date;

 public Expense(double amount, String category, String date) {
     this.amount = amount;
     this.category = category;
     this.date = date;
 }

 public double getAmount() { return amount; }
 public String getCategory() { return category; }
 public String getDate() { return date; }

 public void setAmount(double amount) { this.amount = amount; }
 public void setCategory(String category) { this.category = category; }

 @Override
 public String toString() {
     return "Amount: " + amount + ", Category: " + category + ", Date: " + date;
 }
}

//-------------------- User Class --------------------
class User implements Serializable {
 private String username;
 private String password;
 private double budget;
 private ArrayList<Expense> expenses;

 public User(String username, String password, double budget) {
     this.username = username;
     this.password = password;
     this.budget = budget;
     this.expenses = new ArrayList<>();
 }

 public String getUsername() { return username; }
 public boolean checkPassword(String pass) { return password.equals(pass); }
 public double getBudget() { return budget; }
 public ArrayList<Expense> getExpenses() { return expenses; }

 public void addExpense(Expense e) { expenses.add(e); }

 public void showExpenses() {
     if (expenses.isEmpty()) {
         System.out.println("No expenses recorded.");
         return;
     }
     for (int i = 0; i < expenses.size(); i++) {
         System.out.println(i + ". " + expenses.get(i));
     }
 }

 // -------- EDIT EXPENSE --------
 public void editExpense(int index, double amount, String category) {
     if (index >= 0 && index < expenses.size()) {
         expenses.get(index).setAmount(amount);
         expenses.get(index).setCategory(category);
     } else {
         System.out.println("Invalid index.");
     }
 }

 // -------- DELETE EXPENSE --------
 public void deleteExpense(int index) {
     if (index >= 0 && index < expenses.size()) {
         expenses.remove(index);
         System.out.println("Expense deleted.");
     } else {
         System.out.println("Invalid index.");
     }
 }

 // -------- SEARCH --------
 public ArrayList<Expense> searchByCategory(String cat) {
     ArrayList<Expense> result = new ArrayList<>();
     for (Expense e : expenses) {
         if (e.getCategory().equalsIgnoreCase(cat)) result.add(e);
     }
     return result;
 }

 public double getTotalSpending() {
     double total = 0;
     for (Expense e : expenses) total += e.getAmount();
     return total;
 }

 // -------- CATEGORY SUMMARY (sorted by amount descending) --------
 public void categorySummary() {
     if (expenses.isEmpty()) {
         System.out.println("No data to summarize.");
         return;
     }
     HashMap<String, Double> map = getCategorySummary();

     List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(map.entrySet());
     sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

     System.out.println("\n--- Current Spending Summary (Sorted by Amount) ---");
     for (Map.Entry<String, Double> entry : sortedEntries) {
         System.out.printf("%-12s: PKR %.2f\n", entry.getKey(), entry.getValue());
     }
 }

 // -------- BUDGET CHECK --------
 public String checkBudget() {
     double spent = getTotalSpending();
     if (spent > budget) return "EXCEEDED";
     if (spent > budget * 0.8) return "WARNING";
     return "OK";
 }

 // -------- SORT --------
 public void sortExpenses() {
     expenses.sort(Comparator.comparingDouble(Expense::getAmount));
     System.out.println("Expenses sorted by amount.");
 }

 // -------- NO-SPEND STREAK --------
 public int noSpendStreak() {
     HashSet<String> days = new HashSet<>();
     for (Expense e : expenses) days.add(e.getDate());
     return Math.max(0, 30 - days.size());
 }

 // -------- PREDICTION --------
 public double predictSpending() {
     if (expenses.isEmpty()) return 0;
     double avg = getTotalSpending() / expenses.size();
     return avg * 30;
 }

 public int getNoSpendDays() {
     HashSet<String> dates = new HashSet<>();
     for (Expense e : expenses) dates.add(e.getDate());
     return Math.max(0, 30 - dates.size());
 }

 public String getPrediction() {
     if (expenses.isEmpty()) return "No expenses yet to predict from.";
     double avg = getTotalSpending() / expenses.size();
     double predicted = avg * 30;
     String r = "Predicted Monthly Spending: PKR " + String.format("%.2f", predicted);
     if (predicted > budget) r += " - May exceed budget!";
     return r;
 }

 public String getBudgetStatus() {
     double spent = getTotalSpending();
     if (spent > budget) return "BUDGET EXCEEDED! Spent: PKR " + String.format("%.2f", spent);
     else if (spent > budget * 0.8) return "WARNING: 80%+ used. Spent: PKR " + String.format("%.2f", spent);
     else return "Remaining: PKR " + String.format("%.2f", budget - spent);
 }

 public HashMap<String, Double> getCategorySummary() {
     HashMap<String, Double> map = new HashMap<>();
     for (Expense e : expenses)
         map.put(e.getCategory(), map.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
     return map;
 }

 // -------- SMART MONTHLY PLANNER (29-Day Logic) --------
 public void generateMonthlyPlan() {
     HashSet<String> uniqueDays = new HashSet<>();
     for (Expense e : expenses) uniqueDays.add(e.getDate());

     if (uniqueDays.size() < 29) {
         System.out.println("\n[!] PLAN BLOCKED: Insufficient Data.");
         System.out.println("You have tracked " + uniqueDays.size() + " unique days.");
         System.out.println("You need at least 29 different days of expenses to unlock the Smart Plan.");
         return;
     }

     HashMap<String, Double> categoryTotals = getCategorySummary();
     double totalSpent = getTotalSpending();

     List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(categoryTotals.entrySet());
     sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

     System.out.println("\n=== SMART MONTHLY PLAN FOR NEXT MONTH ===");
     System.out.println("Based on 29+ days of analysis. Target Budget: PKR " + budget);
     

     for (Map.Entry<String, Double> entry : sortedEntries) {
         double habitPercentage = entry.getValue() / totalSpent;
         double recommendedLimit = habitPercentage * budget;
         System.out.printf("Category: %-12s | Recommended Limit: PKR %.2f (%.1f%%)\n",
                           entry.getKey(), recommendedLimit, habitPercentage * 100);
     }
 }

}


public class PennyPoint extends Application{
	
	//Colors for background and buttons
	private static final String dark_green = "#0C3D1F";
	private static final String medium_green= "#1A5C30";
	private static final String gold = "#C8960C";
	private static final String light_gold = "#E8B800";
	private static final String gold_txt = "#1A1A00";
    private static final String white = "#FFFFFF";
	
	//Application State
    private ArrayList <User> users = new ArrayList<>();
    private User current = null;
    private Stage primaryStage;
    
    //Persistence
    private void loadUsers() {
    	try (ObjectInputStream in = new ObjectInputStream(new FileInputStream ("users.dat"))){
    		users = (ArrayList<User>) in.readObject();
    	} catch (Exception ignored) {};
    }
    
    private void saveUsers() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream ("users.dat"))){
    		out.writeObject(users);
    	} catch (Exception ignored) {};
	}
    
    private User find(String username) {
    	for (User u: users) {
    		if (u.getUsername().equals(username)) {
    			return u;
    		}
    	}
    	return null;
    }
    
    // ------------------ LAUNCHING THE APP ----------------
    
    public void start (Stage s1) {
    	this.primaryStage = s1;
    	loadUsers();
    	s1.setTitle("PennyPoint");
    	s1.setWidth(900);
    	s1.setHeight(600);
    	s1.setResizable (false);
    	showWelcomeScreen();
    	s1.show();
    }
    
    // ----------------- WELCOME SCREEN -------------------
    public void showWelcomeScreen () {
    	StackPane root = new StackPane();
    	root.setStyle("-fx-background-color: " + dark_green + ";");
    	
    	//Coins Falling
    	Pane coins = buildParticles();
    	
    	VBox middle = new VBox(22);
    	middle.setAlignment (Pos.CENTER);
    	middle.setPadding(new Insets(40));
    	
    	Label c = coinLogo(80);
    	Label name = new Label ("PennyPoint");
    	name.setStyle ("-fx-font-family: Georgia; -fx-font-size: 22px; " + "-fx-text-fill: " + gold + "; -fx-font-style: italic;");
    	
    	Label title = new Label ("WELCOME TO PENNYPOINT!");
    	title.setStyle("-fx-font-family: Georgia; -fx-font-size: 40px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");
    	title.setWrapText (true);
    	title.setTextAlignment (TextAlignment.CENTER);
    	
    	Label sub = new Label ("Choose An Action");
    	sub.setStyle("-fx-font-family: Georgia; -fx-font-size: 20px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");
    	
    	HBox btnRow = new HBox(40);
    	btnRow.setAlignment (Pos.CENTER);
    	Button reg = goldBtn ("REGISTER", 180);
    	Button login = goldBtn ("SIGN IN", 180);
    	btnRow.getChildren().addAll(reg, login);
    	
    	middle.getChildren().addAll(c, name, title, sub, btnRow);
        root.getChildren().addAll(coins, middle);
 
        reg.setOnAction(e  -> registerScreen());
        login.setOnAction(e -> showLoginScreen());
 
        primaryStage.setScene(new Scene(root));
    }


// --------------------- REGISTER SCREEN ---------------

private void registerScreen(){
	StackPane root = new StackPane();
	root.setStyle ("-fx-background-color: " + dark_green + ";");
	
	VBox card = centerCard(400);
	
	Label title = screenTitle ("CREATE ACCOUNT");
	TextField userField = inputField ("Enter Username");
	TextField passwordField = inputField ("Enter Password");
	TextField budgetField = inputField ("Enter Monthly Budget (PKR)");
	Label err = errLabel();
	Button go = goldBtn ("REGISTER", 260);
	Button back = ghostBtn ("Back");
	
	card.getChildren().addAll(title, fieldLabel("USERNAME"), userField, fieldLabel("PASSWORD"), passwordField, fieldLabel("MONTHLY BUDGET (PKR)"), budgetField, err, go, back);
	root.getChildren().add(card);
	
	go.setOnAction(e -> {
		String u = userField.getText().trim();
        String p = passwordField.getText().trim();
        String b = budgetField.getText().trim();
        
        if (u.isEmpty() || p.isEmpty() || b.isEmpty()) {
            err.setText("All fields are required."); return;
        }
        
        if (find(u) != null) {
            err.setText("Username already exists."); return;
        }
        
        try {
        	double budget = Double.parseDouble(b);
        	users.add (new User(u, p, budget));
        	saveUsers();
        	infoPopup ("ACCOUNT CREATED! PLEASE SIGN IN.");
        	showLoginScreen();
        } catch (NumberFormatException ex) {
        	err.setText("INVALID BUDGET AMOUNT.");
        }
	});
	
	back.setOnAction (e -> showWelcomeScreen());
	
	primaryStage.setScene (new Scene (root));
	
}


// ------------------ LOGIN SCREEN ---------------------
private void showLoginScreen() {
	StackPane root = new StackPane();
	root.setStyle ("-fx-background-color: " + dark_green + ";");
	
	VBox out = new VBox(30);
	out.setAlignment(Pos.CENTER);
	out.setPadding(new Insets(40));
	
	//3-COIN display
	HBox coins = new HBox(80);
    coins.setAlignment(Pos.CENTER);
    for (int i = 0; i < 3; i++) {
        VBox col = new VBox(8);
        col.setAlignment(Pos.CENTER);
        Label brand = new Label("PennyPoint");
        brand.setStyle("-fx-font-family: Georgia; -fx-font-size: 13px; " + "-fx-text-fill: " + gold + "; -fx-font-style: italic;");
        col.getChildren().addAll(coinLogo(65), brand);
        coins.getChildren().add(col);
    }
    
    //Login Fields
    HBox fields = new HBox(50);
    fields.setAlignment (Pos.CENTER);
    
    VBox user_box = new VBox(10);
    user_box.setAlignment (Pos.CENTER);
    TextField userField = inputField("");
    userField.setPrefWidth(270);
    user_box.getChildren().addAll(fieldLabel("USERNAME"), userField);
    
    VBox pass_box = new VBox(10);
  	pass_box.setAlignment (Pos.CENTER);
    TextField passField = inputField("");
    passField.setPrefWidth(270);
    pass_box.getChildren().addAll(fieldLabel("PASSWORD"), passField);
    
    fields.getChildren().addAll(user_box, pass_box);
    
    Label err = errLabel();
	Button login = goldBtn ("SIGN IN", 260);
	Button back = ghostBtn ("Back");
	
	login.setOnAction(e -> {
		User u1 = find(userField.getText().trim());
        
        if (u1 != null && u1.checkPassword(passField.getText().trim())) {
            current = u1;
            showDashboard();
        } else {
        	err.setText("INVALID LOGIN.");
        }
	});
	
	back.setOnAction(e -> showWelcomeScreen());
	
	out.getChildren().addAll(coins, fields, err, login, back);
	root.getChildren().add(out);
	primaryStage.setScene(new Scene (root));
}


// ------------------- DASHBOARD -------------------
public void showDashboard(){
	StackPane root = new StackPane();
	root.setStyle ("-fx-background-color: " + dark_green + ";");
	
	VBox out = new VBox(32);
	out.setAlignment(Pos.CENTER);
	out.setPadding(new Insets(40));
	
	Label q = new Label("WHAT DO YOU WANT TO DO?");
	q.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; " + "-fx-font-weight: bold; -fx-text-fill: " + gold + ";");
	
	//Row 1 contains 3 buttons.
	HBox r1 = new HBox (28);
	r1.setAlignment (Pos.CENTER);
	Button add = dashBtn ("ADD EXPENSE");
	Button manage = dashBtn ("MANAGE\nEXPENSES");
	Button budget = dashBtn ("VIEW BUDGET");
	r1.getChildren().addAll(add, manage, budget);
		
	//Row 2 contains 2 buttons.
	HBox r2 = new HBox (28);
	r2.setAlignment (Pos.CENTER);
	Button predict = dashBtn ("PREDICT BUDGET");
	Button plan = dashBtn ("PLAN BUDGET");
	Button summary = dashBtn ("VIEW SUMMARY");
	r2.getChildren().addAll(predict, plan, summary);
	
	Button logout = ghostBtn("SIGN OUT");
	
	out.getChildren().addAll(q, r1, r2, logout);
	root.getChildren().add(out);
	
	add.setOnAction (e -> showAddExpensePopup());
	manage.setOnAction (e -> showManageScreen());
	plan.setOnAction(e -> showPlanWindow());
	summary.setOnAction (e -> showSummary());
	budget.setOnAction (e -> showBudgetPopup());
	predict.setOnAction (e -> showPredictionPopup());
	logout.setOnAction(e -> {current = null; showWelcomeScreen();});
	
	primaryStage.setScene (new Scene(root));
}

// ----------------- ADD EXPENSE POP-UP ------------------------

private void showAddExpensePopup() {

    Stage pop = modal("Add Expense", 520, 400);

    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox b1 = new VBox(14);
    b1.setAlignment(Pos.CENTER);
    b1.setPadding(new Insets(36));

    Label title = new Label("ADD NEW EXPENSE\n(POPUP WINDOW)");
    title.setStyle("-fx-font-family: Georgia; -fx-font-size: 20px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + "; " + "-fx-text-alignment: center;");
    title.setTextAlignment(TextAlignment.CENTER);

    TextField amountField  = inputField("AMOUNT HERE");  amountField.setPrefWidth(340);
    TextField categoryField  = inputField("CATEGORY (e.g. Food)"); categoryField.setPrefWidth(340);
    TextField dateField = inputField("DATE (e.g. 2024-04-25)"); dateField.setPrefWidth(340);
    Label err = errLabel();

    HBox btns = hbox();
    Button save   = goldBtn("SAVE", 140);
    Button cancel = ghostBtn("Cancel");
    btns.getChildren().addAll(save, cancel);

    b1.getChildren().addAll(title, amountField, categoryField, dateField, err, btns);
    root.getChildren().add(b1);

    save.setOnAction(e -> {
        try {
            double amt = Double.parseDouble(amountField.getText().trim());
            String cat  = categoryField.getText().trim();
            String date = dateField.getText().trim();
            
            if (cat.isEmpty() || date.isEmpty()) { err.setText("All fields required."); return; }
         // Validate date format
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                err.setText("Date must be in YYYY-MM-DD format.");
                return;
            }

            current.addExpense(new Expense(amt, cat, date));
            saveUsers();
            pop.close();
            infoPopup("Expense added!");
        } catch (NumberFormatException ex) { err.setText("Enter a valid number for amount."); }
    });
    cancel.setOnAction(e -> pop.close());

    pop.setScene(new Scene(root));
    pop.showAndWait();
}

//------------------- MANAGE EXPENSES --------------------------
private void showManageScreen(){
	StackPane root = new StackPane();
	root.setStyle ("-fx-background-color: " + dark_green + ";");
	
	VBox out = new VBox(32);
	out.setAlignment(Pos.CENTER);
	out.setPadding(new Insets(40));
	
	Label q = new Label("WHAT DO YOU WANT TO DO?");
	q.setStyle("-fx-font-family: Georgia; -fx-font-size: 24px; " + "-fx-font-weight: bold; -fx-text-fill: " + gold + ";");
	
	//Row 1 contains 4 buttons.
		HBox r1 = new HBox (28);
		r1.setAlignment (Pos.CENTER);
		Button edit   = dashBtn("EDIT EXPENSE");
		Button delete = dashBtn("DELETE EXPENSE");
		Button search = dashBtn("SEARCH BY CATEGORY");
		Button sort   = dashBtn("SORT BY AMOUNT");
		r1.getChildren().addAll(edit, delete, search, sort);
		
		Button btnBack = ghostBtn("← Back to Dashboard");
	    btnBack.setOnAction(e -> showDashboard());
	    
	    out.getChildren().addAll(r1, btnBack);
		root.getChildren().add(out);
		
		edit.setOnAction (e -> showEditPopupChoice());
		delete.setOnAction (e -> showDeleteSelectPopup());
		search.setOnAction (e -> showSearchPopup());
		sort.setOnAction (e -> showSortedScreen());
		
		primaryStage.setScene (new Scene(root));
}

// -------------------- SHOW VIEW EXPENSES -----------------
private void showViewScreen() {
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox outer = new VBox(18);
    outer.setAlignment(Pos.TOP_CENTER);
    outer.setPadding(new Insets(36));

    Label title = screenTitle("ALL EXPENSES");

    ScrollPane scroll = styledScroll();
    scroll.setPrefHeight(360);
    refreshExpenseList(scroll);

    Button btnBack = ghostBtn("← Back to Dashboard");
    btnBack.setOnAction(e -> showDashboard());

    outer.getChildren().addAll(title, scroll, btnBack);
    root.getChildren().add(outer);
    primaryStage.setScene(new Scene(root));
}

private void refreshExpenseList(ScrollPane scroll) {
    VBox list = new VBox(10);
    list.setPadding(new Insets(10));
    ArrayList<Expense> expenses = current.getExpenses();

    if (expenses.isEmpty()) {
        Label none = new Label("No expenses recorded yet.");
        none.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 14px; -fx-font-family: Georgia;");
        list.getChildren().add(none);
    } else {
        for (int i = 0; i < expenses.size(); i++) {
            final int idx = i;
            Expense exp = expenses.get(i);

            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: " + medium_green + ";" + "-fx-background-radius: 12px;" + "-fx-border-color: " + gold + ";" + "-fx-border-radius: 12px;" + "-fx-border-width: 1px;");

            Label info = new Label(String.format("$%.2f  |  %s  |  %s",
                    exp.getAmount(), exp.getCategory(), exp.getDate()));
            info.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 13px; -fx-font-family: Georgia;");
            HBox.setHgrow(info, Priority.ALWAYS);

            Button btnEdit = smallGoldBtn("Edit");
            Button btnDel  = smallGoldBtn("Delete");

            btnEdit.setOnAction(e -> { showEditPopup(idx, scroll); });
            btnDel.setOnAction(e -> {
                current.deleteExpense(idx);
                saveUsers();
                refreshExpenseList(scroll);
            });

            row.getChildren().addAll(info, btnEdit, btnDel);
            list.getChildren().add(row);
        }
    }
    scroll.setContent(list);
}

// ----------------------- EDIT EXPENSE -----------------
private void showEditPopupChoice() {
	StackPane root = new StackPane();
	root.setStyle("-fx-background-color: " + dark_green + ";");
	
	VBox out = new VBox(18);
    out.setAlignment(Pos.TOP_CENTER);
    out.setPadding(new Insets(36));
    
    Label title = screenTitle ("EDIT AN EXPENSE");
    Label select = whiteLbl ("Select which expense to edit.");
    
    ScrollPane scr = styledScroll();
    scr.setPrefHeight(360);
    refreshExpenseList(scr, true);
    
    Button back = ghostBtn ("Back to Dashboard");
    back.setOnAction (e -> showDashboard());
    
    out.getChildren().addAll(title, select, scr, back);
    root.getChildren().add(out);
    primaryStage.setScene(new Scene(root));
}

private void showEditPopup (int idx, ScrollPane scroll) {
	Expense ex = current.getExpenses().get(idx);
	Stage pop = modal ("Edit Expense", 460, 360);
	
	StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");
    
    VBox box = new VBox(14);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(34));
    
    Label title = new Label("EDIT EXPENSE");
    title.setStyle("-fx-font-family: Georgia; -fx-font-size: 20px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");
    
    TextField amtFld = inputField(String.valueOf(ex.getAmount()));
    amtFld.setPrefWidth(300);
    TextField catFld = inputField(ex.getCategory());
    catFld.setPrefWidth(300);
    Label err = errLabel();

    HBox btns = hbox();
    Button save   = goldBtn("SAVE", 130);
    Button cancel = ghostBtn("Cancel");
    btns.getChildren().addAll(save, cancel);

    box.getChildren().addAll(title, fieldLabel("NEW AMOUNT"), amtFld, fieldLabel("NEW CATEGORY"), catFld, err, btns);
    
    root.getChildren().add(box);
    
    save.setOnAction(e -> {
    	try {
    		double amt = Double.parseDouble(amtFld.getText().trim());
    		String cat = catFld.getText().trim();
    		if (cat.isEmpty()) {
    			err.setText("Category Required.");
    			return;
    		}
    		
    		current.editExpense(idx, amt, cat);
    		saveUsers();
    		refreshExpenseList(scroll, true);
    		pop.close();
    	}catch (NumberFormatException exc) {err.setText ("Invalid Amount.");}
    });
    
    cancel.setOnAction(e -> pop.close());
    
    pop.setScene(new Scene(root));
    pop.showAndWait ();
}

// ------------------- DELETE EXPENSE ---------------------
private void showDeleteSelectPopup() {
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox out = new VBox(18);
    out.setAlignment(Pos.TOP_CENTER);
    out.setPadding(new Insets(36));

    Label title = screenTitle("DELETE AN EXPENSE");
    Label select  = whiteLbl("Click Delete on any expense to remove it.");

    ScrollPane scr = styledScroll();
    scr.setPrefHeight(360);

    // Build delete-only list
    VBox list = new VBox(10);
    list.setPadding(new Insets(10));
    ArrayList<Expense> exp = current.getExpenses();

    if (exp.isEmpty()) {
        list.getChildren().add(whiteLbl("No expenses recorded yet."));
    } else {
        for (int i = 0; i < exp.size(); i++) {
            final int idx = i;
            Expense expns = exp.get(i);

            HBox r = new HBox(14);
            r.setAlignment(Pos.CENTER_LEFT);
            r.setPadding(new Insets(12, 16, 12, 16));
            r.setStyle("-fx-background-color: " + medium_green + ";" + "-fx-background-radius: 12px;" + "-fx-border-color: " + gold + ";" + "-fx-border-radius: 12px; -fx-border-width: 1px;");

            Label info = new Label(String.format("PKR %.2f  |  %s  |  %s",
            expns.getAmount(), expns.getCategory(), expns.getDate()));
            info.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 13px; -fx-font-family: Georgia;");
            HBox.setHgrow(info, Priority.ALWAYS);

            Button delete = smallGoldBtn("Delete");
            delete.setOnAction(e -> {
                current.deleteExpense(idx);
                saveUsers();
                showDeleteSelectPopup(); // refresh screen
            });

            r.getChildren().addAll(info, delete);
            list.getChildren().add(r);
        }
    }
    scr.setContent(list);

    Button back = ghostBtn("← Back to Dashboard");
    back.setOnAction(e -> showDashboard());

    out.getChildren().addAll(title, select, scr, back);
    root.getChildren().add(out);
    primaryStage.setScene(new Scene(root));
}

// ------------------------ PLAN -------------------------
private void showPlanWindow() {
	Stage pop = modal("Monthly Plan", 560, 500);

    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox box = new VBox(16);
    box.setAlignment(Pos.TOP_CENTER);
    box.setPadding(new Insets(36));

    Label title = new Label("SMART MONTHLY PLAN");
    title.setStyle("-fx-font-family: Georgia; -fx-font-size: 22px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");
    
    VBox plan = new VBox(12);
    plan.setPadding(new Insets(8, 0, 0, 10));

    // Check 29-day requirement
    HashSet<String> uniqueDays = new HashSet<>();
    for (Expense e : current.getExpenses()) uniqueDays.add(e.getDate());

    if (uniqueDays.size() < 29) {
        Label blocked = new Label("PLAN BLOCKED: Insufficient Data.\n\n" + "You have tracked " + uniqueDays.size() + " unique days.\n" + "You need at least 29 different days to unlock the Smart Plan.");
        blocked.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 14px; " + "-fx-font-family: Georgia; -fx-wrap-text: true;");
        blocked.setMaxWidth(460);
        plan.getChildren().add(blocked);
    } else {
        HashMap<String, Double> map = current.getCategorySummary();
        double total = current.getTotalSpending();
        double budget = current.getBudget();

        // Sort by amount descending
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(map.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> entry : sorted) {
            double pct   = entry.getValue() / total;
            double limit = pct * budget;
            Label l = new Label(String.format("• %-12s → Limit: PKR %.2f (%.1f%%)", entry.getKey(), limit, pct * 100));
            l.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 14px; " + "-fx-font-family: Georgia; -fx-wrap-text: true;");
            l.setMaxWidth(460);
            plan.getChildren().add(l);
        }
    }

    ScrollPane scr = styledScroll();
    scr.setContent(plan);
    scr.setPrefHeight(320);
    scr.setFitToWidth(true);

    Button close = goldBtn("CLOSE", 150);
    close.setOnAction(e -> pop.close());

    box.getChildren().addAll(title, scr, close);
    root.getChildren().add(box);
    pop.setScene(new Scene(root));
    pop.showAndWait();
}

// ----------------------- SUMMARY ------------------------
public void showSummary(){
	StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");
    
    VBox out = new VBox(18);
	out.setAlignment(Pos.TOP_CENTER);
	out.setPadding(new Insets(36));
	
	Label title = screenTitle ("EXPENSE SUMMARY");
	
	//stats
	HBox stats = new HBox (20);
	stats.setAlignment(Pos.CENTER);
	stats.getChildren().addAll(statCard("TOTAL SPENT: ", String.format("PKR.%.2f", current.getTotalSpending())), statCard("No-Spend Days: ", current.getNoSpendDays() + "/30"), statCard ("Budget: ", String.format ("PKR.%.2f", current.getBudget())));
	
	//Categories
	VBox categories = new VBox (10);
	categories.setPadding (new Insets(8));
	HashMap<String, Double> map = current.getCategorySummary();
	
	//Sorting Logic
	if (map.isEmpty()) {
	    categories.getChildren().add(whiteLbl("No expenses yet."));
	} else {
	    double total = current.getTotalSpending();
	    List<Map.Entry<String, Double>> sorted = new ArrayList<>(map.entrySet());
	    sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
	    for (Map.Entry<String, Double> e : sorted)
	        categories.getChildren().add(categoryBar(e.getKey(), e.getValue(), total));
	}
    
    ScrollPane scr = styledScroll();
    scr.setContent (categories);
    scr.setPrefHeight(200);
    
    Button sort = goldBtn("SORT BY AMOUNT", 220);
    sort.setOnAction (e -> {current.sortExpenses(); saveUsers(); showSummary(); });
    
    Button back = ghostBtn("Back");
    back.setOnAction (e -> showDashboard());
    
    out.getChildren().addAll(title, stats, scr, sort, back);
    root.getChildren().add(out);
    primaryStage.setScene(new Scene(root));
}

//----------------- SEARCH BY CATEGORY ---------------------
private void showSearchPopup() {
    Stage pop = modal("Search by Category", 520, 480);

    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox box = new VBox(16);
    box.setAlignment(Pos.TOP_CENTER);
    box.setPadding(new Insets(36));

    Label title = new Label("SEARCH BY CATEGORY");
    title.setStyle("-fx-font-family: Georgia; -fx-font-size: 20px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");

    HBox rowSearch = new HBox(12);
    rowSearch.setAlignment(Pos.CENTER);
    TextField categoryField = inputField("Enter category name");
    categoryField.setPrefWidth(260);
    Button go = goldBtn("SEARCH", 110);
    rowSearch.getChildren().addAll(categoryField, go);

    VBox res = new VBox(10);
    res.setPadding(new Insets(10, 0, 0, 0));

    ScrollPane scr = styledScroll();
    scr.setContent(res);
    scr.setPrefHeight(260);

    Button close = ghostBtn("Close");
    close.setOnAction(e -> pop.close());

    go.setOnAction(e -> {
        res.getChildren().clear();
        String category = categoryField.getText().trim();
        if (category.isEmpty()) return;

        boolean found = false;
        for (Expense exp : current.getExpenses()) {
            if (exp.getCategory().equalsIgnoreCase(category)) {
                found = true;
                Label l = new Label(String.format("PKR %.2f  |  %s  |  %s", exp.getAmount(), exp.getCategory(), exp.getDate()));
                l.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 13px; " + "-fx-font-family: Georgia; -fx-background-color: " + medium_green + "; " + "-fx-background-radius: 10px; -fx-padding: 10 14;");
                l.setMaxWidth(420);
                res.getChildren().add(l);
            }
        }
        if (!found) {
            res.getChildren().add(whiteLbl("No expenses found for: " + category));
        }
    });

    box.getChildren().addAll(title, rowSearch, scr, close);
    root.getChildren().add(box);
    pop.setScene(new Scene(root));
    pop.showAndWait();
}

// --------------------- CHECK BUDGET ----------------------
private void showBudgetPopup() {
    Stage pop = modal("Budget Status", 480, 320);

    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox box = new VBox(20);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(40));

    Label title = new Label("BUDGET STATUS");
    title.setStyle("-fx-font-family: Georgia; -fx-font-size: 22px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");

    double spent = current.getTotalSpending();
    double budget = current.getBudget();
    double left = budget - spent;

    String statusText;
    String statusColor;
    if (spent > budget) {
        statusText  = "⚠ BUDGET EXCEEDED!";
        statusColor = "#FF4444";
    } else if (spent > budget * 0.8) {
        statusText  = "⚠ WARNING: 80%+ BUDGET USED";
        statusColor = "#FFA500";
    } else {
        statusText  = "✓ WITHIN BUDGET";
        statusColor = "#44FF88";
    }

    Label status = new Label(statusText);
    status.setStyle("-fx-font-family: Georgia; -fx-font-size: 18px; " + "-fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");

    Label details = new Label(String.format("Budget:    PKR %.2f\nSpent:     PKR %.2f\nRemaining: PKR %.2f", budget, spent, left));
    details.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 15px; " + "-fx-text-fill: " + white + "; -fx-text-alignment: left;");

    Button close = goldBtn("OK", 130);
    close.setOnAction(e -> pop.close());

    box.getChildren().addAll(title, status, details, close);
    root.getChildren().add(box);
    pop.setScene(new Scene(root));
    pop.showAndWait();
}

// --------------------- SORT BY AMOUNT --------------------
private void showSortedScreen() {
    current.sortExpenses();
    saveUsers();

    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox out = new VBox(18);
    out.setAlignment(Pos.TOP_CENTER);
    out.setPadding(new Insets(36));

    Label title = screenTitle("SORTED BY AMOUNT");
    Label hint  = whiteLbl("Expenses sorted from lowest to highest.");

    ScrollPane scr = styledScroll();
    scr.setPrefHeight(360);
    refreshExpenseList(scr, false);

    Button back = ghostBtn("Back to Dashboard");
    back.setOnAction(e -> showDashboard());

    out.getChildren().addAll(title, hint, scr, back);
    root.getChildren().add(out);
    primaryStage.setScene(new Scene(root));
}

// ---------------- NO-SPEND STREAK ------------------------
private void showStreakPopup() {
    Stage pop = modal("No-Spend Streak", 460, 300);

    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox box = new VBox(22);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(40));

    Label title = new Label("NO-SPEND STREAK");
    title.setStyle("-fx-font-family: Georgia; -fx-font-size: 22px; " +
            "-fx-font-weight: bold; -fx-text-fill: " + white + ";");

    int days = current.getNoSpendDays();

    Label streak = new Label(days + " DAYS");
    streak.setStyle("-fx-font-family: Georgia; -fx-font-size: 52px; " + "-fx-font-weight: bold; -fx-text-fill: " + light_gold + ";");

    Label sub = new Label("days this month with no spending");
    sub.setStyle("-fx-font-family: Georgia; -fx-font-size: 14px; -fx-text-fill: " + white + ";");

    Button close = goldBtn("CLOSE", 130);
    close.setOnAction(e -> pop.close());

    box.getChildren().addAll(title, streak, sub, close);
    root.getChildren().add(box);
    pop.setScene(new Scene(root));
    pop.showAndWait();
}

// -------------------- PREDICTION -------------------------
private void showPredictionPopup() {
    Stage pop = modal("Spending Prediction", 560, 480);

    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox box = new VBox(16);
    box.setAlignment(Pos.TOP_CENTER);
    box.setPadding(new Insets(36));

    Label title = new Label("PREDICT SPENDING");
    title.setStyle("-fx-font-family: Georgia; -fx-font-size: 22px; " +
            "-fx-font-weight: bold; -fx-text-fill: " + white + ";");

    Label note = new Label("(in case of queue/generation popup)");
    note.setStyle("-fx-text-fill: rgba(255,255,255,0.45); " + "-fx-font-size: 12px; -fx-font-family: Georgia;");

    VBox plan = new VBox(14);
    plan.setPadding(new Insets(8, 0, 0, 10));

    String predict = current.getPrediction();
    String status = current.getBudgetStatus();
    int streak = current.getNoSpendDays();

    String[] lines = { predict, status, "No-spend streak: " + streak + " days this month", "Tip: Log every expense daily to stay on track", "Goal: Aim for " + (streak + 5) + " no-spend days next month"};

    for (String line : lines) {
        Label l = new Label("• " + line);
        l.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 14px; " + "-fx-font-family: Georgia; -fx-wrap-text: true;");
        l.setMaxWidth(460);
        plan.getChildren().add(l);
    }

    ScrollPane scr = styledScroll();
    scr.setContent(plan);
    scr.setPrefHeight(280);
    scr.setFitToWidth(true);

    Button close = goldBtn("CLOSE", 150);
    close.setOnAction(e -> pop.close());

    box.getChildren().addAll(title, note, scr, close);
    root.getChildren().add(box);
    pop.setScene(new Scene(root));
    pop.showAndWait();
}

//-------------------- GENERAL INFO ------------------------
private void infoPopup(String msg) {

    Stage pop = modal("Info", 440, 230);
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: " + dark_green + ";");

    VBox box = new VBox(22);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(36));

    Label l = new Label(msg);
    l.setStyle("-fx-font-family: Georgia; -fx-font-size: 16px; " + "-fx-text-fill: " + white + "; -fx-wrap-text: true; " + "-fx-text-alignment: center;");
    l.setMaxWidth(360);
    l.setTextAlignment(TextAlignment.CENTER);

    Button b_ok = goldBtn("OK", 130);
    b_ok.setOnAction(e -> pop.close());

    box.getChildren().addAll(l, b_ok);
    root.getChildren().add(box);
    pop.setScene(new Scene(root));
    pop.showAndWait();
}

// ------------------ COMPONENT HELPERS -------------------------
private Label coinLogo(int size) {
    Label c = new Label("$");
    c.setPrefSize(size, size);
    c.setAlignment(Pos.CENTER);
    int r = size / 2;
    c.setStyle("-fx-background-color: radial-gradient(center 38% 32%, radius 62%, #F5CC48, #A06800);" + "-fx-background-radius: " + r + "px;" + "-fx-border-color: #6A4000; -fx-border-radius: " + r + "px; -fx-border-width: 3px;" + "-fx-font-size: " + (size / 2.5) + "px; -fx-font-weight: bold;" + "-fx-font-family: 'Courier New'; -fx-text-fill: #4A2800;");
    ScaleTransition sc = new ScaleTransition(Duration.millis(900), c);
    sc.setFromX(1); sc.setToX(1.06);
    sc.setFromY(1); sc.setToY(1.06);
    sc.setAutoReverse(true);
    sc.setCycleCount(Animation.INDEFINITE);
    sc.play();
    return c;
}

private Label screenTitle(String text) {
    Label l = new Label(text);
    l.setStyle("-fx-font-family: Georgia; -fx-font-size: 26px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");
    return l;
}

private Label fieldLabel(String text) {
    Label l = new Label(text);
    l.setStyle("-fx-font-family: Georgia; -fx-font-size: 13px; " + "-fx-font-weight: bold; -fx-text-fill: " + white + ";");
    return l;
}

private Label errLabel() {
    Label l = new Label("");
    l.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-font-family: Georgia;");
    return l;
}

private Label whiteLbl(String text) {
    Label l = new Label(text);
    l.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 14px; -fx-font-family: Georgia;");
    return l;
}

private TextField inputField(String prompt) {
    TextField f = new TextField();
    f.setPromptText(prompt);
    f.setPrefHeight(44);
    f.setStyle("-fx-background-color: white; -fx-background-radius: 10px;" + "-fx-font-size: 14px; -fx-font-family: Georgia; -fx-padding: 0 12;");
    return f;
}

private PasswordField passField(String prompt) {
    PasswordField f = new PasswordField();
    f.setPromptText(prompt);
    f.setPrefHeight(44);
    f.setStyle("-fx-background-color: white; -fx-background-radius: 10px;" + "-fx-font-size: 14px; -fx-font-family: Georgia; -fx-padding: 0 12;");
    return f;
}

private Button goldBtn(String text, int width) {
    Button b1 = new Button(text);
    b1.setPrefWidth(width);
    b1.setPrefHeight(48);
    String base ="-fx-background-color: " + gold + "; -fx-background-radius: 10px;" + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: Georgia;" + "-fx-text-fill: " + gold_txt + "; -fx-cursor: hand;";
    b1.setStyle(base);
    b1.setOnMouseEntered(e -> b1.setStyle(base.replace(gold, light_gold)));
    b1.setOnMouseExited(e  -> b1.setStyle(base));
    return b1;
}

private Button dashBtn(String text) {
    Button b1 = new Button(text);
    b1.setPrefSize(170, 70);
    b1.setTextAlignment(TextAlignment.CENTER);
    String base = "-fx-background-color: " + gold + "; -fx-background-radius: 14px;" + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: Georgia;" + "-fx-text-fill: " + gold_txt + "; -fx-cursor: hand; -fx-alignment: center;";
    b1.setStyle(base);
    b1.setOnMouseEntered(e -> b1.setStyle(base.replace(gold, light_gold)));
    b1.setOnMouseExited(e  -> b1.setStyle(base));
    return b1;
}

private Button smallGoldBtn(String text) {
    Button b1 = new Button(text);
    b1.setPrefSize(64, 30);
    String base = "-fx-background-color: " + gold + "; -fx-background-radius: 8px;" + "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: Georgia;" + "-fx-text-fill: " + gold_txt + "; -fx-cursor: hand;";
    b1.setStyle(base);
    b1.setOnMouseEntered(e -> b1.setStyle(base.replace(gold, light_gold)));
    b1.setOnMouseExited(e  -> b1.setStyle(base));
    return b1;
}

private Button ghostBtn(String text) {
    Button b1 = new Button(text);
    b1.setStyle("-fx-background-color: transparent;" + "-fx-border-color: rgba(255,255,255,0.35); -fx-border-radius: 8px;" + "-fx-font-size: 13px; -fx-font-family: Georgia;" + "-fx-text-fill: rgba(255,255,255,0.65); -fx-cursor: hand; -fx-padding: 6 18;");
    return b1;
}

private VBox centerCard(int maxWidth) {
    VBox card1 = new VBox(14);
    card1.setAlignment(Pos.CENTER);
    card1.setPadding(new Insets(48));
    card1.setMaxWidth(maxWidth);
    return card1;
}

private HBox hbox() {
    HBox h1 = new HBox(18);
    h1.setAlignment(Pos.CENTER);
    return h1;
}

private ScrollPane styledScroll() {
    ScrollPane scr = new ScrollPane();
    scr.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
    scr.setFitToWidth(true);
    return scr;
}

private Stage modal(String title, int w, int h) {
    Stage st = new Stage();
    st.initModality(Modality.APPLICATION_MODAL);
    st.initOwner(primaryStage);
    st.setTitle(title);
    st.setWidth(w);
    st.setHeight(h);
    st.setResizable(false);
    return st;
}

private VBox statCard(String label, String value) {
    VBox card1 = new VBox(6);
    card1.setAlignment(Pos.CENTER);
    card1.setPadding(new Insets(14, 22, 14, 22));
    card1.setStyle("-fx-background-color: " + medium_green + ";" + "-fx-background-radius: 12px;" + "-fx-border-color: " + gold + ";" + "-fx-border-radius: 12px; -fx-border-width: 1.5px;");
    Label val = new Label(value);
    val.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; " + "-fx-text-fill: " + gold + "; -fx-font-family: Georgia;");
    Label lbl = new Label(label);
    lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.65); " + "-fx-font-family: Georgia;");
    card1.getChildren().addAll(val, lbl);
    return card1;
}

private HBox categoryBar(String cat, double amt, double total) {
    HBox row = new HBox(12);
    row.setAlignment(Pos.CENTER_LEFT);
    row.setPadding(new Insets(8, 14, 8, 14));
    row.setStyle("-fx-background-color: " + medium_green + "; -fx-background-radius: 10px;");

    Label categoryLabel = new Label(cat);
    categoryLabel.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 13px; -fx-font-family: Georgia;");
    categoryLabel.setPrefWidth(110);

    Rectangle bar1 = new Rectangle(0, 13);
    bar1.setFill(Color.web(gold));
    bar1.setArcWidth(6); bar1.setArcHeight(6);
    double pct = (total > 0) ? amt / total : 0;
    Timeline tl = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(bar1.widthProperty(), pct * 260)));
    tl.play();

    Label amtLbl = new Label(String.format("PKR %.2f", amt));
    amtLbl.setStyle("-fx-text-fill: " + light_gold + "; -fx-font-size: 13px; -fx-font-family: Georgia;");

    row.getChildren().addAll(categoryLabel, bar1, amtLbl);
    return row;
}

private Pane buildParticles() {
    Pane p1 = new Pane();
    p1.setMouseTransparent(true);
    p1.setPrefSize(900, 600);
    Random rnd = new Random(99);
    for (int i = 0; i < 24; i++) {
        double size = 12 + rnd.nextInt(22);
        Circle c1 = new Circle(size / 2);
        c1.setFill(Color.web(rnd.nextBoolean() ? gold : light_gold));
        c1.setOpacity(0.45 + rnd.nextDouble() * 0.45);
        c1.setLayoutX(rnd.nextInt(900));
        c1.setLayoutY(rnd.nextInt(600));

        TranslateTransition tr2 = new TranslateTransition(Duration.millis(3200 + rnd.nextInt(2800)), c1);
        tr2.setByY(700); tr2.setByX(-50 + rnd.nextInt(100));
        tr2.setCycleCount(Animation.INDEFINITE);
        tr2.setDelay(Duration.millis(rnd.nextInt(3000)));
        tr2.setInterpolator(Interpolator.LINEAR);
        tr2.play();

        RotateTransition rt = new RotateTransition(Duration.millis(1000 + rnd.nextInt(1200)), c1);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();

        p1.getChildren().add(c1);
    }
    return p1;
}

private void refreshExpenseList(ScrollPane scroll, boolean showEditDelete) {
    VBox list = new VBox(10);
    list.setPadding(new Insets(10));
    ArrayList<Expense> expenses = current.getExpenses();

    if (expenses.isEmpty()) {
        Label none = new Label("No expenses recorded yet.");
        none.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 14px; -fx-font-family: Georgia;");
        list.getChildren().add(none);
    } else {
        for (int i = 0; i < expenses.size(); i++) {
            final int idx = i;
            Expense exp = expenses.get(i);

            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: " + medium_green + ";" +
                "-fx-background-radius: 12px;" + "-fx-border-color: " + gold + ";" +
                "-fx-border-radius: 12px;" + "-fx-border-width: 1px;");

            Label info = new Label(String.format("PKR %.2f  |  %s  |  %s",
                    exp.getAmount(), exp.getCategory(), exp.getDate()));
            info.setStyle("-fx-text-fill: " + white + "; -fx-font-size: 13px; -fx-font-family: Georgia;");
            HBox.setHgrow(info, Priority.ALWAYS);
            row.getChildren().add(info);

            if (showEditDelete) {
                Button btnEdit = smallGoldBtn("Edit");
                Button btnDel  = smallGoldBtn("Delete");
                btnEdit.setOnAction(e -> showEditPopup(idx, scroll));
                btnDel.setOnAction(e -> {
                    current.deleteExpense(idx);
                    saveUsers();
                    refreshExpenseList(scroll, true);
                });
                row.getChildren().addAll(btnEdit, btnDel);
            }
            list.getChildren().add(row);
        }
    }
    scroll.setContent(list);
}

	public static void main(String [] args) {launch(args);}
}
