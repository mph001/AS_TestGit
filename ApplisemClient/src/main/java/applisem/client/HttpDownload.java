package applisem.client;

import java.io.*;
import java.net.*;

/**
 * UNUSED Class to manage Http Communication 
 * 
 * @author LBO
 */
public class HttpDownload {
	protected HttpURLConnection server;
	protected URL url;
	protected String folder;
	protected String filename;

	/*
	 * public HttpDownload(String url, String folder) throws Exception{ try{
	 * this.folder = folder ; this.url = new URL(url); filename =
	 * this.url.getHost(); String path = this.url.getPath(); int dot =
	 * path.lastIndexOf('.'); if (dot==-1) filename =filename + path +"/"; else
	 * filename = filename + path; filename = filename.replace('/', '$'); }
	 * catch(Exception e){ throw new Exception("Invalid URL:"+url); } }
	 */
	public HttpDownload(String url, String folder) throws Exception {
		try {
			this.folder = folder;
			this.url = new URL(url);
			String host = this.url.getHost();
			String path = this.url.getPath();
			int dot = path.lastIndexOf('.');
			int slash = path.lastIndexOf('/');
			if (dot == -1 || (dot < slash))
				filename = host + path + "/";
			else
				filename = host + path;
			filename = filename.replace('/', '$');
		} catch (Exception e) {
			throw new Exception("Invalid URL:" + url);
		}
	}

	public void connect(String method) throws Exception {
		try {
			server = (HttpURLConnection) url.openConnection();
			server.setDoInput(true);
			server.setDoOutput(true);
			server.setRequestMethod(method);
			server.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			server.connect();
		} catch (Exception e) {
			throw new Exception("Connection failed");
		}
	}

	public void disconnect() {
		server.disconnect();
	}

	public void createFile() throws Exception {
		try {
			File fd = new File(folder + "/" + filename);
			FileOutputStream out = new FileOutputStream(fd);
			InputStream in = server.getInputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e1) {
			throw new Exception("File not found");
		} catch (Exception e) {
			throw new Exception("Message 1: Unable to read input stream");
		}
	}

	public void post(String s) throws Exception {
		try {
			OutputStream os = server.getOutputStream();
			OutputStreamWriter osr = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osr);
			bw.write(s, 0, s.length());
			bw.flush();
			bw.close();
		} catch (Exception e) {
			throw new Exception("Unable to write to output stream");
		}
	}
}
