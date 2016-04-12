package applisem.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

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
import applisem.objects.SharedObject;
import applisem.objects.Statement;
import applisem.objects.SubjectAnalyseInfo;
import applisem.objects.SubjectRessemblance;
import applisem.objects.SubjectSheet;

/**
 * THis interface is a backward compatibility with inefficient implementation from MME.
 * 
 * The previous implementation devided communication between ClientSession an a service locator.
 * This division prevent the same runtime of different servers.
 * 
 * The new communication only define a client session to keep compatibility and reduce developpement we created a virtual interface called ServiceLocator containing definition of the method previously defined int the class ServiceLocator  
 * 
 * @author LBO
 */
public interface ServiceLocator {

	public abstract void setLocale(Locale l);

	public abstract void hasLocaleSession(String session_id)
			throws NoSessionException;

	public abstract String[] listSessions(String session_id)
			throws NoSessionException, ClassNotFoundException, IOException,
			UnknownApplisemServerException;

	public abstract int countSessions(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException;

	public abstract void deleteSession(String session_id, String id)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract void deleteAllSessions(String session_id)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract void createSQLCollection(String session_id, String name,
			String server) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			ExistCollectionException, SQLException, NoCollectionException,
			ExistCategoryException, ExistSameObjectException, NoLinkException,
			StringLengthException, NoCategoryException, SQLConnectionException;

	public abstract void createSQLCollection(String session_id, String name,
			String server, String model) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			ExistCollectionException, SQLException, NoCollectionException,
			ExistCategoryException, ExistSameObjectException, NoLinkException,
			StringLengthException, NoCategoryException, SQLConnectionException;

	public abstract boolean existCollection(String session_id, String name)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract String[] listCollections(String session_id)
			throws NoSessionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract void deleteCollection(String session_id, String name)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoCollectionException,
			SQLException, NoRoleException, SQLConnectionException;

	public abstract boolean isConnected(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException;

	public abstract void renameCollection(String session_id, String src,
			String dst) throws NoSessionException, NoCollectionException,
			ExistCollectionException, SQLException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, SQLConnectionException;

	public abstract void loadCollection(String session_id, String name)
			throws NoCollectionException, NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract void importCollection(String session_id, String name,
			String file_name) throws UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSessionException,
			NoSelectedCollectionException, ImportException,
			PartialImportException, NoCollectionException;

	public abstract void createAndImportCollection(String session_id,
			String name, String file_name)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException, NoCollectionException,
			ImportException, ExistCollectionException, PartialImportException;

	public abstract byte[] exportCollectionModel(String session_id)
			throws UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSessionException, NoSelectedCollectionException,
			ExportException;

	public abstract String importCollectionModel(String session_id, byte[] model)
			throws UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSessionException, NoSelectedCollectionException,
			ImportException, PartialImportException;

	public abstract byte[] exportCollectionQueries(String session_id)
			throws UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSessionException, NoSelectedCollectionException,
			ExportException;

	public abstract String importCollectionQueries(String session_id,
			byte[] model) throws UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSessionException,
			NoSelectedCollectionException, ImportException,
			PartialImportException;

	public abstract void exportCollection(String session_id, String name,
			String file) throws UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSessionException,
			NoCollectionException, ExportException;

	public abstract void saveCollection(String session_id, String collection)
			throws NoSessionException, NoCollectionException, SQLException,
			ClassNotFoundException, IOException, UnknownApplisemServerException;

	public abstract void selectCollection(String session_id, String collection)
			throws NoSessionException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException;

	public abstract String selectedCollection(String session_id)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	/*-----------------------------------------------------------------*/
	/*-  SUBJECTS                                                      */
	/*-----------------------------------------------------------------*/
	public abstract void createSubject(String session_id, String subject)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, ExistSubjectException, SQLException,
			StringLengthException, NoRightsException, ExistSameObjectException,
			NoCollectionException, SQLConnectionException;

	public abstract void createSubjects(String session_id, String[] subjects)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, ExistSubjectException, SQLException,
			NoRightsException, StringLengthException, NoCollectionException,
			ExistSameObjectException, SQLConnectionException;

	public abstract void createSubjectSynonym(String session_id,
			String subject, String synonym)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException,
			StringLengthException, SQLException, NoRightsException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;

	public abstract void createInBetweenInput(String session_id,
			String inbetween, String subject)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoInBetweenException,
			NoObjectException, ExistInputException, ExistOutputException,
			StringLengthException, SQLException, SQLConnectionException;

	public abstract void createInBetweenOutput(String session_id,
			String inbetween, String subject)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoInBetweenException,
			NoObjectException, ExistOutputException, ExistInputException,
			StringLengthException, SQLException;

	public abstract void createUser(String session_id, String name)
			throws NoSelectedCollectionException, NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, StringLengthException, SQLException,
			ExistUserException, ExistSameObjectException,
			SQLConnectionException;

	public abstract void createRole(String session_id, String name)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException, StringLengthException,
			SQLException, ExistSameObjectException, ExistRoleException,
			SQLConnectionException;

	public abstract boolean existSubject(String session_id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoRightsException, NoCollectionException;

	public abstract boolean existUser(String session_id, String user)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract boolean existRole(String session_id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract void renameSubject(String session_id, String from, String to)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, SQLException,
			NoSubjectException, ExistSubjectException, NoRightsException,
			StringLengthException, ExistSameObjectException,
			NoCollectionException, NoSelectedCollectionException,
			SQLConnectionException;

	public abstract void renameInBetween(String session_id, String from,
			String to) throws NoSelectedCollectionException,
			NoSessionException, StringLengthException, NoInBetweenException,
			ExistInBetweenException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, SQLException,
			ExistSameObjectException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteSubject(String session_id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoSubjectException, NoRightsException, SQLException,
			NoCollectionException, NoLinkException, SQLConnectionException;

	public abstract void deleteSubjectSynonym(String session_id,
			String subject, String synonym) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoRightsException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteInBetweenInput(String session_id,
			String inbetween, String subject) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoObjectException,
			NoInBetweenException, SQLException, SQLConnectionException;

	public abstract void deleteInBetweenOutput(String session_id,
			String inbetween, String subject) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoObjectException,
			NoInBetweenException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract Object[][] listSubjects(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoRightsException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract String[] listSubjectSynonyms(String session_id,
			String subject) throws NoSelectedCollectionException,
			NoSessionException, NoSubjectException, NoCollectionException,
			NoRightsException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract String[] listInBetweenInputs(String session_id,
			String inbetween) throws NoSelectedCollectionException,
			NoSessionException, NoInBetweenException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract String[] listInBetweenOutputs(String session_id,
			String inbetween) throws NoSelectedCollectionException,
			NoSessionException, NoInBetweenException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract Object[][] listUsers(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			ClassNotFoundException, UnknownApplisemServerException, IOException;

	public abstract Object[][] listRoles(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract Object[][] listRoles(String session_id, String orderBy,
			String orderType) throws NoSelectedCollectionException,
			NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract Object[][] listSubjects(String session_id, String orderBy,
			String orderType) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException, ClassNotFoundException,
			IOException, NoRightsException, UnknownApplisemServerException;

	public abstract Object[][] listUsers(String session_id, String orderBy,
			String orderType) throws NoSelectedCollectionException,
			NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract Object[][] listUsers(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract String[] listUserRoles(String session_id, String user)
			throws NoSelectedCollectionException, NoSessionException,
			NoUserException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract String[] listRoleCollections(String session_id, String role)
			throws NoSelectedCollectionException, NoSessionException,
			ClassNotFoundException, NoRoleException, IOException,
			UnknownApplisemServerException;

	public abstract String[] directSearch(String session_id, String exp,
			String in) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoLinkException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract String[] directSearch(String session_id, String exp,
			String in, int from, int number) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoLinkException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract SharedObject[] searchDico(String session_id, String exp)
			throws NoSessionException, NoSelectedCollectionException,
			NoCollectionException, IOException, ClassNotFoundException,
			UnknownApplisemServerException;

	public abstract String[] searchSubjects(String session_id, String exp,
			String startWith) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException, IOException,
			ClassNotFoundException, UnknownApplisemServerException;

	public abstract String[] searchLinks(String session_id, String exp,
			String startWith) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException, IOException,
			ClassNotFoundException, UnknownApplisemServerException;

	public abstract String[] searchCategories(String session_id, String exp,
			String startWith) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException, IOException,
			ClassNotFoundException, UnknownApplisemServerException;

	public abstract String[] searchBoards(String session_id, String exp,
			String startWith) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException, IOException,
			ClassNotFoundException, UnknownApplisemServerException;

	public abstract int countCollections(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException;

	public abstract int countSubjects(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException, NoCollectionException,
			NoSelectedCollectionException, NoRightsException;

	public abstract int countSubjectSynonyms(String session_id, String subject)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException, NoCollectionException,
			NoSubjectException, NoSelectedCollectionException,
			NoRightsException;

	public abstract void classifiesToBoards(String session_id, Classify[] cl)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException,
			NoBoardException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void classifies(String session_id, Classify[] cl)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void classify(String session_id, String category,
			String subject) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void classifyBoard(String session_id, String board,
			String subject) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException,
			NoBoardException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void addTraceFinderToUserPreference(String session_id,
			String tracefinder) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoInBetweenException, SQLException,
			NoCollectionException, SQLConnectionException;

	public abstract void addQueryToUserPreference(String session_id,
			String query) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException, SQLException,
			NoCollectionException, SQLConnectionException;

	public abstract void deClassify(String session_id, String category,
			String subject) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoCategoryException, NoRightsException, SQLException,
			NoCollectionException, SQLConnectionException;

	public abstract void deleteTraceFinderFromUserPreference(String session_id,
			String tracefinder) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoInBetweenException,
			SQLException, NoCollectionException, SQLConnectionException;

	public abstract void deleteQueryFromUserPreference(String session_id,
			String query) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			SQLException, NoCollectionException, SQLConnectionException;

	public abstract void deClassifyBoard(String session_id, String board,
			String subject) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoBoardException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void setSubjectText(String session_id, String subject,
			String text) throws NoSelectedCollectionException,
			NoSessionException, NoSubjectException, NoRightsException,
			NoLinkException, TextLengthException, SQLException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteSubjectText(String session_id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoSubjectException, NoRightsException, SQLException,
			NoCollectionException, NoLinkException, SQLConnectionException;

	public abstract String getSubjectText(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoRightsException, SQLException,
			SQLConnectionException;

	public abstract String isFileServerStarted(String session_id, String host,
			String port) throws NoSessionException, ClassNotFoundException,
			IOException, UnknownApplisemServerException;

	public abstract String getParameter(String session_id, String name)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoParameterException;

	public abstract void createSubjectFile(String session_id, String subject,
			String path) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException, NoLinkException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;

	public abstract void createSubjectImage(String session_id, String subject,
			String path) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException, NoLinkException,
			SQLConnectionException;

	public abstract void createSubjectURL(String session_id, String subject,
			String path) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException, NoLinkException,
			SQLConnectionException;

	public abstract void deleteSubjectFile(String session_id, String subject,
			String path) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoLinkException,
			NoSubjectException, NoRightsException, NoCollectionException,
			SQLException, NoFileException, SQLConnectionException;

	public abstract String[] listUserPreferedTraceFinders(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoRightsException;

	public abstract String[] listUserPreferedQueries(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoRightsException;

	public abstract String[] listSubjectFiles(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoRightsException;

	public abstract String[] listAssociedSubject(String session_id,
			String associated) throws NoSessionException,
			UnknownApplisemServerException, IOException, NoSessionException,
			NoSelectedCollectionException, NoFileException, NoRightsException,
			NoCollectionException, ClassNotFoundException;

	public abstract String[] listFileSubjects(String session_id, String file)
			throws NoSelectedCollectionException, NoSessionException,
			NoFileException, NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException, NoCollectionException;

	public abstract int countAssociedSubjects(String session_id, String file)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoRightsException,
			NoCollectionException;

	public abstract int countFileSubjects(String session_id, String file)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoRightsException,
			NoCollectionException;

	public abstract String[] listRSSSubjects(String session_id, String rss)
			throws NoSelectedCollectionException, NoSessionException,
			NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException, NoCollectionException;

	public abstract int countRSSSubjects(String session_id, String rss)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoRightsException,
			NoCollectionException;

	public abstract int countImageSubjects(String session_id, String file)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoRightsException,
			NoCollectionException;

	public abstract void createSubjectRss(String session_id, String subject,
			String path) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException, NoLinkException,
			NoRightsException, SQLException, StringLengthException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;

	public abstract void deleteSubjectRss(String session_id, String subject,
			String path) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoLinkException, NoSubjectFileException, NoRightsException,
			SQLException, NoCollectionException, NoFileException,
			SQLConnectionException;

	public abstract String[] listSubjectRss(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoRightsException;

	public abstract void deleteSubjectURL(String session_id, String subject,
			String path) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoLinkException, NoSubjectImageException, NoRightsException,
			SQLException, NoCollectionException, MalformedURLException,
			SQLConnectionException;

	public abstract void deleteSubjectImage(String session_id, String subject,
			String path) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoLinkException, NoSubjectImageException, NoRightsException,
			SQLException, NoCollectionException, MalformedURLException,
			SQLConnectionException;

	public abstract String[] ListSubjectImages(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException, NoRightsException,
			UnknownApplisemServerException, IOException, ClassNotFoundException;

	public abstract String[] listSubjectURLs(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException, NoRightsException,
			UnknownApplisemServerException, IOException, ClassNotFoundException;

	public abstract String[] suggestSubjectLinks(String session_id,
			String subject) throws NoSelectedCollectionException,
			NoSessionException, NoSubjectException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoRightsException, NoCategoryException,
			NoCollectionException;

	public abstract String subjectGraph(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			SQLException, NoRightsException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSubjectException, SQLConnectionException;

	public abstract String traceFinderGraph(String session_id,
			String tracefinder) throws NoSelectedCollectionException,
			NoSessionException, NoInputsException, SQLException,
			EmptyJoinException, NoOutputsException, NoInputsException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoInBetweenException, NoCollectionException;

	public abstract Statement[] listSubjectStatements(String session_id,
			String subject, int start, int page)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException, NoRightsException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract Statement[] listSubjectStatements(String session_id,
			String subject) throws NoSelectedCollectionException,
			NoSessionException, NoSubjectException, NoCollectionException,
			NoRightsException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract String[] listSubjectLinks(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException, NoRightsException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract String[] listSubjectLinkObjects(String session_id,
			String subject, String link) throws NoSelectedCollectionException,
			NoSessionException, NoSubjectException, NoCollectionException,
			NoRightsException, NoLinkException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract String[] listSubjectLinkObjects(String session_id,
			String subject, String link, int from, int number)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException, NoRightsException,
			NoLinkException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract String[][] listDoubleSubjectStatements(String session_id,
			String subject) throws NoSelectedCollectionException,
			NoSessionException, NoRightsException, NoSubjectException,
			ClassNotFoundException, UnknownApplisemServerException,
			IOException, NoCollectionException;

	public abstract String[] listAll(String session_id)
			throws NoSessionException, NoRightsException,
			ClassNotFoundException, UnknownApplisemServerException, IOException;

	public abstract void mergeSubjects(String session_id, String subject1,
			String subject2) throws NoSelectedCollectionException,
			NoSessionException, TextLengthException, NoRightsException,
			SQLException, NoSubjectException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoCollectionException,
			SQLConnectionException;

	public abstract void duplicateSubject(String session_id, String subject1,
			String subject2) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSubjectException, ExistSubjectException,
			NoSelectedCollectionException, StringLengthException,
			NoRightsException, ExistSameObjectException, NoCollectionException,
			SQLException, SQLConnectionException;

	public abstract String[] listSubjectSearch(String session_id,
			String subject, String link, String object, String in)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, NoRightsException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	/*-----------------------------------------------------------------*/
	/*-  LINKS                                                         */
	/*-----------------------------------------------------------------*/
	public abstract void createLink(String session_id, String rel, String irel)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, StringLengthException, SQLException,
			NoCollectionException, SQLConnectionException, NoRightsException;

	public abstract void createReverseLink(String session_id, String rel,
			String irel) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			StringLengthException, SQLException, NoCollectionException,
			SQLConnectionException;

	// Cardinality
	public abstract void setLinkCardinality(String session_id, String link,
			String card) throws NoSelectedCollectionException,
			NoSessionException, SQLException, StringLengthException,
			UnknownApplisemServerException, NoLinkException,
			ClassNotFoundException, IOException, NoCollectionException,
			SQLConnectionException;

	public abstract int getLinkCardinality(String session_id, String link)
			throws NoSelectedCollectionException, NoSessionException,
			UnknownApplisemServerException, NoLinkException,
			ClassNotFoundException, IOException;

	public abstract boolean existLink(String session_id, String link)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract Object[][] listLinks(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, NoRightsException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	// multiple links
	public abstract String[] listReverseLinks(String session_id, String link)
			throws NoSelectedCollectionException, NoSessionException,
			NoLinkException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract String[] listDoubleLinks(String session_id, int from,
			int number) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException, NoRightsException,
			ClassNotFoundException, UnknownApplisemServerException, IOException;

	public abstract String[] listDoubleLinks(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract String reverseLink(String session_id, String link)
			throws NoSelectedCollectionException, NoSessionException,
			NoLinkException, NoRightsException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract void renameLink(String session_id, String from, String to)
			throws NoSelectedCollectionException, NoSessionException,
			NoLinkException, ExistSameObjectException, NoCollectionException,
			SQLException, StringLengthException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, ExistLinkException, SQLConnectionException;

	public abstract void deleteLink(String session_id, String link)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoLinkException, SQLException, NoCollectionException,
			SQLConnectionException;

	// multiple reverse links
	public abstract void deleteReverseLink(String session_id, String link,
			String ilink) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoLinkException,
			SQLException, NoCollectionException, SQLConnectionException;

	public abstract Object[][] listLinks(String session_id, String orderBy,
			String orderType) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException, NoRightsException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract String[] listDoubleLinks(String session_id, String orderBy,
			String orderType) throws NoSelectedCollectionException,
			NoSessionException, NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException, NoCollectionException;

	public abstract int countLinks(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			NoRightsException;

	public abstract int countReverseLinks(String session_id, String link)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			NoLinkException;

	public abstract int countDoubleLinks(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			NoRightsException;

	public abstract String[] suggestLinkObjects(String session_id, String link)
			throws NoSelectedCollectionException, NoSessionException,
			NoLinkException, NoRightsException, NoCollectionException,
			NoSubjectException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract void mergeLinks(String session_id, String link1,
			String link2) throws NoSelectedCollectionException,
			NoSessionException, NoLinkException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoRightsException, NoCollectionException,
			SQLConnectionException, SQLException;

	public abstract String[] listLinkObjectType(String session_id, String link)
			throws NoSelectedCollectionException, NoSessionException,
			NoLinkException, NoCollectionException,
			UnknownApplisemServerException, IOException, ClassNotFoundException;

	public abstract void createLinkObjectType(String session_id, String link,
			String category) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteLinkObjectType(String session_id, String link,
			String category) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoLinkException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract String[] listLinkSubjectType(String session_id, String link)
			throws NoSelectedCollectionException, NoSessionException,
			NoRightsException, NoCollectionException,
			UnknownApplisemServerException, IOException, ClassNotFoundException;

	public abstract void createLinkSubjectType(String session_id, String link,
			String category) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteLinkSubjectType(String session_id, String link,
			String category) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoLinkException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	/** ----------------------------------------------------------------- **/
	/** - CATEGORIES **/
	/**
	 * -----------------------------------------------------------------*
	 * 
	 * @throws SQLConnectionException
	 */
	public abstract void renameCategory(String session_id, String from,
			String to) throws NoSelectedCollectionException,
			NoSessionException, SQLException, NoRightsException,
			NoCollectionException, ExistSameObjectException,
			NoCategoryException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, ExistCategoryException,
			SQLConnectionException;

	public abstract void renameBoard(String session_id, String from, String to)
			throws NoSelectedCollectionException, NoSessionException,
			ExistSameObjectException, ExistBoardException,
			NoCollectionException, SQLException, StringLengthException,
			NoBoardException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, SQLConnectionException;

	public abstract void categoryAddLink(String session_id, String category,
			String link) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void categoryRemoveLink(String session_id, String category,
			String link) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			NoCategoryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract String[] listParameters(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract Object[][] listCategories(String session_id,
			String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract String[] listSubjectCategories(String session_id,
			String subject, String complement)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSubjectException;

	public abstract String[] listLinkCategories(String session_id, String link)
			throws NoSelectedCollectionException, NoSessionException,
			NoLinkException, NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract String[] listSubjectBoards(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSubjectException;

	public abstract String[] listUserPerferedQueries(String session_id,
			String subject) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSubjectException;

	public abstract String[] listUserPerferedTraceFinders(String session_id,
			String subject) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract Object[][] listBoards(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract Object[][] listCategories(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException, NoCollectionException;

	public abstract Object[][] listBoards(String session_id, int from,
			int number) throws NoSelectedCollectionException,
			NoCollectionException, NoSessionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract Object[][] listBoards(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoCollectionException,
			NoSessionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract int countQueryVariables(String session_id, String query)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			NoCollectionException;

	// EXCEPTION DONE
	public abstract int countCategories(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException, NoCollectionException,
			NoSelectedCollectionException, NoRightsException;

	// EXCEPTION done
	public abstract int countBoards(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCollectionException;

	public abstract void mergeCategories(String session_id, String cat1,
			String cat2) throws NoSelectedCollectionException,
			NoSessionException, SQLException, NoRightsException,
			NoCollectionException, NoLinkException, NoCategoryException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, SQLConnectionException;

	public abstract void mergeBoards(String session_id, String board1,
			String board2) throws NoSelectedCollectionException,
			NoSessionException, SQLException, NoCollectionException,
			NoBoardException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, SQLConnectionException;

	public abstract String[] listCategoriesLinks(String session_id,
			String categories, String sep)
			throws NoSelectedCollectionException, NoSessionException,
			NoCategoryException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract String[] listLinkCategoryTypeObjects(String session_id,
			String cat) throws NoSelectedCollectionException,
			NoSessionException, NoCategoryException, NoLinkException,
			NoRightsException, NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract String[] listCategoryDynamicLinks(String session_id,
			String category) throws NoSelectedCollectionException,
			NoSessionException, NoCategoryException, NoRightsException,
			NoSubjectException, ClassNotFoundException,
			UnknownApplisemServerException, IOException, NoCollectionException;

	public abstract String[] listCategoryLinks(String session_id,
			String category) throws NoSelectedCollectionException,
			NoSessionException, NoCategoryException, NoRightsException,
			ClassNotFoundException, UnknownApplisemServerException,
			IOException, NoCollectionException;

	public abstract Object[][] listCategoryMembers(String session_id,
			String category, int from, int number, String orderBy,
			String orderType) throws NoSelectedCollectionException,
			NoSessionException, NoCategoryException, NoRightsException,
			ClassNotFoundException, UnknownApplisemServerException,
			IOException, NoCollectionException;

	public abstract String[] filterCategoryMembers(String session_id,
			String category, String links)
			throws NoSelectedCollectionException, NoSessionException,
			NoCategoryException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract int countFilterCategoryMembers(String session_id,
			String category, String links)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCategoryException,
			NoRightsException, NoCollectionException;

	public abstract String[] filterCategoryMembers(String session_id,
			String category, String links, int from, int number,
			String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoCategoryException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract Object[][] listCategoryMembers(String session_id,
			String category, int from, int number)
			throws NoSelectedCollectionException, NoSessionException,
			NoCategoryException, NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException, NoCollectionException;

	public abstract Object[][] listBoardMembers(String session_id,
			String board, int from, int number)
			throws NoSelectedCollectionException, NoSessionException,
			NoBoardException, NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract Object[][] listBoardMembers(String session_id,
			String board, int from, int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoBoardException, NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract int countCategoryMembers(String session_id, String category)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCategoryException,
			NoRightsException, NoCollectionException;

	public abstract int countBoardMembers(String session_id, String board)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoBoardException,
			NoCollectionException;

	// EXCEPTION d
	public abstract int countCategoryLinks(String session_id, String category)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCategoryException,
			NoRightsException, NoCollectionException;

	public abstract boolean existCategory(String session_id, String category)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException;

	public abstract boolean existBoard(String session_id, String board)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract void deleteCategory(String session_id, String category)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoCollectionException,
			NoSelectedCollectionException, NoCategoryException, SQLException,
			NoRightsException, SQLConnectionException;

	public abstract void deleteBoard(String session_id, String board)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoBoardException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void createCategory(String session_id, String category)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException, ExistCategoryException,
			NoSelectedCollectionException, StringLengthException, SQLException,
			ExistSameObjectException, NoRightsException, NoCollectionException,
			SQLConnectionException;

	public abstract void createBoard(String session_id, String board)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException,
			ExistBoardException, SQLConnectionException;

	public abstract Object[][] listCategoryMembers(String session_id,
			String category) throws NoSelectedCollectionException,
			NoSessionException, NoCategoryException, NoRightsException,
			ClassNotFoundException, UnknownApplisemServerException,
			IOException, NoCollectionException;

	public abstract Object[][] listCategoryMembers(String session_id,
			String category, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoCategoryException, NoRightsException, ClassNotFoundException,
			UnknownApplisemServerException, IOException, NoCollectionException;

	public abstract String[] filterCategoryMembers(String session_id,
			String category, String links, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoCategoryException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract Object[][] listBoardMembers(String session_id, String board)
			throws NoSelectedCollectionException, NoSessionException,
			NoBoardException, NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract String[] suggestCategoryLinks(String session_id,
			String category) throws NoSelectedCollectionException,
			NoSessionException, NoCategoryException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	/*-------------------------------------------------------------*/
	/*-  STATEMENTS                                                */
	/*-------------------------------------------------------------*/
	public abstract void createStatement(String session_id, String s, String v,
			String c) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			StringLengthException, SQLException, NoCollectionException,
			ExistSameObjectException, ExistSubjectException,
			ExistStatementException, SQLConnectionException;

	public abstract void createStatements(String session_id,
			Statement[] statements) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			StringLengthException, SQLException, NoCollectionException,
			ExistSameObjectException, ExistSubjectException,
			ExistStatementException, SQLConnectionException;

	public abstract void createStatements2(String session_id,
			Statement[] statements) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoLinkException,
			StringLengthException, SQLException, NoCollectionException,
			ExistSameObjectException, ExistSubjectException,
			ExistStatementException, SQLConnectionException;

	public abstract void deleteStatement(String session_id, String s, String v,
			String c) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoSubjectException,
			NoLinkException, NoComplementException, SQLException,
			NoCollectionException, SQLConnectionException;

	public abstract GraphResult getCategoryGraph(String session_id,
			String category) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoCategoryException, NoRightsException,
			NoLinkException, NoObjectException;

	public abstract GraphResult getSubjectGraph(String session_id,
			String subject) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSubjectException, NoRightsException,
			NoLinkException, NoObjectException;

	public abstract String[] listStatements(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	// EXCEPTION DONE
	public abstract String[] checkCollection(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException, NoLinkException,
			NoCollectionException;

	public abstract String[] getStatementInfos(String session_id, String s,
			String v, String c) throws NoSelectedCollectionException,
			NoSessionException, NoSubjectException, NoLinkException,
			NoComplementException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract List<QueryParameterInfo> parseAndGetQueryParameters(
			String session_id, String queryText) throws NoSessionException,
			IOException, UnknownApplisemServerException,
			ClassNotFoundException, NoSelectedCollectionException,
			StringLengthException, SQLException, BadFormatException,
			SQLConnectionException;

	public abstract QueryResults parseAndExecuteQuery(String session_id,
			String queryText, Map<String, String> parameters)
			throws NoLinkException, NoObjectException, SQLException,
			ClassNotFoundException, IOException, BadFormatException,
			NoSessionException, NoSelectedCollectionException,
			StringLengthException, EmptySelectException, EmptyJoinException,
			SQLConnectionException, UnknownApplisemServerException;

	public abstract void setQueryBody(String session_id, String query,
			String body) throws NoSessionException,
			NoSelectedCollectionException, StringLengthException,
			NoCollectionException, NoQueryException,
			UnknownApplisemServerException, SQLException,
			ClassNotFoundException, IOException, SQLConnectionException;

	public abstract String getQueryBody(String session_id, String body)
			throws SQLException, ClassNotFoundException, IOException,
			NoSessionException, NoSelectedCollectionException,
			StringLengthException, SQLConnectionException,
			UnknownApplisemServerException;

	public abstract void createQuery(String session_id, String query)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, StringLengthException,
			ExistSameObjectException, SQLException, ExistQueryException,
			NoCollectionException, SQLConnectionException;

	public abstract void createInBetween(String session_id, String inBetween)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, ExistInBetweenException,
			StringLengthException, ExistSameObjectException, SQLException,
			NoCollectionException, SQLConnectionException;

	public abstract void createQueryTCView(String session_id, String view,
			String query) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			ExistViewException, StringLengthException, SQLException,
			ExistSameObjectException, NoCollectionException,
			SQLConnectionException;

	public abstract void createQueryTSView(String session_id, String view,
			String query) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			StringLengthException, SQLException, ExistViewException,
			ExistSameObjectException, NoCollectionException,
			SQLConnectionException;

	public abstract void createTCViewCell(String session_id, String view,
			String query, String value) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, StringLengthException, SQLException,
			NoCollectionException, ExistViewException, NoViewException,
			SQLConnectionException;

	public abstract void deleteTCViewCell(String session_id, String view,
			String query, String value) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, SQLException, NoCollectionException,
			NoViewException, SQLConnectionException;

	public abstract void deleteTCViewRow(String session_id, String view,
			String query, String value) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, SQLException, NoCollectionException,
			NoViewException, SQLConnectionException;

	public abstract void deleteTCViewCol(String session_id, String view,
			String query, String value) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, SQLException, NoCollectionException,
			NoViewException, SQLConnectionException;

	public abstract void deleteTSViewCol(String session_id, String view,
			String query, String value) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, SQLException, NoCollectionException,
			NoViewException, SQLConnectionException;

	public abstract String getTCViewCell(String session_id, String view,
			String query) throws NoSelectedCollectionException,
			NoSessionException, UnknownApplisemServerException,
			NoQueryException, NoQueryViewException, ClassNotFoundException,
			IOException, StringLengthException, SQLException,
			SQLConnectionException;

	public abstract void createTCViewCol(String session_id, String view,
			String query, String value) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, StringLengthException, SQLException,
			NoCollectionException, NoViewException, SQLConnectionException;

	public abstract void createTSViewCol(String session_id, String view,
			String query, String value) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, StringLengthException, SQLException,
			NoCollectionException, SQLConnectionException;

	public abstract String[] listTSViewVariable(String session_id, String view,
			String query) throws IOException, UnknownApplisemServerException,
			ClassNotFoundException, SQLException, NoSessionException,
			NoQueryException, NoViewException, SQLConnectionException;

	public abstract void createTSViewCols(String session_id, String[] values,
			String view, String query) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			StringLengthException, SQLException, NoViewException,
			SQLConnectionException;

	public abstract void createTCViewRow(String session_id, String view,
			String query, String value) throws UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoQueryException,
			NoQueryViewException, SQLException, NoCollectionException,
			NoViewException, StringLengthException, SQLConnectionException;

	public abstract boolean existQuery(String session_id, String query)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract void renameQuery(String session_id, String from, String to)
			throws NoSelectedCollectionException, NoSessionException,
			NoQueryException, ExistQueryException, SQLException,
			NoCollectionException, StringLengthException,
			StringLengthException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, ExistSameObjectException,
			SQLConnectionException;

	public abstract void renameQueryTCView(String session_id, String from,
			String to) throws NoSelectedCollectionException,
			NoSessionException, NoQueryViewException, StringLengthException,
			ExistViewException, ExistSameObjectException, SQLException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoCollectionException, SQLConnectionException;

	public abstract void renameQueryTSView(String session_id, String from,
			String to) throws NoSelectedCollectionException,
			NoSessionException, NoQueryViewException, ExistSameObjectException,
			ExistViewException, NoViewException, StringLengthException,
			SQLException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteQuery(String session_id, String query)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoQueryException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteInBetween(String session_id, String inbetween)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoInBetweenException, SQLException, NoCollectionException,
			SQLConnectionException;

	public abstract void deleteQueryTCView(String session_id, String query,
			String view) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoViewException,
			SQLException, NoCollectionException, NoQueryException,
			SQLConnectionException;

	public abstract void deleteQueryTSView(String session_id, String query,
			String view) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoViewException,
			SQLException, NoCollectionException, NoQueryException,
			SQLConnectionException;

	public abstract Object[][] listQueries(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract Object[][] listQueries(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract Object[][] listInBetweens(String session_id, int from,
			int number, String orderBy, String orderType)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract Object[][] listInBetweens(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, ClassNotFoundException,
			UnknownApplisemServerException, IOException;

	public abstract int countInBetweens(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCollectionException;

	public abstract int countQueries(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoCollectionException;

	public abstract String[] listQueryTCViews(String session_id, String query)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoQueryException,
			SQLConnectionException, SQLException;

	public abstract String[] listQueryTSViews(String session_id, String query)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoQueryException,
			SQLConnectionException, SQLException;

	public abstract String getTCViewCol(String session_id, String view,
			String query) throws NoSelectedCollectionException,
			NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoQueryException,
			NoQueryViewException, SQLException, SQLConnectionException;

	public abstract String[] listTSViewCols(String session_id, String view,
			String query) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoQueryException, NoQueryViewException;

	public abstract String[] listViewRows(String session_id, String view,
			String query) throws NoSelectedCollectionException,
			NoSessionException, NoCollectionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoQueryException, NoQueryViewException;

	public abstract String getTCViewRow(String session_id, String view,
			String query) throws NoSelectedCollectionException,
			NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoQueryException,
			NoQueryViewException, SQLConnectionException, SQLException;

	public abstract List<QueryParameterInfo> listQueryParameter(
			String session_id, String query) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoQueryException, SQLException, BadFormatException,
			NoCollectionException, SQLConnectionException;

	public abstract String[] listQueryVariables(String session_id, String query)
			throws NoSelectedCollectionException, NoSessionException,
			BadFormatException, NoQueryException, SQLException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoCollectionException, SQLConnectionException;

	public abstract String[] listQueryLinks(String session_id, String query)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoQueryException;

	public abstract String[] listQueryCategories(String session_id, String query)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoQueryException;

	public abstract QueryResults executeQuery(String session_id, String query,
			Map<String, String> parameter) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			EmptySelectException, EmptyJoinException, BadFormatException,
			SQLException, NoCollectionException, SQLConnectionException;

	public abstract String[] executeInbetweenFrom_To_(String session_id,
			String in, String out, String path) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoInputsException,
			NoOutputsException, NoCollectionException;

	public abstract String[] executeInBetween(String session_id,
			String inbetween) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			NoInputsException, NoOutputsException, NoCollectionException;

	public abstract PathFinderResult executePathFinderFrom_To_(
			String session_id, String in, String out, String path)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			NoInputsException, NoOutputsException, NoCollectionException,
			TraceFinderException;

	public abstract PathFinderResult executePathFinder(String session_id,
			String inbetween) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			NoInputsException, NoOutputsException, NoCollectionException,
			TraceFinderException;

	public abstract PathFinderResult executePathFinder(String session_id,
			String inbetween, String force) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoQueryException,
			NoInputsException, NoOutputsException, NoCollectionException,
			TraceFinderException;

	public abstract void print(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract String[] listDico(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			ClassNotFoundException, UnknownApplisemServerException, IOException;

	public abstract void stopServer(String session_id)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException;

	public abstract void setParameter(String session_id, String name,
			String value) throws NoSelectedCollectionException,
			NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract void removeParameter(String session_id, String name)
			throws NoSelectedCollectionException, NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract int countUsers(String session_id)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException;

	public abstract int countRoles(String session_id)
			throws UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSessionException;

	public abstract void activateUser(String session_id, String user)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoUserException, SQLException,
			SQLConnectionException, NoRightsException;

	public abstract void desactivateUser(String session_id, String user)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoUserException, SQLException,
			SQLConnectionException, NoRightsException;

	public abstract void deleteUser(String session_id, String user)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoUserException, SQLException,
			SQLConnectionException, NoRightsException;

	public abstract void deleteUserRole(String session_id, String user,
			String role) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoUserException, NoRoleException, SQLException,
			SQLConnectionException, NoRightsException;

	public abstract void deleteRoleCollection(String session_id, String role,
			String collection) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoCollectionException, NoRoleException, SQLException,
			SQLConnectionException;

	public abstract void deleteRole(String session_id, String role)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoRoleException, SQLException,
			SQLConnectionException, NoRightsException;

	public abstract void renameUser(String session_id, String from, String to)
			throws NoSelectedCollectionException, NoSessionException,
			NoUserException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, ExistUserException,
			SQLException, ExistSameObjectException, SQLConnectionException,
			NoRightsException;

	public abstract void renameRole(String session_id, String from, String to)
			throws NoSelectedCollectionException, NoSessionException,
			ExistSameObjectException, SQLException, ExistRoleException,
			NoRoleException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, SQLConnectionException,
			NoRightsException;

	public abstract void setUserPassword(String session_id, String user,
			String password) throws NoSelectedCollectionException,
			NoSessionException, NoUserException, SQLException,
			PasswordLengthException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, ExistSameObjectException,
			StringLengthException, SQLConnectionException;

	public abstract void setUserEmail(String session_id, String user,
			String email) throws NoSelectedCollectionException,
			NoSessionException, NoUserException, SQLException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, ExistSameObjectException,
			StringLengthException, SQLConnectionException;

	public abstract String getUserPassword(String session_id, String user)
			throws NoSelectedCollectionException, NoSessionException,
			NoUserException, UnknownApplisemServerException, IOException,
			ClassNotFoundException;

	public abstract String getUserEmail(String session_id, String user)
			throws NoSelectedCollectionException, NoSessionException,
			NoUserException, UnknownApplisemServerException, IOException,
			ClassNotFoundException;

	public abstract String isActiveUser(String session_id, String user)
			throws NoSelectedCollectionException, NoSessionException,
			NoUserException, UnknownApplisemServerException, IOException,
			ClassNotFoundException;

	public abstract void setUserLogin(String session_id, String user,
			String login) throws NoSelectedCollectionException,
			NoSessionException, NoUserException, ExistLoginException,
			StringLengthException, SQLException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, ExistSameObjectException,
			SQLConnectionException;

	public abstract void setUserRole(String session_id, String user, String role)
			throws NoSelectedCollectionException, NoSessionException,
			NoUserException, NoRoleException, StringLengthException,
			SQLException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, SQLConnectionException, NoRightsException;

	public abstract void setRoleCollection(String session_id, String role,
			String collection) throws NoSelectedCollectionException,
			NoSessionException, NoRoleException, NoCollectionException,
			SQLException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, SQLConnectionException;

	public abstract Rights getRoleRights(String session_id, String role)
			throws NoSessionException, NoRoleException, NoRightsException,
			UnknownApplisemServerException, IOException, ClassNotFoundException;

	public abstract void setRoleRights(String session_id, String role,
			Map<String, Boolean> rights) throws NoSessionException,
			NoRoleException, NoRightsException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, SQLException,
			SQLConnectionException;

	public abstract void setRoleProperty(String session_id, String role,
			String name, String value) throws NoSelectedCollectionException,
			NoSessionException, NoRoleException, StringLengthException,
			SQLException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, SQLConnectionException;

	public abstract void setUserProperty(String session_id, String user,
			String name, String value) throws NoSelectedCollectionException,
			NoSessionException, NoUserException, SQLException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, SQLConnectionException;

	public abstract String getUserLogin(String session_id, String user)
			throws NoSelectedCollectionException, NoSessionException,
			NoUserException, UnknownApplisemServerException, IOException,
			ClassNotFoundException;

	public abstract String getRoleProperty(String session_id, String role,
			String name) throws NoSelectedCollectionException,
			NoSessionException, NoRoleException,
			UnknownApplisemServerException, IOException, ClassNotFoundException;

	public abstract String getUserProperty(String session_id, String user,
			String name) throws NoSelectedCollectionException,
			NoSessionException, NoUserException,
			UnknownApplisemServerException, IOException, ClassNotFoundException;

	public abstract String[] listRoleProperties(String session_id, String role)
			throws NoSelectedCollectionException, NoSessionException,
			NoRoleException, UnknownApplisemServerException,
			ClassNotFoundException, IOException;

	public abstract void register(String id, String host, String port,
			String clazz, String type) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract void unRegister(String id, String host, String port,
			String clazz, String type) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException, IOException;

	public abstract SubjectSheet getSubjectSheet(String id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoSubjectException, SQLException, SQLConnectionException;

	public abstract String getImagesPath(String id) throws NoSessionException,
			UnknownException, IOException, ClassNotFoundException,
			NoSelectedCollectionException, NoCollectionException,
			UnknownApplisemServerException;

	public abstract String getFilesPath(String id) throws NoSessionException,
			UnknownException, IOException, ClassNotFoundException,
			NoSelectedCollectionException, NoCollectionException,
			UnknownApplisemServerException;

	public abstract String getVCardsPath(String id) throws NoSessionException,
			UnknownException, IOException, ClassNotFoundException,
			NoSelectedCollectionException, NoCollectionException,
			UnknownApplisemServerException;

	public abstract String getPreferedImage(String id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoSubjectException, SQLException, NoRightsException,
			NoCollectionException;

	public abstract FileServerConf getFileServerConfig(String id)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract FileServerConf getFileServerConfig(String id,
			String collection) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract List<String> listFileServers(String id)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException;

	public abstract List<SharedObject> searchParamInDico(String id,
			String query, String queryBody, String parameter, String exp)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoQueryException,
			SQLException, BadFormatException, SQLConnectionException;

	public abstract QueryHighLight hightLightQuery(String id, String query,
			String queryBody) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoQueryException, SQLException,
			BadFormatException, SQLConnectionException;

	public abstract String[] matchDico(String id, String[] matches)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException;

	public abstract SharedObject getObjectFromId(String id, int oid)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException;

	public abstract int getObjectId(String id, String object)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException;

	public abstract SubjectSheet getSubjectSheetById(String id, int objectId)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			SQLConnectionException, SQLException, NoSubjectException;

	public abstract int countSubjectImage(String id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoSubjectException;

	public abstract int countSubjectFile(String id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoSubjectException;

	public abstract int countSubjectURL(String id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoSubjectException;

	public abstract int countSubjectRSS(String id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoSubjectException;

	public abstract String[] listSubjectInUserPreference(String id)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLException, SQLConnectionException;

	public abstract void addSubjectToUserPreference(String id, String subject)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLException, SQLConnectionException,
			NoSubjectException;

	public abstract void setKeepConnected(boolean b);

	public abstract void upLink(String session_id, String link)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException, SQLException,
			NoCollectionException, SQLConnectionException, NoRightsException,
			NoLinkException;

	public abstract void downLink(String session_id, String link)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, SQLException, NoCollectionException,
			SQLConnectionException, NoRightsException, NoLinkException;

	public abstract void upSubjectImage(String session_id, String subject,
			String image) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, SQLException, NoCollectionException,
			SQLConnectionException, NoSubjectException, NoRightsException;

	public abstract void downSubjectImage(String session_id, String subject,
			String image) throws IOException, ClassNotFoundException,
			NoSessionException, NoSelectedCollectionException, SQLException,
			NoRightsException, NoCollectionException, SQLConnectionException,
			NoSubjectException, UnknownApplisemServerException;

	public abstract void upBoard(String session_id, String board)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, SQLException, NoCollectionException,
			SQLConnectionException, NoRightsException, NoBoardException;

	public abstract void downBoard(String session_id, String board)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, SQLException, NoCollectionException,
			SQLConnectionException, NoRightsException, NoBoardException;

	public abstract void upCategory(String session_id, String category)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, SQLException, NoCollectionException,
			SQLConnectionException, NoRightsException, NoCategoryException;

	public abstract void downCategory(String session_id, String category)
			throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, SQLException, NoCollectionException,
			SQLConnectionException, NoRightsException, NoCategoryException;

	public abstract String[] listAllFiles(String session_id)
			throws NoSelectedCollectionException, NoSessionException,
			NoCollectionException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoRightsException;

	public abstract void createSubjectVCard(String session_id, String subject,
			String path) throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException, NoSubjectException, NoLinkException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;

	public abstract void deleteSubjectVCard(String session_id, String subject,
			String path) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, NoLinkException,
			NoSubjectException, NoRightsException, NoCollectionException,
			SQLException, NoFileException, SQLConnectionException;

	public abstract String getSubjectVCard(String session_id, String subject)
			throws NoSelectedCollectionException, NoSessionException,
			NoSubjectException, NoCollectionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoRightsException;

	public abstract void createGenerator(String id, String generator)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, ExistSameObjectException, SQLException,
			SQLConnectionException;

	public abstract List<Object[]> listGenerator(String id, int from,
			int number, String orderBy, String orderType)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract String getGeneratorQuery(String id, String generator)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException;

	public abstract String getGeneratorProduction(String id, String generator)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException;

	public abstract void setGeneratorQuery(String id, String generator,
			String query) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException, SQLException,
			SQLConnectionException;

	public abstract void setGeneratorProduction(String id, String generator,
			String prod) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException, SQLException,
			SQLConnectionException;

	public abstract void executeGenerator(String id, String generator)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, NoSelectedCollectionException, NoCollectionException,
			NoObjectException, SQLException, SQLConnectionException,
			ClassNotFoundException;

	public abstract void cleanGenerator(String id, String generator)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, NoSelectedCollectionException, NoCollectionException,
			NoObjectException, SQLException, SQLConnectionException,
			ClassNotFoundException;

	public abstract void renameGenerator(String id, String generator,
			String newName) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			NoSelectedCollectionException, NoCollectionException,
			NoObjectException, SQLException, SQLConnectionException,
			ExistSameObjectException, ClassNotFoundException;

	public abstract void deleteGenerator(String id, String generator)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException, SQLException,
			SQLConnectionException;

	public abstract String hightLightProductionRule(String id,
			String generator, String lang) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException;

	public abstract String[] listEmails(String session_id)
			throws NoSessionException, NoCollectionException, IOException,
			ClassNotFoundException, UnknownApplisemServerException;

	public abstract void createAlert(String id, String alert)
			throws NoSessionException, NoSelectedCollectionException,
			SQLException, UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoCollectionException,
			SQLConnectionException, ExistSameObjectException;

	public abstract List<Object[]> listAlert(String id, int from, int number,
			String orderBy, String orderType) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract void setAlertEmails(String id, String alert, String[] emails)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLConnectionException, SQLException,
			NoObjectException;

	public abstract String getAlertQuery(String id, String alert)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException;

	public abstract String[] getAlertEmails(String id, String alert)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoObjectException;

	public abstract void setAlertQuery(String id, String alert, String body)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLConnectionException, SQLException,
			NoObjectException;

	public abstract void deleteAlert(String id, String alert)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLConnectionException, SQLException,
			NoObjectException;

	public abstract void renameAlert(String id, String alert, String newName)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLConnectionException, SQLException,
			NoObjectException, ExistSameObjectException;

	public abstract LinkPartitions computLinkPartioning(String id, double factor)
			throws NoSessionException, NoSelectedCollectionException,
			NoCollectionException, UnknownApplisemServerException, IOException,
			ClassNotFoundException;

	public abstract void doLinkPartioning(String id, LinkPartitions lp)
			throws NoSessionException, NoSelectedCollectionException,
			NoCollectionException, ClassNotFoundException, IOException,
			UnknownApplisemServerException, SQLException,
			SQLConnectionException;

	public abstract ConnexPartitions computConnexPartioning(String id)
			throws NoSessionException, NoSelectedCollectionException,
			NoCollectionException, UnknownApplisemServerException, IOException,
			ClassNotFoundException;

	public abstract void doConnexPartioning(String id, ConnexPartitions lp)
			throws NoSessionException, NoSelectedCollectionException,
			NoCollectionException, ClassNotFoundException, IOException,
			UnknownApplisemServerException, SQLException,
			SQLConnectionException;

	public abstract int countGenerators(String id) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			ClassNotFoundException, IOException, UnknownApplisemServerException;

	public abstract int countAlerts(String id) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			ClassNotFoundException, IOException, UnknownApplisemServerException;

	public abstract void removeAlertEmail(String id, String alert,
			String email, String idx) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			ClassNotFoundException, IOException,
			UnknownApplisemServerException, NoObjectException,
			SQLConnectionException, SQLException;

	public abstract GeneratorSimulation simulateGeneratorAdd(String id,
			String generator) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			ClassNotFoundException, IOException,
			UnknownApplisemServerException, NoObjectException;

	public abstract GeneratorSimulation simulateGeneratorRemove(String id,
			String generator) throws NoSessionException,
			NoSelectedCollectionException, NoCollectionException,
			ClassNotFoundException, IOException,
			UnknownApplisemServerException, NoObjectException;

	public abstract void deleteLinksIfNotUsed(String sid,
			Collection<String[]> toremove) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, SQLConnectionException,
			SQLException;

	public abstract void deleteCategoriesIfNotUsed(String sid,
			Collection<String> toremove) throws NoSessionException,
			UnknownApplisemServerException, ClassNotFoundException,
			IOException, NoSelectedCollectionException, SQLConnectionException,
			SQLException;

	public abstract void deleteOrphan(String sid, Collection<String> verif)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			SQLConnectionException, SQLException;

	public abstract void deClassifyMassive(String sid, Collection<Classify> dc)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			SQLConnectionException, SQLException;

	public abstract void deleteStatements(String sid, Collection<Statement> sts)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			SQLConnectionException, SQLException;

	public abstract void updateDicoEntry(String sid, Collection<String[]> data)
			throws NoSessionException, UnknownApplisemServerException,
			ClassNotFoundException, IOException, NoSelectedCollectionException,
			SQLConnectionException, SQLException;

	public abstract void setMultiViewVariables(String sid, String query,
			String view, Vector<String> vars) throws SQLConnectionException,
			SQLException, NoSessionException, IOException,
			UnknownApplisemServerException, ClassNotFoundException,
			NoSelectedCollectionException, NoQueryException, NoViewException,
			StringLengthException;

	public abstract void createQueryMultiView(String sid, String query,
			String view) throws SQLConnectionException, SQLException,
			StringLengthException, ExistViewException,
			ExistSameObjectException, NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, NoQueryException;

	public abstract void deleteQueryMultiView(String sid, String query,
			String view) throws SQLConnectionException, SQLException,
			NoViewException, NoQueryException, NoSessionException, IOException,
			ClassNotFoundException, UnknownApplisemServerException,
			NoSelectedCollectionException, NoCollectionException;

	public abstract List<String> listQueryMultiView(String sid, String query)
			throws NoSessionException, IOException, ClassNotFoundException,
			UnknownApplisemServerException, NoSelectedCollectionException,
			NoCollectionException, NoQueryException;

	public abstract void renameQueryMultiView(String sid, String query,
			String from, String to) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLException, SQLConnectionException,
			StringLengthException, NoViewException, ExistSameObjectException,
			ExistViewException, NoQueryException;

	public abstract List<SubjectRessemblance> similarSubjects(String id,
			String subject, double factor) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoSubjectException;

	public abstract void reorderLinks(String id, String src, String dest)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLException, SQLConnectionException;

	public abstract void reorderBoards(String id, String src, String dest)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLException, SQLConnectionException;

	public abstract void reorderCategories(String id, String src, String dest)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException, SQLException, SQLConnectionException;

	public abstract List<String[]> findReverseStatement(String id, String s,
			String l, String o) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract List<LinkAnalyseInfo> listLinksAnalyseInfo(String id)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract List<SubjectAnalyseInfo> listSubjectsAnalyseInfo(String id)
			throws NoSessionException, UnknownApplisemServerException,
			IOException, ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public abstract List<Boolean> existsStatements(String id,
			List<Statement> sts) throws NoSessionException,
			UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSelectedCollectionException,
			NoCollectionException;

	public void createLinks(String id, List<String[]> links) 
	throws NoSessionException, UnknownApplisemServerException,
	IOException, ClassNotFoundException, NoSelectedCollectionException,
	NoCollectionException,NoRightsException,SQLConnectionException,SQLException,
	ExistSameObjectException,StringLengthException ;
	
	public abstract void createSubjectsFiles(String session_id, List<String[]> association)
	throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;
	public abstract void createSubjectsImages(String session_id, List<String[]> association)
	throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;
	public abstract void createSubjectsUrls(String session_id, List<String[]> association)
	throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;
	public abstract void createSubjectsRSS(String session_id, List<String[]> association)
	throws UnknownApplisemServerException, IOException,
			ClassNotFoundException, NoSessionException,
			NoSelectedCollectionException,
			NoRightsException, StringLengthException, SQLException,
			NoCollectionException, ExistSameObjectException,
			SQLConnectionException;
}