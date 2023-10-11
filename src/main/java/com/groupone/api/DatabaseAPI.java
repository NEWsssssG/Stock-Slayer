package com.groupone.api;

import com.groupone.model.Stock;
import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseAPI implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void initDatabase() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY, email STRING NOT NULL, password STRING NOT NULL, availableFunds DOUBLE, isLocked BOOLEAN);");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS stock(id INTEGER PRIMARY KEY, ownerId INTEGER NOT NULL REFERENCES user (id), symbol STRING NOT NULL, volume DOUBLE, value DOUBLE, FOREIGN KEY (ownerId) REFERENCES user (id));");

        List<User> users = queryForUsers("SELECT * FROM user");
        List<Stock> stocks = queryForStocks("SELECT * FROM stock");
        pairUsersToStocks(users, stocks);

        if (!users.isEmpty() || !stocks.isEmpty()) {
            users.forEach(System.out::println);
            stocks.forEach(System.out::println);
        } else {
            System.out.println("No Records to Display");
        }
    }

    public void addUserRecord(String email, String password) throws Exception {
        if (getUserByEmail(email) != null) throw new Exception("User already in database");
        jdbcTemplate.update("INSERT INTO user (email, password) VALUES (?, ?)", email, password);
        System.out.printf("Added User: %s%n", email);
    }

    public void addUserRecord(User user) throws Exception {
        addUserRecord(user.getEmail(), user.getPassword());
    }

    public User getUserByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM user WHERE email = ?", new Object[]{email}, (resultSet, rowNum) -> new User(
                    resultSet.getInt("id"),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getBoolean("isLocked"),
                    resultSet.getDouble("availableFunds")));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void deleteUserRecord(int userId) {
        final String deleteSql = "DELETE FROM user WHERE id=?";
        jdbcTemplate.update(deleteSql, userId);
        deleteUserStocks(userId);
        System.out.printf("Deleted User: %s%n", userId);
    }

    public void updateUserRecord(User oldUser, User newUser) {
        final String updateSql = "UPDATE user SET email = ?, password = ?, isLocked = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, newUser.getEmail(), newUser.getPassword(), newUser.getIsLocked(), oldUser.getId());
        System.out.printf("Updated User: %1$s %n%2$s %n", oldUser.getId(), newUser);
    }

    public List<User> pairUsersToStocks(List<User> users, List<Stock> stocks) {
        users.forEach(user -> stocks.stream().filter(stock -> user.getId() == stock.getOwnerId()).forEach(user::addStock));
        return users;
    }

    public void addStockRecord(int ownerId, String symbol, double volume, double value) throws Exception {
        if (getUserById(ownerId) == null) throw new Exception("Not Valid Owner ID!");
        jdbcTemplate.update("INSERT INTO stock (ownerId, symbol, volume, value) VALUES (?, ?, ?, ?)", ownerId, symbol, volume, value);
        System.out.printf("Added Stock to user: %s%n", ownerId);
    }

    public void addStockRecord(Stock stock) throws Exception {
        addStockRecord(stock.getOwnerId(), stock.getSymbol(), stock.getVolume(), stock.getValue());
    }

    public void deleteStockRecord(int stockId) {
        jdbcTemplate.update("DELETE FROM stock WHERE id = ?", stockId);
        System.out.printf("Deleted Stock w/ id: %s%n", stockId);
    }

    public List<User> getUserTableInfo() {
        return queryForUsers("SELECT * FROM user");
    }

    public List<Stock> getStockTableInfo() {
        return queryForStocks("SELECT * FROM stock");
    }

    public void addAvailableFunds(User user, double funds) {
        User userRecord = getUserByEmail(user.getEmail());
        userRecord.addFunds(funds);
        jdbcTemplate.update("UPDATE user SET availableFunds = ? WHERE id = ?", userRecord.getAvailableFunds(), userRecord.getId());
        System.out.printf("Set availableFunds %1$s to user %2$s", userRecord.getAvailableFunds(), user.getEmail());
    }

    public void subtractAvailableFunds(User user, double funds) {
        User userRecord = getUserByEmail(user.getEmail());
        userRecord.subtractFunds(funds);
        jdbcTemplate.update("UPDATE user SET availableFunds = ? WHERE id = ?", userRecord.getAvailableFunds(), userRecord.getId());
        System.out.printf("Set availableFunds %1$s to user %2$s", userRecord.getAvailableFunds(), user.getEmail());
    }

    public void toggleUserLocked(User user) {
        User userRecord = getUserByEmail(user.getEmail());
        userRecord.toggleLock();
        jdbcTemplate.update("UPDATE user SET isLocked = ? WHERE id = ?", userRecord.getIsLocked(), userRecord.getId());
        System.out.printf("isLocked set to: %1$s user account: %2$s", userRecord.getIsLocked(), userRecord.getEmail());
    }

    private User getUserById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM user WHERE id = ?", new Object[]{id}, (resultSet, rowNum) -> new User(
                    resultSet.getInt("id"),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getBoolean("isLocked"),
                    resultSet.getDouble("availableFunds")));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void deleteUserStocks(int userId) {
        jdbcTemplate.update("DELETE FROM stock WHERE ownerId = ?", userId);
        System.out.printf("Deleted stocks owned by User ID: %s%n", userId);
    }

    private List<User> queryForUsers(String query) {
        return jdbcTemplate.query(query, (resultSet, rowNum) -> new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getBoolean("isLocked"),
                resultSet.getDouble("availableFunds")));
    }

    private List<Stock> queryForStocks(String query) {
        return jdbcTemplate.query(query, (resultSet, rowNum) -> new Stock(
                resultSet.getInt("id"),
                resultSet.getInt("ownerId"),
                resultSet.getString("symbol"),
                resultSet.getDouble("volume"),
                resultSet.getDouble("value")));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printDatabaseAPIStart() {
        System.out.println("Database API Running...");
    }

    @Override
    public void run(String... args) throws Exception {
        // logic for CommandLineRunner (if any is intended)
    }
}
            
