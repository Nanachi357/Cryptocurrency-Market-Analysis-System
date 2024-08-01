package com.example.CryptocurrencyMarketAnalysisSystem.util;

import com.binance.api.client.domain.market.Candlestick;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CandlestickWrapperTest {
    private Candlestick createTestCandlestick() {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1688158800000L);
        candlestick.setCloseTime(1688162399999L);
        candlestick.setOpen("1.0");
        candlestick.setClose("2.0");
        candlestick.setHigh("3.0");
        candlestick.setLow("0.5");
        candlestick.setVolume("1000");
        candlestick.setQuoteAssetVolume("2000");
        candlestick.setNumberOfTrades(500L);
        candlestick.setTakerBuyBaseAssetVolume("700");
        candlestick.setTakerBuyQuoteAssetVolume("1400");
        return candlestick;
    }

    //Positive Tests

    @Test
    public void testEquals_SameObject() {
        Candlestick candlestick = createTestCandlestick();
        CandlestickWrapper wrapper = new CandlestickWrapper(candlestick);

        assertThat(wrapper).isEqualTo(wrapper);
    }

    @Test
    public void testEquals_EqualObjects() {
        Candlestick candlestick1 = createTestCandlestick();
        Candlestick candlestick2 = createTestCandlestick();
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1).isEqualTo(wrapper2);
    }

    @Test
    public void testNotEquals_DifferentOpenTime() {
        Candlestick candlestick1 = createTestCandlestick();
        Candlestick candlestick2 = createTestCandlestick();
        candlestick2.setOpenTime(candlestick2.getOpenTime() + 1);
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1).isNotEqualTo(wrapper2);
    }

    @Test
    public void testHashCode_EqualObjects() {
        Candlestick candlestick1 = createTestCandlestick();
        Candlestick candlestick2 = createTestCandlestick();
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1.hashCode()).isEqualTo(wrapper2.hashCode());
    }

    @Test
    public void testHashCode_DifferentObjects() {
        Candlestick candlestick1 = createTestCandlestick();
        Candlestick candlestick2 = createTestCandlestick();
        candlestick2.setOpenTime(candlestick2.getOpenTime() + 1);
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1.hashCode()).isNotEqualTo(wrapper2.hashCode());
    }

    @Test
    public void testGetCandlestick() {
        Candlestick candlestick = createTestCandlestick();
        CandlestickWrapper wrapper = new CandlestickWrapper(candlestick);

        assertThat(wrapper.getCandlestick()).isEqualTo(candlestick);
    }

    //Negative Tests

    @Test
    public void testEquals_NullObject() {
        Candlestick candlestick = createTestCandlestick();
        CandlestickWrapper wrapper = new CandlestickWrapper(candlestick);

        assertThat(wrapper).isNotEqualTo(null);
    }

    @Test
    public void testEquals_DifferentClass() {
        Candlestick candlestick = createTestCandlestick();
        CandlestickWrapper wrapper = new CandlestickWrapper(candlestick);

        assertThat(wrapper).isNotEqualTo(new Object());
    }

    @Test
    public void testHashCode_DifferentCandlesticks() {
        Candlestick candlestick1 = createTestCandlestick();
        Candlestick candlestick2 = createTestCandlestick();
        candlestick2.setOpen("1.1");
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1.hashCode()).isNotEqualTo(wrapper2.hashCode());
    }

    @Test
    public void testConstructor_NullCandlestick() {
        assertThatThrownBy(() -> new CandlestickWrapper(null))
                .isInstanceOf(NullPointerException.class);
    }

    // Boundary Tests

    @Test
    public void testEquals_BoundaryOpenTime() {
        Candlestick candlestick1 = createTestCandlestickWithOpenTime();
        Candlestick candlestick2 = createTestCandlestickWithOpenTime();
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1).isEqualTo(wrapper2);
    }

    @Test
    public void testEquals_BoundaryCloseTime() {
        Candlestick candlestick1 = createTestCandlestickWithCloseTime();
        Candlestick candlestick2 = createTestCandlestickWithCloseTime();
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1).isEqualTo(wrapper2);
    }

    @Test
    public void testHashCode_BoundaryOpenTime() {
        Candlestick candlestick1 = createTestCandlestickWithOpenTime();
        Candlestick candlestick2 = createTestCandlestickWithOpenTime();
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1.hashCode()).isEqualTo(wrapper2.hashCode());
    }

    @Test
    public void testHashCode_BoundaryCloseTime() {
        Candlestick candlestick1 = createTestCandlestickWithCloseTime();
        Candlestick candlestick2 = createTestCandlestickWithCloseTime();
        CandlestickWrapper wrapper1 = new CandlestickWrapper(candlestick1);
        CandlestickWrapper wrapper2 = new CandlestickWrapper(candlestick2);

        assertThat(wrapper1.hashCode()).isEqualTo(wrapper2.hashCode());
    }


    private Candlestick createTestCandlestickWithOpenTime() {
        return createTestCandlestickWithOpenTimeAndCloseTime(Long.MIN_VALUE, 1688162399999L);
    }

    private Candlestick createTestCandlestickWithCloseTime() {
        return createTestCandlestickWithOpenTimeAndCloseTime(1688158800000L, Long.MAX_VALUE);
    }

    private Candlestick createTestCandlestickWithOpenTimeAndCloseTime(long openTime, long closeTime) {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(openTime);
        candlestick.setCloseTime(closeTime);
        candlestick.setOpen("1.0");
        candlestick.setClose("2.0");
        candlestick.setHigh("3.0");
        candlestick.setLow("0.5");
        candlestick.setVolume("1000");
        candlestick.setQuoteAssetVolume("2000");
        candlestick.setNumberOfTrades(500L);
        candlestick.setTakerBuyBaseAssetVolume("700");
        candlestick.setTakerBuyQuoteAssetVolume("1400");
        return candlestick;
    }
}
