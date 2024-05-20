package com.example.MarkPriceController.controller;

import com.binance.api.client.domain.market.OrderBook;
import com.example.MarkPriceController.service.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderBookController {

    private final OrderBookService orderBookService;

    @Autowired
    public OrderBookController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    // Handles requests for order book data
    @GetMapping("/orderBook")
    public String getOrderBook(@RequestParam String symbol,
                               @RequestParam(defaultValue = "100") int limit,
                               Model model) {
        OrderBook orderBook = orderBookService.getOrderBook(symbol, limit);
        model.addAttribute("symbol", symbol);
        model.addAttribute("limit", limit);
        model.addAttribute("orderBook", orderBook);
        return "orderBook"; // Назва шаблону, який відображає дані про orderBook
    }
}