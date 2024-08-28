package org.example;

import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.Properties;

public class DatabaseConnector {

    private Properties prop;
    private String dbUrl;

    public DatabaseConnector() {
        prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find database.properties");
                return;
            }
            prop.load(input);
            dbUrl = prop.getProperty("db.url");

            // Ensure the data directory exists
            Files.createDirectories(Paths.get("data"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        initializeDatabase();
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "price REAL NOT NULL)";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database.");
            e.printStackTrace();
        }
    }

    public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, prop.getProperty("db.user"), prop.getProperty("db.password"));
            System.out.println("Successfully connected to the database!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
        }
        return connection;
    }

    public void selectAllBooks() {
        String sql = "SELECT * FROM books";
        try (Connection connection = this.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("ID\tTitle\tAuthor\tPrice");
            System.out.println("----------------------------------");
            while (rs.next()) {
                System.out.printf("%d\t%s\t%s\t%.2f%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error executing SELECT statement");
            e.printStackTrace();
        }
    }

    public void insertSampleData() {
        String sql = "INSERT INTO books (title, author, price) VALUES (?, ?, ?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Sample book 1
            pstmt.setString(1, "To Kill a Mockingbird");
            pstmt.setString(2, "Harper Lee");
            pstmt.setDouble(3, 12.99);
            pstmt.executeUpdate();

            // Sample book 2
            pstmt.setString(1, "1984");
            pstmt.setString(2, "George Orwell");
            pstmt.setDouble(3, 10.99);
            pstmt.executeUpdate();

            // Sample book 3
            pstmt.setString(1, "Pride and Prejudice");
            pstmt.setString(2, "Jane Austen");
            pstmt.setDouble(3, 9.99);
            pstmt.executeUpdate();

            System.out.println("Sample data inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting sample data.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector();
        connector.insertSampleData();
        connector.selectAllBooks();
    }
}