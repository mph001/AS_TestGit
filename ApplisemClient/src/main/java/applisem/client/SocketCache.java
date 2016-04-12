package applisem.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class whitch contains unused socket to solve the problem of socket stack on
 * windows XP. This problem cause connection failure on heavy load use.
 *
 * This cache allow to keep track of socket opened to spécifique serveur. This
 * allow to use less socket on single user and single thread use. This make the
 * previous solution à persitant connection useless.
 *
 * The Socket cache use à static methode to have only one socket cache for à
 * serveur spécification
 *
 * @author LBO
 *
 */
public class SocketCache {

    /**
     * The main container of the socket cache. This container is a map keyed by
     * à string représentation of the server end point
     */
    public static Map<String, SocketCache> caches = new HashMap<String, SocketCache>();

    /**
     * Main synchronistion object for stacic acces to cache
     */
    public static final Integer lock = new Integer(10);

    public synchronized static SocketCache getSocketCache(String host, int port) {
        synchronized (lock) {
            String key = host + "!!" + String.valueOf(port);
            if (!caches.containsKey(key)) {
                SocketCache sc = new SocketCache();
                caches.put(key, sc);
            }
            return caches.get(key);
        }
    }

    /**
     * List of the socketcache entry for a socket cache. This is a list ordered
     * by the last access time stamp
     */
    List<SocketCacheEntry> sockets = new ArrayList<SocketCacheEntry>();

    /**
     * THis is the synchronisation object for Sacket cache Opération
     * synchronisation
     */
    public final Integer ilock = new Integer(20);

    /**
     * This methode return the most ancien free Socket managed by this socket
     * cache return null if no socket are in the cache or the most ancien socket
     * in the cache
     */
    public Socket getSocket() {
        synchronized (ilock) {
//        System.out.println("SocketCache : getSocket = " + ilock);
        if (sockets.isEmpty()) {
//                System.out.println("SocketCache : sockets.isEmpty");
                return null;
            }
//            System.out.println("SocketCache : sockets");
            SocketCacheEntry e = sockets.remove(0);
//            System.out.println("SocketCache : sockets " + e.theSocket);
            return e.theSocket;
        }
    }

    /**
     * This add a free socket to the cache.
     *
     * @param s the free socket to keep track
     * @throws IOException
     */
    public void putSocket(Socket s) throws IOException {
        synchronized (ilock) {
            if (sockets.size() > 10) {
                System.err.println("close socket");
                close(s);
                return;
            }
            SocketCacheEntry e = new SocketCacheEntry(s);
            sockets.add(e);
            // Collections.sort(sockets);
        }
    }

    /**
     * THis methode close the socket properly by sending to the server ok
     * message to free the serveur handler for the socket
     *
     * @param s the socket to close
     * @throws IOException
     */
    private void close(Socket s) throws IOException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeBoolean(true);
            oos.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        s.close();
    }

    /**
     * Internal Class witch contains the necessary information to keep the
     * socket cache
     *
     * This information are the socket instance and the last accesse time as a
     * timestam
     *
     * This class implement comparable to sort the Cache in order to have the
     * older socket first
     *
     * @author lbo
     */
    private class SocketCacheEntry implements Comparable<SocketCacheEntry> {

        public final Socket theSocket;

        public final long lastAcces;

        SocketCacheEntry(Socket socket) {
            theSocket = socket;
            lastAcces = System.currentTimeMillis();
        }

        /**
         * @see Comparable
         */
        public int compareTo(SocketCacheEntry o) {
            long r = lastAcces - o.lastAcces;
            if (r < 0) {
                return -1;
            }
            if (r > 0) {
                return 1;
            }
            return 0;
        }

    }
}
