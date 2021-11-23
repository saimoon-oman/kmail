/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class SendToManyscenecontroller implements Initializable {

    @FXML
    TableView<SendToManyAllAddress> tableViewAllEmails;
    @FXML
    TableColumn<SendToManyAllAddress, CheckBox> tableColumnSelect;
    @FXML
    TableColumn<SendToManyAllAddress, String> tableColumnEmail, tableColumnName;
    @FXML
    TableColumn<SendToManyAllAddress, Integer> tableColumnID;
    @FXML
    CheckBox checkBoxSelectAll;

    ObservableList<SendToManyAllAddress> mailAddresses = FXCollections.observableArrayList();

    String Id, Name, Email, Subject, Content;
    File fileImage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        checkBoxSelectAll.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

                for (SendToManyAllAddress tempAddress : mailAddresses) {

                    if (checkBoxSelectAll.isSelected()) {
                        tempAddress.getSelect().setSelected(true);
                    } else {
                        tempAddress.getSelect().setSelected(false);
                    }

                }
            }

        });

    }

    private void getEmailAddresses() {

        int id;
        String email, name;

        tableColumnSelect.setCellValueFactory(new PropertyValueFactory<>("select"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableViewAllEmails.setItems(mailAddresses);

        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Select ID,Name,Email from accounts_details order by ID");

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                id = rs.getInt(1);
                name = rs.getString(2);
                email = rs.getString(3);
                CheckBox ck = new CheckBox();
                mailAddresses.add(new SendToManyAllAddress(ck, email, id, name));

            }

        } catch (SQLException ex) {
            Logger.getLogger(SendToManyscenecontroller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setData(String id, String name, String email, String subject, String content, File file) {
        Id = id;
        Name = name;
        Email = email;
        Subject = subject;
        Content = content;
        this.fileImage = file;

        getEmailAddresses();

    }

    private void sentMail(String emailTo) {
        String dateAndTime;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        dateAndTime = formatter.format(now);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Insert Into mails values (?,?,?,?,?,?,?,?,?,?)");
            stm.setString(1, Email);
            stm.setString(2, emailTo);
            stm.setString(3, Subject);
            stm.setString(4, Content);
            stm.setString(5, dateAndTime);
            stm.setInt(6, 0);
            stm.setInt(7, 0);
            stm.setInt(8, 0);
            stm.setInt(9, 0);

            if (fileImage != null) {
                FileInputStream fileinputstream;
                try {
                    fileinputstream = new FileInputStream(fileImage);

                    try {
                        stm.setBinaryStream(10, fileinputstream, fileinputstream.available());
                    } catch (IOException ex) {
                        Logger.getLogger(Composescenecontroller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Composescenecontroller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            else {
                stm.setInt(10, 1);
            }
            
            stm.execute();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void buttonSendM(ActionEvent event) {

        int c = 0;

        for (SendToManyAllAddress tempmail : mailAddresses) {

            if (tempmail.getSelect().isSelected()) {
                c++;
                sentMail(tempmail.getEmail());
            }

        }

        if (c == 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Please select atleast one email!");

            alert.showAndWait();
        } else {
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sent mails successfully");
            alert.setHeaderText(c + " mails sent successfully");

            alert.showAndWait();

        }

    }

}
