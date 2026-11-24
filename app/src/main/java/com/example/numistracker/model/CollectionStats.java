package com.example.numistracker.model;

public final class CollectionStats {

    private final int coinTypes;
    private final int banknoteTypes;

    public CollectionStats(int coinTypes, int banknoteTypes) {
        if (coinTypes < 0 || banknoteTypes < 0) {
            throw new IllegalArgumentException(
                    "Type counts cannot be negative");
        }

        this.coinTypes = coinTypes;
        this.banknoteTypes = banknoteTypes;
    }

    public int getCoinTypes() {
        return coinTypes;
    }

    public int getBanknoteTypes() {
        return banknoteTypes;
    }

    public int getTotalTypes() {
        return coinTypes + banknoteTypes;
    }

    public double getCoinPercentage() {
        return getTotalTypes() == 0
                ? 0.0
                : 100.0 * coinTypes / getTotalTypes();
    }

    public double getBanknotePercentage() {
        return getTotalTypes() == 0
                ? 0.0
                : 100.0 * banknoteTypes / getTotalTypes();
    }
}