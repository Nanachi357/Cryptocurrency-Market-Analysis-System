package com.example.MarkPriceController.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RSIData {
    private List<String> dates;
    private List<Double> rsiValues;

    public RSIData() {
        dates = new ArrayList<>();
        rsiValues = new ArrayList<>();
    }
}