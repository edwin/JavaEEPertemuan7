package com.edw.main.transaction;

import com.edw.bean.Dosen;
import com.edw.util.HiberUtil;
import java.util.Random;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *  transaction example using hibernate
 *
 *  scenario :
 *      +---------------+     +---------------+                            +--------------------------------+
 *      | insert dosen1 | --> | insert dosen2 | -- WITHOUT transaction --> | dosen1 inserted, dosen2 failed |
 *      +---------------+     +---------------+                            +--------------------------------+
 *                                    +
 *                                    +                               +------------------------------+
 *                                    +--------- WITH transaction---> | dosen1 failed, dosen2 failed |
 *                                                                    +------------------------------+
 *
 *  @author edw
 *  @date Jun 25, 2011
 *  @time 3:24:24 PM
 *
 *  @see http://en.wikipedia.org/wiki/Database_transaction
 *  @see http://it.toolbox.com/wiki/index.php/Database_Transaction
 */
public class TransactionMain {

    public TransactionMain() {
    }

    /**
     * plain queries without Transaction
     */
    private void plainExample() throws Exception{
        Session session = null;
        try {
            session = HiberUtil.getSessionFactory().openSession();

            // ordinary object
            Dosen dosen1 = new Dosen(createRandomIds(),"plain 1");

            // a very long string to create an sql exception
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                sb.append("x")                ;
            }
            Dosen dosen2 = new Dosen(createRandomIds(),sb.toString());

            // insert dosens
            session.save(dosen1); // success
            session.save(dosen2); // failed

            session.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(session != null)
                session.close();
        }
    }

    /**
     * queries using Transaction
     */
    private void transactionExample(){
        Transaction transaction = null;
        Session session = null;
        try {
            session = HiberUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            transaction.begin();

            // ordinary object
            Dosen dosen1 = new Dosen(createRandomIds(),"transaction 1");

            // a very long string to create an sql exception
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                sb.append("x")                ;
            }
            Dosen dosen2 = new Dosen(createRandomIds(),sb.toString());

            // insert dosens
            session.save(dosen1); // success
            session.save(dosen2); // failed

            // if success
            transaction.commit();
        } catch (Exception e) {
            // if failed
            transaction.rollback();
            e.printStackTrace();
        } finally{
            if(session != null)
                session.close();
        }
    }

    /**
     * create a random IDs
     * @return randomID
     */
    private String createRandomIds(){
        Random random = new Random();
        String randomValue = ""+random.nextInt();

        if(randomValue.length() >10)
            return randomValue.substring(0,10);
        return randomValue;
    }

    public static void main(String[] args) throws Exception {
        TransactionMain main = new TransactionMain();
        main.plainExample();
        main.transactionExample();
    }

}
