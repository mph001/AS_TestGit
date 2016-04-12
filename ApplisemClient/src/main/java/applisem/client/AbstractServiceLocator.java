package applisem.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import applisem.client.exceptions.UnknownApplisemServerException;
import static applisem.log.Applisem_Log.ApplisemLog.*;

/**
 * THis abstract class was defined to regroup socket manipulation
 *
 * - Connecting the socket to the server - closing socket - Writing to the
 * socket - Reading from the socket
 *
 * The new communication solution with percistant socket was made in this class
 *
 * @author LBO
 */
public abstract class AbstractServiceLocator {
    
    /**
     * the adresse of the server
     */
    protected final String host;
    /**
     * the port of the server
     */
    protected final int port;

    public AbstractServiceLocator(String h, int p) {
        host = h;
        port = p;
        // keepConnected = connected;
    }

    /**
     * the methode is called by user method to connect to the server
     *
     * This use the socket cache to reduce socket connection if the cache
     * contain a socket a message is send to test the validity of the socket
     * with the server if the cache is empty (null socket) à new connection is
     * made
     *
     * @return the socket connected to the server nd ready to process operation
     * @throws UnknownApplisemServerException Exception thrown if the socket
     * connection faill.
     */
    protected Socket connect() throws UnknownApplisemServerException {

        Log_Applisem_Entree();
        SocketCache cache = SocketCache.getSocketCache(host, port);
        if (Log_Applisem_Actif()) {
            Log_Applisem("   ==> cache : host = " + host);
            Log_Applisem("   ==> cache : port = " + port);
        }
        Socket s = cache.getSocket();
        if (s != null) {
            try {
                Log_Applisem("writeObject  ------------    Object = 10");
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(new Integer(10));
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                Integer i = (Integer) ois.readObject();
                Log_Applisem("readObject i = " + i);
            } catch (Throwable e) {
                s = null;
            }
        }
        if (s == null) {
            try {
                s = new Socket(host, port);
            } catch (IOException e) {
                e.printStackTrace();
                throw new UnknownApplisemServerException();
            }
        }
        Log_Applisem_Sortie();
        return s;
        /*
         * if (keepConnected == false) { try { return new Socket(host, port); }
         * catch (IOException e) { throw new UnknownApplisemServerException(); }
         * } else { if (csocket == null) { try { return csocket = new
         * Socket(host, port); } catch (IOException e) { throw new
         * UnknownApplisemServerException(); } } return csocket; }
         */
    }

    /**
     * this method free the socket the only thing it does is to put the socket
     * in the cache if the cache contain too many socket the socket is properly
     * closed by the cache
     *
     * @param s
     * @throws IOException
     */
    protected void disconnect(Socket s) throws IOException {
        SocketCache cache = SocketCache.getSocketCache(host, port);
        cache.putSocket(s);
        /*
         if (keepConnected == false) {
         s.close();
         if (csocket != null) {
         csocket = null;
         }
         }
         */
    }

    /**
     * Method to send message (operation) to the server
     *
     * @param s the communication socket
     * @param object the object to serialize on the socket
     * @throws IOException Exception thrown if the Write operation fail
     */
    protected final void writeToServer(Socket s, Object object) throws IOException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("Socket = " + s.toString());
            Log_Applisem("Object = " + object.toString());
            Log_Applisem("Longeur = " + object.getClass().getFields().length);
            java.lang.reflect.Field[] Champs_obj = object.getClass().getFields();
            for (int i = 0;i < object.getClass().getFields().length; i++) {
                Log_Applisem("Type = " + Champs_obj[i].getType().getName() + " - Nom = " + Champs_obj[i].getName());
            }
        }
        OutputStream is = null;
        try {
            is = s.getOutputStream();
            if (Log_Applisem_Actif()) Log_Applisem("getOutputStream = " + is.getClass());
        } catch (SocketException e) {
            if (Log_Applisem_Actif()) Log_Applisem("ocketException = " + e.getMessage());
            e.printStackTrace();
            s.close();
            throw e;
        }
        if (Log_Applisem_Actif()) Log_Applisem("OutputStream = " + is.toString());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(is);
            if (Log_Applisem_Actif()) Log_Applisem("ObjectOutputStream = " + oos.toString());
            oos.writeObject(object);
            if (Log_Applisem_Actif()) Log_Applisem("writeObject = " + object.toString());
            oos.flush();
            if (Log_Applisem_Actif()) Log_Applisem("flush = " + oos.toString());
        } catch (IOException e){
            if (Log_Applisem_Actif()) Log_Applisem("IOException = " + e.getMessage());
            e.printStackTrace();
            s.close();
            throw e;
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method to read message from the server
     *
     * @param s the communication socket
     * @return the object read from the socket
     * @throws IOException Exception thrown if the read operation fail
     */
    protected final Object readFromServer(Socket s) throws IOException, ClassNotFoundException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("Socket " + s);
        }
        Object obj = null;
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        if (Log_Applisem_Actif()) Log_Applisem("ObjectInputStream " + ois);
        obj = ois.readObject();
        if (Log_Applisem_Actif()) {
            Log_Applisem("readObject " + obj);
            Log_Applisem_Sortie();
        }
        return obj;
    }

}
