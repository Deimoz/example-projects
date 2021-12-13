package info.kgeorgiy.ja.vircev.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Person> persons = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> passportsAccountIds = new ConcurrentHashMap<>();

    public RemoteBank(final int port) {
        this.port = port;
    }

    @Override
    public boolean createAccount(String subId, String passport) throws RemoteException {
        final String id = getId(subId, passport);
        final Account account = new RemoteAccount(id, port);
        if (accounts.putIfAbsent(id, account) == null) {
            if (passportsAccountIds.get(passport) == null) {
                passportsAccountIds.put(passport, new ConcurrentSkipListSet<>());
            }
            passportsAccountIds.get(passport).add(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Account getAccount(String subId, String passport) throws RemoteException {
        return accounts.get(getId(subId, passport));
    }

    @Override
    public LocalPerson getLocalPerson(String passport) throws RemoteException {
        if (passport == null) {
            System.err.println("Can't find LocalPerson with null arguments");
            return null;
        }
        Person person = getRemotePerson(passport);
        Map<String, LocalAccount> localAccounts = new HashMap<>();
        if (passportsAccountIds.containsKey(passport)) {
            passportsAccountIds.get(passport).forEach((id) -> {
                try {
                    localAccounts.put(id, new LocalAccount(id, accounts.get(id).getAmount()));
                } catch (RemoteException e) {
                    System.err.println("Can't create local account: " + e.getMessage());
                }
            });
        }
        return new LocalPerson(
                person.getFirstName(),
                person.getLastName(),
                person.getPassport(),
                localAccounts);
    }

    @Override
    public Person getRemotePerson(String passport) throws RemoteException {
        if (passport == null) {
            System.err.println("Can't find RemotePerson with null arguments");
            return null;
        }
        return persons.get(passport);
    }

    @Override
    public Person searchPerson(String passport, TypeOfPerson typeOfPerson) throws RemoteException {
        return typeOfPerson == TypeOfPerson.LOCAL ? getLocalPerson(passport) : getRemotePerson(passport);
    }

    @Override
    public boolean createPerson(String passport, String firstName, String lastName) throws RemoteException {
        if (passport == null || firstName == null || lastName == null) {
            System.err.println("Can't create person with null arguments");
            return false;
        }
        final Person person = new RemotePerson(firstName, lastName, passport, port);
        return persons.putIfAbsent(passport, person) == null;
    }

    @Override
    public boolean changeAmount(String subId, String passport, int amount) throws RemoteException {
        Account account = getAccount(subId, passport);
        if (account == null) {
            return false;
        }
        account.setAmount(account.getAmount() + amount);
        return true;
    }

    private String getId(String subId, String passport) {
        return passport + ':' + subId;
    }
}
