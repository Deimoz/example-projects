package info.kgeorgiy.ja.vircev.bank;

import java.rmi.RemoteException;

public class AbstractAccount implements Account{
    private final String id;
    private int amount;

    public AbstractAccount(final String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized int getAmount() {
        return amount;
    }

    @Override
    public synchronized void setAmount(final int amount) {
        this.amount = amount;
    }
}
