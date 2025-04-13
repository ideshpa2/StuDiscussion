package application;

import java.sql.SQLException;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class ViewReviewUpdatesPage {
    private DatabaseHelper databaseHelper;

    public ViewReviewUpdatesPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("New Review Updates");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        VBox updatesContainer = new VBox(10);

        try {
            List<ReviewUpdate> updates = databaseHelper.getUnviewedReviewUpdates(user.getId());

            if (updates.isEmpty()) {
                updatesContainer.getChildren().add(new Label("No new updates."));
            } else {
                for (ReviewUpdate update : updates) {
                    HBox updateBox = new HBox(10);
                    
                    Label updateLabel = new Label(update.getReviewerName() + " updated a review: " + update.getReviewText());
                    Button markViewedButton = new Button("Mark Viewed");

                    markViewedButton.setOnAction(e -> {
                        try {
                            databaseHelper.markReviewUpdateAsViewed(update.getId());
                            new ViewReviewUpdatesPage(databaseHelper).show(primaryStage, user);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });

                    updateBox.getChildren().addAll(updateLabel, markViewedButton);
                    updatesContainer.getChildren().add(updateBox);
                }
            }

            Button backButton = new Button("Back");
            backButton.setOnAction(e -> new showTrustedReviewersPage(databaseHelper).show(primaryStage, user));

            layout.getChildren().addAll(titleLabel, updatesContainer, backButton);
            primaryStage.setScene(new Scene(layout, 800, 400));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
