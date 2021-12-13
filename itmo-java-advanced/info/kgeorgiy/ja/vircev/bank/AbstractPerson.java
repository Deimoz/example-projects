package info.kgeorgiy.ja.vircev.bank;

import java.rmi.RemoteException;
import java.util.Map;

public abstract class AbstractPerson implements Person {
    private final String firstName;
    private final String lastName;
    private final String passport;

    public AbstractPerson(String firstName, String lastName, String passport) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passport = passport;
    }


    @Override
    public String getFirstName() throws RemoteException {
        return firstName;
    }

    @Override
    public String getLastName() throws RemoteException {
        return lastName;
    }

    @Override
    public String getPassport() throws RemoteException {
        return passport;
    }
}
