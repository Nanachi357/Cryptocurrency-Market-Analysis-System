package com.example.MarkPriceController.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "candlestick_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "open_time", "close_time", "open", "close", "high", "low", "volume", "quote_asset_volume", "number_of_trades", "taker_buy_base_asset_volume", "taker_buy_quote_asset_volume", "interval"})
})
public class CandlestickEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "symbol")
    private String symbol;

    @NotNull
    @Column(name = "open_time")
    private LocalDateTime openTime;

    @NotNull
    @Column(name = "close_time")
    private LocalDateTime closeTime;

    @NotNull
    @Column(name = "open")
    private Double open;

    @NotNull
    @Column(name = "close")
    private Double close;

    @NotNull
    @Column(name = "high")
    private Double high;

    @NotNull
    @Column(name = "low")
    private Double low;

    @NotNull
    @Column(name = "volume")
    private Double volume;

    @NotNull
    @Column(name = "quote_asset_volume")
    private Double quoteAssetVolume;

    @NotNull
    @Column(name = "number_of_trades")
    private Long numberOfTrades;

    @NotNull
    @Column(name = "taker_buy_base_asset_volume")
    private Double takerBuyBaseAssetVolume;

    @NotNull
    @Column(name = "taker_buy_quote_asset_volume")
    private Double takerBuyQuoteAssetVolume;

    @NotNull
    @Column(name = "interval")
    private String interval;


    public CandlestickEntity() {
        System.out.println("CandlestickEntity instance created.");
    }

    public CandlestickEntity(@NotNull String symbol, @NotNull LocalDateTime openTime, @NotNull LocalDateTime closeTime, double open, double close, double high, double low, double volume, double quoteAssetVolume, long numberOfTrades, double takerBuyBaseAssetVolume, double takerBuyQuoteAssetVolume, @NotNull String interval) {
        this.symbol = symbol;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.quoteAssetVolume = quoteAssetVolume;
        this.numberOfTrades = numberOfTrades;
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
        this.interval = interval;
    }
    @Override
    public String toString() {
        return "CandlestickEntity{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", quoteAssetVolume=" + quoteAssetVolume +
                ", numberOfTrades=" + numberOfTrades +
                ", takerBuyBaseAssetVolume=" + takerBuyBaseAssetVolume +
                ", takerBuyQuoteAssetVolume=" + takerBuyQuoteAssetVolume +
                ", interval='" + interval + '\'' +
                '}';
    }
}