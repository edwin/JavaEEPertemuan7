package com.edw.main.locking;

import com.edw.bean.Dosen;
import com.edw.bean.Matakuliah;
import com.edw.util.HiberUtil;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *  database locking using Hibernate
 *  Pessimistic Locking is achieved using database's versioning capability
 *  Optimistic Locking is achieved using Hibernate's versioning capability
 *
 *  scenario 1 (pessimistic locking) :
 *
 *      +------------+      +-----------------+                                                               +-----------------+
 *      | Session 1  | ---> | select object 1 |---------------------------|  xx   LOCKED  xx  |-------------> | update object 1 | ---> SUCCESS
 *      +------------+      +-----------------+                                                               +-----------------+
 *
 *      +------------+                          +-----------------+       +-----------------+
 *      | Session 2  | -----------------------> | select object 1 | ----> | update object 1 | ----------------------------------------> FAILED
 *      +------------+                          +-----------------+       +-----------------+
 *
 * 
 *  scenario 2 (optimistic locking) :
 *
 *      +------------+      +-----------------+                                                               +-----------------+
 *      | Session 1  | ---> | select object 1 |---------------------------|   ++   IDLE   ++  |-------------> | update object 1 | ----> FAILED
 *      +------------+      +-----------------+                                                               +-----------------+
 *
 *      +------------+                          +-----------------+       +-----------------+
 *      | Session 2  | -----------------------> | select object 1 | ----> | update object 1 | ----------------------------------------> SUCCESS
 *      +------------+                          +-----------------+       +-----------------+
 *
 *  @author edw
 *
 *  @date Jul 1, 2011
 *  @time 6:48:49 PM
 *  @see http://en.wikipedia.org/wiki/Lock_(database)
 */
public class LockingMain {

    private Logger logger = Logger.getLogger(this.getClass());
    private boolean done = false;

    public LockingMain() {
    }

    /**
     *  start simulation using Pessimistic locking
     *
     */
    private void doPessimisticSimulation(){
        logger.debug("START PESSIMISTIC SIMULATION");
        for (int i = 0; i < 2; i++) {
            SimulatePessimisticLocking simulatePessimisticLocking = new SimulatePessimisticLocking();
            simulatePessimisticLocking.setThreadName("Thread "+i);
            if(i == 0)
                simulatePessimisticLocking.setDelayTime(10000l);
            else
                simulatePessimisticLocking.setDelayTime(3000l);

            Thread thread = new Thread(simulatePessimisticLocking);
            if(i == 0)
                thread.setPriority(Thread.MAX_PRIORITY); // the first thread starts first
            else
                thread.setPriority(Thread.MIN_PRIORITY); // the failing second thread starts later
            thread.start();
        }
    }

    /**
     *  start simulation using Optimistic locking
     *
     */
    private void doOptimisticSimulation(){
        
        logger.debug("START OPTIMISTIC SIMULATION");

        // first access (FAIL)
        SimulateOptimisticLocking simulateOptimisticLocking = new SimulateOptimisticLocking();
        simulateOptimisticLocking.setThreadName("Thread 1");
        simulateOptimisticLocking.setDelayTime(10000l); // do some lengthy works

        Thread thread = new Thread(simulateOptimisticLocking);
        thread.start();

        // second access (SUCCESS)
        SimulateOptimisticLocking simulateOptimisticLocking2 = new SimulateOptimisticLocking();
        simulateOptimisticLocking2.setThreadName("Thread 2");
        simulateOptimisticLocking2.setDelayTime(200l);

        Thread thread2 = new Thread(simulateOptimisticLocking2);
        thread2.start();

    }

    public static void main(String[] args) {
        LockingMain lockingMain = new LockingMain();
        lockingMain.doPessimisticSimulation();
        lockingMain.doOptimisticSimulation();
    }

    private class SimulatePessimisticLocking implements Runnable{

        private long delayTime = 0l;
        private String threadName = "";

        public SimulatePessimisticLocking() {
        }

        public void run() {
             // let the game begin
            logger.debug(" >>> start "+threadName);

            Session session = null;
            try {

                session = HiberUtil.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                
                logger.debug(" >>> select "+threadName);
                Dosen dosen = (Dosen)session.load(Dosen.class, "321");
                logger.debug(" >>> end select "+threadName);

                /*
                 *  IMPORTANT
                 *  pessimistic locking (LockMode.UPGRADE)
                 */
                session.lock(dosen, LockMode.UPGRADE);                

                // sleep for x second
                Thread.sleep(delayTime);
                
                dosen.setNamadosen(threadName);

                logger.debug(" >>> update "+threadName);
                session.update(dosen);
                logger.debug(" >>> end update "+threadName);
                          
                transaction.commit();
            } catch (Exception e) {
                logger.error(e,e);
            } finally{
                if(session != null)
                    session.close();
            }
            
            // the game has ended
            logger.debug(" >>> finish "+threadName);
        }

        public long getDelayTime() {
            return delayTime;
        }
        
        public void setDelayTime(long delayTime) {
            this.delayTime = delayTime;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }
    }

    private class SimulateOptimisticLocking implements Runnable{

        private long delayTime = 0l;
        private String threadName = "";

        public SimulateOptimisticLocking() {
        }

        public void run() {

             // let the game begin
            logger.debug(" >>> start "+threadName);

            Session session = null;
            try {

                session = HiberUtil.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();

                logger.debug(" >>> select "+threadName);
                Matakuliah matakuliah = (Matakuliah)session.load(Matakuliah.class, "123");
                logger.debug(" >>> end select "+threadName);               

                matakuliah.setNamamatakuliah("mk "+threadName);

                // sleep for x second
                Thread.sleep(delayTime);

                logger.debug(" >>> update "+threadName);
                session.update(matakuliah);
                logger.debug(" >>> end update "+threadName);
                
                transaction.commit();                
            } catch (Exception e) {
                logger.error(e,e);
            } finally{
                if(session != null)
                    session.close();
            }

            // the game has ended
            logger.debug(" >>> finish "+threadName);
        }

        public long getDelayTime() {
            return delayTime;
        }

        public void setDelayTime(long delayTime) {
            this.delayTime = delayTime;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }
    }

}