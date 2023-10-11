package com.groupone.controller;

import com.groupone.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/database")  // Move common path segment here for better structure
public class DatabaseController {

    @Autowired
    DatabaseService databaseService;

    private final RedirectView defaultRedirection = new RedirectView("/database");

    @GetMapping
    public String databaseIndex(Model model) {
        model.addAttribute("userList", databaseService.getUserTableInfo());
        model.addAttribute("stockList", databaseService.getStockTableInfo());
        return "databaseIndex";
    }

    @GetMapping("/delete/user/{userId}")
    public RedirectView deleteUser(@PathVariable int userId) {
        databaseService.deleteUserRecord(userId);
        return defaultRedirection;
    }

    @GetMapping("/delete/stock/{stockId}")
    public RedirectView deleteStock(@PathVariable int stockId) {
        databaseService.deleteStockRecord(stockId);
        return defaultRedirection;
    }

    @PostMapping("/update")
    public RedirectView updateUser(@ModelAttribute User user) {
        // I simplified the logic here. Ideally, your service layer should handle checks
        if (user != null && user.getID() != null) {
            databaseService.updateUserRecord(user);
        }
        return defaultRedirection;
    }

    @PostMapping("/add/user")
    public RedirectView addUser(@ModelAttribute User user) {
        if (user != null) {
            databaseService.addUserRecord(user.getEmail(), user.getPassword());
        }
        return defaultRedirection;
    }

    @PostMapping("/add/stock")
    public RedirectView addStock(@RequestParam int ownerId, @RequestParam String symbol,
                                 @RequestParam Double volume, @RequestParam Double value) {
        databaseService.addStockRecord(ownerId, symbol, volume, value);
        return defaultRedirection;
    }
}
