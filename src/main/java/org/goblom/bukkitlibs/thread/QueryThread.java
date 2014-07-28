/*
 * The MIT License
 *
 * Copyright 2014 Goblom.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.goblom.bukkitlibs.thread;

import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * QueryThread v1.0
 * 
 * This will make threaded queries to an SQL connection and feed results through a {@link DataHandler} for you to interpret
 * 
 * @author Goblom
 */
public class QueryThread {

    /**
     * Keep this class entirely static
     */
    private QueryThread() {}
    
    static {
        QueryThread.thread = new SQLThread();
        QueryThread.thread.start();
        
        QueryThread.toMake = Lists.newArrayList();
        QueryThread.queriesMade = 0;
        QueryThread.queriesFailed = 0;
        QueryThread.waitTime = 1000;
    }

    /**
     * The thread task that runs all the queries
     */
    private static SQLThread thread;
    
    /**
     * A List of all scheduled queries to be ran
     */
    private static List<Query> toMake;

    /**
     * A counter to count the amount of queries made... because stats
     */
    private static long queriesMade;
    
    /**
     * A counter to count the amount of queries failed... because stats
     */
    private static long queriesFailed;
    
    /**
     * The amount of time the {@link QueryThread#thread} should wait before running another query task
     */
    private static long waitTime;

    /**
     * Stop the {@link SQLThread}
     * 
     * @param andFinishAllQueries should we run all remaining SQL queries before stopping the thread
     */
    public static void stopThread(boolean andFinishAllQueries) {
        QueryThread.SQLThread.instantiated = false;
        
        if (andFinishAllQueries) {
            QueryThread.thread.wait = false;
            
            while (!toMake.isEmpty()) {
                doQuery();
            }
        }
        
        QueryThread.thread.interrupt();
        QueryThread.thread = null;
    }
    
    /**
     * If you have stopped the thread with {@link QueryThread#stopThread(boolean)} you can use this to start the thread again
     */
    public static void startThread() {
        if (QueryThread.thread != null || QueryThread.thread.hasStarted()) {
            return;
        }
        
        QueryThread.thread = new SQLThread();
        QueryThread.thread.start();
    }
    
    /**
     * Set how long the {@link SQLThread} should wait before running another query task
     * 
     * @param time How long the {@link SQLThread} should wait
     */
    public static void setWaitTime(long time) {
        if (time == 0) {
            return;
        }
        QueryThread.waitTime = time;
    }

    /**
     * @see QueryThread#queriesFailed
     * 
     * @return The amount of queries that have failed
     */
    public static long queriesFailed() {
        return QueryThread.queriesFailed;
    }

    /**
     * @see QueryThread#queriesMade
     * 
     * @return The amount of queries that have been made
     */
    public static long queriesMade() {
        return QueryThread.queriesMade;
    }

    /**
     * How many queries are left to be ran
     * 
     * @return The size of the query pool
     */
    public static int queryPool() {
        return QueryThread.toMake.size();
    }

    /**
     * Schedule a query to be ran inside the {@link SQLThread} later on
     * 
     * @param conn The SQL Connection to the sql query on
     * @param sql The SQL Query to run on the connection
     * @param handler The {@link DataHandler} to handle the data that is received through the query.
     */
    public static void scheduleQuery(Connection conn, String sql, DataHandler handler) {
        QueryThread.toMake.add(new Query(conn, sql, handler));
    }

    /**
     * A simple way of getting the first query of {@link QueryThread#toMake} and removing it in order to have a sort of progression
     * 
     * @return The first {@link Query} in {@link QueryThread#toMake} returns null if there is no query or there was an exception thrown
     */
    private static Query getFirst() {
        try {
            return QueryThread.toMake.remove(0);
        } catch (Exception e) { }
        return null;
    }

    /**
     * The Method that runs the first {@link Query} in the {@link QueryThread#queryPool()}
     */
    private static void doQuery() {
        QueryThread.Query query = QueryThread.getFirst();

        try {
            String sql = query.getQuery();
            DataHandler handler = query.getHandler();
            Connection conn = query.getConnection();

            handler.start = System.currentTimeMillis();
            handler.onQuery(sql);
            ResultSet rs = null;

            try {
                rs = conn.prepareStatement(sql).executeQuery();
            } catch (SQLException e) {
                handler.sqlException = e;
                QueryThread.queriesFailed++;
            }

            handler.end = System.currentTimeMillis();
            handler.onDataRecieved(rs == null, rs);
        } catch (Exception e) {
            QueryThread.queriesFailed++;
            e.printStackTrace();
        }

        QueryThread.queriesMade++;
    }

    /**
     * Read and interpret data that is fed from the {@link Query}
     */
    public static class DataHandler {

        private SQLException sqlException = null;
        private long start, end;

        /**
         * The time that the {@link Query} started
         * 
         * @return 
         */
        public final long getStartTime() {
            return start;
        }

        /**
         * The time that {@link Query} finished
         * 
         * @return 
         */
        public final long getEndTime() {
            return end;
        }

        /**
         * The {@link SQLException} that was generated by the query if it failed
         * 
         * @return the {@link SQLException} that was generated if the query failed
         */
        public final SQLException getException() {
            return sqlException;
        }

        /**
         * @param sqlQuery The SQL query to be made on the {@link Connection}
         */
        public void onQuery(String sqlQuery) { }
        
        /**
         * When the query is finished the data is fed through this
         * 
         * @param failed If the resultset is null or there was an {@link SQLException}
         * @param rs The return data from the SQL query on the {@link Connection}
         */
        public void onDataRecieved(boolean failed, ResultSet rs) { }
    }

    /**
     * This is a handler class to hold all the data of a {@link QueryThread#scheduleQuery(java.sql.Connection, java.lang.String, org.goblom.bukkitlibs.thread.QueryThread.DataHandler)}
     */
    private static class Query {

        private final String sql;
        private final DataHandler handler;
        private final Connection connection;

        /**
         * Do not allow any outside forces instantiate this class
         * 
         * @param conn The connection that is used to run the SQL query
         * @param sql The SQL Query to be made on the connection
         * @param handler The handler that is fed all the data from the Result
         */
        private Query(Connection conn, String sql, DataHandler handler) {
            this.connection = conn;
            this.sql = sql;
            this.handler = handler;
        }

        /**
         * @return The stored connection
         */
        public Connection getConnection() {
            return this.connection;
        }

        /**
         * @return The SQL Query to be made on the {@link Query#getConnection()}
         */
        public String getQuery() {
            return this.sql;
        }

        /**
         * @return The {@link DataHandler} that is fed all the data collected
         */
        public DataHandler getHandler() {
            return this.handler;
        }
    }

    /**
     * The SQL Thread that runs all the {@link Query} from the {@link QueryThread#queryPool()}
     */
    private static class SQLThread extends Thread {
        
        /**
         * Is thread already created
         */
        private static boolean instantiated = false;

        /**
         * Has thread started?
         */
        private boolean started = false;
        
        /**
         * Should thread wait before running another {@link Query}
         */
        private boolean wait = true;
        
        /**
         * Do not allow any outside forces instantiate this class
         */
        private SQLThread() {
            if (SQLThread.instantiated) {
                //if already created do not allow it to be created again
                throw new UnsupportedOperationException("The SQL Thread is already running!");
            }

            QueryThread.SQLThread.instantiated = true;
            setName("Threaded SQL Querying");
        }

        /**
         * Check if thread has started
         * 
         * @return true if thread has started
         */
        public boolean hasStarted() {
            return this.started;
        }
        
        /**
         * Start the {@link SQLThread}
         */
        @Override
        public void start() {
            if (this.started) {
                throw new UnsupportedOperationException("The SQLThread is already running!");
            }
            
            this.started = true;
            super.start();
        }
        
        /**
         * The Task that runs all the {@link Query} and waits if told so
         */
        @Override
        public void run() {
            QueryThread.doQuery();

            if (this.wait) {
                try {
                    sleep(QueryThread.waitTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
