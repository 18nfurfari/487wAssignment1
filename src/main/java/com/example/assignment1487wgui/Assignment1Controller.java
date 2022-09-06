package com.example.assignment1487wgui;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.transform.Result;
import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.util.ResourceBundle;

public class Assignment1Controller implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField psuIdFilter;

    @FXML
    private TextField entryTimeFilter;

    @FXML
    private TextField departureTimeFilter;

    @FXML
    private Label loginNotification;

    @FXML
    private Button loginButton;

    @FXML
    private Button filterButton;

    @FXML
    private TableView<EntryRecord> tableView;

    @FXML
    private TableColumn<EntryRecord, Integer> actionId;

    @FXML
    private TableColumn<EntryRecord, Integer> psuId;

    @FXML
    private TableColumn<EntryRecord, String> role;

    @FXML
    private TableColumn<EntryRecord, String> entryTime;

    @FXML
    private TableColumn<EntryRecord, String> departureTime;

    @FXML
    private TextField scanTextField;

    @FXML
    private Button scanButton;

    private boolean successfulLogin = false;
    private String user;
    private String pass;
    private String url = "jdbc:mysql://localhost:3306/sunlab_database";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actionId.setCellValueFactory(new PropertyValueFactory<EntryRecord, Integer>("actionId"));
        psuId.setCellValueFactory(new PropertyValueFactory<EntryRecord, Integer>("psuId"));
        role.setCellValueFactory(new PropertyValueFactory<EntryRecord, String>("role"));
        entryTime.setCellValueFactory(new PropertyValueFactory<EntryRecord, String>("entryTime"));
        departureTime.setCellValueFactory(new PropertyValueFactory<EntryRecord, String>("departureTime"));
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws Exception {
        setUser(username.getText());
        setPass(password.getText());   // not secure

        try (Connection conn = DriverManager.getConnection(getUrl(), getUser(), getPass())) {
            loginNotification.setText("Connected to database!");
            setSuccessfulLogin(true);
            Statement state = conn.createStatement();

        } catch (SQLException se) {
            loginNotification.setText("Error: Incorrect Database Credentials");
        }
    }

    @FXML
    protected void onFilterButtonClick(ActionEvent event) throws Exception {
        if (successfulLogin) {
            try (Connection conn = DriverManager.getConnection(getUrl(), getUser(), getPass())) {
                Statement state = conn.createStatement();
                String query = "select * from sunlab_entry_records";
                String filterQuery = "";
                boolean multipleFilters = false;

                if (psuIdFilter.getText().length() == 9) {
                    String psuId = psuIdFilter.getText();
                    filterQuery = filterQuery + " id = " + psuId;
                    multipleFilters = true;
                }

                if (entryTimeFilter.getText().length() == 19 && departureTimeFilter.getText().length() == 19) {
                    if (multipleFilters) {
                        filterQuery += " and ";
                    }
                    String entryTime = entryTimeFilter.getText();
                    String departureTime = departureTimeFilter.getText();

                    filterQuery = filterQuery + " (entry_time between '" + entryTime + "' and '" + departureTime + "'"
                            + "or departure_time between '" + entryTime + "' and '" + departureTime + "')";
                }

                if (filterQuery.length() > 1) {
                    query = query + " where " + filterQuery + ";";
                }
                ResultSet resultSet = state.executeQuery(query);
                ObservableList<EntryRecord> list = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    EntryRecord entryRecord = new EntryRecord(
                            resultSet.getInt("action_id"),
                            resultSet.getInt("id"),
                            "Student",
                            resultSet.getString("entry_time"),
                            resultSet.getString("departure_time")
                    );
                    list.add(entryRecord);
                }

                tableView.setItems(list);
            }
        } else {
            loginNotification.setText("Error: Not logged in to database!");
        }

    }

    @FXML
    protected void onScanButtonClick(ActionEvent event) throws Exception {
        if (successfulLogin) {
            if (scanTextField.getText() != null && scanTextField.getText().length() >= 11) {
                try (Connection conn = DriverManager.getConnection(getUrl(), getUser(), getPass())) {
                    Statement state = conn.createStatement();

                    String input = scanTextField.getText();
                    input = input.substring(2, 11);


                    String query = "select * from sunlab_entry_records where id = " + input + " and departure_time is null";
                    ResultSet resultSet = state.executeQuery(query);

                    if (!resultSet.next()) {
                        query = "INSERT INTO sunlab_entry_records (id, entry_time)" +
                                "VALUES (" + input + ", current_timestamp()  )";
                    } else {
                        query = "update sunlab_entry_records set departure_time = current_timestamp()" +
                                "where id = " + input + " and departure_time is null";
                    }

                    state.executeUpdate(query);
                    loginNotification.setText("Successfully Entered SUNLabs!");

                } catch (SQLException se) {
                    loginNotification.setText("Error: PSU ID not allowed in SUNLabs (not in database)!");
                }
            }
        } else {
            loginNotification.setText("Error: Must be logged into database to scan IDs to database");
        }
    }

    public boolean isSuccessfulLogin() {
        return successfulLogin;
    }

    public void setSuccessfulLogin(boolean successfulLogin) {
        this.successfulLogin = successfulLogin;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUrl() {
        return url;
    }
}