package com.example.MarkPriceController.model;

import lombok.Getter;

import java.util.List;

@Getter
public class RSIData {
    private List<String> dates;
    private List<Double> rsiValues;

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public void setRsiValues(List<Double> rsiValues) {
        this.rsiValues = rsiValues;
    }
}