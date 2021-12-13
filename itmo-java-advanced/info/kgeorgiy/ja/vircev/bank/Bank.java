package info.kgeorgiy.ja.vircev.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bank extends Remote {

    boolean createAccount(String subId, String passport) throws RemoteException;

    Account getAccount(String subId, String passport) throws RemoteException;

    LocalPerson getLocalPerson(String passport) throws RemoteException;

    Person getRemotePerson(String passport) throws RemoteException;

    Person searchPerson(String passport, TypeOfPerson typeOfPerson) throws RemoteException;

    boolean createPerson(String passport, String firstName, String lastName) throws RemoteException;

    boolean changeAmount(String subId, String passport, int amount) throws RemoteException;
}
