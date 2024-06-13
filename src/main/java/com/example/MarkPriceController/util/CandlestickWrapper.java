package com.example.MarkPriceController.util;

import com.binance.api.client.domain.market.Candlestick;

import java.util.Objects;

public record CandlestickWrapper(Candlestick candlestick) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandlestickWrapper that = (CandlestickWrapper) o;
        return Objects.equals(candlestick.getOpenTime(), that.candlestick.getOpenTime()) &&
                Objects.equals(candlestick.getCloseTime(), that.candlestick.getCloseTime()) &&
                Objects.equals(candlestick.getOpen(), that.candlestick.getOpen()) &&
                Objects.equals(candlestick.getClose(), that.candlestick.getClose()) &&
                Objects.equals(candlestick.getHigh(), that.candlestick.getHigh()) &&
                Objects.equals(candlestick.getLow(), that.candlestick.getLow()) &&
                Objects.equals(candlestick.getVolume(), that.candlestick.getVolume()) &&
                Objects.equals(candlestick.getQuoteAssetVolume(), that.candlestick.getQuoteAssetVolume()) &&
                Objects.equals(candlestick.getNumberOfTrades(), that.candlestick.getNumberOfTrades()) &&
                Objects.equals(candlestick.getTakerBuyBaseAssetVolume(), that.candlestick.getTakerBuyBaseAssetVolume()) &&
                Objects.equals(candlestick.getTakerBuyQuoteAssetVolume(), that.candlestick.getTakerBuyQuoteAssetVolume());
    }

    @Override
    public int hashCode() {
        return Objects.hash(candlestick.getOpenTime(), candlestick.getCloseTime(), candlestick.getOpen(), candlestick.getClose(),
                candlestick.getHigh(), candlestick.getLow(), candlestick.getVolume(), candlestick.getQuoteAssetVolume(),
                candlestick.getNumberOfTrades(), candlestick.getTakerBuyBaseAssetVolume(), candlestick.getTakerBuyQuoteAssetVolume());
    }
}