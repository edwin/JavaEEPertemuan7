package com.edw.main.caching;

import com.edw.bean.Dosen;
import com.edw.bean.Matakuliah;
import com.edw.util.HiberUtil;
import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.Session;


/**
 *  simple caching using Hibernate
 *  Object Dosen is not cacheable while Object MataKuliah is cacheable
 *
 *  scenario :
 *          +-------+                         +----------+
 *          | query | <--- WITHOUT cache ---> | Database |
 *          +-------+                         +----------+
 *
 *          +-------+                      +-------+        +----------+
 *          | query | <--- WITH cache ---> | Cache | <----> | Database |
 *          +-------+                      +-------+        +----------+
 *
 *  @author edw
 *
 *  @date Jun 25, 2011
 *  @time 6:53:32 PM
 *
 *  @see http://en.wikipedia.org/wiki/Database_caching
 */
public class CachingMain {

    private Logger logger = Logger.getLogger(this.getClass());
    
    public CachingMain() {
    }

    /**
     *  do some repeated queries for table Dosen
     *  queries results are taken directly from database
     */
    private void withoutCache() {
        Session session = null;
        try {
            for (int i = 0; i < 3; i++) {
               // open session
                session = HiberUtil.getSessionFactory().openSession();

                // time needed
                long now = new Date().getTime();

                // select
                Dosen dosen = (Dosen)session.load(Dosen.class, "321");

                // print
                logger.debug("\t======================");
                logger.debug("\tdosen  >>>> "+dosen.getNamadosen());
                logger.debug("\tTime  >>>> " + (new Date().getTime() - now) + " ms");
                logger.debug("\t======================");
                
                // sleep for 3seconds
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     *  do some repeated queries for table Matakuliah
     *  query results are taken from cache memory instead of database
     *  @see ehcache.xml
     */
    private void withCache() {

        Session session = null;
        try {            
            for (int i = 0; i < 3; i++) {

                // open session
                session = HiberUtil.getSessionFactory().openSession();                

                // time needed
                long now = new Date().getTime();

                // select
                Matakuliah matakuliah = (Matakuliah)session.load(Matakuliah.class, "123");

                // print
                logger.debug("\t======================");
                logger.debug("\tmatakuliah >>>> "+matakuliah.getNamamatakuliah());
                logger.debug("\tTime  >>>> " + (new Date().getTime() - now) + " ms");
                logger.debug("\t======================");
                
                // sleep for 3seconds
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
 

    public static void main(String[] args) {
        CachingMain main = new CachingMain();
        main.withoutCache();
        main.withCache();
    }
}
