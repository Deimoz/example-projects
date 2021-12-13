package info.kgeorgiy.ja.vircev.bank;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;

public final class Server {
    private final static int DEFAULT_PORT = 8888;

    public static void main(final String... args) {
        final int port = DEFAULT_PORT;

        final Bank bank = new RemoteBank(port);
        try {
            Registry localReg = LocateRegistry.createRegistry(DEFAULT_PORT);
            UnicastRemoteObject.exportObject(bank, port);
            localReg.rebind("//localhost/bank", bank);
            System.out.println("Server started");
        } catch (final RemoteException e) {
            System.out.println("Cannot export object: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
