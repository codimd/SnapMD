package de.claudiuscoenen.snapmd.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import okhttp3.OkHttpClient;

public class SocketIoWrapper {
	private final String apiUrl;
	private final OkHttpClient client;
	private Socket socket;

	private JSONObject cursor = null;
	private String documentContent = null;
	private long documentRevision = 0;
	private String textToAppend = "";

	public SocketIoWrapper(String url, OkHttpClient httpClient) {
		apiUrl = url;
		client = httpClient;
		socket = null;
	}

	public void connect(String padId) {
		Log.i("socketio", "Establishing socket connection to pad " + padId);

		IO.Options opts = new IO.Options();
		opts.secure = true;
		opts.callFactory = client;
		opts.webSocketFactory = client;

		try {
			socket = IO.socket(apiUrl, opts);
		} catch (URISyntaxException e) {
			Log.w("socketio", e.toString());
			return;
		}

		socket.io().on(Manager.EVENT_TRANSPORT, args -> {
			Transport transport = (Transport) args[0];

			transport.on(Transport.EVENT_REQUEST_HEADERS, args12 -> {
				@SuppressWarnings("unchecked")
				Map<String, List<String>> headers = (Map<String, List<String>>) args12[0];
				String padURL = apiUrl + padId;
				Log.v("socketio", "Pad-URL " + padURL);
				headers.put("Referer", Arrays.asList(padURL));
			});

			transport.on(Transport.EVENT_ERROR, args1 -> {
				Exception e = (Exception) args1[0];
				Log.e("socketio", "transport error " + e);
				e.printStackTrace();
				e.getCause().printStackTrace();
			});
		});

		socket.on(Socket.EVENT_CONNECT, args -> Log.w("socketio", "connected."));

		socket.on(Socket.EVENT_DISCONNECT, args -> Log.w("socketio", "disconnected?"));
		socket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> Log.e("socketio", "connect timeout"));
		socket.on(Socket.EVENT_CONNECT_ERROR, args -> Log.e("socketio", "econnect error"));

		socket.on("doc", args -> {
			try {
				JSONObject ob = (JSONObject) args[0];
				documentContent = ob.getString("str");
				documentRevision = ob.getLong("revision");
				Log.i("socketio", "doc received, content: " + documentContent);
				addText();
			} catch (JSONException e) {
				Log.e("socketio", "json for 'doc' did not contain the exepected format.\n" + e.toString());
			}
		});
		socket.on("online users", args -> {
			Log.v("socketio", "online users received");
			try {
				JSONArray users = ((JSONObject) args[0]).getJSONArray("users");
				for (int i = 0; i < users.length(); i++) {
					JSONObject user = users.getJSONObject(i);
					// String name = user.getString("name");
					Log.v("socketio", "- parsed user: " + user.getString("name") + " / " + user.getString("id") + " / " + user.getString("userid"));
					if (!user.isNull("cursor")) { // TODO also check for user name or something like that!
						cursor = user.getJSONObject("cursor");
					}
				}
				addText();
			} catch (JSONException e) {
				Log.e("socketio", "json for 'online users' did not contain the expected format\n" + e.toString());
			}
		});

		socket.connect();
		Log.i("socketio", "trying to connect");
	}

	public void disconnect() {
		if (socket != null) {
			socket.disconnect();
		}
	}

	public void setText(String text) {
		textToAppend = text;
		addText();
	}

	private void addText() {
		if (documentContent == null) {
			Log.i("socketio", "Document not yet loaded");
			return;
		}

		// todo check availability of a valid cursor
		if (false) {
			Log.i("socketio", "No Cursor Found");
			return;
		}
		if (textToAppend == null) {
			Log.i("socketio", "no Image URL");
			return;
		}

		Log.i("socketio", "attempting to edit the document");
		//cursor.getLong("line");
		//cursor.getLong("ch");
		//cursor.getLong("xRel");
		// socket.emit("operation", "bla");

		// 42["operation", 1, [163, "https", 1], {ranges: [{anchor: 168, head: 168}]}]
		//        ^name    ^rev ^before ^ins ^after
		JSONArray operation = new JSONArray();
		operation.put(documentContent.length() - 1);
		// todo could also include some meta info in the alt attribute.

		operation.put(textToAppend);
		operation.put(1);

		JSONObject selection = new JSONObject();
		JSONArray selectionArray = new JSONArray();
		JSONObject selectionItem = new JSONObject();
		try {
			selection.put("ranges", selectionArray);
			selectionArray.put(selectionItem);
			selectionItem.put("anchor", documentContent.length());
			selectionItem.put("head", documentContent.length());
			socket.emit("operation", documentRevision, operation, selection, (Ack) args -> Log.i("socketio", "sent operation successfully"));
			documentContent = null;
			textToAppend = null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
