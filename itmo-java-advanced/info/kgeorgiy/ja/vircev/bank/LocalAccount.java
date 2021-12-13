package info.kgeorgiy.ja.vircev.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LocalAccount extends AbstractAccount implements Serializable {
    public LocalAccount(final String id, int amount) {
        super(id, amount);
    }
}
