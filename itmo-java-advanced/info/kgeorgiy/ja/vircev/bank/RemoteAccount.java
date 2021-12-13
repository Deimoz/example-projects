package info.kgeorgiy.ja.vircev.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteAccount extends AbstractAccount {
    public RemoteAccount(final String id, int port) throws RemoteException {
        super(id, 0);
        UnicastRemoteObject.exportObject(this, port);
    }
}
