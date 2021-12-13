package info.kgeorgiy.ja.vircev.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Objects;

public final class Client {
    /** Utility class. */
    private Client() {}
    private final static int DEFAULT_PORT = 8888;

    public static void main(final String[] args) throws RemoteException {
        final Bank bank;
        try {
            Registry registry = LocateRegistry.getRegistry(DEFAULT_PORT);
            bank = (Bank) registry.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        }

        if (args == null || args.length != 5 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Arguments should be: [firstName] [lastName] [passport] [account] [amount]");
            return;
        }

        String firstName = args[0];
        String lastName = args[1];
        String passport = args[2];
        String id = args[3];
        int amount = Integer.parseInt(args[4]);

        try {
            Person person = bank.getRemotePerson(passport);
            if (person == null) {
                bank.createPerson(passport, firstName, lastName);
                bank.createAccount(id, passport);
            } else {
                if (bank.getAccount(id, passport) == null) {
                    bank.createAccount(id, passport);
                }
            }

            Account account = bank.getAccount(id, passport);
            System.out.println("Account of " + firstName + " " + lastName + ":");
            System.out.println("ID: " + account.getId());
            System.out.println("Current amount on balance: " + account.getAmount());

            bank.changeAmount(id, passport, amount);

            System.out.println("Current amount on balance after recalculation: " + account.getAmount());

        } catch (RemoteException e) {
            System.err.println("Remote exception " + e.getMessage());
        }
    }
}
