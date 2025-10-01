import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class HRMSUI extends Application {

    private String loggedInUsername;
    private String loggedInRole;
    private Integer loggedInEmpId; // For manager login

    private TableView<Employee> employeeTable = new TableView<>();
    private Connection connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        connectDB();
        showLogin(stage);
    }

    // ================== DATABASE CONNECTION ==================
    private void connectDB() throws Exception {
        String url = "jdbc:mysql://localhost:3306/hrms_simple?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";   // your DB username
        String pass = "root";   // your DB password
        connection = DriverManager.getConnection(url, user, pass);
    }

    // ================== LOGIN SCREEN ==================
    // ================== LOGIN SCREEN ==================
    private void showLogin(Stage stage) {
        // Create Labels and Fields
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Label empLabel = new Label("Employee ID (for Manager):");
        TextField empField = new TextField();

        Button loginBtn = new Button("Login");

        // Grid Layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(15);
        grid.setHgap(10);

        // Bind widths to 40% of screen
        userField.prefWidthProperty().bind(stage.widthProperty().multiply(0.4));
        passField.prefWidthProperty().bind(stage.widthProperty().multiply(0.4));
        empField.prefWidthProperty().bind(stage.widthProperty().multiply(0.4));

        // Add components
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);

        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);

        grid.add(empLabel, 0, 2);
        grid.add(empField, 1, 2);

        grid.add(loginBtn, 1, 3);
        GridPane.setHalignment(loginBtn, HPos.CENTER); // center button

        // Login button logic
        loginBtn.setOnAction(e -> {
            try {
                if (authenticate(userField.getText(), passField.getText(), empField.getText())) {
                    showDashboard(stage);
                } else {
                    showAlert("Login failed!");
                }
            } catch (Exception ex) {
                showAlert("Error: " + ex.getMessage());
            }
        });

        // Root
        StackPane root = new StackPane(grid);
        Scene scene = new Scene(root, 1000, 700);

        stage.setScene(scene);
        stage.setTitle("HRMS Login");
        stage.setMaximized(true);   // Full screen window
        stage.show();
    }



    private boolean authenticate(String username, String password, String empIdText) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT u.role, u.emp_id FROM users u WHERE u.username=? AND u.password=?"
        );
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            loggedInUsername = username;
            loggedInRole = rs.getString("role");
            if ("MANAGER".equalsIgnoreCase(loggedInRole)) {
                if (empIdText == null || empIdText.isEmpty()) {
                    showAlert("Manager login requires Employee ID!");
                    return false;
                }
                loggedInEmpId = Integer.parseInt(empIdText);
                if (loggedInEmpId != rs.getInt("emp_id")) {
                    showAlert("Employee ID does not match username!");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // ================== DASHBOARD ==================
    private void showDashboard(Stage stage) {
        Button loadBtn = new Button("Load Employees");
        Button addBtn = new Button("Add Employee");
        Button deleteBtn = new Button("Delete Employee");
        Button queryBtn = new Button("Execute Query");
        Button graphBtn = new Button("Graphs");
        Button logoutBtn = new Button("Logout");
        Button auditBtn = new Button("View Audit Log");

        HBox buttonBox = new HBox(10, loadBtn, addBtn, deleteBtn, queryBtn, graphBtn);
        if ("ADMIN".equalsIgnoreCase(loggedInRole)) {
            buttonBox.getChildren().add(auditBtn);
            auditBtn.setOnAction(e -> viewAuditLog());
        }
        buttonBox.getChildren().add(logoutBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));



        VBox root = new VBox(10, buttonBox, employeeTable);
        root.setPadding(new Insets(10));

        // Make the table fill remaining space
        VBox.setVgrow(employeeTable, Priority.ALWAYS);
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setupTable();

        // BUTTON ACTIONS
        loadBtn.setOnAction(e -> loadEmployees());
        addBtn.setOnAction(e -> addEmployee());
        deleteBtn.setOnAction(e -> deleteEmployee());
        queryBtn.setOnAction(e -> executeQuery());
        graphBtn.setOnAction(e -> showGraphs());
        logoutBtn.setOnAction(e -> {
            loggedInUsername = null;
            loggedInRole = null;
            loggedInEmpId = null;
            employeeTable.getItems().clear();
            try {
                start(stage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        stage.setScene(new Scene(root));
        stage.setTitle("HRMS Dashboard - " + loggedInRole + " (" + loggedInUsername + ")");
        stage.setMaximized(true);   // Full screen window
        stage.setFullScreen(true);
        stage.show();
    }

    private void viewAuditLog() {
        Stage logStage = new Stage();
        TableView<AuditLog> logTable = new TableView<>();

        TableColumn<AuditLog, Integer> colLogId = new TableColumn<>("Log ID");
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));

        TableColumn<AuditLog, Integer> colEmpId = new TableColumn<>("Employee ID");
        colEmpId.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<AuditLog, String> colChangedBy = new TableColumn<>("Changed By");
        colChangedBy.setCellValueFactory(new PropertyValueFactory<>("changedBy"));

        TableColumn<AuditLog, String> colColumn = new TableColumn<>("Column");
        colColumn.setCellValueFactory(new PropertyValueFactory<>("columnName"));

        TableColumn<AuditLog, String> colOld = new TableColumn<>("Old Value");
        colOld.setCellValueFactory(new PropertyValueFactory<>("oldValue"));

        TableColumn<AuditLog, String> colNew = new TableColumn<>("New Value");
        colNew.setCellValueFactory(new PropertyValueFactory<>("newValue"));

        TableColumn<AuditLog, Timestamp> colTime = new TableColumn<>("Changed At");
        colTime.setCellValueFactory(new PropertyValueFactory<>("changedAt"));

        logTable.getColumns().addAll(colLogId, colEmpId, colChangedBy, colColumn, colOld, colNew, colTime);

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM audit_log ORDER BY changed_at DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logTable.getItems().add(new AuditLog(
                        rs.getInt("log_id"),
                        rs.getInt("emp_id"),
                        rs.getString("changed_by"),
                        rs.getString("column_name"),
                        rs.getString("old_value"),
                        rs.getString("new_value"),
                        rs.getTimestamp("changed_at")
                ));
            }
        } catch (SQLException e) {
            showAlert(e.getMessage());
        }

        BorderPane pane = new BorderPane(logTable);
        Scene scene = new Scene(pane, 800, 400);
        logStage.setScene(scene);
        logStage.setTitle("Audit Log Viewer");
        logStage.setMaximized(true);
        logStage.show();
    }


    // ================== TABLE SETUP ==================
    private void setupTable() {
        employeeTable.getColumns().clear();
        employeeTable.setEditable(true);

        // --- Employee ID (not editable) ---
        TableColumn<Employee, Integer> colId = new TableColumn<>("Emp ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("empId"));

        // --- First Name ---
        TableColumn<Employee, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        colFirstName.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setFirstName(e.getNewValue());
            updateEmployee(emp, "first_name", e.getNewValue());
        });

        // --- Last Name ---
        TableColumn<Employee, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colLastName.setCellFactory(TextFieldTableCell.forTableColumn());
        colLastName.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setLastName(e.getNewValue());
            updateEmployee(emp, "last_name", e.getNewValue());
        });

        // --- Phone ---
        TableColumn<Employee, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setPhone(e.getNewValue());
            updateEmployee(emp, "phone_number", e.getNewValue());
        });

        // --- Email ---
        TableColumn<Employee, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setEmail(e.getNewValue());
            updateEmployee(emp, "email", e.getNewValue());
        });

        // --- Age ---
        TableColumn<Employee, Integer> colAge = new TableColumn<>("Age");
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colAge.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));
        colAge.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setAge(e.getNewValue());
            updateEmployee(emp, "age", e.getNewValue());
        });

        // --- Gender ---
        TableColumn<Employee, String> colGender = new TableColumn<>("Gender");
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colGender.setCellFactory(TextFieldTableCell.forTableColumn());
        colGender.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setGender(e.getNewValue().toUpperCase());
            updateEmployee(emp, "gender", e.getNewValue().toUpperCase());
        });

        // --- Years of Experience ---
        TableColumn<Employee, Integer> colExperience = new TableColumn<>("Years of Experience");
        colExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience"));
        colExperience.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));
        colExperience.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setYearsExperience(e.getNewValue());
            updateEmployee(emp, "years_experience", e.getNewValue());
        });

        // --- Salary ---
        TableColumn<Employee, Double> colSalary = new TableColumn<>("Salary");
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colSalary.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
        colSalary.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setSalary(e.getNewValue());
            updateEmployee(emp, "salary", e.getNewValue());
        });

        // --- Department ---
        TableColumn<Employee, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDept.setCellFactory(TextFieldTableCell.forTableColumn());
        colDept.setOnEditCommit(e -> {
            Employee emp = e.getRowValue();
            emp.setDepartment(e.getNewValue());
            updateEmployee(emp, "department", e.getNewValue());
        });

        // --- Manager ID (not editable) ---
        TableColumn<Employee, Integer> colMgr = new TableColumn<>("Manager ID");
        colMgr.setCellValueFactory(new PropertyValueFactory<>("managerId"));

        employeeTable.getColumns().addAll(
                colId, colFirstName, colLastName, colPhone, colEmail,
                colAge, colGender, colExperience, colSalary, colDept, colMgr
        );

        employeeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void updateEmployee(Employee emp, String column, Object newValue) {
        String sqlSelect = "SELECT " + column + " FROM employees WHERE emp_id = ?";
        String sqlUpdate = "UPDATE employees SET " + column + " = ? WHERE emp_id = ?";
        try {
            // Fetch old value first
            PreparedStatement psSelect = connection.prepareStatement(sqlSelect);
            psSelect.setInt(1, emp.getEmpId());
            ResultSet rs = psSelect.executeQuery();
            String oldValue = null;
            if (rs.next()) {
                Object obj = rs.getObject(column);
                oldValue = (obj != null) ? obj.toString() : null;
            }

            // Update with new value
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate);
            psUpdate.setObject(1, newValue);
            psUpdate.setInt(2, emp.getEmpId());
            psUpdate.executeUpdate();

            // Insert audit log
            PreparedStatement psLog = connection.prepareStatement(
                    "INSERT INTO audit_log (emp_id, changed_by, column_name, old_value, new_value) VALUES (?,?,?,?,?)"
            );
            psLog.setInt(1, emp.getEmpId());
            psLog.setString(2, loggedInUsername);
            psLog.setString(3, column);
            psLog.setString(4, oldValue);
            psLog.setString(5, newValue != null ? newValue.toString() : null);
            psLog.executeUpdate();

        } catch (SQLException e) {
            showAlert("Failed to update " + column + ": " + e.getMessage());
        }
    }



    // ================== LOAD EMPLOYEES ==================
    private void loadEmployees() {
        employeeTable.getItems().clear();
        String sql = "SELECT * FROM employees";
        if ("MANAGER".equalsIgnoreCase(loggedInRole)) {
            sql += " WHERE manager_id = ?";
        }
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            if ("MANAGER".equalsIgnoreCase(loggedInRole)) {
                ps.setInt(1, loggedInEmpId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("emp_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getInt("years_experience"),
                        rs.getDouble("salary"),
                        rs.getString("department"),
                        rs.getInt("manager_id")
                );
                employeeTable.getItems().add(emp);
            }
        } catch (SQLException e) {
            showAlert(e.getMessage());
        }
    }

    // ================== ADD EMPLOYEE ==================
    private void addEmployee() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Add Employee");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField phone = new TextField();
        TextField email = new TextField();
        TextField age = new TextField();
        TextField gender = new TextField();
        TextField exp = new TextField();
        TextField salary = new TextField();
        TextField dept = new TextField();

        grid.addRow(0, new Label("First Name:"), firstName);
        grid.addRow(1, new Label("Last Name:"), lastName);
        grid.addRow(2, new Label("Phone:"), phone);
        grid.addRow(3, new Label("Email:"), email);
        grid.addRow(4, new Label("Age:"), age);
        grid.addRow(5, new Label("Gender(MALE/FEMALE/OTHER):"), gender);
        grid.addRow(6, new Label("Years Of Experience:"), exp);
        grid.addRow(7, new Label("Salary:"), salary);
        grid.addRow(8, new Label("Department:"), dept);

        dialog.getDialogPane().setContent(grid);
        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == addBtn) {
                try {
                    int ageVal = Integer.parseInt(age.getText());
                    int expVal = Integer.parseInt(exp.getText());
                    double salaryVal = Double.parseDouble(salary.getText());

                    return new Employee(
                            0,
                            firstName.getText(),
                            lastName.getText(),
                            phone.getText(),
                            email.getText(),
                            ageVal,
                            gender.getText().toUpperCase(),
                            expVal,
                            salaryVal,
                            dept.getText(),
                            "MANAGER".equalsIgnoreCase(loggedInRole) ? loggedInEmpId : null
                    );
                } catch (NumberFormatException ex) {
                    showAlert("Please enter valid numeric values for Age, Experience, and Salary.");
                    return null;
                }
            }
            return null;
        });


        dialog.showAndWait().ifPresent(emp -> {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO employees (first_name,last_name,phone_number,email,age,gender,years_experience,salary,department,manager_id) VALUES (?,?,?,?,?,?,?,?,?,?)"
                );
                ps.setString(1, emp.getFirstName());
                ps.setString(2, emp.getLastName());
                ps.setString(3, emp.getPhone());
                ps.setString(4, emp.getEmail());
                ps.setInt(5, emp.getAge());
                ps.setString(6, emp.getGender());
                ps.setInt(7, emp.getYearsExperience());
                ps.setDouble(8, emp.getSalary());
                ps.setString(9, emp.getDepartment());
                if (emp.getManagerId() != null) ps.setInt(10, emp.getManagerId());
                else ps.setNull(10, Types.INTEGER);
                ps.executeUpdate();
                loadEmployees();
            } catch (SQLException e) {
                showAlert(e.getMessage());
            }
        });
    }

    // ================== DELETE EMPLOYEE ==================
    private void deleteEmployee() {
        // Get selected employees
        var selected = employeeTable.getSelectionModel().getSelectedItems();

        // Convert to a mutable list if needed
        if (selected == null || selected.size() == 0) {
            showAlert("Select at least one employee to delete.");
            return;
        }


        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected employee(s)?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                for (Employee emp : selected) {
                    PreparedStatement ps = connection.prepareStatement("DELETE FROM employees WHERE emp_id=?");
                    ps.setInt(1, emp.getEmpId());
                    ps.executeUpdate();
                }
                loadEmployees();
            } catch (SQLException e) {
                showAlert(e.getMessage());
            }
        }
    }



    // ================== EXECUTE QUERY ==================
    private void executeQuery() {
        TextInputDialog dialog = new TextInputDialog("SELECT * FROM employees");
        dialog.setTitle("Execute SQL");
        dialog.setHeaderText("Enter your SQL query");

        dialog.showAndWait().ifPresent(query -> {
            try {
                if ("MANAGER".equals(loggedInRole)) {
                    if (!query.toLowerCase().contains("where")) {
                        showAlert("Managers can only query their employees!");
                        return;
                    }
                    query += " AND manager_id=" + loggedInEmpId;
                }
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                employeeTable.getItems().clear();
                while (rs.next()) {
                    Employee emp = new Employee(
                            rs.getInt("emp_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("phone_number"),
                            rs.getString("email"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getInt("years_experience"),
                            rs.getDouble("salary"),
                            rs.getString("department"),
                            rs.getInt("manager_id")
                    );
                    employeeTable.getItems().add(emp);
                }
            } catch (SQLException e) {
                showAlert(e.getMessage());
            }
        });
    }

    // ================== GRAPHS ==================
    // ================== GRAPHS ==================
    private void showGraphs() {
        Stage graphStage = new Stage();
        BorderPane root = new BorderPane();

        try {
            // Gender Pie Chart
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT gender, COUNT(*) as cnt FROM employees GROUP BY gender"
            );
            ResultSet rs = ps.executeQuery();
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Gender Distribution");

            while (rs.next()) {
                String gender = rs.getString("gender");
                int count = rs.getInt("cnt");

                PieChart.Data data = new PieChart.Data(gender, count);
                pieChart.getData().add(data);

                // Add tooltip to show exact count
                Tooltip tooltip = new Tooltip(gender + ": " + count);
                Tooltip.install(data.getNode(), tooltip);

                // Show label alongside slice
                data.nameProperty().bind(
                        javafx.beans.binding.Bindings.concat(data.getName(), " (", data.pieValueProperty().asString("%.0f"), ")")
                );
            }

            // Salary per department Bar Chart
            ps = connection.prepareStatement(
                    "SELECT department, AVG(salary) as avg_salary FROM employees GROUP BY department"
            );
            rs = ps.executeQuery();
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Department");
            yAxis.setLabel("Average Salary");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Average Salary per Department");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("department"), rs.getDouble("avg_salary")));
            }
            barChart.getData().add(series);

            VBox chartsBox = new VBox(20, pieChart, barChart);
            chartsBox.setAlignment(Pos.BOTTOM_RIGHT);
            chartsBox.setPadding(new Insets(10));

            root.setCenter(chartsBox);

        } catch (SQLException e) {
            showAlert(e.getMessage());
        }

        graphStage.setScene(new Scene(root, 800, 700));
        graphStage.setTitle("Graphs");
        graphStage.show();
    }


    // ================== HELPER ==================
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}