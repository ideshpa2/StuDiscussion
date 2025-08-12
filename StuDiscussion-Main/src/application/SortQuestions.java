package application;

import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SortQuestions {
    private final DatabaseHelper databaseHelper;

    public SortQuestions(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public List<Question> getSortedQuestions(String sortBy, boolean onlyUnresolved, String tagFilter) throws SQLException {
        List<Question> questions = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Questions WHERE 1=1");

        if (onlyUnresolved) {
            query.append(" AND is_resolved = FALSE");
        }
        if (tagFilter != null && !tagFilter.isEmpty()) {
            query.append(" AND tags LIKE ?");
        }
        switch (sortBy) {
            case "date":
                query.append(" ORDER BY date_created DESC");
                break;
            default:
                query.append(" ORDER BY id");
        }

        try (Connection connection = databaseHelper.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query.toString())) {

            if (tagFilter != null && !tagFilter.isEmpty()) {
                pstmt.setString(1, "%" + tagFilter + "%"); // Apply tag filter
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = databaseHelper.getUserById(rs.getInt("user_id"));
                    String tags = rs.getString("tags");
                    boolean isResolved = rs.getBoolean("is_resolved");

                    Question question = new Question(rs.getInt("id"), rs.getString("content"), user, tags);
                    question.setResolved(isResolved);
                    questions.add(question);
                }
            }
        }
        return questions;
    }
}