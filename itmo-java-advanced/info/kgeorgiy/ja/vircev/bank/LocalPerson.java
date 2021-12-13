package info.kgeorgiy.ja.vircev.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Set;

public class LocalPerson extends AbstractPerson implements Serializable {
    private final Map<String, LocalAccount> accounts;

    public LocalPerson(String firstName, String lastName, String passport, Map<String, LocalAccount> accounts) {
        super(firstName, lastName, passport);
        this.accounts = accounts;
    }

    Account getAccount(String id) {
        return accounts.get(id);
    }

    Set<String> getAccounts() {
        return accounts.keySet();
    }
}
