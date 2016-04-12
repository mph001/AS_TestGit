package applisem.client.session;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import applisem.client.AbstractServiceLocator;
import applisem.client.ServiceLocator;
import applisem.client.exceptions.AuthentificationException;
import applisem.client.exceptions.BadFormatException;
import applisem.client.exceptions.EmptyJoinException;
import applisem.client.exceptions.EmptySelectException;
import applisem.client.exceptions.ExistBoardException;
import applisem.client.exceptions.ExistCategoryException;
import applisem.client.exceptions.ExistCollectionException;
import applisem.client.exceptions.ExistInBetweenException;
import applisem.client.exceptions.ExistInputException;
import applisem.client.exceptions.ExistLinkException;
import applisem.client.exceptions.ExistLoginException;
import applisem.client.exceptions.ExistOutputException;
import applisem.client.exceptions.ExistQueryException;
import applisem.client.exceptions.ExistRoleException;
import applisem.client.exceptions.ExistSameObjectException;
import applisem.client.exceptions.ExistStatementException;
import applisem.client.exceptions.ExistSubjectException;
import applisem.client.exceptions.ExistUserException;
import applisem.client.exceptions.ExistViewException;
import applisem.client.exceptions.ExportException;
import applisem.client.exceptions.ImportException;
import applisem.client.exceptions.NoBoardException;
import applisem.client.exceptions.NoCategoryException;
import applisem.client.exceptions.NoCollectionException;
import applisem.client.exceptions.NoComplementException;
import applisem.client.exceptions.NoFileException;
import applisem.client.exceptions.NoInBetweenException;
import applisem.client.exceptions.NoInputsException;
import applisem.client.exceptions.NoLinkException;
import applisem.client.exceptions.NoObjectException;
import applisem.client.exceptions.NoOutputsException;
import applisem.client.exceptions.NoParameterException;
import applisem.client.exceptions.NoQueryException;
import applisem.client.exceptions.NoQueryViewException;
import applisem.client.exceptions.NoRightsException;
import applisem.client.exceptions.NoRoleException;
import applisem.client.exceptions.NoSelectedCollectionException;
import applisem.client.exceptions.NoSessionException;
import applisem.client.exceptions.NoSubjectException;
import applisem.client.exceptions.NoSubjectFileException;
import applisem.client.exceptions.NoSubjectImageException;
import applisem.client.exceptions.NoUserException;
import applisem.client.exceptions.NoViewException;
import applisem.client.exceptions.PartialImportException;
import applisem.client.exceptions.PasswordLengthException;
import applisem.client.exceptions.SQLConnectionException;
import applisem.client.exceptions.StringLengthException;
import applisem.client.exceptions.TextLengthException;
import applisem.client.exceptions.TraceFinderException;
import applisem.client.exceptions.UnknownApplisemServerException;
import applisem.client.exceptions.UnknownException;
import applisem.client.requests.MsgAuth;
import applisem.client.requests.Request;
import static applisem.log.Applisem_Log.ApplisemLog.*;
import applisem.objects.Classify;
import applisem.objects.ConnexPartitions;
import applisem.objects.FileServerConf;
import applisem.objects.GeneratorSimulation;
import applisem.objects.GraphResult;
import applisem.objects.LinkAnalyseInfo;
import applisem.objects.LinkPartitions;
import applisem.objects.PathFinderResult;
import applisem.objects.QueryHighLight;
import applisem.objects.QueryParameterInfo;
import applisem.objects.QueryResults;
import applisem.objects.Rights;
import applisem.objects.SessionView;
import applisem.objects.SharedObject;
import applisem.objects.Statement;
import applisem.objects.SubjectAnalyseInfo;
import applisem.objects.SubjectRessemblance;
import applisem.objects.SubjectSheet;

/**
 * Class containing all operation between a client and the Server.
 *
 * THis is the LBO version.
 *
 * The MME version was made with poor knownledge in java. MMME Thought that
 * références to methode were stocked in the Object instance also in made the
 * numerous client methode part of a différent class défined as static THis
 * solution doesn't allow to access différent server in the same runtime. My
 * good knownledge of java allow me to affirm that methode référence are stored
 * in the class object witch is unique for every instance of object of the class
 *
 * I regroup the client and service in one class witch don't consume more memory
 * space. The compatibility is done by the getService Method witch return the
 * client session whitch is a service locator so in code using this no
 * modification are necessary to mihgrate from one version to another
 *
 * it extends abstract service locator for socket management it implements
 * ServiceLocator for backward compatibility
 *
 * @author LBO
 */
public class ClientSession extends AbstractServiceLocator implements ServiceLocator {

    /**
     * The session identifier on the server
     */
    String session_id;
    /**
     * the session object containing more information like the right on the
     * current collection
     */
    SessionView sessionObject;
    /**
     * the user of the session
     */
    String username;
    /**
     * the password of the user of the session
     */
    String password;
    /**
     * the current collection of the session
     */
    String collection;

    /**
     * the locale in the client side
     */
    Locale currentLocal;
	//final ServiceLocator service;

    //private final ClientSession service;
    /**
     * the default constructor this send the authentification message to the
     * server
     */
    public ClientSession(String host, int port, String user, String pass)
            throws UnknownApplisemServerException, ClassNotFoundException,
            IOException, AuthentificationException, SQLException {
        super(host, port);
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   ==> host = " + host);
            Log_Applisem("   ==> port = " + port);
            Log_Applisem("   ==> user = " + user);
            Log_Applisem("   ==> pass = " + pass);
            Log_Applisem("   ==> currentLocal = " + Locale.getDefault());
        }
        this.username = user;
        this.password = pass;
        //this.keepConnected = false;
        currentLocal = Locale.getDefault();
        sessionObject = createSession();
        Log_Applisem_Sortie();
    }

    /**
     * Deprecated constructor used when the persistant socket where used
     */
    public ClientSession(String host, int port, String user, String pass,
            boolean keepConnected) throws UnknownApplisemServerException,
            ClassNotFoundException, IOException, AuthentificationException,
            SQLException {
        super(host, port);
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   ==> host = " + host);
            Log_Applisem("   ==> port = " + port);
            Log_Applisem("   ==> user = " + user);
            Log_Applisem("   ==> pass = " + pass);
            Log_Applisem("   ==> currentLocal = " + Locale.getDefault());
            Log_Applisem("   ==> keepConnected = " + keepConnected);
        }
        this.username = user;
        this.password = pass;
        //this.keepConnected = keepConnected;
        currentLocal = Locale.getDefault();
        sessionObject = createSession();
        session_id = sessionObject.getID();
        //service = this;
        Log_Applisem_Sortie();
    }

    /**
     * the destruction method used to close session on server side when the
     * session on client side is destroyed
     */
    protected void finalize() throws Throwable {

        Log_Applisem_Entree();
        try {
            if (session_id != null) {
                closeSession(getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method used to keep compatibility with the MME version with dicision of
     * object
     *
     * @return this object in the interface ServiceLocator
     */
    public ServiceLocator getService() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   service = " + this);
            Log_Applisem_Sortie();
        }
        return this;
    }

    public void setCollection(String collection) {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   Collection = " + collection);
            Log_Applisem_Sortie();
        }
        this.collection = collection;
    }

    /**
     * get the current right of the user in the session
     *
     * @return
     */
    public Rights getRights() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   Rights = " + sessionObject.getRights());
            Log_Applisem_Sortie();
        }
        return sessionObject.getRights();
    }

    /**
     * only for backward compatibility
     *
     * @return
     */
    public boolean isKeepConnected() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem_Sortie();
        }
        return true;/*keepConnected;*/

    }

    /**
     * only for backward compatibility
     */
    public void setKeepConnected(boolean keepConnected) {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   setKeepConnected = " + keepConnected);
            Log_Applisem_Sortie();
        }
        //this.keepConnected = keepConnected;
    }

    /**
     * get the adresse of the server
     *
     * @return the server name
     */
    public String getHost() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   host = " + host);
            Log_Applisem_Sortie();
        }
        return host;
    }

    /**
     * get the server port
     *
     * @return the server port
     */
    public int getPort() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   port = " + port);
            Log_Applisem_Sortie();
        }
        return port;
    }

    /**
     * get the user of the session
     *
     * @return the user name
     */
    public String getUsername() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   username = " + username);
            Log_Applisem_Sortie();
        }
       return username;
    }

    /**
     * get te password of the current session
     *
     * @return the password of the session
     */
    public String getPassword() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   password = " + password);
            Log_Applisem_Sortie();
        }
        return password;
    }

    /**
     * set the id of the session
     *
     * @param id the new id
     */
    public void setID(String id) {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   id = " + id);
            Log_Applisem_Sortie();
        }
        this.session_id = id;
    }

    /**
     * get the current session id
     *
     * @return the current session id
     */
    public String getID() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   session_id = " + session_id);
            Log_Applisem_Sortie();
        }
        return session_id;
    }

    /**
     * get the current collection
     *
     * @return the current collection
     */
    public String getCollection() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   Collection = " + collection);
            Log_Applisem_Sortie();
        }
        return collection;
    }

    /**
     * set the locale
     *
     * @param l the new locale
     */
    public void setLocale(Locale l) {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem_Sortie();
        }
        currentLocal = l;
    }

    /**
     * get the session locale
     *
     * @return the locale
     */
    public Locale getLocale() {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   Locale = " + currentLocal);
            Log_Applisem_Sortie();
        }
        return currentLocal;
    }

    /**
     * internal method used to create a session this call a special
     * Communication message with the server
     *
     * @return the session view corresponding to the created session
     * @throws ClassNotFoundException exception thrown if communication error
     * occure
     * @throws UnknownApplisemServerException exception thrown if the connection
     * fail
     * @throws IOException exception thrown if communication error occure
     * @throws AuthentificationException exception thrown if the
     * authentification fail on server
     * @throws SQLException exception thrown if error accurs with the database
     */
    public SessionView createSession() throws ClassNotFoundException,
            UnknownApplisemServerException, IOException,
            AuthentificationException, SQLException {
        Object obj = null;
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("      ==> host = " + host);
            Log_Applisem("      ==> port = " + port);
            Log_Applisem("(get) ==> host = " + getHost());
            Log_Applisem("(get) ==> port = " + getPort());
            Log_Applisem("(get) ==> user = " + getUsername());
            Log_Applisem("(get) ==> mdp  = " + getPassword());
        }
        MsgAuth msg_auth = new MsgAuth("createSession", getHost(), getPort(), getUsername(), getPassword(), true);
        Socket s = connect();
        writeToServer(s, msg_auth);
        obj = readFromServer(s);
        disconnect(s);
        if (obj.equals("AuthentificationException")) {
            throw new AuthentificationException();
        }
        if (obj.equals("AuthentificationException")) {
            throw new AuthentificationException();
        }
        if (obj.equals("ClassNotFoundException")) {
            throw new ClassNotFoundException();
        }
        if (obj.equals("UnknownApplisemServerException")) {
            throw new UnknownApplisemServerException();
        }
        if (obj.equals("AuthentificationException")) {
            throw new AuthentificationException();
        }
        if (obj.equals("SQLException")) {
            throw new SQLException();
        }
        if (obj.equals("IOException")) {
            throw new IOException();
        }
        SessionView session_v = (SessionView) obj;
        session_id = session_v.getID();
        Log_Applisem_Sortie();
        return session_v;
    }

    /**
     * Operation to manualy close the session on the server
     *
     * @param id the id of the session to close
     * @throws IOException exception thrown if communication error occur
     * @throws ClassNotFoundException exception thrown if communication error
     * occur
     * @throws NoSessionException exception thrown if the session doesn't exist
     * on the server
     * @throws UnknownApplisemServerException exception thrown if connection to
     * the server fail
     */
    public void closeSession(String id) throws IOException,
            ClassNotFoundException, NoSessionException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(getID(), "closeSession");
        Socket s = connect();
        writeToServer(s, request);
        readFromServer(s);
        disconnect(s);
        session_id = null;
        Log_Applisem_Sortie();
    }

    /**
     * return the current session view of the session
     *
     * @return the session view Object
     */
    public SessionView getSessionView() {

        Log_Applisem_Entree();
        Log_Applisem_Sortie();
        return sessionObject;
    }

    /**
     * Method witch verify the existance of a session
     *
     * @throws NoSessionException exception thrown if the session doesn't exist
     */
    public void hasLocaleSession(String session_id) throws NoSessionException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("    ==> session_id : " + session_id);
        }
        if (session_id == null) {
            throw new NoSessionException("closed session");
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method witch return the list of active session from the server
     *
     * @param session_id the current session id
     */
    public String[] listSessions(String session_id) throws NoSessionException,
            ClassNotFoundException, IOException, UnknownApplisemServerException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   ==> session_id : " + session_id);
        }
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSessions");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] sessions = (String[]) obj;
            if (Log_Applisem_Actif()) {
                Log_Applisem("   ==> sessions : " + sessions);
                Log_Applisem_Sortie();
            }
            return sessions;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else {
                Log_Applisem_Sortie();
                return null;
            }
        }
    }

    /**
     * Method witch return number of active session from the server
     *
     * @param session_id the current session id
     */
    public int countSessions(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException {
        if (Log_Applisem_Actif()) { 
            Log_Applisem_Entree();
            Log_Applisem("   ==> session_id : " + session_id);
        }
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countSessions");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            String[] sessions = (String[]) obj;
            if (Log_Applisem_Actif()) {
                Log_Applisem("   ==> Nb Sessions : " + count);
                Log_Applisem_Sortie();
            }
            return count.intValue();
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else {
                Log_Applisem_Sortie();
                return 0;
            }
        }
    }

    /**
     * Method witch delete an active session from the server
     *
     * @param session_id the current session id
     * @param id the id of the session to remove
     */
    public void deleteSession(String session_id, String id)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   ==> session_id : " + session_id);
            Log_Applisem("   ==> id : " + id);
        }
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSession", id);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            Log_Applisem_Sortie();
            throw new NoSessionException();
        } else {
            Log_Applisem_Sortie();
            return;
        }
    }

    /**
     * Method witch delete all active session from the server
     *
     * @param session_id the current session id
     */
    public void deleteAllSessions(String session_id) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   ==> session_id : " + session_id);
        }
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteAllSessions");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            Log_Applisem_Sortie();
            throw new NoSessionException();
        } else {
            Log_Applisem_Sortie();
            return;
        }
    }

    /**
     * Method witch create a new collection on the server
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @param server the name of the server used to store files
     */
    public void createSQLCollection(String session_id, String name,
            String server) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            ExistCollectionException, SQLException, NoCollectionException,
            ExistCategoryException, ExistSameObjectException, NoLinkException,
            StringLengthException, NoCategoryException, SQLConnectionException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem("   ==> session_id : " + session_id);
            Log_Applisem(" name = " + name);
            Log_Applisem(" server = " + server);
        }
        hasLocaleSession(session_id);
        if (Log_Applisem_Actif()) Log_Applisem(" hasLocalSession" + session_id);
        Request request = new Request(session_id, "createSQLCollection", name,server);
        if (Log_Applisem_Actif()) Log_Applisem(" Request " + session_id + " " + name + " " + server);
        Socket socket = connect();
        if (Log_Applisem_Actif()) Log_Applisem(" connect " + socket);
        writeToServer(socket, request);
        if (Log_Applisem_Actif()) Log_Applisem(" writeToServer " + request);
        Object obj = readFromServer(socket);
        if (Log_Applisem_Actif()) Log_Applisem(" Object " + obj);
        disconnect(socket);
        if (Log_Applisem_Actif()) Log_Applisem(" disconnect " + session_id);
        String res = (String) obj;
        if (Log_Applisem_Actif()) Log_Applisem(" res " + res);
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("ExistCollectionException")) {
            throw new ExistCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistCategoryException")) {
            throw new ExistCategoryException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("NoCategoryException")) {
            throw new NoCategoryException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method witch create a new collection on the server using an other
     * collection as model
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @param server the name of the server used to store files
     * @param model the name of the model collection
     */
    public void createSQLCollection(String session_id, String name,
            String server, String model) throws UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSessionException,
            ExistCollectionException, SQLException, NoCollectionException,
            ExistCategoryException, ExistSameObjectException, NoLinkException,
            StringLengthException, NoCategoryException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSQLCollection", name, server, model);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("ExistCollectionException")) {
            throw new ExistCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistCategoryException")) {
            throw new ExistCategoryException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("NoCategoryException")) {
            throw new NoCategoryException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method witch verify the existance of a collection
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @return true if the collection existe
     */
    public boolean existCollection(String session_id, String name)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existCollection", name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (Log_Applisem_Actif()) Log_Applisem(" res (Vrai ou Faux) = " + res);
        if (res.equals("true")) {
            Log_Applisem_Sortie();
            return true;
        } else {
            Log_Applisem_Sortie();
            return false;
        }
    }

    /**
     * Method witch list all collection on the server
     *
     * @param session_id the current session id
     * @return an array containing the name of every collection
     */
    public String[] listCollections(String session_id)
            throws NoSessionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCollections");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] collections = (String[]) obj;
            if (Log_Applisem_Actif()) {
                Log_Applisem("Collections = " + collections);
                Log_Applisem_Sortie();
            }
            return collections;
        } else {
            String exp = (String) obj;
            if (exp.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exp.equals("UnknownApplisemServerException")) {
                throw new UnknownApplisemServerException();
            } else if (exp.equals("ClassNotFoundException")) {
                throw new ClassNotFoundException();
            } else if (exp.equals("IOException")) {
                throw new IOException();
            } else {
                return null;
            }
        }
    }

    /**
     * Method witch delete a collection on the server
     *
     * @param session_id the current session id
     * @param name the name of the collection
     */
    public void deleteCollection(String session_id, String name)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoCollectionException,
            SQLException, NoRoleException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteCollection", name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method witch verify the connection to the server
     *
     * @param session_id the current session id
     * @return true if the connection exist
     */
    public boolean isConnected(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        boolean connected = false;
        if (session_id == null) {
            return false;
        }
        Request request = new Request(session_id, "isConnected");
        Socket socket = connect();
        if (Log_Applisem_Actif()) {
            Log_Applisem("Info Socket  : InputStream = " + socket.getInputStream());
            Log_Applisem("             : LocalPort = " + socket.getLocalPort());
            Log_Applisem("             : OutputStream = " + socket.getOutputStream());
            Log_Applisem("             : Port = " + socket.getPort());
            Log_Applisem("             : RemoteSocketAddress = " + socket.getRemoteSocketAddress());
            Log_Applisem("             : isConnected = " + socket.isConnected());
            Log_Applisem("Info Request : ID = " + request.getID());
            Log_Applisem("             : Operation = " + request.getOperation());
            Log_Applisem("             : V1        = " + request.getV1());
            Log_Applisem("             : V2        = " + request.getV2());
            Log_Applisem("             : V3        = " + request.getV3());
            Log_Applisem("             : V4        = " + request.getV4());
            Log_Applisem("             : V5        = " + request.getV5());
            Log_Applisem("             : V6        = " + request.getV6());
            Log_Applisem("             : V7        = " + request.getV7());
            Log_Applisem("             : V8        = " + request.getV8());
        }
        writeToServer(socket, request);
        if (Log_Applisem_Actif()) Log_Applisem("retour de writeToServer ------------------ ");
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (Log_Applisem_Actif()) {
            Log_Applisem("Info res  : equals = " + res.equals("true"));
            char cRes_mph[] = res.toCharArray();
            int lg_res = res.length();
            String sRes_mph = "";
            for (int i = 0; i < lg_res; i++) {
                sRes_mph = sRes_mph + String.valueOf(cRes_mph[i]);
            }
            Log_Applisem("          : res = " + sRes_mph);
        }
        if (res.equals("true")) {
            connected = true;
        } else if (res.equals("false")) {
            connected = false;
        }
        Log_Applisem_Sortie();
        return connected;
    }

    /**
     * Unpossible methode
     */
    public void renameCollection(String session_id, String src, String dst)
            throws NoSessionException, NoCollectionException,
            ExistCollectionException, SQLException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        // rename
        Request request1 = new Request(session_id, "renameCollection", src, dst);
        Socket socket = connect();
        writeToServer(socket, request1);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;

        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("ExistCollectionException")) {
            throw new ExistCollectionException();
        }

        // on doit selectioner la nouvelle collection.
        Request request2 = new Request(session_id, "selectCollection", dst);
        socket = connect();
        writeToServer(socket, request2);
        obj = readFromServer(socket);
        disconnect(socket);
        res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Unused methode
     */
    public void loadCollection(String session_id, String name)
            throws NoCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "loadCollection", name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("UnknownApplisemServerException")) {
            throw new UnknownApplisemServerException();
        } else if (res.equals("ClassNotFoundException")) {
            throw new ClassNotFoundException();
        } else if (res.equals("IOException")) {
            throw new IOException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method witch import a collection from a file into the server
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @param file_name the path to the file
     */
    public void importCollection(String session_id, String name,
            String file_name) throws UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSessionException,
            NoSelectedCollectionException, ImportException,
            PartialImportException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "importCollection", name, file_name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ImportException")) {
            throw new ImportException();
        } else if (res.equals("PartialImportException")) {
            throw new PartialImportException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method witch export a collection from the server to a file
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @param file_name the path of the file
     */
    public void createAndImportCollection(String session_id, String name,
            String file_name) throws UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSessionException,
            NoCollectionException, ImportException, ExistCollectionException,
            PartialImportException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createAndImportCollection", name, file_name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ImportException")) {
            throw new ImportException();
        } else if (res.equals("PartialImportException")) {
            throw new PartialImportException();
        } else if (res.equals("ExistCollectionException")) {
            throw new ExistCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * Method witch export the model of the current collection
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @return the file of the export as an byte array
     */
    public byte[] exportCollectionModel(String session_id)
            throws UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSessionException, NoSelectedCollectionException,
            ExportException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "exportCollectionModel");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof byte[]) {
            if (Log_Applisem_Actif()) {
                Log_Applisem("obj = " + obj);
                Log_Applisem_Sortie();
            }
            return (byte[]) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (Log_Applisem_Actif()) {
                Log_Applisem("res = " + res);
                Log_Applisem_Sortie();
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("ExportException")) {
                throw new ExportException();
            }
        }
        throw new ExportException();
    }

    /**
     * Method witch import a model in the current collection
     *
     * @param session_id the current session id
     * @param model the byte array containing the file of the model
     * @return a string validating the operation
     */
    public String importCollectionModel(String session_id, byte[] model)
            throws UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSessionException, NoSelectedCollectionException,
            ImportException, PartialImportException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "importCollectionModel");
        request.setObject(model);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("ImportException")) {
            throw new ImportException();
        } else if (res.equals("PartialImportException")) {
            throw new PartialImportException();
        }
        Log_Applisem_Sortie();
        return res;
    }

    /**
     * Method witch export the queries of the current collection
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @return the file of the export as an byte array
     */
    public byte[] exportCollectionQueries(String session_id)
            throws UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSessionException, NoSelectedCollectionException,
            ExportException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "exportCollectionQueries");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof byte[]) {
            if (Log_Applisem_Actif()) {
                Log_Applisem("obj = " + obj);
                Log_Applisem_Sortie();
            }
            return (byte[]) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (Log_Applisem_Actif()) {
                Log_Applisem("res = " + res);
                Log_Applisem_Sortie();
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("ExportException")) {
                throw new ExportException();
            }
        }
        throw new ExportException();
    }

    /**
     * Method witch import queries in the current collection
     *
     * @param session_id the current session id
     * @param model the byte array containing the file of the queries
     * @return a string validating the operation
     */
    public String importCollectionQueries(String session_id, byte[] model)
            throws UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSessionException, NoSelectedCollectionException,
            ImportException, PartialImportException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "importCollectionQueries");
        request.setObject(model);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("ImportException")) {
            throw new ImportException();
        } else if (res.equals("PartialImportException")) {
            throw new PartialImportException();
        }
        Log_Applisem_Sortie();
        return res;
    }

    /**
     * Method witch export a collection from the server to a file
     *
     * @param session_id the current session id
     * @param name the name of the collection
     * @param file the path of the file
     */
    public void exportCollection(String session_id, String name, String file)
            throws UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSessionException, NoCollectionException,
            ExportException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "exportCollection", name, file);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("FileNotFoundException")) {
            throw new FileNotFoundException();
        } else if (res.equals("IOException")) {
            throw new IOException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExportException")) {
            throw new ExportException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * unused method
     */
    public void saveCollection(String session_id, String collection)
            throws NoSessionException, NoCollectionException, SQLException,
            ClassNotFoundException, IOException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "saveCollection", collection);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("FileNotFoundException")) {
            throw new IOException();
        } else if (res.equals("IOException")) {
            throw new IOException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to change the selected collection
     *
     * @param session_id the id of the current session
     * @param collection the name of the collection
     */
    public void selectCollection(String session_id, String collection)
            throws NoSessionException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem(" session_id = " + session_id);
            Log_Applisem(" collection = " + collection);
        }
        hasLocaleSession(session_id);
        if (!existCollection(session_id, collection)) {
            throw new NoCollectionException();
        }
        Request request = new Request(session_id, "selectCollection", collection);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        /*
         * if(obj instanceof String []){ String [] rights = (String []) obj;
         * return rights; } else {
         */
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (exception.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (exception.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        /* } */
        /* return null ; */
        Log_Applisem_Sortie();
    }

    /**
     * method to get the selected collection
     *
     * @param session_id the id of the current session
     * @return the name of the selected collection
     */
    public String selectedCollection(String session_id)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {
        if (Log_Applisem_Actif()) {
            Log_Applisem_Entree();
            Log_Applisem(" session_id = " + session_id);
        }
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "selectedCollection");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String retour = (String) obj;
        if (Log_Applisem_Actif()) Log_Applisem("Retour = " + retour);
        if (retour.equals("NoSessionException")) {
            Log_Applisem_Sortie();
            throw new NoSessionException();
        } else {
            Log_Applisem_Sortie();
            return retour;
        }
    }

    /**
     * method to create a subject in the selected collection
     *
     * @param session_id the id of the current session
     * @param subject the name of the subject
     */
    public void createSubject(String session_id, String subject)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, ExistSubjectException, SQLException,
            StringLengthException, NoRightsException, ExistSameObjectException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubject", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("ExistSubjectException")) {
            throw new ExistSubjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("NoRightsExcetion")) {
            throw new NoRightsException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to create subjects in the selected collection
     *
     * @param session_id the id of the current session
     * @param subjects the name of the subjects as an array
     */
    public void createSubjects(String session_id, String[] subjects)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, ExistSubjectException, SQLException,
            NoRightsException, StringLengthException, NoCollectionException,
            ExistSameObjectException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjects", subjects);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("ExistSubjectException")) {
            throw new ExistSubjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsExcetion")) {
            throw new NoRightsException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to create synonym to a subject in the selected collection
     *
     * @param session_id the id of the current session
     * @param subject the name of the subject
     * @param subject the synonym of the subject
     */
    public void createSubjectSynonym(String session_id, String subject,
            String synonym) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException,
            StringLengthException, SQLException, NoRightsException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectSynonym", subject, synonym);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoSubjectException")) {
            throw new NoSubjectException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsExcetion")) {
            throw new NoRightsException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to add an input to a TRaceFinder in the selected collection
     *
     * @param session_id the id of the current session
     * @param inbetween the name of the tracefinder
     * @param subject the subject
     */
    public void createInBetweenInput(String session_id, String inbetween,
            String subject) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoInBetweenException,
            NoObjectException, ExistInputException, ExistOutputException,
            StringLengthException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        Request request = new Request(session_id, "createInBetweenInput", inbetween, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoInBetweenException")) {
            throw new NoInBetweenException();
        } else if (res.equals("NoObjectException")) {
            throw new NoObjectException();
        } else if (res.equals("ExistInputException")) {
            throw new ExistInputException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistOutputException")) {
            throw new ExistOutputException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to add an output to a TRaceFinder in the selected collection
     *
     * @param session_id the id of the current session
     * @param inbetween the name of the tracefinder
     * @param subject the subject
     */
    public void createInBetweenOutput(String session_id, String inbetween,
            String subject) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoInBetweenException,
            NoObjectException, ExistOutputException, ExistInputException,
            StringLengthException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createInBetweenOutput", inbetween, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoInBetweenException")) {
            throw new NoInBetweenException();
        } else if (res.equals("NoObjectException")) {
            throw new NoObjectException();
        } else if (res.equals("ExistOutputException")) {
            throw new ExistOutputException();
        } else if (res.equals("ExistInputException")) {
            throw new ExistInputException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to add an user to the server
     *
     * @param session_id the id of the current session
     * @param name the name of the user
     */
    public void createUser(String session_id, String name)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, StringLengthException, SQLException,
            ExistUserException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createUser", name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("ExistUserException")) {
            throw new ExistUserException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to add an role to the server
     *
     * @param session_id the id of the current session
     * @param name the name of the role
     */
    public void createRole(String session_id, String name)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException, StringLengthException,
            SQLException, ExistSameObjectException, ExistRoleException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createRole", name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("ExistRoleException")) {
            throw new ExistRoleException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to verify the existence of a subject
     *
     * @param session_id the id of the current session
     * @param subject the name of the subject to verify
     * @return true if the subject exist
     */
    public boolean existSubject(String session_id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoRightsException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existSubject", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (Log_Applisem_Actif()) Log_Applisem("res = " + res);
        if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoRightsExcetion")) {
            throw new NoRightsException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("true")) {
            Log_Applisem_Sortie();
            return true;
        } else {
            Log_Applisem_Sortie();
            return false;
        }
    }

    /**
     * method to verify the existence of a user
     *
     * @param session_id the id of the current session
     * @param user the name of the user to verify
     * @return true if the user exist
     */
    public boolean existUser(String session_id, String user)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existUser", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (Log_Applisem_Actif()) Log_Applisem("res = " + res);
        if (res.equals("true")) {
            Log_Applisem_Sortie();
            return true;
        } else {
            Log_Applisem_Sortie();
            return false;
        }
    }

    /**
     * method to verify the existence of a role
     *
     * @param session_id the id of the current session
     * @param subject the name of the role to verify
     * @return true if the role exist
     */
    public boolean existRole(String session_id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existRole", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (Log_Applisem_Actif()) Log_Applisem("res = " + res);
        if (res.equals("true")) {
            Log_Applisem_Sortie();
            return true;
        } else {
            Log_Applisem_Sortie();
            return false;
        }
    }

    /**
     * method to rename a subject
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     * @throws applisem.client.exceptions.NoSessionException
     * @throws applisem.client.exceptions.UnknownApplisemServerException
     * @throws java.lang.ClassNotFoundException
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws applisem.client.exceptions.NoSubjectException
     * @throws applisem.client.exceptions.ExistSubjectException
     * @throws applisem.client.exceptions.NoRightsException
     * @throws applisem.client.exceptions.StringLengthException
     * @throws applisem.client.exceptions.ExistSameObjectException
     * @throws applisem.client.exceptions.NoCollectionException
     * @throws applisem.client.exceptions.NoSelectedCollectionException
     * @throws applisem.client.exceptions.SQLConnectionException
     */
    @Override
    public void renameSubject(String session_id, String from, String to)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, SQLException,
            NoSubjectException, ExistSubjectException, NoRightsException,
            StringLengthException, ExistSameObjectException,
            NoCollectionException, NoSelectedCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameSubject", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "ExistSubjectException":
                throw new ExistSubjectException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "StringLengthException":
                throw new StringLengthException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    /**
     * method to rename a TraceFinder
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     * @throws applisem.client.exceptions.NoSelectedCollectionException
     * @throws applisem.client.exceptions.NoSessionException
     * @throws applisem.client.exceptions.StringLengthException
     * @throws applisem.client.exceptions.NoInBetweenException
     * @throws applisem.client.exceptions.ExistInBetweenException
     * @throws applisem.client.exceptions.UnknownApplisemServerException
     * @throws java.lang.ClassNotFoundException
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws applisem.client.exceptions.ExistSameObjectException
     * @throws applisem.client.exceptions.NoCollectionException
     * @throws applisem.client.exceptions.SQLConnectionException
     */
    @Override
    public void renameInBetween(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            StringLengthException, NoInBetweenException,
            ExistInBetweenException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, SQLException,
            ExistSameObjectException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameInBetween", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoInBetweenException":
                throw new NoInBetweenException();
            case "ExistInBetweenException":
                throw new ExistInBetweenException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void deleteSubject(String session_id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoRightsException, SQLException,
            NoCollectionException, NoLinkException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubject", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void deleteSubjectSynonym(String session_id, String subject,
            String synonym) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoSubjectException,
            NoRightsException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubjectSynonym", subject, synonym);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void deleteInBetweenInput(String session_id, String inbetween,
            String subject) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoObjectException,
            NoInBetweenException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteInBetweenInput", inbetween, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoObjectException":
                throw new NoObjectException();
            case "NoInBetweenException":
                throw new NoInBetweenException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void deleteInBetweenOutput(String session_id, String inbetween,
            String subject) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoObjectException,
            NoInBetweenException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteInBetweenOutput", inbetween, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoObjectException":
                throw new NoObjectException();
            case "NoInBetweenException":
                throw new NoInBetweenException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public Object[][] listSubjects(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoRightsException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjects", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        } else {
            Object[][] subjects = (Object[][]) obj;
            Log_Applisem_Sortie();
            return subjects;
        }
    }

    @Override
    public String[] listSubjectSynonyms(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException, NoRightsException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectSynonyms", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] synonyms = (String[]) obj;
            Log_Applisem_Sortie();
            return synonyms;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] listInBetweenInputs(String session_id, String inbetween)
            throws NoSelectedCollectionException, NoSessionException,
            NoInBetweenException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listInBetweenInputs", inbetween);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoInBetweenException":
                    throw new NoInBetweenException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] listInBetweenOutputs(String session_id, String inbetween)
            throws NoSelectedCollectionException, NoSessionException,
            NoInBetweenException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listInBetweenOutputs", inbetween);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoInBetweenException":
                    throw new NoInBetweenException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param from
     * @param number
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     */
    @Override
    public Object[][] listUsers(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            ClassNotFoundException, UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUsers", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] users = (Object[][]) obj;
            Log_Applisem_Sortie();
            return users;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param from
     * @param number
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Object[][] listRoles(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listRoles", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] users = (Object[][]) obj;
            Log_Applisem_Sortie();
            return users;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Object[][] listRoles(String session_id, String orderBy,
            String orderType) throws NoSelectedCollectionException,
            NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listRoles", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] users = (Object[][]) obj;
            Log_Applisem_Sortie();
            return users;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     */
    @Override
    public Object[][] listSubjects(String session_id, String orderBy,
            String orderType) throws NoSelectedCollectionException,
            NoSessionException, NoCollectionException, ClassNotFoundException,
            IOException, NoRightsException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjects", "0", "0", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Object[][] listUsers(String session_id, String orderBy,
            String orderType) throws NoSelectedCollectionException,
            NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUsers", "0", "0", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] users = (Object[][]) obj;
            Log_Applisem_Sortie();
            return users;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Object[][] listUsers(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUsers", "0", "0", "0", "0");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            Object[][] users = (Object[][]) obj;
            Log_Applisem_Sortie();
            return users;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param user
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoUserException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public String[] listUserRoles(String session_id, String user)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUserRoles", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] users = (String[]) obj;
            Log_Applisem_Sortie();
            return users;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoUserException":
                    throw new NoUserException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param role
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws ClassNotFoundException
     * @throws NoRoleException
     * @throws IOException
     * @throws UnknownApplisemServerException
     */
    @Override
    public String[] listRoleCollections(String session_id, String role)
            throws NoSelectedCollectionException, NoSessionException,
            ClassNotFoundException, NoRoleException, IOException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listRoleCollections", role);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] collections = (String[]) obj;
            Log_Applisem_Sortie();
            return collections;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRoleException":
                    throw new NoRoleException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param exp
     * @param in
     * @return
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoLinkException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public String[] directSearch(String session_id, String exp, String in)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoLinkException, SQLException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "directSearch", exp, in);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "SQLException":
                    throw new SQLException();
                case "SQLConnectionException":
                    throw new SQLConnectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] directSearch(String session_id, String exp, String in,
            int from, int number) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoSubjectException,
            NoLinkException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "directSearch", exp, in, String.valueOf(from), String.valueOf(number));
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "SQLException":
                    throw new SQLException();
                case "SQLConnectionException":
                    throw new SQLConnectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public SharedObject[] searchDico(String session_id, String exp)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, IOException, ClassNotFoundException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "searchDico", exp);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof SharedObject[]) {
            SharedObject[] subjects = (SharedObject[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param exp
     * @param startWith
     * @return
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoCollectionException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     */
    @Override
    public String[] searchSubjects(String session_id, String exp,
            String startWith) throws NoSessionException,
            NoSelectedCollectionException, NoCollectionException, IOException,
            ClassNotFoundException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "searchSubjects", exp, startWith);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] searchLinks(String session_id, String exp, String startWith)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, IOException, ClassNotFoundException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "searchLinks", exp, startWith);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] searchCategories(String session_id, String exp,
            String startWith) throws NoSessionException,
            NoSelectedCollectionException, NoCollectionException, IOException,
            ClassNotFoundException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "searchCategories", exp, startWith);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] searchBoards(String session_id, String exp, String startWith)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, IOException, ClassNotFoundException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "searchBoards", exp, startWith);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public int countCollections(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countCollections");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            // String exception = (String) obj;
        }
        Log_Applisem_Sortie();
        return 0;
    }

    /**
     *
     * @param session_id
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws NoSelectedCollectionException
     * @throws NoRightsException
     */
    @Override
    public int countSubjects(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException, NoCollectionException,
            NoSelectedCollectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countSubjects");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            }
            switch (exception) {
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    @Override
    public int countSubjectSynonyms(String session_id, String subject)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException, NoCollectionException,
            NoSubjectException, NoSelectedCollectionException,
            NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countSubjectSynonyms", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param cl
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoBoardException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void classifiesToBoards(String session_id, Classify[] cl)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException,
            NoBoardException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "classifiesToBoards", cl);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoBoardException":
                throw new NoBoardException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param cl
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoCategoryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void classifies(String session_id, Classify[] cl)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "classifies", cl);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void classify(String session_id, String category, String subject)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "classify", category, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void classifyBoard(String session_id, String board, String subject)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException,
            NoBoardException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "classifyBoard", board, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoBoardException":
                throw new NoBoardException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
    }

    /**
     *
     * @param session_id
     * @param tracefinder
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoInBetweenException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void addTraceFinderToUserPreference(String session_id,
            String tracefinder) throws UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoInBetweenException, SQLException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "addTraceFinderToUserPreference", tracefinder);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoQueryException":
                throw new NoInBetweenException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "SQLException":
                throw new SQLException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param query
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoQueryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void addQueryToUserPreference(String session_id, String query)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException, SQLException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "addQueryToUserPreference", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoQueryException":
                throw new NoQueryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException(":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param category
     * @param subject
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoCategoryException
     * @throws NoRightsException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void deClassify(String session_id, String category, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoCategoryException, NoRightsException,
            SQLException, NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deClassify", category, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void deleteTraceFinderFromUserPreference(String session_id,
            String tracefinder) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoInBetweenException,
            SQLException, NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteTraceFinderFromUserPreference", tracefinder);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoInBetweenException":
                throw new NoInBetweenException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param query
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoQueryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteQueryFromUserPreference(String session_id, String query)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoQueryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteQueryFromUserPreference", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoQueryException":
                throw new NoQueryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param board
     * @param subject
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoBoardException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void deClassifyBoard(String session_id, String board, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoBoardException, SQLException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deClassifyBoard", board, subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoBoardException":
                throw new NoBoardException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param text
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoRightsException
     * @throws NoLinkException
     * @throws TextLengthException
     * @throws SQLException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void setSubjectText(String session_id, String subject, String text)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoRightsException, NoLinkException,
            TextLengthException, SQLException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setSubjectText", subject, text);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "TextLengthException":
                throw new TextLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoRightsException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws NoLinkException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteSubjectText(String session_id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoRightsException, SQLException,
            NoCollectionException, NoLinkException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubjectText", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoRightsException
     * @throws SQLException
     * @throws SQLConnectionException
     */
    @Override
    public String getSubjectText(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoRightsException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getSubjectText", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "SQLException":
                throw new SQLException();
            default:
                Log_Applisem_Sortie();
                return res;
        }
    }

    /**
     *
     * @param session_id
     * @param host
     * @param port
     * @return
     * @throws NoSessionException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws UnknownApplisemServerException
     */
    @Override
    public String isFileServerStarted(String session_id, String host,
            String port) throws NoSessionException, ClassNotFoundException,
            IOException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "isFileServerStarted", host, port);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else {
            Log_Applisem_Sortie();
            return res;
        }
    }

    /**
     *
     * @param session_id
     * @param name
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoParameterException
     */
    @Override
    public String getParameter(String session_id, String name)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoParameterException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getParameter", name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoParameterException":
                throw new NoParameterException();
            default:
                Log_Applisem_Sortie();
                return res;
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoLinkException
     * @throws NoRightsException
     * @throws StringLengthException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws ExistSameObjectException
     * @throws SQLConnectionException
     */
    @Override
    public void createSubjectFile(String session_id, String subject, String path)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException, NoLinkException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectFile", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoRightsException
     * @throws StringLengthException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws ExistSameObjectException
     * @throws NoLinkException
     * @throws SQLConnectionException
     */
    @Override
    public void createSubjectImage(String session_id, String subject,
            String path) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException, NoLinkException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectImage", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "MalformedURLException":
                throw new MalformedURLException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
            case "NoLinkException":
                throw new NoLinkException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoRightsException
     * @throws StringLengthException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws ExistSameObjectException
     * @throws NoLinkException
     * @throws SQLConnectionException
     */
    @Override
    public void createSubjectURL(String session_id, String subject, String path)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException, NoLinkException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectURL", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "MalformedURLException":
                throw new MalformedURLException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
            case "NoLinkException":
                throw new NoLinkException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws NoSubjectException
     * @throws NoRightsException
     * @throws NoCollectionException
     * @throws SQLException
     * @throws NoFileException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteSubjectFile(String session_id, String subject, String path)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoLinkException, NoSubjectException, NoRightsException,
            NoCollectionException, SQLException, NoFileException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubjectFile", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "NoPathException":
                throw new NoFileException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoRightsException
     */
    @Override
    public String[] listUserPreferedTraceFinders(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUserPreferedTraceFinders");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] files = (String[]) obj;
            Log_Applisem_Sortie();
            return files;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoRightsException
     */
    @Override
    public String[] listUserPreferedQueries(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUserPreferedQueries");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] files = (String[]) obj;
            Log_Applisem_Sortie();
            return files;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoRightsException
     */
    @Override
    public String[] listSubjectFiles(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectFiles", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] files = (String[]) obj;
            Log_Applisem_Sortie();
            return files;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param associated
     * @return
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoFileException
     * @throws NoRightsException
     * @throws NoCollectionException
     * @throws ClassNotFoundException
     */
    @Override
    public String[] listAssociedSubject(String session_id, String associated)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, NoSessionException, NoSelectedCollectionException,
            NoFileException, NoRightsException, NoCollectionException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listAssociedSubject", associated);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoFileException":
                    throw new NoFileException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param file
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoFileException
     * @throws NoRightsException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws NoCollectionException
     */
    @Override
    public String[] listFileSubjects(String session_id, String file)
            throws NoSelectedCollectionException, NoSessionException,
            NoFileException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listFileSubjects", file);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoFileException":
                    throw new NoFileException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param file
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoRightsException
     * @throws NoCollectionException
     */
    @Override
    public int countAssociedSubjects(String session_id, String file)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoRightsException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countAssociedSubjects", file);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
            }
        }
        Log_Applisem_Sortie();
        return 0;
    }

    /**
     *
     * @param session_id
     * @param file
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoRightsException
     * @throws NoCollectionException
     */
    @Override
    public int countFileSubjects(String session_id, String file)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoRightsException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countFileSubjects", file);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
            }
        }
        Log_Applisem_Sortie();
        return 0;
    }

    /**
     *
     * @param session_id
     * @param rss
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoRightsException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws NoCollectionException
     */
    @Override
    public String[] listRSSSubjects(String session_id, String rss)
            throws NoSelectedCollectionException, NoSessionException,
            NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listRSSSubjects", rss);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param rss
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoRightsException
     * @throws NoCollectionException
     */
    @Override
    public int countRSSSubjects(String session_id, String rss)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoRightsException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countRSSSubjects", rss);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
            }
        }
        Log_Applisem_Sortie();
        return 0;
    }

    /**
     *
     * @param session_id
     * @param file
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoRightsException
     * @throws NoCollectionException
     */
    @Override
    public int countImageSubjects(String session_id, String file)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoRightsException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countImageSubjects", file);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
            }
        }
        Log_Applisem_Sortie();
        return 0;
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoLinkException
     * @throws NoRightsException
     * @throws SQLException
     * @throws StringLengthException
     * @throws NoCollectionException
     * @throws ExistSameObjectException
     * @throws SQLConnectionException
     */
    @Override
    public void createSubjectRss(String session_id, String subject, String path)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException, NoLinkException,
            NoRightsException, SQLException, StringLengthException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectRss", subject,  path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "StringLengthException":
                throw new StringLengthException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoLinkException
     * @throws NoSubjectFileException
     * @throws NoRightsException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws NoFileException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteSubjectRss(String session_id, String subject, String path)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoLinkException, NoSubjectFileException,
            NoRightsException, SQLException, NoCollectionException,
            NoFileException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubjectRss", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoSubjectFileException":
                throw new NoSubjectFileException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoPathException":
                throw new NoFileException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoRightsException
     */
    @Override
    public String[] listSubjectRss(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectRss", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] files = (String[]) obj;
            Log_Applisem_Sortie();
            return files;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoLinkException
     * @throws NoSubjectImageException
     * @throws NoRightsException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws MalformedURLException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteSubjectURL(String session_id, String subject, String path)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoLinkException, NoSubjectImageException,
            NoRightsException, SQLException, NoCollectionException,
            MalformedURLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubjectURL", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoSubjectImageException":
                throw new NoSubjectImageException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "MalformedURLException":
                throw new MalformedURLException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param path
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoSubjectException
     * @throws NoLinkException
     * @throws NoSubjectImageException
     * @throws NoRightsException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws MalformedURLException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteSubjectImage(String session_id, String subject,
            String path) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoSubjectException,
            NoLinkException, NoSubjectImageException, NoRightsException,
            SQLException, NoCollectionException, MalformedURLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubjectImage", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoSubjectImageException":
                throw new NoSubjectImageException();
            case "NoRightsExcetion":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "MalformedURLException":
                throw new MalformedURLException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public String[] ListSubjectImages(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException, NoRightsException,
            UnknownApplisemServerException, IOException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectImages", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] images = (String[]) obj;
            Log_Applisem_Sortie();
            return images;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public String[] listSubjectURLs(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException, NoRightsException,
            UnknownApplisemServerException, IOException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectURLs", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] images = (String[]) obj;
            Log_Applisem_Sortie();
            return images;
        } else {
            String res = (String) obj;
            switch (res) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoRightsException
     * @throws NoCategoryException
     * @throws NoCollectionException
     */
    @Override
    public String[] suggestSubjectLinks(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoRightsException,
            NoCategoryException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "suggestSubjectLinks", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoCategoryException":
                    throw new NoCategoryException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws SQLException
     * @throws NoRightsException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSubjectException
     * @throws SQLConnectionException
     */
    @Override
    public String subjectGraph(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            SQLException, NoRightsException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSubjectException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "subjectGraph", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String xml = "";
        xml = (String) obj;
        if (xml == null) {
            Log_Applisem_Sortie();
            return xml;
        }
        switch (xml) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoSubjectException":
                throw new NoSubjectException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoRightsException":
                throw new NoRightsException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            default:
                Log_Applisem_Sortie();
                return xml;
        }
    }

    /**
     *
     * @param session_id
     * @param tracefinder
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoInputsException
     * @throws SQLException
     * @throws EmptyJoinException
     * @throws NoOutputsException
     * @throws NoInputsException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoInBetweenException
     * @throws NoCollectionException
     */
    @Override
    public String traceFinderGraph(String session_id, String tracefinder)
            throws NoSelectedCollectionException, NoSessionException,
            NoInputsException, SQLException, EmptyJoinException,
            NoOutputsException, NoInputsException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoInBetweenException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "traceFinderGraph", tracefinder);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String xml = "";
        xml = (String) obj;
        if (xml == null) {
            Log_Applisem_Sortie();
            return xml;
        }
        switch (xml) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoInBetweenException":
                throw new NoInBetweenException();
            case "NoInputsException":
                throw new NoInputsException();
            case "NoOutputsException":
                throw new NoOutputsException();
            case "EmptyJoinException":
                throw new EmptyJoinException();
            case "SQLException":
                throw new SQLException();
            case "NoCollectionException":
                throw new NoCollectionException();
            default:
                Log_Applisem_Sortie();
                return xml;
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param start
     * @param page
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Statement[] listSubjectStatements(String session_id, String subject,
            int start, int page) throws NoSelectedCollectionException,
            NoSessionException, NoSubjectException, NoCollectionException,
            NoRightsException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectStatements", subject, String.valueOf(start), String.valueOf(page));
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Statement[]) {
            Statement[] statements = (Statement[]) obj;
            Log_Applisem_Sortie();
            return statements;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Statement[] listSubjectStatements(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException, NoRightsException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectStatements", subject, "0", "0");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Statement[]) {
            Statement[] statements = (Statement[]) obj;
            Log_Applisem_Sortie();
            return statements;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public String[] listSubjectLinks(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException, NoRightsException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectLinks", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param link
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws NoLinkException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public String[] listSubjectLinkObjects(String session_id, String subject,
            String link) throws NoSelectedCollectionException,
            NoSessionException, NoSubjectException, NoCollectionException,
            NoRightsException, NoLinkException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectLinkObjects", subject, link, "0", "0");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param link
     * @param from
     * @param number
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoSubjectException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws NoLinkException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public String[] listSubjectLinkObjects(String session_id, String subject,
            String link, int from, int number)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException, NoRightsException,
            NoLinkException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectLinkObjects", subject, link, String.valueOf(from), String.valueOf(number));
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoRightsException
     * @throws NoSubjectException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws NoCollectionException
     */
    @Override
    public String[][] listDoubleSubjectStatements(String session_id,
            String subject) throws NoSelectedCollectionException,
            NoSessionException, NoRightsException, NoSubjectException,
            ClassNotFoundException, UnknownApplisemServerException,
            IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listDoubleSubjectStatements", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[][]) {
            String[][] statements = (String[][]) obj;
            Log_Applisem_Sortie();
            return statements;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @return
     * @throws NoSessionException
     * @throws NoRightsException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     */
    @Override
    public String[] listAll(String session_id) throws NoSessionException,
            NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listAll");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] statements = (String[]) obj;
            Log_Applisem_Sortie();
            return statements;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoRightsExcetion":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject1
     * @param subject2
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws TextLengthException
     * @throws NoRightsException
     * @throws SQLException
     * @throws NoSubjectException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void mergeSubjects(String session_id, String subject1,
            String subject2) throws NoSelectedCollectionException,
            NoSessionException, TextLengthException, NoRightsException,
            SQLException, NoSubjectException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "mergeSubjects", subject1, subject2);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        switch (exception) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoRightsException":
                throw new NoRightsException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "TextLengthException":
                throw new TextLengthException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject1
     * @param subject2
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSubjectException
     * @throws ExistSubjectException
     * @throws NoSelectedCollectionException
     * @throws StringLengthException
     * @throws NoRightsException
     * @throws ExistSameObjectException
     * @throws NoCollectionException
     * @throws SQLException
     * @throws SQLConnectionException
     */
    @Override
    public void duplicateSubject(String session_id, String subject1,
            String subject2) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSubjectException, ExistSubjectException,
            NoSelectedCollectionException, StringLengthException,
            NoRightsException, ExistSameObjectException, NoCollectionException,
            SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "duplicateSubjects", subject1, subject2);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        }
        if (exception.equals("NoSubjectException")) {
            throw new NoSubjectException();
        }
        switch (exception) {
            case "ExistSubjectException":
                throw new ExistSubjectException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoRightsException":
                throw new NoRightsException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param subject
     * @param link
     * @param object
     * @param in
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public String[] listSubjectSearch(String session_id, String subject,
            String link, String object, String in)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, NoRightsException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectSearch", subject, link, object, in);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /*-----------------------------------------------------------------*/
    /*-  LINKS                                                         */
    /*-----------------------------------------------------------------*/

    /**
     *
     * @param session_id
     * @param rel
     * @param irel
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws StringLengthException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     * @throws NoRightsException
     */
    
    @Override
    public void createLink(String session_id, String rel, String irel)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, StringLengthException, SQLException,
            NoCollectionException, SQLConnectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createLink", rel, irel);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoRightsException":
                throw new NoRightsException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param id
     * @param links
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSelectedCollectionException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws SQLConnectionException
     * @throws SQLException
     * @throws ExistSameObjectException
     * @throws StringLengthException
     */
    @Override
    public void createLinks(String id, List<String[]> links)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoRightsException, SQLConnectionException, SQLException,
            ExistSameObjectException, StringLengthException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createLinks");
        request.setObject(links);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoRightsException":
                throw new NoRightsException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
            case "StringLengthException":
                throw new StringLengthException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param rel
     * @param irel
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws StringLengthException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void createReverseLink(String session_id, String rel, String irel)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            StringLengthException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createReverseLink", rel, irel);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    // Cardinality

    /**
     *
     * @param session_id
     * @param link
     * @param card
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws SQLException
     * @throws StringLengthException
     * @throws UnknownApplisemServerException
     * @throws NoLinkException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
        @Override
    public void setLinkCardinality(String session_id, String link, String card)
            throws NoSelectedCollectionException, NoSessionException,
            SQLException, StringLengthException,
            UnknownApplisemServerException, NoLinkException,
            ClassNotFoundException, IOException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setLinkCardinality", link, card);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param link
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws NoLinkException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public int getLinkCardinality(String session_id, String link)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, NoLinkException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getLinkCardinality", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer card = (Integer) obj;
            Log_Applisem_Sortie();
            return card;
        } else {
            String exp = (String) obj;
            switch (exp) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "NumberFormatException":
                    throw new NumberFormatException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param link
     * @return
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoCollectionException
     */
    @Override
    public boolean existLink(String session_id, String link)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existLink", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "true":
                Log_Applisem_Sortie();
                return true;
            default:
                Log_Applisem_Sortie();
                return false;
        }
    }

    /**
     *
     * @param session_id
     * @param from
     * @param number
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Object[][] listLinks(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, NoRightsException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listLinks", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] links = (Object[][]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    // multiple links

    /**
     *
     * @param session_id
     * @param link
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoLinkException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
        @Override
    public String[] listReverseLinks(String session_id, String link)
            throws NoSelectedCollectionException, NoSessionException,
            NoLinkException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listReverseLinks", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoLinkException":
                    throw new NoLinkException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] listDoubleLinks(String session_id, int from, int number)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listDoubleLinks", String.valueOf(from), String.valueOf(number));
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     */
    @Override
    public String[] listDoubleLinks(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listDoubleLinks", "0", "0");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param link
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoLinkException
     * @throws NoRightsException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public String reverseLink(String session_id, String link)
            throws NoSelectedCollectionException, NoSessionException,
            NoLinkException, NoRightsException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "reverseLink", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoRightsException":
                throw new NoRightsException();
            case "NoCollectionException":
                throw new NoCollectionException();
            default:
                Log_Applisem_Sortie();
                return (String) obj;
        }
    }

    /**
     *
     * @param session_id
     * @param from
     * @param to
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoLinkException
     * @throws ExistSameObjectException
     * @throws NoCollectionException
     * @throws SQLException
     * @throws StringLengthException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws ExistLinkException
     * @throws SQLConnectionException
     */
    @Override
    public void renameLink(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            NoLinkException, ExistSameObjectException, NoCollectionException,
            SQLException, StringLengthException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, ExistLinkException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameLink", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "ExistLinkException":
                throw new ExistLinkException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param link
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteLink(String session_id, String link)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoLinkException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteLink", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
    }

    // multiple reverse links

    /**
     *
     * @param session_id
     * @param link
     * @param ilink
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
        @Override
    public void deleteReverseLink(String session_id, String link, String ilink)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoLinkException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteReverseLink", link, ilink);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws NoRightsException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public Object[][] listLinks(String session_id, String orderBy,
            String orderType) throws NoSelectedCollectionException,
            NoSessionException, NoCollectionException, NoRightsException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listLinks", "0", "0", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] links = (Object[][]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
           }
        }
    }

    /**
     *
     * @param session_id
     * @param orderBy
     * @param orderType
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoRightsException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws NoCollectionException
     */
    @Override
    public String[] listDoubleLinks(String session_id, String orderBy,
            String orderType) throws NoSelectedCollectionException,
            NoSessionException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listDoubleLinks", "0", "0", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoCollectionException
     * @throws NoRightsException
     */
    @Override
    public int countLinks(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCollectionException,
            NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countLinks");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param link
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoCollectionException
     * @throws NoLinkException
     */
    @Override
    public int countReverseLinks(String session_id, String link)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCollectionException,
            NoLinkException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countReverseLinks", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoLinkException":
                    throw new NoLinkException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    /**
     *
     * @param session_id
     * @return
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoCollectionException
     * @throws NoRightsException
     */
    @Override
    public int countDoubleLinks(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCollectionException,
            NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countDoubleLinks");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    @Override
    public String[] suggestLinkObjects(String session_id, String link)
            throws NoSelectedCollectionException, NoSessionException,
            NoLinkException, NoRightsException, NoCollectionException,
            NoSubjectException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "suggestLinkObjects", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] objects = (String[]) obj;
            Log_Applisem_Sortie();
            return objects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "IOException":
                    throw new IOException();
                case "NoRightsException":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public void mergeLinks(String session_id, String link1, String link2)
            throws NoSelectedCollectionException, NoSessionException,
            NoLinkException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoRightsException,
            NoCollectionException, SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "mergeLinks", link1, link2);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        switch (exception) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoRightsException":
                throw new NoRightsException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public String[] listLinkObjectType(String session_id, String link)
            throws NoSelectedCollectionException, NoSessionException,
            NoLinkException, NoCollectionException,
            UnknownApplisemServerException, IOException, ClassNotFoundException {
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listLinkObjectType", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] categories = (String[]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param link
     * @param category
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws NoCategoryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void createLinkObjectType(String session_id, String link,
            String category) throws UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createLinkObjectType", link, category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "ClassNotFoundException":
                throw new ClassNotFoundException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param link
     * @param category
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws NoCategoryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteLinkObjectType(String session_id, String link,
            String category) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoLinkException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteLinkObjectType", link, category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public String[] listLinkSubjectType(String session_id, String link)
            throws NoSelectedCollectionException, NoSessionException,
            NoRightsException, NoCollectionException,
            UnknownApplisemServerException, IOException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listLinkSubjectType", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] categories = (String[]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param link
     * @param category
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws NoCategoryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void createLinkSubjectType(String session_id, String link,
            String category) throws UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createLinkSubjectType", link, category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param link
     * @param category
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws NoCategoryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void deleteLinkSubjectType(String session_id, String link,
            String category) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoLinkException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteLinkSubjectType",
                link, category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void renameCategory(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            SQLException, NoRightsException, NoCollectionException,
            ExistSameObjectException, NoCategoryException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, ExistCategoryException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameCategory", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "ExistCategoryException":
                throw new ExistCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoRightsException":
                throw new NoRightsException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistSameObjectException":
                throw new ExistCategoryException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param from
     * @param to
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws ExistSameObjectException
     * @throws ExistBoardException
     * @throws NoCollectionException
     * @throws SQLException
     * @throws StringLengthException
     * @throws NoBoardException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SQLConnectionException
     */
    @Override
    public void renameBoard(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            ExistSameObjectException, ExistBoardException,
            NoCollectionException, SQLException, StringLengthException,
            NoBoardException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameBoard", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoBoardException":
                throw new NoBoardException();
            case "StringLengthException":
                throw new StringLengthException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "ExistBoardException":
                throw new ExistBoardException();
            case "ExistSameObjectException":
                throw new ExistSameObjectException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @param category
     * @param link
     * @throws UnknownApplisemServerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSessionException
     * @throws NoSelectedCollectionException
     * @throws NoLinkException
     * @throws NoCategoryException
     * @throws SQLException
     * @throws NoCollectionException
     * @throws SQLConnectionException
     */
    @Override
    public void categoryAddLink(String session_id, String category, String link)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "categoryAddLink", category, link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void categoryRemoveLink(String session_id, String category,
            String link) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            NoCategoryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "categoryRemoveLink", category, link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        switch (res) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    /**
     *
     * @param session_id
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public String[] listParameters(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listParameters");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] categories = (String[]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public Object[][] listCategories(String session_id, String orderBy,
            String orderType) throws NoSelectedCollectionException,
            NoSessionException, NoCollectionException, NoRightsException,
            ClassNotFoundException, UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategories", "0", "0", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] categories = (Object[][]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] listSubjectCategories(String session_id, String subject,
            String complement) throws NoSelectedCollectionException,
            NoSessionException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectCategories", subject, complement);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);

        if (obj instanceof String[]) {
            String[] categories = (String[]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param link
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoLinkException
     * @throws NoCollectionException
     * @throws ClassNotFoundException
     * @throws UnknownApplisemServerException
     * @throws IOException
     */
    @Override
    public String[] listLinkCategories(String session_id, String link)
            throws NoSelectedCollectionException, NoSessionException,
            NoLinkException, NoCollectionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listLinkCategories", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] categories = (String[]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /**
     *
     * @param session_id
     * @param subject
     * @return
     * @throws NoSelectedCollectionException
     * @throws NoSessionException
     * @throws NoCollectionException
     * @throws UnknownApplisemServerException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws NoSubjectException
     */
    @Override
    public String[] listSubjectBoards(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listSubjectBoards", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] boards = (String[]) obj;
            Log_Applisem_Sortie();
            return boards;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] listUserPerferedQueries(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUserPerferedQueries", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] queries = (String[]) obj;
            Log_Applisem_Sortie();
            return queries;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] listUserPerferedTraceFinders(String session_id,
            String subject) throws NoSelectedCollectionException,
            NoSessionException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listUserPerferedTraceFinders", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] tracefinders = (String[]) obj;
            Log_Applisem_Sortie();
            return tracefinders;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public Object[][] listBoards(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listBoards", "0", "0", "", "asc");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] categories = (Object[][]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public Object[][] listCategories(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategories", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] categories = (Object[][]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public Object[][] listBoards(String session_id, int from, int number)
            throws NoSelectedCollectionException, NoCollectionException,
            NoSessionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listBoards", String.valueOf(from), String.valueOf(number), "", "asc");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] categories = (Object[][]) obj;
            Log_Applisem_Sortie();
            return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public Object[][] listBoards(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoCollectionException,
            NoSessionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listBoards", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] categories = (Object[][]) obj;
            Log_Applisem_Sortie();
           return categories;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public int countQueryVariables(String session_id, String query)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countQueryVariables", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoQueryException":
                    throw new NoQueryException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    // EXCEPTION DONE
    @Override
    public int countCategories(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException, NoCollectionException,
            NoSelectedCollectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countCategories");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoRightsException":
                    throw new NoRightsException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    // EXCEPTION done
    @Override
    public int countBoards(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countBoards");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            Log_Applisem_Sortie();
            return count;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return 0;
            }
        }
    }

    @Override
    public void mergeCategories(String session_id, String cat1, String cat2)
            throws NoSelectedCollectionException, NoSessionException,
            SQLException, NoRightsException, NoCollectionException,
            NoLinkException, NoCategoryException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "mergeCategories", cat1, cat2);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        switch (exception) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoCategoryException":
                throw new NoCategoryException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoRightsException":
                throw new NoRightsException();
            case "NoCollectionException":
                throw new NoCollectionException();
            case "NoLinkException":
                throw new NoLinkException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public void mergeBoards(String session_id, String board1, String board2)
            throws NoSelectedCollectionException, NoSessionException,
            SQLException, NoCollectionException, NoBoardException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "mergeBoards", board1, board2);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;

        switch (exception) {
            case "NoSessionException":
                throw new NoSessionException();
            case "NoBoardException":
                throw new NoBoardException();
            case "NoSelectedCollectionException":
                throw new NoSelectedCollectionException();
            case "SQLException":
                throw new SQLException();
            case "SQLConnectionException":
                throw new SQLConnectionException();
            case "NoCollectionException":
                throw new NoCollectionException();
        }
        Log_Applisem_Sortie();
    }

    @Override
    public String[] listCategoriesLinks(String session_id, String categories,
            String sep) throws NoSelectedCollectionException,
            NoSessionException, NoCategoryException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategoriesLinks", categories, sep);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            Log_Applisem_Sortie();
            return links;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCategoryException":
                    throw new NoCategoryException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    @Override
    public String[] listLinkCategoryTypeObjects(String session_id, String cat)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, NoLinkException, NoRightsException,
            NoCollectionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listLinkCategoryTypeObjects", cat, "0", "0");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] objects = (String[]) obj;
            Log_Applisem_Sortie();
            return objects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoLinkException":
                    throw new NoLinkException();
                case "NoRightsException":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    /*
     * public String [] listCategoryTypeSubjects(String session_id,String link)
     * throws NoSelectedCollectionException,NoSessionException,NoLinkException,
     * UnknownApplisemServerException, ClassNotFoundException,IOException {
     * hasLocaleSession(session_id); Request request= new
     * Request(session_id,"listCategoryTypeSubjects", link,"0","0"); Socket
     * socket = connect(); writeToServer( socket,request); Object obj =
     * readFromServer( socket ); disconnect( socket ); if (obj instanceof String
     * []){ String [] subjects = (String []) obj; return subjects; } else {
     * String exception = (String) obj; w.listCategoryTypeSubjects(req); if
     * (exception.equals("NoSessionException")) throw new NoSessionException();
     * else if (exception.equals("NoSelectedCollectionException")) throw new
     * NoSelectedCollectionException(); else if
     * (exception.equals("NoLinkException")) throw new NoLinkException(); else
     * return null; } }
     */
    @Override
    public String[] listCategoryDynamicLinks(String session_id, String category)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, NoRightsException, NoSubjectException,
            ClassNotFoundException, UnknownApplisemServerException,
            IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategoryDynamicLinks", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            Log_Applisem_Sortie();
            return subjects;
        } else {
            String exception = (String) obj;
            switch (exception) {
                case "NoSessionException":
                    throw new NoSessionException();
                case "NoSelectedCollectionException":
                    throw new NoSelectedCollectionException();
                case "NoCategoryException":
                    throw new NoCategoryException();
                case "NoRightsException":
                    throw new NoRightsException();
                case "NoCollectionException":
                    throw new NoCollectionException();
                case "NoSubjectException":
                    throw new NoSubjectException();
                default:
                    Log_Applisem_Sortie();
                    return null;
            }
        }
    }

    public String[] listCategoryLinks(String session_id, String category)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategoryLinks", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listCategoryMembers(String session_id, String category,
            int from, int number, String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategoryMembers", category, String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public String[] filterCategoryMembers(String session_id, String category,
            String links) throws NoSelectedCollectionException,
            NoSessionException, NoCategoryException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "filterCategoryMembers", category, links);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else {
                return null;
            }
        }
    }

    public int countFilterCategoryMembers(String session_id, String category,
            String links) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCategoryException,
            NoRightsException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countFilterCategoryMembers", category, links);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return 0;
            }
        }
    }

    public String[] filterCategoryMembers(String session_id, String category,
            String links, int from, int number, String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "filterCategoryMembers", category, links, String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listCategoryMembers(String session_id, String category,
            int from, int number) throws NoSelectedCollectionException,
            NoSessionException, NoCategoryException, NoRightsException,
            ClassNotFoundException, UnknownApplisemServerException,
            IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategoryMembers", category, String.valueOf(from), String.valueOf(number));
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listBoardMembers(String session_id, String board, int from,
            int number) throws NoSelectedCollectionException,
            NoSessionException, NoBoardException, NoCollectionException,
            ClassNotFoundException, UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listBoardMembers", board, String.valueOf(from), String.valueOf(number), "", "asc");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoBoardException")) {
                throw new NoBoardException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listBoardMembers(String session_id, String board, int from,
            int number, String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoBoardException, NoCollectionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listBoardMembers", board, String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoBoardException")) {
                throw new NoBoardException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public int countCategoryMembers(String session_id, String category)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCategoryException,
            NoRightsException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countCategoryMembers", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return 0;
            }
        }
    }

    public int countBoardMembers(String session_id, String board)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoBoardException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countBoardMembers", board);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoBoardException")) {
                throw new NoBoardException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return 0;
            }
        }
    }

    // EXCEPTION d
    public int countCategoryLinks(String session_id, String category)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCategoryException,
            NoRightsException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countCategoryLinks", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return 0;
            }
        }
    }

    public boolean existCategory(String session_id, String category)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existCategory", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean existBoard(String session_id, String board)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existBoard", board);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteCategory(String session_id, String category)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoCollectionException,
            NoSelectedCollectionException, NoCategoryException, SQLException,
            NoRightsException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteCategory", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCategoryException")) {
            throw new NoCategoryException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void deleteBoard(String session_id, String board)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoBoardException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteBoard", board);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoBoardException")) {
            throw new NoBoardException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void createCategory(String session_id, String category)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException, ExistCategoryException,
            NoSelectedCollectionException, StringLengthException, SQLException,
            ExistSameObjectException, NoRightsException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Log_Applisem("Catégorie =  " + category);
        Request request = new Request(session_id, "createCategory", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("ExistCategoryException")) {
            throw new ExistCategoryException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void createBoard(String session_id, String board)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException,
            ExistBoardException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createBoard", board);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("IOException")) {
            throw new IOException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("ExistBoardException")) {
            throw new ExistBoardException();
        }
    }

    public Object[][] listCategoryMembers(String session_id, String category)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategoryMembers", category, "0", "0");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listCategoryMembers(String session_id, String category,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, NoRightsException, ClassNotFoundException,
            UnknownApplisemServerException, IOException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listCategoryMembers", category, "0", "0", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public String[] filterCategoryMembers(String session_id, String category,
            String links, String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "filterCategoryMembers", category, links, "0", "0", orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] subjects = (String[]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listBoardMembers(String session_id, String board)
            throws NoSelectedCollectionException, NoSessionException,
            NoBoardException, NoCollectionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listBoardMembers", board, "0", "0", "", "asc");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] subjects = (Object[][]) obj;
            return subjects;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoBoardException")) {
                throw new NoBoardException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public String[] suggestCategoryLinks(String session_id, String category)
            throws NoSelectedCollectionException, NoSessionException,
            NoCategoryException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "suggestCategoryLinks", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] links = (String[]) obj;
            return links;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else {
                return null;
            }
        }
    }

    /*-------------------------------------------------------------*/
    /*-  STATEMENTS                                                */
    /*-------------------------------------------------------------*/
    public void createStatement(String session_id, String s, String v, String c)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            StringLengthException, SQLException, NoCollectionException,
            ExistSameObjectException, ExistSubjectException,
            ExistStatementException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createStatement", s, v, c);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("ExistSubjectException")) {
            throw new ExistSubjectException();
        } else if (res.equals("ExistStatementException")) {
            throw new ExistStatementException();
        }
    }

    public void createStatements(String session_id, Statement[] statements)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            StringLengthException, SQLException, NoCollectionException,
            ExistSameObjectException, ExistSubjectException,
            ExistStatementException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createStatements", statements);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("ExistSubjectException")) {
            throw new ExistSubjectException();
        } else if (res.equals("ExistStatementException")) {
            throw new ExistStatementException();
        }
    }

    public void createStatements2(String session_id, Statement[] statements)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoLinkException,
            StringLengthException, SQLException, NoCollectionException,
            ExistSameObjectException, ExistSubjectException,
            ExistStatementException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createStatements2", statements);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("ExistSubjectException")) {
            throw new ExistSubjectException();
        } else if (res.equals("ExistStatementException")) {
            throw new ExistStatementException();
        }
    }

    public void deleteStatement(String session_id, String s, String v, String c)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoSubjectException, NoLinkException, NoComplementException,
            SQLException, NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteStatement", s, v, c);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoSubjectException")) {
            throw new NoSubjectException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("NoComplementException")) {
            throw new NoComplementException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public GraphResult getCategoryGraph(String session_id, String category)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoCategoryException,
            NoRightsException, NoLinkException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getCategoryGraph", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof GraphResult) {
            GraphResult statements = (GraphResult) obj;
            return statements;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (exception.equals("NoCategoryException")) {
                throw new NoCategoryException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoLinkException")) {
                throw new NoLinkException();
            } else if (exception.equals("NoObjectException")) {
                throw new NoObjectException();
            } else {
                return null;
            }
        }
    }

    public GraphResult getSubjectGraph(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSubjectException,
            NoRightsException, NoLinkException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getSubjectGraph", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof GraphResult) {
            GraphResult statements = (GraphResult) obj;
            return statements;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (exception.equals("NoSubjectException")) {
                throw new NoSubjectException();
            } else if (exception.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (exception.equals("NoLinkException")) {
                throw new NoLinkException();
            } else if (exception.equals("NoObjectException")) {
                throw new NoObjectException();
            } else {
                return null;
            }
        }
    }

    public String[] listStatements(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listStatements");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] statements = (String[]) obj;
            return statements;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    // EXCEPTION DONE
    public String[] checkCollection(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException, NoLinkException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "checkCollection");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (exception.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (exception.equals("NoSubjectException")) {
            throw new NoSubjectException();
        } else if (exception.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (exception.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        return null;
    }

    public String[] getStatementInfos(String session_id, String s, String v,
            String c) throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoLinkException, NoComplementException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getStatementInfos", s, v, c);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] infos = (String[]) obj;
            return infos;
        } else {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            } else if (res.equals("NoLinkException")) {
                throw new NoLinkException();
            } else if (res.equals("NoComplementException")) {
                throw new NoComplementException();
            } else {
                return null;
            }
        }
    }

    public List<QueryParameterInfo> parseAndGetQueryParameters(
            String session_id, String queryText) throws NoSessionException,
            IOException, UnknownApplisemServerException,
            ClassNotFoundException, NoSelectedCollectionException,
            StringLengthException, SQLException, BadFormatException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "parseAndGetQueryParameters", queryText);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (obj.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (obj.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (obj.equals("SQLException")) {
            throw new SQLException();
        } else if (obj.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (obj.equals("UnknownApplisemServerException")) {
            throw new UnknownApplisemServerException();
        } else if (obj.equals("BadFormatException")) {
            throw new BadFormatException();
        }
        List<QueryParameterInfo> res = (List<QueryParameterInfo>) obj;
        return res;
    }

    public QueryResults parseAndExecuteQuery(String session_id,
            String queryText, Map<String, String> parameters)
            throws NoLinkException, NoObjectException, SQLException,
            ClassNotFoundException, IOException, BadFormatException,
            NoSessionException, NoSelectedCollectionException,
            StringLengthException, EmptySelectException, EmptyJoinException,
            SQLConnectionException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "parseAndExecuteQuery", queryText);
        request.setObject(parameters);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (obj.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (obj.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (obj.equals("SQLException")) {
            throw new SQLException();
        } else if (obj.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (obj.equals("UnknownApplisemServerException")) {
            throw new UnknownApplisemServerException();
        } else if (obj.equals("BadFormatException")) {
            throw new BadFormatException();
        } else if (obj.equals("EmptySelectException")) {
            throw new EmptySelectException();
        } else if (obj.equals("EmptyJoinException")) {
            throw new EmptyJoinException();
        } else if (obj.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (obj.equals("NoObjectException")) {
            throw new NoObjectException();
        }
        if (obj instanceof QueryResults) {
            QueryResults res = (QueryResults) obj;
            return res;
        }
        return null;
    }

    public void setQueryBody(String session_id, String query, String body)
            throws NoSessionException, NoSelectedCollectionException,
            StringLengthException, NoCollectionException, NoQueryException,
            UnknownApplisemServerException, SQLException,
            ClassNotFoundException, IOException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setQueryBody", query, body);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public String getQueryBody(String session_id, String body)
            throws SQLException, ClassNotFoundException, IOException,
            NoSessionException, NoSelectedCollectionException,
            StringLengthException, SQLConnectionException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getQueryBody", body);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else {
            return res;
        }
    }

    public void createQuery(String session_id, String query)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, StringLengthException,
            ExistSameObjectException, SQLException, ExistQueryException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createQuery", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("ExistQueryException")) {
            throw new ExistQueryException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void createInBetween(String session_id, String inBetween)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, ExistInBetweenException,
            StringLengthException, ExistSameObjectException, SQLException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createInBetween", inBetween);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("ExistInBetweenException")) {
            throw new ExistInBetweenException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void createQueryTCView(String session_id, String view, String query)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            ExistViewException, StringLengthException, SQLException,
            ExistSameObjectException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createQueryTCView", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("ExistViewException")) {
            throw new ExistViewException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void createQueryTSView(String session_id, String view, String query)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            StringLengthException, SQLException, ExistViewException,
            ExistSameObjectException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createQueryTSView", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("ExistViewException")) {
            throw new ExistViewException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void createTCViewCell(String session_id, String view, String query,
            String value) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, StringLengthException, SQLException,
            NoCollectionException, ExistViewException, NoViewException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createTCViewCell", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistViewException")) {
            throw new ExistViewException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        }
    }

    public void deleteTCViewCell(String session_id, String view, String query,
            String value) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, SQLException, NoCollectionException,
            NoViewException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteTCViewCell", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        }
    }

    public void deleteTCViewRow(String session_id, String view, String query,
            String value) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, SQLException, NoCollectionException,
            NoViewException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteTCViewRow", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        }
    }

    public void deleteTCViewCol(String session_id, String view, String query,
            String value) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, SQLException, NoCollectionException,
            NoViewException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteTCViewCol", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        }
    }

    public void deleteTSViewCol(String session_id, String view, String query,
            String value) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, SQLException, NoCollectionException,
            NoViewException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteTSViewCol", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        }
    }

    public String getTCViewCell(String session_id, String view, String query)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, NoQueryException,
            NoQueryViewException, ClassNotFoundException, IOException,
            StringLengthException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getTCViewCell", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
        return res;
    }

    public void createTCViewCol(String session_id, String view, String query,
            String value) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, StringLengthException, SQLException,
            NoCollectionException, NoViewException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createTCViewCol", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        }
    }

    public void createTSViewCol(String session_id, String view, String query,
            String value) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, StringLengthException, SQLException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createTSViewCol", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public String[] listTSViewVariable(String session_id, String view,
            String query) throws IOException, UnknownApplisemServerException,
            ClassNotFoundException, SQLException, NoSessionException,
            NoQueryException, NoViewException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listTSViewVariable", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else if (res.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (res.equals("NoViewException")) {
                throw new NoViewException();
            }
        }
        return (String[]) obj;
    }

    public void createTSViewCols(String session_id, String[] values,
            String view, String query) throws UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            StringLengthException, SQLException, NoViewException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createTSViewCols", values, view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        }
    }

    public void createTCViewRow(String session_id, String view, String query,
            String value) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoQueryException,
            NoQueryViewException, SQLException, NoCollectionException,
            NoViewException, StringLengthException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createTCViewRow", view, query, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        }
    }

    public boolean existQuery(String session_id, String query)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "existQuery", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * method to rename a Query
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     */
    public void renameQuery(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            NoQueryException, ExistQueryException, SQLException,
            NoCollectionException, StringLengthException,
            StringLengthException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameQuery", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("ExistQueryException")) {
            throw new ExistQueryException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        }
    }

    /**
     * method to rename a Cross View of a Query
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     */
    public void renameQueryTCView(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            NoQueryViewException, StringLengthException, ExistViewException,
            ExistSameObjectException, SQLException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameQueryTCView", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistViewException")) {
            throw new ExistViewException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        }
    }

    /**
     * method to rename a Filtered View of a Query
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     */
    public void renameQueryTSView(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            NoQueryViewException, ExistSameObjectException, ExistViewException,
            NoViewException, StringLengthException, SQLException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameQueryTSView", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        } else if (res.equals("ExistViewException")) {
            throw new ExistViewException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        }
    }

    public void deleteQuery(String session_id, String query)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoQueryException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteQuery", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (exception.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (exception.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (exception.equals("SQLException")) {
            throw new SQLException();
        } else if (exception.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (exception.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void deleteInBetween(String session_id, String inbetween)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoInBetweenException, SQLException, NoCollectionException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteInBetween", inbetween);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (exception.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (exception.equals("NoInBetweenException")) {
            throw new NoInBetweenException();
        } else if (exception.equals("SQLException")) {
            throw new SQLException();
        } else if (exception.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (exception.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void deleteQueryTCView(String session_id, String query, String view)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoViewException, SQLException, NoCollectionException,
            NoQueryException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteQueryTCView", query, view);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (exception.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (exception.equals("NoViewException")) {
            throw new NoViewException();
        } else if (exception.equals("SQLException")) {
            throw new SQLException();
        } else if (exception.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (exception.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (exception.equals("NoQueryException")) {
            throw new NoQueryException();
        }
    }

    public void deleteQueryTSView(String session_id, String query, String view)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoViewException, SQLException, NoCollectionException,
            NoQueryException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteQueryTSView", query, view);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (exception.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (exception.equals("NoViewException")) {
            throw new NoViewException();
        } else if (exception.equals("SQLException")) {
            throw new SQLException();
        } else if (exception.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (exception.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (exception.equals("NoQueryException")) {
            throw new NoQueryException();
        }
    }

    public Object[][] listQueries(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueries");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] queries = (Object[][]) obj;
            return queries;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listQueries(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueries", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] queries = (Object[][]) obj;
            return queries;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listInBetweens(String session_id, int from, int number,
            String orderBy, String orderType)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listInBetweens", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] inbetweens = (Object[][]) obj;
            return inbetweens;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public Object[][] listInBetweens(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, ClassNotFoundException,
            UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listInBetweens");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Object[][]) {
            Object[][] inbetweens = (Object[][]) obj;
            return inbetweens;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public int countInBetweens(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countInBetweens");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return 0;
            }
        }
    }

    public int countQueries(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countQueries");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return 0;
            }
        }
    }

    public String[] listQueryTCViews(String session_id, String query)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoQueryException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueryTCViews", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);

        if (obj instanceof String[]) {
            String[] rules = (String[]) obj;
            return rules;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (exception.equals("SQLException")) {
                throw new SQLException();
            } else if (exception.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else {
                return null;
            }
        }
    }

    public String[] listQueryTSViews(String session_id, String query)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoQueryException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueryTSViews", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);

        if (obj instanceof String[]) {
            String[] rules = (String[]) obj;
            return rules;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (exception.equals("SQLException")) {
                throw new SQLException();
            } else if (exception.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
            return null;
        }
    }

    public String getTCViewCol(String session_id, String view, String query)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoQueryException, NoQueryViewException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getTCViewCol", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String column = (String) obj;
        if (column == null) {
            return "";
        }
        if (column.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (column.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (column.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (column.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (column.equals("SQLException")) {
            throw new SQLException();
        } else if (column.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else {
            return column;
        }
    }

    public String[] listTSViewCols(String session_id, String view, String query)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoQueryException,
            NoQueryViewException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listTSViewCols", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] rules = (String[]) obj;
            return rules;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoQueryViewException")) {
                throw new NoQueryViewException();
            } else if (exception.equals("NoQueryViewException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public String[] listViewRows(String session_id, String view, String query)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoQueryException,
            NoQueryViewException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listViewRows", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] rules = (String[]) obj;
            return rules;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoQueryViewException")) {
                throw new NoQueryViewException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public String getTCViewRow(String session_id, String view, String query)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoQueryException, NoQueryViewException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getTCViewRow", view, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String row = (String) obj;
        if (row == null) {
            return "";
        } else if (row.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (row.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (row.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (row.equals("NoQueryViewException")) {
            throw new NoQueryViewException();
        } else if (row.equals("SQLException")) {
            throw new SQLException();
        } else if (row.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else {
            return row;
        }
    }

    public List<QueryParameterInfo> listQueryParameter(String session_id,
            String query) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoQueryException, SQLException, BadFormatException,
            NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueryParameter", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        List<QueryParameterInfo> res = null;
        if (obj instanceof String) {
            if (obj.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (obj.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (obj.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (obj.equals("SQLException")) {
                throw new SQLException();
            } else if (obj.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else if (obj.equals("BadFormatException")) {
                throw new BadFormatException();
            } else if (obj.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        if (obj instanceof List) {
            res = (List<QueryParameterInfo>) obj;

        }
        return res;
    }

    public String[] listQueryVariables(String session_id, String query)
            throws NoSelectedCollectionException, NoSessionException,
            BadFormatException, NoQueryException, SQLException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueryVariables", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] rules = (String[]) obj;
            return rules;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exception.equals("RecognitionException")) {
                throw new BadFormatException();
            } else if (exception.equals("SQLException")) {
                throw new SQLException();
            } else if (exception.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public String[] listQueryLinks(String session_id, String query)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoQueryException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueryLinks", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] rules = (String[]) obj;
            return rules;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public String[] listQueryCategories(String session_id, String query)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoQueryException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listQueryCategories", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] rules = (String[]) obj;
            return rules;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public QueryResults executeQuery(String session_id, String query,
            Map<String, String> parameter) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoQueryException,
            EmptySelectException, EmptyJoinException, BadFormatException,
            SQLException, NoCollectionException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "executeQuery", query);
        request.setObject(parameter);
        Socket socket = connect();
        Log_Applisem("ClientSession ==> session_id = " + session_id);
        Log_Applisem("              ==> request = " + request);
        Log_Applisem("              ==> parameter = " + parameter);
        Log_Applisem("              ==> socket = " + socket.toString());
        writeToServer(socket, request);
        Log_Applisem("ClientSession ==> Retour traitement Request");
        Object obj = readFromServer(socket);
        disconnect(socket);
        Log_Applisem("              ==> obj = " + obj);
        if (obj != null && obj instanceof QueryResults) {
            QueryResults res = (QueryResults) obj;
            Log_Applisem("              ==> res = " + res);
            return res;
        } else if (obj != null) {
            String exp = (String) obj;
            Log_Applisem("              ==> exp = " + exp);
            if (exp.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exp.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exp.equals("NoQueryException")) {
                throw new NoQueryException();
            } else if (exp.equals("EmptySelectException")) {
                throw new EmptySelectException();
            } else if (exp.equals("EmptyJoinException")) {
                throw new EmptyJoinException();
            } else if (exp.equals("BadFormatException")) {
                throw new BadFormatException();
            } else if (exp.equals("SQLException")) {
                throw new SQLException();
            } else if (exp.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else if (exp.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String[] executeInbetweenFrom_To_(String session_id, String in,
            String out, String path) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoInputsException,
            NoOutputsException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "executeInbetweenFrom_To_", in, out, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj != null && obj instanceof String[]) {
            String[] res = (String[]) obj;
            return res;
        } else if (obj != null) {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoInputsException")) {
                throw new NoInputsException();
            } else if (exception.equals("NoOutputsException")) {
                throw new NoOutputsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String[] executeInBetween(String session_id, String inbetween)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            NoQueryException, NoInputsException, NoOutputsException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "executeInBetween", inbetween);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj != null && obj instanceof String[]) {
            String[] res = (String[]) obj;
            return res;
        } else if (obj != null) {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoInBetweenException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoInputsException")) {
                throw new NoInputsException();
            } else if (exception.equals("NoOutputsException")) {
                throw new NoOutputsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public PathFinderResult executePathFinderFrom_To_(String session_id,
            String in, String out, String path) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoInputsException,
            NoOutputsException, NoCollectionException, TraceFinderException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "executePathFinderFrom_To_", in, out, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj;
        try {
            obj = readFromServer(socket);
        } catch (OutOfMemoryError e) {
            obj = null;
            obj = "TraceFinderException";
        }
        disconnect(socket);
        if (obj != null && obj instanceof PathFinderResult) {
            PathFinderResult res = (PathFinderResult) obj;
            return res;
        } else if (obj != null) {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoInputsException")) {
                throw new NoInputsException();
            } else if (exception.equals("NoOutputsException")) {
                throw new NoOutputsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (exception.equals("TraceFinderException")) {
                throw new TraceFinderException();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public PathFinderResult executePathFinder(String session_id,
            String inbetween) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoQueryException,
            NoInputsException, NoOutputsException, NoCollectionException,
            TraceFinderException {

        Log_Applisem_Entree();
        return executePathFinder(session_id, inbetween, "false");
    }

    public PathFinderResult executePathFinder(String session_id,
            String inbetween, String force) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoQueryException,
            NoInputsException, NoOutputsException, NoCollectionException,
            TraceFinderException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "executePathFinder", inbetween, force);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj;
        try {
            obj = readFromServer(socket);
        } catch (OutOfMemoryError e) {
            obj = null;
            obj = "TraceFinderException";
        }
        disconnect(socket);
        if (obj != null && obj instanceof PathFinderResult) {
            PathFinderResult res = (PathFinderResult) obj;
            return res;
        } else if (obj != null) {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoInBetweenException")) {
                throw new NoQueryException();
            } else if (exception.equals("NoInputsException")) {
                throw new NoInputsException();
            } else if (exception.equals("NoOutputsException")) {
                throw new NoOutputsException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (exception.equals("TraceFinderException")) {
                throw new TraceFinderException();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void print(String session_id) throws NoSelectedCollectionException,
            NoSessionException, NoCollectionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "print");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String exception = (String) obj;
        if (exception.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (exception.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (exception.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public String[] listDico(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            ClassNotFoundException, UnknownApplisemServerException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listDico");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] entrees = (String[]) obj;
            return entrees;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else {
                return null;
            }
        }
    }

    public void stopServer(String session_id) throws NoSessionException,
            UnknownApplisemServerException, IOException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "stopServer");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
        }
    }

    public void setParameter(String session_id, String name, String value)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setParameter", name, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("IOException")) {
            throw new IOException();
        }
    }

    public void removeParameter(String session_id, String name)
            throws NoSelectedCollectionException, NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "removeParameter", name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("IOException")) {
            throw new IOException();
        }
    }

    public int countUsers(String session_id)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countUsers");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            return 0;
        }
    }

    public int countRoles(String session_id)
            throws UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSessionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "countRoles");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Integer) {
            Integer count = (Integer) obj;
            return count.intValue();
        } else {
            return 0;
        }
    }

    /**
     * Method witch activate an user allowing it to connect
     *
     * @param session_id the id of the current session
     * @param user the user to activate
     */
    public void activateUser(String session_id, String user)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoUserException, SQLException,
            SQLConnectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "activateUser", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    /**
     * Method witch desactivate an user prohibiting it to connect
     *
     * @param session_id the id of the current session
     * @param user the user to activate
     */
    public void desactivateUser(String session_id, String user)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoUserException, SQLException,
            SQLConnectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "desactivateUser", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    public void deleteUser(String session_id, String user)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoUserException, SQLException,
            SQLConnectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteUser", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    public void deleteUserRole(String session_id, String user, String role)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoUserException,
            NoRoleException, SQLException, SQLConnectionException,
            NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteUserRole", user, role);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    public void deleteRoleCollection(String session_id, String role,
            String collection) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoCollectionException, NoRoleException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteRoleCollection", role, collection);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void deleteRole(String session_id, String role)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoRoleException, SQLException,
            SQLConnectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteRole", role);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }

    }

    /**
     * method to rename an User
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     */
    public void renameUser(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, ExistUserException,
            SQLException, ExistSameObjectException, SQLConnectionException,
            NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameUser", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("ExistUserException")) {
            throw new ExistUserException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    /**
     * method to rename a Role
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     */
    public void renameRole(String session_id, String from, String to)
            throws NoSelectedCollectionException, NoSessionException,
            ExistSameObjectException, SQLException, ExistRoleException,
            NoRoleException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, SQLConnectionException,
            NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "renameRole", from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("ExistRoleException")) {
            throw new ExistRoleException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    public void setUserPassword(String session_id, String user, String password)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, SQLException, PasswordLengthException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, ExistSameObjectException,
            StringLengthException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setUserPassword", user, password);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("PasswordLengthException")) {
            throw new PasswordLengthException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void setUserEmail(String session_id, String user, String email)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, SQLException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, ExistSameObjectException,
            StringLengthException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setUserEmail", user, email);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public String getUserPassword(String session_id, String user)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getUserPassword", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        }
        return res;
    }

    public String getUserEmail(String session_id, String user)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getUserEmail", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        }
        return res;
    }

    public String isActiveUser(String session_id, String user)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "isActiveUser", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        }
        return res;
    }

    public void setUserLogin(String session_id, String user, String login)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, ExistLoginException, StringLengthException,
            SQLException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setUserLogin", user, login);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("ExistLoginException")) {
            throw new ExistLoginException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void setUserRole(String session_id, String user, String role)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, NoRoleException, StringLengthException,
            SQLException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, SQLConnectionException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setUserRole", user, role);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    public void setRoleCollection(String session_id, String role,
            String collection) throws NoSelectedCollectionException,
            NoSessionException, NoRoleException, NoCollectionException,
            SQLException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setRoleCollection", role, collection);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public Rights getRoleRights(String session_id, String role)
            throws NoSessionException, NoRoleException, NoRightsException,
            UnknownApplisemServerException, IOException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getRoleRights", role);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof Rights) {
            return (Rights) obj;
        }
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
        return new Rights(new HashMap<String, Boolean>());
    }

    public void setRoleRights(String session_id, String role,
            Map<String, Boolean> rights) throws NoSessionException,
            NoRoleException, NoRightsException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setRoleRights", role);
        request.setObject(rights);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        }
    }

    /*
     * LBO public void setRoleCollectionRights(String session_id, String role,
     * String collection,String col, String cat, String sub,String lnk, String
     * req, String boa) throws
     * NoSelectedCollectionException,NoSessionException,NoRoleException,
     * UnknownApplisemServerException,
     * IOException,ClassNotFoundException,NoCollectionException{
     * hasLocaleSession(session_id); Request request= new
     * Request(session_id,"setRoleCollectionRights"
     * ,role,collection,col,cat,sub,lnk,req,boa); Socket socket = connect();
     * writeToServer( socket,request); Object obj = readFromServer( socket );
     * disconnect( socket ); String res = (String ) obj; if
     * (res.equals("NoSessionException")) throw new NoSessionException(); else
     * if (res.equals("NoSelectedCollectionException")) throw new
     * NoSelectedCollectionException(); else if
     * (res.equals("NoCollectionException")) throw new NoCollectionException();
     * else if (res.equals("NoRoleException")) throw new NoRoleException(); }
     */
    public void setRoleProperty(String session_id, String role, String name,
            String value) throws NoSelectedCollectionException,
            NoSessionException, NoRoleException, StringLengthException,
            SQLException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setRoleProperty", role, name, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void setUserProperty(String session_id, String user, String name,
            String value) throws NoSelectedCollectionException,
            NoSessionException, NoUserException, SQLException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "setUserProperty", user, name, value);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public String getUserLogin(String session_id, String user)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getUserLogin", user);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        }
        return res;
    }

    public String getRoleProperty(String session_id, String role, String name)
            throws NoSelectedCollectionException, NoSessionException,
            NoRoleException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getRoleProperty", role, name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoRoleException")) {
            throw new NoRoleException();
        }
        return res;
    }

    public String getUserProperty(String session_id, String user, String name)
            throws NoSelectedCollectionException, NoSessionException,
            NoUserException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getUserProperty", user, name);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoUserException")) {
            throw new NoUserException();
        }
        return res;
    }

    public String[] listRoleProperties(String session_id, String role)
            throws NoSelectedCollectionException, NoSessionException,
            NoRoleException, UnknownApplisemServerException,
            ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listRoleProperties", role);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] properties = (String[]) obj;
            return properties;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (exception.equals("NoRoleException")) {
                throw new NoRoleException();
            } else {
                return null;
            }
        }
    }

    /*
     * LBO public String [] getUserCollectionRights(String session_id, String
     * collection) throws
     * NoSelectedCollectionException,NoSessionException,NoUserException,
     * UnknownApplisemServerException,
     * IOException,ClassNotFoundException,NoCollectionException,
     * NoRightsException{ hasLocaleSession(session_id); Request request= new
     * Request(session_id,"getUserCollectionRights",collection); Socket socket =
     * connect(); writeToServer( socket,request); Object obj = readFromServer(
     * socket ); disconnect( socket ); if (obj instanceof String []){ String []
     * rights = (String []) obj; return rights; } else { String exception =
     * (String) obj; if (exception.equals("NoSessionException")) throw new
     * NoSessionException(); else if
     * (exception.equals("NoSelectedCollectionException")) throw new
     * NoSelectedCollectionException(); else if
     * (exception.equals("NoUserException")) throw new NoUserException(); else
     * if (exception.equals("NoRightsExcetion")) throw new NoRightsException();
     * else return null; } }
     */
    public void register(String id, String host, String port, String clazz,
            String type) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "register", host, port, clazz, type);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        }
    }

    public void unRegister(String id, String host, String port, String clazz,
            String type) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException, IOException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "unRegister", host, port, clazz, type);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        }
    }

    public SubjectSheet getSubjectSheet(String id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoSubjectException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getSubjectSheet", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof SubjectSheet) {
            return (SubjectSheet) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
        return null;
    }

    public String getImagesPath(String id) throws NoSessionException,
            UnknownException, IOException, ClassNotFoundException,
            NoSelectedCollectionException, NoCollectionException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getImagesPath");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        return (String) obj;
    }

    public String getFilesPath(String id) throws NoSessionException,
            UnknownException, IOException, ClassNotFoundException,
            NoSelectedCollectionException, NoCollectionException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getFilesPath");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof SubjectSheet) {
            return (String) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        return (String) obj;
    }

    public String getVCardsPath(String id) throws NoSessionException,
            UnknownException, IOException, ClassNotFoundException,
            NoSelectedCollectionException, NoCollectionException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getVCardsPath");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof SubjectSheet) {
            return (String) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        return (String) obj;
    }

    public String getPreferedImage(String id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoSubjectException, SQLException, NoRightsException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getPreferedImage", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            } else if (res.equals("NoRightsException")) {
                throw new NoRightsException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
            return res;
        }
        return null;
    }

    public FileServerConf getFileServerConfig(String id)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getFileServerConfig", "");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof FileServerConf) {
            return (FileServerConf) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        return null;
    }

    public FileServerConf getFileServerConfig(String id, String collection)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getFileServerConfig", collection);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof FileServerConf) {
            return (FileServerConf) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        return null;
    }

    public List<String> listFileServers(String id) throws NoSessionException,
            UnknownApplisemServerException, IOException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "listFileServers");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
        }
        if (obj instanceof List) {
            return (List<String>) obj;
        }
        return null;
    }

    public List<SharedObject> searchParamInDico(String id, String query,
            String queryBody, String parameter, String exp)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoQueryException,
            SQLException, BadFormatException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        if (query == null) {
            query = "";
        }
        if (queryBody == null) {
            queryBody = "";
        }
        Request request = new Request(id, "searchParamInDico", query,
                queryBody, parameter, exp);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String exception = (String) obj;
            if ("NoSessionException".equals(exception)) {
                throw new NoSessionException();
            } else if ("NoQueryException".equals(exception)) {
                throw new NoQueryException();
            } else if ("SQLException".equals(exception)) {
                throw new SQLException();
            } else if (exception.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else if ("BadFormatException".equals(exception)) {
                throw new BadFormatException();
            }

        }
        List<SharedObject> res = (List<SharedObject>) obj;
        return res;
    }

    public QueryHighLight hightLightQuery(String id, String query,
            String queryBody) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoQueryException, SQLException,
            BadFormatException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        if (query == null) {
            query = "";
        }
        if (queryBody == null) {
            queryBody = "";
        }
        Request request = new Request(id, "hightLightQuery", query, queryBody);
        Log_Applisem("  Request : id = " + id);
        Log_Applisem("          : query = " + query);
        Log_Applisem("          : queryBody = " + queryBody);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof QueryHighLight) {
            QueryHighLight res = (QueryHighLight) obj;
            Log_Applisem(" res " + res.toString());
            Log_Applisem_Sortie();
            return res;
        } else {
            String res = (String) obj;
            if ("NoSessionException".equals(res)) {
                throw new NoSessionException();
            } else if ("NoQueryException".equals(res)) {
                throw new NoQueryException();
            } else if ("SQLException".equals(res)) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else if ("BadFormatException".equals(res)) {
                throw new BadFormatException();
            }
            Log_Applisem(" res " + res.toString());
            Log_Applisem_Sortie();
            return null;
        }

    }

    public String[] matchDico(String id, String[] matches)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "matchDico", matches);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
        }
        if (obj instanceof String[]) {
            return (String[]) obj;
        }
        return null;
    }

    public SharedObject getObjectFromId(String id, int oid)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getObjectFromId", String.valueOf(oid));
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof SharedObject) {
            return (SharedObject) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
        }
        return null;
    }

    public int getObjectId(String id, String object) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getObjectId", object);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return -1;
    }

    public SubjectSheet getSubjectSheetById(String id, int objectId)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            SQLConnectionException, SQLException, NoSubjectException {

        Log_Applisem_Entree();
        Request request = new Request(id, "getSubjectSheetById");
        request.setObject(objectId);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
        if (obj instanceof SubjectSheet) {
            return (SubjectSheet) obj;
        }
        return null;
    }

    public int countSubjectImage(String id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "countSubjectImage", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
            if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
            if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            }
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return -1;
    }

    public int countSubjectFile(String id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "countSubjectFile", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
            if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
            if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            }
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return -1;
    }

    public int countSubjectURL(String id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "countSubjectURL", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
            if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
            if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            }
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return -1;
    }

    public int countSubjectRSS(String id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "countSubjectRSS", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
            if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
            if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            }
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return -1;
    }

    public String[] listSubjectInUserPreference(String id)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "listSubjectInUserPreference");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
            if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
            if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
            if (res.equals("SQLException")) {
                throw new SQLException();
            }
            if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
        if (obj instanceof String[]) {
            return (String[]) obj;
        }
        return null;
    }

    public void addSubjectToUserPreference(String id, String subject)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLException, SQLConnectionException,
            NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "addSubjectToUserPreference", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("true")) {
                return;
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            }
            if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            }
            if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
            if (res.equals("SQLException")) {
                throw new SQLException();
            }
            if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
            if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            }
        }
    }

    public void upLink(String session_id, String link)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException, SQLException,
            NoCollectionException, SQLConnectionException, NoRightsException,
            NoLinkException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "upLink", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;

        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void downLink(String session_id, String link)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, SQLException, NoCollectionException,
            SQLConnectionException, NoRightsException, NoLinkException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "downLink", link);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;

        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void upSubjectImage(String session_id, String subject, String image)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, SQLException, NoCollectionException,
            SQLConnectionException, NoSubjectException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "upSubjectImage", subject, image);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoSubjectException")) {
            throw new NoSubjectException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        }
    }

    public void downSubjectImage(String session_id, String subject, String image)
            throws IOException, ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, SQLException, NoRightsException,
            NoCollectionException, SQLConnectionException, NoSubjectException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "downSubjectImage", subject, image);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;

        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoSubjectException")) {
            throw new NoSubjectException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void upBoard(String session_id, String board)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, SQLException, NoCollectionException,
            SQLConnectionException, NoRightsException, NoBoardException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "upBoard", board);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;

        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoBoardException")) {
            throw new NoBoardException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void downBoard(String session_id, String board)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, SQLException, NoCollectionException,
            SQLConnectionException, NoRightsException, NoBoardException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "downBoard", board);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;

        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoBoardException")) {
            throw new NoBoardException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void upCategory(String session_id, String category)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, SQLException, NoCollectionException,
            SQLConnectionException, NoRightsException, NoCategoryException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "upCategory", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;

        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCategoryException")) {
            throw new NoCategoryException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public void downCategory(String session_id, String category)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, SQLException, NoCollectionException,
            SQLConnectionException, NoRightsException, NoCategoryException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "downCategory", category);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCategoryException")) {
            throw new NoCategoryException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
    }

    public String[] listAllFiles(String session_id)
            throws NoSelectedCollectionException, NoSessionException,
            NoCollectionException, UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listAllFiles");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] files = (String[]) obj;
            return files;
        } else {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoRightsExcetion")) {
                throw new NoRightsException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public void createSubjectVCard(String session_id, String subject,
            String path) throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException, NoSubjectException, NoLinkException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectVCard", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoSubjectException")) {
            throw new NoSubjectException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("NoRightsExcetion")) {
            throw new NoRightsException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        }
    }

    public void deleteSubjectVCard(String session_id, String subject,
            String path) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, NoLinkException,
            NoSubjectException, NoRightsException, NoCollectionException,
            SQLException, NoFileException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "deleteSubjectVCard", subject, path);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoSubjectException")) {
            throw new NoSubjectException();
        } else if (res.equals("NoLinkException")) {
            throw new NoLinkException();
        } else if (res.equals("NoRightsExcetion")) {
            throw new NoRightsException();
        } else if (res.equals("NoPathException")) {
            throw new NoFileException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public String getSubjectVCard(String session_id, String subject)
            throws NoSelectedCollectionException, NoSessionException,
            NoSubjectException, NoCollectionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoRightsException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "getSubjectVCard", subject);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoSubjectException")) {
                throw new NoSubjectException();
            } else if (res.equals("NoRightsExcetion")) {
                throw new NoRightsException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
            return res;
        }
        return "";
    }

    public void createGenerator(String id, String generator)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, ExistSameObjectException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "createGenerator", generator);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("void")) {
                return;
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("ExistSameObjectException")) {
                throw new ExistSameObjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }

    }

    public List<Object[]> listGenerator(String id, int from, int number,
            String orderBy, String orderType) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "listGenerator", String.valueOf(from), String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof List) {
            return (List<Object[]>) obj;
        }
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        return new ArrayList<Object[]>();
    }

    public String getGeneratorQuery(String id, String generator)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getGeneratorQuery", generator);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            }
            return res;
        }
        return "";
    }

    public String getGeneratorProduction(String id, String generator)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getGeneratorProduction", generator);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            }
            return res;
        }
        return "";
    }

    public void setGeneratorQuery(String id, String generator, String query)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "setGeneratorQuery", generator, query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("void")) {
                return;
            }

            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
    }

    public void setGeneratorProduction(String id, String generator, String prod)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "setGeneratorProduction", generator, prod);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("void")) {
                return;
            }

            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
    }

    public void executeGenerator(String id, String generator)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, NoSelectedCollectionException, NoCollectionException,
            NoObjectException, SQLException, SQLConnectionException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "executeGenerator", generator);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("void")) {
                return;
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
    }

    public void cleanGenerator(String id, String generator)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, NoSelectedCollectionException, NoCollectionException,
            NoObjectException, SQLException, SQLConnectionException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "cleanGenerator", generator);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("void")) {
                return;
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
    }

    /**
     * method to rename a Generator
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     */
    public void renameGenerator(String id, String generator, String newName)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, NoSelectedCollectionException, NoCollectionException,
            NoObjectException, SQLException, SQLConnectionException,
            ExistSameObjectException, ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "renameGenerator", generator, newName);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("void")) {
                return;
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            } else if (res.equals("ExistSameObjectException")) {
                throw new ExistSameObjectException();
            }
        }
    }

    public void deleteGenerator(String id, String generator)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "deleteGenerator", generator);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("void")) {
                return;
            }
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            } else if (res.equals("SQLException")) {
                throw new SQLException();
            } else if (res.equals("SQLConnectionException")) {
                throw new SQLConnectionException();
            }
        }
    }

    public String hightLightProductionRule(String id, String generator,
            String lang) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "hightLightProductionRule", generator, lang);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else if (res.equals("NoObjectException")) {
                throw new NoObjectException();
            }
            return res;
        }
        return "";
    }

    public String[] listEmails(String session_id) throws NoSessionException,
            NoCollectionException, IOException, ClassNotFoundException,
            UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "listEmails");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            String[] emails = (String[]) obj;
            return emails;
        } else {
            String exception = (String) obj;
            if (exception.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (exception.equals("UnknownApplisemServerException")) {
                throw new UnknownApplisemServerException();
            } else if (exception.equals("NoCollectionException")) {
                throw new NoCollectionException();
            } else {
                return null;
            }
        }
    }

    public void createAlert(String id, String alert) throws NoSessionException,
            NoSelectedCollectionException, SQLException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoCollectionException,
            SQLConnectionException, ExistSameObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "createAlert", alert);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("void".equals(obj)) {
            return;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoSelectedCollectionException".equals(obj)) {
            throw new NoSelectedCollectionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("SQLException".equals(obj)) {
            throw new SQLException();
        }
        if ("SQLConnectionException".equals(obj)) {
            throw new SQLConnectionException();
        }
        if ("ExistSameObjectException".equals(obj)) {
            throw new ExistSameObjectException();
        }
    }

    public List<Object[]> listAlert(String id, int from, int number,
            String orderBy, String orderType) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {
        hasLocaleSession(id);
        Request request = new Request(id, "listAlert", String.valueOf(from),
                String.valueOf(number), orderBy, orderType);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof List) {
            return (List<Object[]>) obj;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoSelectedCollectionException".equals(obj)) {
            throw new NoSelectedCollectionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        return null;
    }

    public void setAlertEmails(String id, String alert, String[] emails)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLConnectionException, SQLException,
            NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "setAlertEmails", alert);
        request.setObject(emails);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("void".equals(obj)) {
            return;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        if ("SQLConnectionException".equals(obj)) {
            throw new SQLConnectionException();
        }
        if ("SQLException".equals(obj)) {
            throw new SQLException();
        }
    }

    public String getAlertQuery(String id, String alert)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getAlertQuery", alert);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        return (String) obj;
    }

    public String[] getAlertEmails(String id, String alert)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "getAlertEmails", alert);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String[]) {
            return (String[]) obj;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        return null;
    }

    public void setAlertQuery(String id, String alert, String body)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLConnectionException, SQLException,
            NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "setAlertQuery", alert, body);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("void".equals(obj)) {
            return;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        if ("SQLConnectionException".equals(obj)) {
            throw new SQLConnectionException();
        }
        if ("SQLException".equals(obj)) {
            throw new SQLException();
        }
    }

    public void deleteAlert(String id, String alert) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLConnectionException, SQLException,
            NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "deleteAlert", alert);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("void".equals(obj)) {
            return;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        if ("SQLConnectionException".equals(obj)) {
            throw new SQLConnectionException();
        }
        if ("SQLException".equals(obj)) {
            throw new SQLException();
        }
    }

    /**
     * method to rename a Alert
     *
     * @param session_id the id of the current session
     * @param from the old name
     * @param to the new name
     */
    public void renameAlert(String id, String alert, String newName)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLConnectionException, SQLException,
            NoObjectException, ExistSameObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "renameAlert", alert, newName);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("void".equals(obj)) {
            return;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        if ("SQLConnectionException".equals(obj)) {
            throw new SQLConnectionException();
        }
        if ("SQLException".equals(obj)) {
            throw new SQLException();
        }
        if ("NoDicoEntryException".equals(obj)) {
            throw new NoObjectException();
        }
        if ("ExistSameObjectException".equals(obj)) {
            throw new ExistSameObjectException();
        }
    }

    public LinkPartitions computLinkPartioning(String id, double factor)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "executeLinkPartioning", String.valueOf(factor));                 //?????????????
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof LinkPartitions) {
            return (LinkPartitions) obj;
        }
        if (obj.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        }
        return null;
    }

    public void doLinkPartioning(String id, LinkPartitions lp)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, ClassNotFoundException, IOException,
            UnknownApplisemServerException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "doLinkPartioning");
        request.setObject(lp);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj.equals("void")) {
            return;
        }
        if (obj.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        }
        if (obj.equals("SQLException")) {
            throw new SQLException();
        }
        if (obj.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }

    }

    public ConnexPartitions computConnexPartioning(String id)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, UnknownApplisemServerException, IOException,
            ClassNotFoundException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "executeConnexPartioning");                               //?????????
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof ConnexPartitions) {
            return (ConnexPartitions) obj;
        }
        if (obj.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        }
        return null;
    }

    public void doConnexPartioning(String id, ConnexPartitions lp)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, ClassNotFoundException, IOException,
            UnknownApplisemServerException, SQLException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "doConnexPartioning");
        request.setObject(lp);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj.equals("void")) {
            return;
        }
        if (obj.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        }
        if (obj.equals("SQLException")) {
            throw new SQLException();
        }
        if (obj.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }

    }

    public int countGenerators(String id) throws NoSessionException,
            NoSelectedCollectionException, NoCollectionException,
            ClassNotFoundException, IOException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "countGenerators");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        }
        return (Integer) obj;
    }

    public int countAlerts(String id) throws NoSessionException,
            NoSelectedCollectionException, NoCollectionException,
            ClassNotFoundException, IOException, UnknownApplisemServerException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "countAlerts");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj.equals("NoCollectionException")) {
            throw new NoCollectionException();
        }
        if (obj.equals("NoSessionException")) {
            throw new NoSessionException();
        }
        return (Integer) obj;
    }

    public void removeAlertEmail(String id, String alert, String email,
            String idx) throws NoSessionException,
            NoSelectedCollectionException, NoCollectionException,
            ClassNotFoundException, IOException,
            UnknownApplisemServerException, NoObjectException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "removeAlertEmail", alert, email, idx);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("void".equals(obj)) {
            return;
        }
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        if ("SQLConnectionException".equals(obj)) {
            throw new SQLConnectionException();
        }
        if ("SQLException".equals(obj)) {
            throw new SQLException();
        }
        if ("NoDicoEntryException".equals(obj)) {
            throw new NoObjectException();
        }
    }

    public GeneratorSimulation simulateGeneratorAdd(String id, String generator)
            throws NoSessionException, NoSelectedCollectionException,
            NoCollectionException, ClassNotFoundException, IOException,
            UnknownApplisemServerException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "simulateGenerator", generator, "a");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        if (obj instanceof GeneratorSimulation) {
            return (GeneratorSimulation) obj;
        }
        return null;
    }

    public GeneratorSimulation simulateGeneratorRemove(String id,
            String generator) throws NoSessionException,
            NoSelectedCollectionException, NoCollectionException,
            ClassNotFoundException, IOException,
            UnknownApplisemServerException, NoObjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "simulateGenerator", generator, "d");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if ("NoSessionException".equals(obj)) {
            throw new NoSessionException();
        }
        if ("NoCollectionException".equals(obj)) {
            throw new NoCollectionException();
        }
        if ("NoObjectException".equals(obj)) {
            throw new NoObjectException();
        }
        if (obj instanceof GeneratorSimulation) {
            return (GeneratorSimulation) obj;
        }
        return null;
    }

    public void deleteLinksIfNotUsed(String sid, Collection<String[]> toremove)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "deleteLinksIfNotUsed");
        request.setObject(toremove);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void deleteCategoriesIfNotUsed(String sid,
            Collection<String> toremove) throws NoSessionException,
            UnknownApplisemServerException, ClassNotFoundException,
            IOException, NoSelectedCollectionException, SQLConnectionException,
            SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "deleteCategoriesIfNotUsed");
        request.setObject(toremove);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void deleteOrphan(String sid, Collection<String> verif)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "deleteOrphan");
        request.setObject(verif);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void deClassifyMassive(String sid, Collection<Classify> dc)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "deClassifyMassive");
        request.setObject(dc);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void deleteStatements(String sid, Collection<Statement> sts)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "deleteStatements");
        request.setObject(sts);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void updateDicoEntry(String sid, Collection<String[]> data)
            throws NoSessionException, UnknownApplisemServerException,
            ClassNotFoundException, IOException, NoSelectedCollectionException,
            SQLConnectionException, SQLException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "updateDicoEntry");
        request.setObject(data);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }

    }

    public void setMultiViewVariables(String sid, String query, String view,
            Vector<String> vars) throws SQLConnectionException, SQLException,
            NoSessionException, IOException, UnknownApplisemServerException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoQueryException, NoViewException, StringLengthException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "setMultiViewVariables", query, view);
        request.setObject(vars);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        }
    }

    public void createQueryMultiView(String sid, String query, String view)
            throws SQLConnectionException, SQLException, StringLengthException,
            ExistViewException, ExistSameObjectException, NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, NoQueryException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "createQueryMultiView", query, view);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("ExistViewException")) {
            throw new ExistViewException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        }
    }

    public void deleteQueryMultiView(String sid, String query, String view)
            throws SQLConnectionException, SQLException, NoViewException,
            NoQueryException, NoSessionException, IOException,
            ClassNotFoundException, UnknownApplisemServerException,
            NoSelectedCollectionException, NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "deleteQueryMultiView", query, view);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        }

    }

    public List<String> listQueryMultiView(String sid, String query)
            throws NoSessionException, IOException, ClassNotFoundException,
            UnknownApplisemServerException, NoSelectedCollectionException,
            NoCollectionException, NoQueryException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "listQueryMultiView", query);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof List) {
            return (List<String>) obj;
        }
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        }
        return null;
    }

    /**
     * method to rename a Multi View of a Query
     *
     * @param sid the id of the current session
     * @param query the parent query
     * @param from the old name
     * @param to the new name
     */
    public void renameQueryMultiView(String sid, String query, String from,
            String to) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLException, SQLConnectionException,
            StringLengthException, NoViewException, ExistSameObjectException,
            ExistViewException, NoQueryException {

        Log_Applisem_Entree();
        hasLocaleSession(sid);
        Request request = new Request(sid, "renameQueryMultiView", query, from, to);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        } else if (res.equals("NoViewException")) {
            throw new NoViewException();
        } else if (res.equals("ExistViewException")) {
            throw new ExistViewException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("NoQueryException")) {
            throw new NoQueryException();
        }

    }

    public List<SubjectRessemblance> similarSubjects(String id, String subject,
            double factor) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoSubjectException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "similarSubjects", subject, String.valueOf(factor));
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String excep = (String) obj;
            if (excep.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (excep.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (excep.equals("NoSubjectException")) {
                throw new NoSubjectException();
            }
        }
        return (List<SubjectRessemblance>) obj;

    }

    public void reorderLinks(String id, String src, String dest)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "reorderLinks", src, dest);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void reorderBoards(String id, String src, String dest)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "reorderBoards", src, dest);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public void reorderCategories(String id, String src, String dest)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException, SQLException, SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "reorderCategories", src, dest);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        }
    }

    public List<String[]> findReverseStatement(String id, String s, String l,
            String o) throws NoSessionException,
            UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "findReverseStatement", s, l, o);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        if (obj instanceof List) {
            return (List<String[]>) obj;
        }
        return null;
    }

    public List<LinkAnalyseInfo> listLinksAnalyseInfo(String id)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "listLinksAnalyseInfo");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        if (obj instanceof List) {
            return (List<LinkAnalyseInfo>) obj;
        }
        return null;
    }

    public List<SubjectAnalyseInfo> listSubjectsAnalyseInfo(String id)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "listSubjectsAnalyseInfo");
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        if (obj instanceof List) {
            return (List<SubjectAnalyseInfo>) obj;
        }
        return null;
    }

    public List<Boolean> existsStatements(String id, List<Statement> sts)
            throws NoSessionException, UnknownApplisemServerException,
            IOException, ClassNotFoundException, NoSelectedCollectionException,
            NoCollectionException {

        Log_Applisem_Entree();
        hasLocaleSession(id);
        Request request = new Request(id, "existsStatements");
        request.setObject(sts);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        if (obj instanceof String) {
            String res = (String) obj;
            if (res.equals("NoSessionException")) {
                throw new NoSessionException();
            } else if (res.equals("NoSelectedCollectionException")) {
                throw new NoSelectedCollectionException();
            } else if (res.equals("NoCollectionException")) {
                throw new NoCollectionException();
            }
        }
        if (obj instanceof List) {
            return (List<Boolean>) obj;
        }
        return null;
    }

    public void createSubjectsFiles(String session_id, List<String[]> association)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectsFiles");
        request.setObject(association);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        }
    }

    public void createSubjectsImages(String session_id, List<String[]> association)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectsImages");
        request.setObject(association);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        }
    }

    public void createSubjectsUrls(String session_id, List<String[]> association)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectsUrls");
        request.setObject(association);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        }
    }

    public void createSubjectsRSS(String session_id, List<String[]> association)
            throws UnknownApplisemServerException, IOException,
            ClassNotFoundException, NoSessionException,
            NoSelectedCollectionException,
            NoRightsException, StringLengthException, SQLException,
            NoCollectionException, ExistSameObjectException,
            SQLConnectionException {

        Log_Applisem_Entree();
        hasLocaleSession(session_id);
        Request request = new Request(session_id, "createSubjectsRSS");
        request.setObject(association);
        Socket socket = connect();
        writeToServer(socket, request);
        Object obj = readFromServer(socket);
        disconnect(socket);
        String res = (String) obj;
        if (res.equals("NoSessionException")) {
            throw new NoSessionException();
        } else if (res.equals("NoSelectedCollectionException")) {
            throw new NoSelectedCollectionException();
        } else if (res.equals("SQLException")) {
            throw new SQLException();
        } else if (res.equals("SQLConnectionException")) {
            throw new SQLConnectionException();
        } else if (res.equals("NoCollectionException")) {
            throw new NoCollectionException();
        } else if (res.equals("NoRightsException")) {
            throw new NoRightsException();
        } else if (res.equals("ExistSameObjectException")) {
            throw new ExistSameObjectException();
        } else if (res.equals("StringLengthException")) {
            throw new StringLengthException();
        }
    }
}
