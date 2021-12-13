package info.kgeorgiy.ja.vircev.bank;

import org.junit.Assert;
import org.junit.BeforeClass;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tests {
    private static Bank bank;
    private static final int PORT = 8888;
    private static final String HOST_NAME = "//localhost/bank";
    private static final String baseFirstName = "baseFirstName";
    private static final String baseLastName = "baseLastName";
    private static final String basePassport = "basePassport";
    private static final String baseId = "baseId";


    @BeforeClass
    public static void beforeClass() {
        bank = new RemoteBank(PORT);
        try {
            Registry localReg = LocateRegistry.createRegistry(PORT);
            UnicastRemoteObject.exportObject(bank, PORT);
            localReg.rebind(HOST_NAME, bank);
        } catch (final RemoteException e) {
            System.out.println("Cannot export object: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    @org.junit.Test
    public void createPersonTest() throws RemoteException {
        String testName = "createPersonTest";
        int count = 5;
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(bank.createPerson(
                    basePassport + testName + i,
                    baseFirstName + testName + i,
                    baseLastName + testName + i));
        }
        TestCreatedPersons(testName, count);
    }

    @org.junit.Test
    public void createMultiThreadPersonTest() throws RemoteException, InterruptedException {
        String testName = "createMultiThreadPersonTest";
        int count = 20;
        ExecutorService workers = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            int finalI = i;
            workers.submit(() -> bank.createPerson(
                    basePassport + testName + finalI,
                    baseFirstName + testName + finalI,
                    baseLastName + testName + finalI));
        }
        workers.shutdown();
        workers.awaitTermination(1, TimeUnit.SECONDS);
        workers.shutdownNow();
        TestCreatedPersons(testName, count);
    }

    private void TestCreatedPersons(String testName, int count) throws RemoteException {
        for (int i = 0; i < count; i++) {
            Assert.assertNotNull(bank.getRemotePerson(basePassport + testName + i));
        }
        Assert.assertNull(bank.getRemotePerson(basePassport + testName + "notExists"));

        for (int i = 0; i < count; i++) {
            Assert.assertFalse(bank.createPerson(
                    basePassport + testName + i,
                    baseFirstName + testName + i,
                    baseLastName + testName + i));
        }
    }

    @org.junit.Test
    public void getLocalPersonTest() throws RemoteException {
        String testName = "getLocalPerson";
        String testFirstName = baseFirstName + testName;
        String testLastName = baseLastName + testName;
        String testPassport = basePassport + testName;
        bank.createPerson(testPassport, testFirstName, testLastName);
        LocalPerson person = (LocalPerson) bank.searchPerson(testPassport, TypeOfPerson.LOCAL);
        checkPersonInfo(person, testFirstName, testLastName, testPassport);
    }

    @org.junit.Test
    public void getRemotePersonTest() throws RemoteException {
        String testName = "getRemotePerson";
        String testFirstName = baseFirstName + testName;
        String testLastName = baseLastName + testName;
        String testPassport = basePassport + testName;
        bank.createPerson(testPassport, testFirstName, testLastName);
        RemotePerson person = (RemotePerson) bank.searchPerson(testPassport, TypeOfPerson.REMOTE);
        checkPersonInfo(person, testFirstName, testLastName, testPassport);
    }

    @org.junit.Test
    public void createMultiThreadAccountTest() throws RemoteException, InterruptedException {
        String testName = "createMultiThreadAccount";
        String testPassport = basePassport + testName;
        String testId = baseId + testName;
        int count = 20;
        ExecutorService workers = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            int finalI1 = i;
            workers.submit(() -> {
                try {
                    Assert.assertTrue(bank.createAccount(testId + finalI1, testPassport + finalI1));
                } catch (RemoteException e) {
                    System.exit(1);
                }
            });
        }
        workers.shutdown();
        workers.awaitTermination(1, TimeUnit.SECONDS);
        workers.shutdownNow();
        for (int i = 0; i < count; i++) {
            Assert.assertNotNull(bank.getAccount(testId + i, testPassport + i));
        }
    }

    @org.junit.Test
    public void createAccountTest() throws RemoteException {
        String testName = "createAccount";
        String testPassport = basePassport + testName;
        String testId = baseId + testName;
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(bank.createAccount(testId + i, testPassport + i));
        }
        for (int i = 0; i < 5; i++) {
            Assert.assertNotNull(bank.getAccount(testId + i, testPassport + i));
        }

        Assert.assertNull(bank.getAccount(testId + "notExists", testPassport));

        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(bank.createAccount(testId + i, testPassport + i));
        }
    }

    @org.junit.Test
    public void getAccountTest() throws RemoteException {
        String testName = "getAccount";
        String testPassport = basePassport + testName;
        String testId = baseId + testName;
        bank.createAccount(testId, testPassport);
        Account account = bank.getAccount(testId, testPassport);
        checkAccountInfo(account, testPassport, testId, 0);
        account.setAmount(100);
        account = bank.getAccount(testId, testPassport);
        checkAccountInfo(account, testPassport, testId, 100);
    }

    @org.junit.Test
    public void changeAmountTest() throws RemoteException, InterruptedException {
        String testName = "changeAmount";
        String testPassport = basePassport + testName;
        String testId = baseId + testName;
        bank.createAccount(testId, testPassport);
        int count = 20;
        ExecutorService workers = Executors.newFixedThreadPool(count);
        Account account = bank.getAccount(testId, testPassport);
        checkAccountInfo(account, testPassport, testId, 0);

        for (int i = 0; i < count; i++) {
            int finalI1 = i;
            workers.submit(() -> {
                try {
                    bank.changeAmount(testId, testPassport, 100);
                } catch (RemoteException e) {
                    System.exit(1);
                }
            });
        }
        workers.shutdown();
        workers.awaitTermination(1, TimeUnit.SECONDS);
        workers.shutdownNow();
        checkAccountInfo(account, testPassport, testId, count * 100);
    }

    @org.junit.Test
    public void changeMultiThreadAmountTest() throws RemoteException {
        String testName = "changeMultiThreadAmount";
        String testPassport = basePassport + testName;
        String testId = baseId + testName;
        bank.createAccount(testId, testPassport);
        Account account = bank.getAccount(testId, testPassport);
        checkAccountInfo(account, testPassport, testId, 0);
        bank.changeAmount(testId, testPassport, 100);
        account = bank.getAccount(testId, testPassport);
        checkAccountInfo(account, testPassport, testId, 100);
    }

    private void checkPersonInfo(Person person, String fName, String lName, String passport) throws RemoteException {
        Assert.assertNotNull(person);
        Assert.assertEquals(person.getFirstName(), fName);
        Assert.assertEquals(person.getLastName(), lName);
        Assert.assertEquals(person.getPassport(), passport);
    }

    private void checkAccountInfo(Account account, String passport, String id, int amount) throws RemoteException {
        Assert.assertNotNull(account);
        Assert.assertEquals(account.getId(), passport + ':' + id);
        Assert.assertEquals(account.getAmount(), amount);
    }
}
