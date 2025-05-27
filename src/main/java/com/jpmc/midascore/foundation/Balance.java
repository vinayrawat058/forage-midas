package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Balance {
    private float balance;

    public Balance() {}

    public Balance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Balance{%.2f}", balance);
    }

    public float getBalance() {
        return balance;
    }
}
