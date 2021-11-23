package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class SentMailscenecontroller implements Initializable {

    @FXML
    TableView<InboxMail> tableViewInboxTable;
    @FXML
    TableColumn<InboxMail, String> tableColumnTo, tableColumnSubject, tableColumnContent, tableColumnDateAndTime;
    @FXML
    TableColumn<InboxMail, Circle> tableColumnIsRead;
    @FXML
    TableColumn<InboxMail, CheckBox> tableColumnSelect;
    @FXML
    CheckBox checkBoxSelectAll;

    private Stage stage;
    private Scene scene;
    private Parent root;
    
    File fileImage;

    ObservableList<InboxMail> mails = FXCollections.observableArrayList();

    private String Id, Name, Email;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        checkBoxSelectAll.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

                for (InboxMail mail : mails) {

                    if (checkBoxSelectAll.isSelected()) {
                        mail.getCheck().setSelected(true);
                    } else {
                        mail.getCheck().setSelected(false);
                    }

                }
            }

        });

    }

    private void sentEmails() throws FileNotFoundException {

        tableViewInboxTable.getItems().clear();

        tableColumnSelect.setCellValueFactory(new PropertyValueFactory<>("check"));
        tableColumnIsRead.setCellValueFactory(new PropertyValueFactory<>("flag"));
        tableColumnTo.setCellValueFactory(new PropertyValueFactory<>("from"));
        tableColumnSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        tableColumnContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        tableColumnDateAndTime.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));

        tableViewInboxTable.setItems(mails);

        String to, subject, content, dateAndTime;
        int sentMailFlag, sentMailDeleteFlag;

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Select ToAddress,Subject,Content,DateAndTime,SentMailFlag,SentMailDeleteFlag,Image from mails where FromAddress=? order by DateAndTime desc");
            stm.setString(1, Email);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                to = rs.getString(1);
                subject = rs.getString(2);
                content = rs.getString(3);
                dateAndTime = rs.getString(4);
                sentMailFlag = rs.getInt(5);
                sentMailDeleteFlag = rs.getInt(6);

                boolean f = false;

                File file = new File("C:\\Users\\USER\\Documents\\NetBeansProjects\\Hello\\src\\application\\temp.jpg");
                
                FileInputStream inputstream = new FileInputStream("C:\\Users\\USER\\Documents\\NetBeansProjects\\Hello\\src\\application\\image.png");
                Image image = new Image(inputstream);
                BufferedImage im = null;
                try {
                    im = ImageIO.read(rs.getBinaryStream(7));
                    if (im == null) {
                        System.out.println("Helloldsfjlsdkjfs");

                    } else {
                        file = new File("C:\\Users\\USER\\Documents\\NetBeansProjects\\Hello\\src\\application\\temp.jpg");
                        image = SwingFXUtils.toFXImage(im, null);
                        ImageIO.write(im, "jpg", file);
                        f = true;
                    }
                    
                } catch (IOException ex) {
                    //Logger.getLogger(quiz.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error");
                }

//                Blob blob = rs.getBlob(7);
//                
//                System.out.println(blob.toString());
//                InputStream inputstream = blob.getBinaryStream();
//
//                Image image = new Image(inputstream);
                Circle c1 = new Circle();
                c1.setCenterX(0);
                c1.setCenterY(0);
                c1.setRadius(5);

                CheckBox ck = new CheckBox();

                if (sentMailFlag != 0) {
                    c1.setVisible(false);
                }
                
                if (sentMailDeleteFlag == 0) {
                    mails.add(new InboxMail(ck, c1, to, subject, content, dateAndTime, f, file));

                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void mailDetails(InboxMail mail) throws IOException, SQLException {
        
        selectImage(mail);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MailDetailsSentMail.fxml"));

        root = loader.load();

        MailDetailsSentMailscenecontroller maildetailssentmailscenecontroller = loader.getController();
        try {
            maildetailssentmailscenecontroller.setData(Id, Name, Email, mail);
        } catch (SQLException ex) {
            Logger.getLogger(Inboxscenecontroller.class.getName()).log(Level.SEVERE, null, ex);
        }

        scene = new Scene(root);

        stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void updateFlag(InboxMail mail) {
        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Update mails set SentMailFlag=? where (FromAddress=? and ToAddress=? and Subject=? and Content=? and DateAndTime=?)");
            stm.setInt(1, 1);
            stm.setString(2, Email);
            stm.setString(3, mail.getFrom());
            stm.setString(4, mail.getSubject());
            stm.setString(5, mail.getContent());
            stm.setString(6, mail.getDateAndTime());

            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Inboxscenecontroller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectImage(InboxMail mail) throws SQLException, FileNotFoundException {
        String emailTo, subject, content, dateAndTime;

        emailTo = mail.getFrom();
        subject = mail.getSubject();
        content = mail.getContent();
        dateAndTime = mail.getDateAndTime();

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

        PreparedStatement stm = con.prepareStatement("Select Image from mails where (FromAddress=? and ToAddress=? and Subject=? and Content=? and DateAndTime=?)");
        stm.setString(1, Email);
        stm.setString(2, emailTo);
        stm.setString(3, subject);
        stm.setString(4, content);
        stm.setString(5, dateAndTime);

        ResultSet rs = stm.executeQuery();

        if (rs.next()) {
            FileInputStream inputstream = new FileInputStream("C:\\Users\\USER\\Documents\\NetBeansProjects\\Hello\\src\\application\\image.png");
            Image image = new Image(inputstream);
            BufferedImage im = null;
            try {
                im = ImageIO.read(rs.getBinaryStream(1));
                if (im == null) {
                    fileImage = new File("");
                } else {
                    fileImage = new File("C:\\Users\\USER\\Documents\\NetBeansProjects\\Hello\\src\\application\\temp.jpg");
                    image = SwingFXUtils.toFXImage(im, null);
                    ImageIO.write(im, "jpg", fileImage);
                }
            } catch (IOException ex) {
                //Logger.getLogger(InboxMail.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
            }
        }

    }
    
    public void setData(String id, String name, String email) throws FileNotFoundException {
        Id = id;
        Name = name;
        Email = email;

        System.out.println(Id + " " + Name + " " + Email);

        sentEmails();

        tableViewInboxTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends InboxMail> ov, InboxMail t, InboxMail t1) -> {
            InboxMail mail = tableViewInboxTable.getSelectionModel().getSelectedItem();

            updateFlag(mail);

            try {
                try {
                    mailDetails(mail);
                } catch (SQLException ex) {
                    Logger.getLogger(SentMailscenecontroller.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                Logger.getLogger(Inboxscenecontroller.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void buttonLogOutM(ActionEvent event) {
        Welcomescenecontroller welcomescenecontroller = new Welcomescenecontroller();
        welcomescenecontroller.buttonLogOutM(event);
    }

    public void buttonReloadM(ActionEvent event) throws FileNotFoundException {
        sentEmails();
        checkBoxSelectAll.setSelected(false);
    }

    public void updateDeleteFlag(InboxMail mail) {
        Connection con;

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
            PreparedStatement stm = con.prepareStatement("Update mails set SentMailDeleteFlag=? where (FromAddress=? and ToAddress=? and Subject=? and Content=? and DateAndTime=?)");
            stm.setInt(1, 1);
            stm.setString(2, Email);
            stm.setString(3, mail.getFrom());
            stm.setString(4, mail.getSubject());
            stm.setString(5, mail.getContent());
            stm.setString(6, mail.getDateAndTime());

            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Inboxscenecontroller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void buttonDeleteM(ActionEvent event) {

        int c = 0;

        ObservableList<InboxMail> tempmails = FXCollections.observableArrayList();

        for (InboxMail mail : mails) {
            if (mail.getCheck().isSelected()) {
                c++;
                tempmails.add(mail);
                updateDeleteFlag(mail);
            }
        }

        if (c == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Please select atleast one email!");

            alert.showAndWait();
        } else {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete emails");
            alert.setHeaderText(c + " emails will be deleted!");
            alert.setContentText("Are you ok with this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                mails.removeAll(tempmails);

                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Deleted Successfully");
                alert.setHeaderText(c + " emails deleted successfully");

                alert.showAndWait();
            }

        }

        checkBoxSelectAll.setSelected(false);

    }

    public void buttonBackM(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));

            root = loader.load();

            Welcomescenecontroller welcomescenecontroller = loader.getController();
            welcomescenecontroller.setData(Id, Email, Name);
            welcomescenecontroller.showNameAndEmail();

            scene = new Scene(root);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
