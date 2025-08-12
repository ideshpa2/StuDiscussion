package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ChangePasswordPage {
    private final DatabaseHelper databaseHelper;
    private final String userName;

    public ChangePasswordPage(DatabaseHelper databaseHelper, String userName) {
        this.databaseHelper = databaseHelper;
        this.userName = userName;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox();
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("Verify OTP");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");

        Button verifyOTPButton = new Button("Verify OTP");
        Label otpResultLabel = new Label();

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter New Password");
        newPasswordField.setVisible(false); // Initially hidden

        Button savePasswordButton = new Button("Save Password");
        savePasswordButton.setVisible(false); // Initially hidden

        Label resultLabel = new Label();

        verifyOTPButton.setOnAction(e -> {
            String enteredOTP = otpField.getText();

            try {
                String storedOTP = databaseHelper.getOneTimePassword(userName); // Get OTP from DB
                if (enteredOTP.equals(storedOTP)) {
                    otpResultLabel.setText("OTP Verified! Enter your new password.");
                    newPasswordField.setVisible(true);
                    savePasswordButton.setVisible(true);
                    otpField.setDisable(true);
                    verifyOTPButton.setDisable(true);
                } else {
                    otpResultLabel.setText("Invalid OTP. Please try again.");
                }
            } catch (SQLException ex) {
                otpResultLabel.setText("Error verifying OTP.");
                ex.printStackTrace();
            }
        });

        savePasswordButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();

            if (newPassword.isEmpty()) {
                resultLabel.setText("Password cannot be empty.");
                return;
            }

            try {
                databaseHelper.updateUserPassword(userName, newPassword);
                resultLabel.setText("Password updated! Please log in again.");
                new UserLoginPage(databaseHelper).show(primaryStage);
            } catch (SQLException ex) {
                resultLabel.setText("Error updating password.");
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(titleLabel, otpField, verifyOTPButton, otpResultLabel, newPasswordField, savePasswordButton, resultLabel);
        Scene changePasswordScene = new Scene(layout, 800, 400);
        primaryStage.setScene(changePasswordScene);
        primaryStage.setTitle("Change Password");
    }
}