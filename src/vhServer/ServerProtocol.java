package vhServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ServerProtocol {
	private String action;
	private String content;
	private List<String> params;
	private StringBuilder response;

	public ServerProtocol() {
		params = new ArrayList<>();
		response = new StringBuilder();
	}

	public static ServerProtocol processInput(String theInput) {
		Gson gson = new Gson();
		ServerProtocol serverMessage = gson.fromJson(theInput, ServerProtocol.class);
		return serverMessage;
	}

	public void execute(Server server) {
		switch (action) {

		case "run":
			runFile(content);
			break;

		case "ping":
			appendServerResponse("success", "Ping OK !\nReçu : '" + content + "'\n");
			break;

		case "sendfile":
			String filename = "test.ini";
			if (params.size() > 0) {
				if (!"".equals(params.get(0)))
					filename = params.get(0);
			}
			copyFile(filename);
			break;

		case "restart":
			appendServerResponse("info", "Redémarrage du serveur...\n");
			server.toRestart();
			break;

		case "stop":
			appendServerResponse("info", "Arrêt du serveur.\n");
			appendServerResponse("error", "Vous n'êtes plus connecté.\n");
			server.toStop();
			break;

		default:
			appendServerResponse("info", "l'action " + action + " n'est pas encore prise en charge par le serveur.\nPour l'implémenter, ajoutez un cas dans la méthode <b>run</b> de la classe <b>ServerProtocol</b> du serveur java.");
			break;
		}
	}

	private String getServerResponse(String type, String content) {
		ServerResponse sr = new ServerResponse(type, content);
		Gson gson = new Gson();
		return gson.toJson(sr);
	}

	private void appendServerResponse(String type, String content) {
		this.response.append(this.getServerResponse(type, content) + "|");
	}

	private void runFile(String filename) {
		File f = new File(filename);
		if (f.exists()) {
			appendServerResponse("info", "Le fichier " + filename + " existe\n");
			if (f.canExecute()) {
				params.add(0, filename);
				ProcessBuilder pb = new ProcessBuilder(params.toArray(new String[0]));
				try {
					pb.start();
					appendServerResponse("success", "Fichier executé avec succès :\n" + filename + "\n");
				} catch (Exception e) {
					appendServerResponse("error", e.getMessage());
				}
			}
		} else {
			appendServerResponse("warning", "Le fichier " + filename + " n'existe pas sur le serveur\n");
		}
	}

	private void copyFile(String filename) {
		File sf = new File(filename);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(sf));
			writer.write(content);
			appendServerResponse("success", "Fichier " + filename + " enregistré sur le serveur\n");
		} catch (IOException e) {
			appendServerResponse("error", e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				appendServerResponse("info", "Ecriture de " + sf.length() + " octets\n");
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the params
	 */
	public List<String> getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(List<String> params) {
		this.params = params;
	}

	/**
	 * @return the response
	 */
	public String getResponse() {
		return response.toString();
	}

}
