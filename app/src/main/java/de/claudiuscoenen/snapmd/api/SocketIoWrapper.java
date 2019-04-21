package de.claudiuscoenen.snapmd.api;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.engineio.client.Transport;
import okhttp3.OkHttpClient;
import timber.log.Timber;

public class SocketIoWrapper {
	private final String apiUrl;
	private final OkHttpClient client;
	private Socket socket;

	private JSONObject cursor = null;
	private String textToAppend = "";
	private String documentContent = "";
	private long documentRevision = 0;

	public SocketIoWrapper(String url, OkHttpClient httpClient) {
		apiUrl = url;
		client = httpClient;
		socket = null;
	}

	public void connect(String padId) {
		Timber.i("Establishing socket connection to pad %s", padId);
		textToAppend = "";
		documentContent = "";
		cursor = null;

		IO.Options opts = new IO.Options();
		opts.timeout = 1000;
		opts.secure = true;
		opts.callFactory = client;
		opts.webSocketFactory = client;

		Uri.Builder padUri = Uri.parse(apiUrl).buildUpon();
		padUri.appendQueryParameter("noteId", padId);

		if (socket != null) {
			socket.disconnect();
		}

		try {
			socket = IO.socket(padUri.toString(), opts);
		} catch (URISyntaxException e) {
			Timber.w(e.toString());
			return;
		}

		socket.io().on(Manager.EVENT_TRANSPORT, args -> {
			Transport transport = (Transport) args[0];

			transport.on(Transport.EVENT_REQUEST_HEADERS, args12 -> {
				@SuppressWarnings("unchecked")
				Map<String, List<String>> headers = (Map<String, List<String>>) args12[0];
				String padURL = apiUrl + padId;
				Timber.v("Pad-URL %s", padURL);
				headers.put("Referer", Arrays.asList(padURL));
			});

			transport.on(Transport.EVENT_ERROR, args1 -> {
				Exception e = (Exception) args1[0];
				Timber.e(e, "transport error");
				e.printStackTrace();
				e.getCause().printStackTrace();
			});
		});

		socket.on(Socket.EVENT_CONNECT, args -> Timber.w("connected."));
		socket.on(Socket.EVENT_DISCONNECT, args -> Timber.w("disconnected"));
		socket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> Timber.e("connect timeout"));
		socket.on(Socket.EVENT_CONNECT_ERROR, args -> Timber.e("econnect error"));

		socket.on("doc", args -> {
			try {
				JSONObject ob = (JSONObject) args[0];
				documentContent = ob.getString("str");
				documentRevision = ob.getLong("revision");
				Timber.d("doc received, content: %s", documentContent);
				addText();
			} catch (JSONException e) {
				Timber.e(e, "json for 'doc' did not contain the exepected format.");
			}
		});
		socket.on("online users", args -> {
			Timber.v("online users received");
			try {
				electCursor(args);
			} catch (JSONException e) {
				Timber.e(e, "json for 'online users' did not contain the expected format");
			}
			addText();
		});

		socket.connect();
		Timber.i("trying to connect");
	}

	private void electCursor(Object[] args) throws JSONException {
		String currentUserId = "";
		JSONArray users = ((JSONObject) args[0]).getJSONArray("users");
		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			if (user.getString("id").equals(socket.id())) {
				currentUserId = user.getString("userid");
			}
		}
		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			// String name = user.getString("name");
			if (user.isNull("cursor")) {
				Timber.v("Skipping cursor for %s: not set", user.getString("name"));
			} else if (user.getString("id").equals(socket.id())) {
				Timber.v("Skipping cursor for %s: own connection", user.getString("name"));
			} else if (!user.getString("userid").equals(currentUserId)) {
				Timber.v("Skipping cursor for %s: different userid", user.getString("name"));
			} else {
				// all previous checks successful, that's our cursor!
				Timber.v("Using cursor for %s: Connection ID: %s, UserId: %s", user.getString("name"), user.getString("id"), user.getString("userid"));
				cursor = user.getJSONObject("cursor");
			}
		}
	}

	public void disconnect() {
		if (socket != null) {
			socket.disconnect();
		}
	}

	public void setText(String text) {
		Timber.d("Incoming new text: %s", text);
		textToAppend = text;
		addText();
	}

	private void addText() {
		if (documentContent.equals("") && textToAppend.equals("")) {
			Timber.v("neither document nor text are set. Nothing to do.");
			return;
		}
		if (textToAppend.equals("")) {
			Timber.i("no text to add to the Document");
			return;
		}
		if (documentContent.equals("")) {
			Timber.i("document not yet loaded.");
			return;
		}

		// todo check availability of a valid cursor
		if (false) {
			Timber.i("No Cursor Found");
			return;
		}

		Timber.i("attempting to edit the document");

		int offset = documentContent.length(); // end of document as default
		if (cursor != null) {
			try {
				int line = cursor.getInt("line");
				int character = cursor.getInt("ch");
				offset = 0;
				while (line > 0) {
					line--;
					offset = documentContent.indexOf('\n', offset) + 1;
				}
				offset += character;
				Timber.v("final offset is %d", offset);
			} catch (JSONException e) {
				Timber.e(e, "While parsing the cursor information");
			}
		}

		// 42["operation", 1, [163, "https", 1], {ranges: [{anchor: 168, head: 168}]}]
		//        ^name    ^rev ^before ^ins ^after
		JSONArray operation = new JSONArray();
		if (offset > 0) {
			operation.put(offset);
		}
		operation.put(textToAppend);
		if (offset < documentContent.length()) {
			operation.put(documentContent.length() - offset);
		}

		JSONObject selection = new JSONObject();
		JSONArray selectionArray = new JSONArray();
		JSONObject selectionItem = new JSONObject();
		try {
			selection.put("ranges", selectionArray);
			selectionArray.put(selectionItem);
			selectionItem.put("anchor", documentContent.length());
			selectionItem.put("head", documentContent.length());
			Object[] data = {documentRevision, operation, selection};
			Timber.d("now emitting OT changeset #%d", documentRevision);
			socket.emit("operation", data, args -> Timber.i("sent operation successfully"));
			documentContent = "";
			textToAppend = "";
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
