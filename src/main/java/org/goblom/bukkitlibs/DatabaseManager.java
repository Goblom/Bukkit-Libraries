/*
 * The MIT License
 *
 * Copyright 2013 Goblom.
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
package org.goblom.bukkitlibs;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.plugin.Plugin;

/**
 * Database Manager v2.0
 *
 * The easiest way in my opinion to manage multiple database (yes is said
 * multiple). This can allow for Fallback/Main databases, Synced local to off
 * site databases. Currently support MySQL, SQLite & MongoDB (supported but not
 * tested).
 *
 * @author Goblom
 * @version 2.0
 */
public class DatabaseManager {

    protected static final String MongoDBDriver = "http://central.maven.org/maven2/org/mongodb/mongo-java-driver/2.11.1/mongo-java-driver-2.11.1.jar";

    protected static Map<String, Connector> dbConnector = new HashMap();
    protected static Map<String, Connection> dbConnection = new HashMap();
    protected static Map<String, Statement> dbStatement = new HashMap();

    public static void registerConnection(String connName, Connector conn) throws SQLException, ClassNotFoundException {
        dbConnector.put(connName, conn);
        dbConnection.put(connName, conn.connect());
        dbStatement.put(connName, dbConnection.get(connName).createStatement());
    }

    public static Connection getConnection(String connName) {
        return dbConnection.get(connName);
    }

    public static Connector getConnector(String connName) {
        return dbConnector.get(connName);
    }

    public static Statement getStatement(String connName) {
        return dbStatement.get(connName);
    }

    public static void reconnect(String connName) {
        try {
            if (dbConnection.get(connName) == null || dbConnection.get(connName).isClosed()) {
                dbConnection.remove(connName);
                dbStatement.remove(connName);
                dbConnection.put(connName, dbConnector.get(connName).connect());
                dbStatement.put(connName, dbConnection.get(connName).createStatement());
            }
            if (dbStatement.get(connName) == null || dbStatement.get(connName).isClosed()) {
                if (dbStatement.get(connName).isClosed()) {
                    dbStatement.remove(connName);
                }
                dbStatement.put(connName, dbConnection.get(connName).createStatement());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown(String connName) {
        try {
            dbStatement.get(connName).close();
            dbConnection.get(connName).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static abstract class Connector extends DatabaseManager {

        protected final Plugin plugin;
        protected final File dbFile;
        protected final String host;
        protected final int port;
        protected final String dbName;
        protected final String[] credentials; //protected final String username, password;

        public Connector(Plugin plugin, String dbFile) {
            this.plugin = plugin;
            this.dbFile = new File(plugin.getDataFolder(), dbFile);

            this.host = null;
            this.port = 0;
            this.dbName = null;
            this.credentials = null;
        }

        public Connector(String host, int port, String dbName, String[] credentials) {
            this.plugin = null;
            this.dbFile = null;

            this.host = host;
            this.port = port;
            this.dbName = dbName;
            this.credentials = credentials;
        }

        protected abstract Connection connect() throws ClassNotFoundException, SQLException;
    }

    public static class MySQL extends Connector {

        public MySQL(String host, int port, String dbName, String[] credentials) {
            super(host, port, dbName, credentials);
        }

        @Override
        protected Connection connect() throws ClassNotFoundException, SQLException {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbName, credentials[0], credentials[1]);
        }
    }

    public static class SQLite extends Connector {

        public SQLite(Plugin plugin, String dbFile) {
            super(plugin, dbFile);
        }

        @Override
        protected Connection connect() throws ClassNotFoundException, SQLException {
            Class.forName("org.sqlite.JDBC");
            if (!dbFile.exists()) {
                try {
                    dbFile.getAbsoluteFile().createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    plugin.getLogger().severe("Couldn't create database file");
                }
            }
            return DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        }
    }

    public static class MongoDB {

        private String host;
        private int port;
        private String dbName;

        private MongoClient mongo;
        private DB database;

        public MongoDB(String host) {
            this(host, 0, null);
        }

        public MongoDB(String host, String dbName) {
            this(host, 0, dbName);
        }

        public MongoDB(String host, int port, String dbName) {
            this.host = host;
            this.port = port;
            this.dbName = dbName;
            
            try { 
                JarUtil.downloadJar(MongoDBDriver, "mongo-java-driver-2.11.1.jar"); 
                JarUtil.addClassPath(JarUtil.getJarUrl(new File("lib/", "mongo-java-driver-2.11.1.jar")));
            } catch (IOException e) { e.printStackTrace(); }
            
            connect();
        }

        public void connect() {
            if (this.mongo != null) {
                this.mongo.close();
                this.mongo = null;
            }

            try {
                if (port != 0) {
                    this.mongo = new MongoClient(host, port);
                } else {
                    this.mongo = new MongoClient(host);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            if (dbName != null) {
                this.database = mongo.getDB(dbName);
            }
        }

        public void setDatabase(String dbName) {
            this.dbName = dbName;
            this.database = mongo.getDB(dbName);

            connect();
        }

        public void setPort(int port) {
            this.mongo.close();
            this.mongo = null;
            this.database = null;

            connect();
        }

        public MongoClient getMongoClient() {
            return mongo;
        }
        
        public DB getDatabase() {
            return database;
        }
        
        public DB getDatabase(String database) {
            return mongo.getDB(dbName);
        }

        public boolean authenticate(DB database, String username, char[] password) {
            return database.authenticate(username, password);
        }

        public boolean authenticate(String username, char[] password) {
            return database.authenticate(username, password);
        }

        public List<String> getDatabases() {
            return mongo.getDatabaseNames();
        }

        public void dropDatabase(String dbName) {
            mongo.dropDatabase(dbName);
        }

        public Set<String> getCollections() {
            return database.getCollectionNames();
        }

        public Set<String> getCollections(DB database) {
            return database.getCollectionNames();
        }

        public DBCollection getCollection(String collectionName) {
            return database.getCollection(dbName);
        }

        public DBCollection getCollection(DB database, String collectionName) {
            return database.getCollection(dbName);
        }

        public void insertDocument(DBCollection coll, BasicDBObject bdbo) {
            coll.insert(bdbo);
        }

        public BasicDBObject createBasicDBObject(String key, Object value) {
            return new BasicDBObject(key, value);
        }

        public BasicDBObject createBasicDBObject(String[] keys, Object[] keyValues) {
            BasicDBObject doc = new BasicDBObject();
            for (int i = 0; i < keys.length; i++) {
                doc.append(keys[i], keyValues[i]);
            }
            return doc;
        }

        public BasicDBObject inserBasicObject(String key, Object[] values) {
            return new BasicDBObject(key, values);
        }

        public Long count(String collectionName) {
            return database.getCollection(collectionName).count();
        }

        public Long count(DB database, String collectionName) {
            return database.getCollection(collectionName).count();
        }

        public DBCursor getDocuments(String collectionName) {
            return database.getCollection(collectionName).find();
        }

        public DBCursor getDocuments(DB database, String collectionName) {
            return database.getCollection(collectionName).find();
        }

        public DBCursor getDocument(String collectionName, BasicDBObject bdbo) {
            return database.getCollection(collectionName).find(bdbo);
        }

        public DBCursor getDocument(DB database, String collectionName, BasicDBObject bdbo) {
            return database.getCollection(collectionName).find(bdbo);
        }
    }

    public static class Manage {

        public static boolean intToBoolean(int num) {
            return (num == 1);
        }

        public static int booleanToInt(boolean bool) {
            return bool ? 1 : 0;
        }

        public static boolean createTable(String connName, String tableName, String columns) {
            if (dbStatement.get(connName) != null) {
                try {
                    dbStatement.get(connName).executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");");
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        public static ResultSet query(String connName, String sql) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);
                if (rs == null) {
                    return null;
                }
                if (rs.isAfterLast()) {
                    return null;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String queryString(String connName, String sql) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);
                if (rs == null) {
                    return null;
                }
                if (rs.isAfterLast()) {
                    return null;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getString(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String queryString(String connName, String sql, String column) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return null;
                }
                if (rs.isAfterLast()) {
                    return null;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getString(column);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static int queryInt(String connName, String sql) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return -1;
                }
                if (rs.isAfterLast()) {
                    return -1;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public static int queryInt(String connName, String sql, String column) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return -1;
                }
                if (rs.isAfterLast()) {
                    return -1;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getInt(column);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public static long queryLong(String connName, String sql) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return -1;
                }
                if (rs.isAfterLast()) {
                    return -1;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getLong(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public static long queryLong(String connName, String sql, String column) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return -1;
                }
                if (rs.isAfterLast()) {
                    return -1;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getLong(column);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public static float queryFloat(String connName, String sql) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return -1F;
                }
                if (rs.isAfterLast()) {
                    return -1F;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getFloat(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public static float queryFloat(String connName, String sql, String column) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return -1F;
                }
                if (rs.isAfterLast()) {
                    return -1F;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getFloat(column);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public static boolean queryBoolean(String connName, String sql) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return false;
                }
                if (rs.isAfterLast()) {
                    return false;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getBoolean(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static boolean queryBoolean(String connName, String sql, String column) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = dbStatement.get(connName).executeQuery(sql);;
                if (rs == null) {
                    return false;
                }
                if (rs.isAfterLast()) {
                    return false;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                return rs.getBoolean(column);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static int update(String connName, String sql) {
            DatabaseManager.reconnect(connName);
            try {
                return dbStatement.get(connName).executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }

        public static boolean tableContains(String connName, String table, String column, String value) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = query(connName, "SELECT COUNT(" + column + ") AS " + column + "Count FROM " + table + " WHERE " + column + "='" + value + "'");
                if (rs == null) {
                    return false;
                }
                if (rs.isAfterLast()) {
                    return false;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                if (rs.getInt(1) == 0) {
                    return false;
                } else {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static boolean tableContains(String connName, String table, String column1, String value1, String column2, String value2) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = query(connName, "SELECT COUNT(" + column1 + ") AS " + column1 + "Count FROM " + table + " WHERE " + column1 + "='" + value1 + "' AND " + column2 + "='" + value2 + "'");
                if (rs == null) {
                    return false;
                }
                if (rs.isAfterLast()) {
                    return false;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                if (rs.getInt(1) == 0) {
                    return false;
                } else {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static boolean tableContains(String connName, String table, String column1, String value1, String column2, String value2, String column3, String value3) {
            DatabaseManager.reconnect(connName);
            try {
                ResultSet rs = query(connName, "SELECT COUNT(" + column1 + ") AS " + column1 + "Count FROM " + table + " WHERE " + column1 + "='" + value1 + "' AND " + column2 + "='" + value2 + "' AND " + column3 + "='" + value3 + "'");
                if (rs == null) {
                    return false;
                }
                if (rs.isAfterLast()) {
                    return false;
                }
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                if (rs.getInt(1) == 0) {
                    return false;
                } else {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
