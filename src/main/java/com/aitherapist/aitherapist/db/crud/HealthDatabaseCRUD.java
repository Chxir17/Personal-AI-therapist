//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class HealthDatabaseCRUD {
//    private Connection connection;
//
//    public HealthDatabaseCRUD(Connection connection) {
//        this.connection = connection;
//    }
//
//
//    public int createUser(String name, Integer age, Boolean male, String chronicDiseases,
//                          Float height, Float weight, String badHabits) throws SQLException {
//        String query = "INSERT INTO users (name, age, male, chronic_diseases, height, weight, bad_habits) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, name);
//            setNullableInt(stmt, 2, age);
//            setNullableBoolean(stmt, 3, male);
//            stmt.setString(4, chronicDiseases);
//            setNullableFloat(stmt, 5, height);
//            setNullableFloat(stmt, 6, weight);
//            stmt.setString(7, badHabits);
//
//            stmt.executeUpdate();
//            try (ResultSet rs = stmt.getGeneratedKeys()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//            }
//        }
//        return -1;
//    }
//
//    public User getUser(int userId) throws SQLException {
//        String query = "SELECT * FROM users WHERE id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, userId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return new User(
//                            rs.getInt("id"),
//                            rs.getString("name"),
//                            rs.getInt("age"),
//                            rs.getBoolean("male"),
//                            rs.getString("chronic_diseases"),
//                            rs.getFloat("height"),
//                            rs.getFloat("weight"),
//                            rs.getString("bad_habits")
//                    );
//                }
//            }
//        }
//        return null;
//    }
//
//    public List<User> getAllUsers() throws SQLException {
//        List<User> users = new ArrayList<>();
//        String query = "SELECT * FROM users";
//        try (Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                users.add(new User(
//                        rs.getInt("id"),
//                        rs.getString("name"),
//                        rs.getInt("age"),
//                        rs.getBoolean("male"),
//                        rs.getString("chronic_diseases"),
//                        rs.getFloat("height"),
//                        rs.getFloat("weight"),
//                        rs.getString("bad_habits")
//                ));
//            }
//        }
//        return users;
//    }
//
//    public int updateUser(int userId, String name, Integer age, Boolean male, String chronicDiseases,
//                          Float height, Float weight, String badHabits) throws SQLException {
//        StringBuilder query = new StringBuilder("UPDATE users SET ");
//        List<Object> params = new ArrayList<>();
//        boolean hasUpdates = false;
//
//        if (name != null) {
//            query.append("name = ?, ");
//            params.add(name);
//            hasUpdates = true;
//        }
//        if (age != null) {
//            query.append("age = ?, ");
//            params.add(age);
//            hasUpdates = true;
//        }
//        if (male != null) {
//            query.append("male = ?, ");
//            params.add(male);
//            hasUpdates = true;
//        }
//        if (chronicDiseases != null) {
//            query.append("chronic_diseases = ?, ");
//            params.add(chronicDiseases);
//            hasUpdates = true;
//        }
//        if (height != null) {
//            query.append("height = ?, ");
//            params.add(height);
//            hasUpdates = true;
//        }
//        if (weight != null) {
//            query.append("weight = ?, ");
//            params.add(weight);
//            hasUpdates = true;
//        }
//        if (badHabits != null) {
//            query.append("bad_habits = ?, ");
//            params.add(badHabits);
//            hasUpdates = true;
//        }
//
//        if (!hasUpdates) {
//            return 0;
//        }
//
//        // Remove trailing comma and space
//        query.setLength(query.length() - 2);
//        query.append(" WHERE id = ?");
//        params.add(userId);
//
//        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
//            for (int i = 0; i < params.size(); i++) {
//                stmt.setObject(i + 1, params.get(i));
//            }
//            return stmt.executeUpdate();
//        }
//    }
//
//    public int deleteUser(int userId) throws SQLException {
//        String query = "DELETE FROM users WHERE id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, userId);
//            return stmt.executeUpdate();
//        }
//    }
//
//    // Health Data CRUD Operations
//
//    public int createHealthData(int userId, Float bloodOxygenLevel, Float temperature,
//                                Float hoursOfSleepToday, Integer pulse, String pressure,
//                                Float sugar, Boolean heartPain, Boolean archythmia) throws SQLException {
//        String query = "INSERT INTO health_data (user_id, blood_oxygen_level, temperature, " +
//                "hours_of_sleep_today, pulse, pressure, sugar, heart_pain, archythmia) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setInt(1, userId);
//            setNullableFloat(stmt, 2, bloodOxygenLevel);
//            setNullableFloat(stmt, 3, temperature);
//            setNullableFloat(stmt, 4, hoursOfSleepToday);
//            setNullableInt(stmt, 5, pulse);
//            stmt.setString(6, pressure);
//            setNullableFloat(stmt, 7, sugar);
//            setNullableBoolean(stmt, 8, heartPain);
//            setNullableBoolean(stmt, 9, archythmia);
//
//            stmt.executeUpdate();
//            try (ResultSet rs = stmt.getGeneratedKeys()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//            }
//        }
//        return -1;
//    }
//
//    public HealthData getHealthData(int recordId) throws SQLException {
//        String query = "SELECT * FROM health_data WHERE id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, recordId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return new HealthData(
//                            rs.getInt("id"),
//                            rs.getInt("user_id"),
//                            rs.getFloat("blood_oxygen_level"),
//                            rs.getFloat("temperature"),
//                            rs.getFloat("hours_of_sleep_today"),
//                            rs.getInt("pulse"),
//                            rs.getString("pressure"),
//                            rs.getFloat("sugar"),
//                            rs.getBoolean("heart_pain"),
//                            rs.getBoolean("archythmia")
//                    );
//                }
//            }
//        }
//        return null;
//    }
//
//    public List<HealthData> getAllHealthDataForUser(int userId) throws SQLException {
//        List<HealthData> healthDataList = new ArrayList<>();
//        String query = "SELECT * FROM health_data WHERE user_id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, userId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    healthDataList.add(new HealthData(
//                            rs.getInt("id"),
//                            rs.getInt("user_id"),
//                            rs.getFloat("blood_oxygen_level"),
//                            rs.getFloat("temperature"),
//                            rs.getFloat("hours_of_sleep_today"),
//                            rs.getInt("pulse"),
//                            rs.getString("pressure"),
//                            rs.getFloat("sugar"),
//                            rs.getBoolean("heart_pain"),
//                            rs.getBoolean("archythmia")
//                    ));
//                }
//            }
//        }
//        return healthDataList;
//    }
//
//    public HealthData getLatestHealthDataForUser(int userId) throws SQLException {
//        String query = "SELECT * FROM health_data WHERE user_id = ? ORDER BY id DESC LIMIT 1";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, userId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return new HealthData(
//                            rs.getInt("id"),
//                            rs.getInt("user_id"),
//                            rs.getFloat("blood_oxygen_level"),
//                            rs.getFloat("temperature"),
//                            rs.getFloat("hours_of_sleep_today"),
//                            rs.getInt("pulse"),
//                            rs.getString("pressure"),
//                            rs.getFloat("sugar"),
//                            rs.getBoolean("heart_pain"),
//                            rs.getBoolean("archythmia")
//                    );
//                }
//            }
//        }
//        return null;
//    }
//
//    public int updateHealthData(int recordId, Float bloodOxygenLevel, Float temperature,
//                                Float hoursOfSleepToday, Integer pulse, String pressure,
//                                Float sugar, Boolean heartPain, Boolean archythmia) throws SQLException {
//        StringBuilder query = new StringBuilder("UPDATE health_data SET ");
//        List<Object> params = new ArrayList<>();
//        boolean hasUpdates = false;
//
//        if (bloodOxygenLevel != null) {
//            query.append("blood_oxygen_level = ?, ");
//            params.add(bloodOxygenLevel);
//            hasUpdates = true;
//        }
//        if (temperature != null) {
//            query.append("temperature = ?, ");
//            params.add(temperature);
//            hasUpdates = true;
//        }
//        if (hoursOfSleepToday != null) {
//            query.append("hours_of_sleep_today = ?, ");
//            params.add(hoursOfSleepToday);
//            hasUpdates = true;
//        }
//        if (pulse != null) {
//            query.append("pulse = ?, ");
//            params.add(pulse);
//            hasUpdates = true;
//        }
//        if (pressure != null) {
//            query.append("pressure = ?, ");
//            params.add(pressure);
//            hasUpdates = true;
//        }
//        if (sugar != null) {
//            query.append("sugar = ?, ");
//            params.add(sugar);
//            hasUpdates = true;
//        }
//        if (heartPain != null) {
//            query.append("heart_pain = ?, ");
//            params.add(heartPain);
//            hasUpdates = true;
//        }
//        if (archythmia != null) {
//            query.append("archythmia = ?, ");
//            params.add(archythmia);
//            hasUpdates = true;
//        }
//
//        if (!hasUpdates) {
//            return 0;
//        }
//
//        query.setLength(query.length() - 2);
//        query.append(" WHERE id = ?");
//        params.add(recordId);
//
//        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
//            for (int i = 0; i < params.size(); i++) {
//                stmt.setObject(i + 1, params.get(i));
//            }
//            return stmt.executeUpdate();
//        }
//    }
//
//    public int deleteHealthData(int recordId) throws SQLException {
//        String query = "DELETE FROM health_data WHERE id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, recordId);
//            return stmt.executeUpdate();
//        }
//    }
//
//    // Helper methods for handling nullable values
//    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
//        if (value != null) {
//            stmt.setInt(index, value);
//        } else {
//            stmt.setNull(index, Types.INTEGER);
//        }
//    }
//
//    private void setNullableFloat(PreparedStatement stmt, int index, Float value) throws SQLException {
//        if (value != null) {
//            stmt.setFloat(index, value);
//        } else {
//            stmt.setNull(index, Types.FLOAT);
//        }
//    }
//
//    private void setNullableBoolean(PreparedStatement stmt, int index, Boolean value) throws SQLException {
//        if (value != null) {
//            stmt.setBoolean(index, value);
//        } else {
//            stmt.setNull(index, Types.BOOLEAN);
//        }
//    }
//
//    // Model classes
//    public static class User {
//        private int id;
//        private String name;
//        private Integer age;
//        private Boolean male;
//        private String chronicDiseases;
//        private Float height;
//        private Float weight;
//        private String badHabits;
//
//        public User(int id, String name, Integer age, Boolean male, String chronicDiseases,
//                    Float height, Float weight, String badHabits) {
//            this.id = id;
//            this.name = name;
//            this.age = age;
//            this.male = male;
//            this.chronicDiseases = chronicDiseases;
//            this.height = height;
//            this.weight = weight;
//            this.badHabits = badHabits;
//        }
//
//        // Getters and setters would go here
//    }
//
//    public static class HealthData {
//        private int id;
//        private int userId;
//        private Float bloodOxygenLevel;
//        private Float temperature;
//        private Float hoursOfSleepToday;
//        private Integer pulse;
//        private String pressure;
//        private Float sugar;
//        private Boolean heartPain;
//        private Boolean archythmia;
//
//        public HealthData(int id, int userId, Float bloodOxygenLevel, Float temperature,
//                          Float hoursOfSleepToday, Integer pulse, String pressure,
//                          Float sugar, Boolean heartPain, Boolean archythmia) {
//            this.id = id;
//            this.userId = userId;
//            this.bloodOxygenLevel = bloodOxygenLevel;
//            this.temperature = temperature;
//            this.hoursOfSleepToday = hoursOfSleepToday;
//            this.pulse = pulse;
//            this.pressure = pressure;
//            this.sugar = sugar;
//            this.heartPain = heartPain;
//            this.archythmia = archythmia;
//        }
//
//        // Getters and setters would go here
//    }
//}