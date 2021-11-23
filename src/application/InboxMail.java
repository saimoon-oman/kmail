package application;

import java.io.File;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

public class InboxMail {

    public String from, subject, content, dateAndTime;
    public Circle flag;
    public CheckBox check;
    public File fileImage;
    public boolean f;

    public InboxMail() {
    }

    public InboxMail(CheckBox check, Circle flag, String from, String subject, String content, String dateAndTime, boolean f, File fileImage) {
        this.check = check;
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.dateAndTime = dateAndTime;
        this.flag = flag;
        this.fileImage = fileImage;
        this.f = f;
    }

    public boolean isF() {
        return f;
    }

    public void setF(boolean f) {
        this.f = f;
    }

    public File getFileImage() {
        return fileImage;
    }

    public void setFileImage(File fileImage) {
        this.fileImage = fileImage;
    }


    public CheckBox getCheck() {
        return check;
    }

    public void setCheck(CheckBox check) {
        this.check = check;
    }

    public Circle getFlag() {
        return flag;
    }

    public void setFlag(Circle flag) {
        this.flag = flag;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

}
