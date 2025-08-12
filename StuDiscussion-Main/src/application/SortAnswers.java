package application;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows users to sort and list answers.
 */
public class SortAnswers {

    private final DatabaseHelper databaseHelper;

    public SortAnswers(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    // Get answers sorted by unread, solution, or date
    public List<Answer> getSortedAnswers(int questionId, String sortBy, int userId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT id, content, user_id, question_id, is_solution FROM Answers WHERE question_id = ? ORDER BY is_solution DESC, date_created DESC";

        try (PreparedStatement pstmt = databaseHelper.connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = databaseHelper.getUserByUserName(rs.getString("username"));
                Question question = databaseHelper.getQuestionById(rs.getInt("question_id"));
                boolean isSolution = rs.getBoolean("is_solution"); // ✅ Ensure this fetches correctly
                System.out.println("DEBUG: Answer ID " + rs.getInt("id") + " isSolution: " + isSolution);

                Answer answer = new Answer(rs.getInt("id"), rs.getString("content"), user, question);
                answer.setSolution(isSolution); // ✅ Save solution status
                answers.add(answer);
            }
        }
        return answers;
    }
}