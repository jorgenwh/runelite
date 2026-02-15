package net.runelite.client.plugins.agentcontroller;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@Slf4j
class AgentWebSocket
{
	private static final String WS_URL = "ws://localhost:8765";

	private final OkHttpClient okHttpClient;
	private WebSocket webSocket;
	private volatile boolean connected;
	private volatile String pendingMessage;

	AgentWebSocket(OkHttpClient okHttpClient)
	{
		this.okHttpClient = okHttpClient;
	}

	void connect()
	{
		if (webSocket != null)
		{
			return;
		}

		Request request = new Request.Builder()
			.url(WS_URL)
			.build();

		webSocket = okHttpClient.newWebSocket(request, new WebSocketListener()
		{
			@Override
			public void onOpen(WebSocket ws, Response response)
			{
				log.info("Agent WebSocket connected to {}", WS_URL);
				connected = true;
			}

			@Override
			public void onMessage(WebSocket ws, String text)
			{
				pendingMessage = text;
			}

			@Override
			public void onClosed(WebSocket ws, int code, String reason)
			{
				log.info("Agent WebSocket closed: {}/{}", code, reason);
				connected = false;
				webSocket = null;
			}

			@Override
			public void onFailure(WebSocket ws, Throwable t, Response response)
			{
				log.warn("Agent WebSocket error", t);
				connected = false;
				webSocket = null;
			}
		});
	}

	void disconnect()
	{
		if (webSocket != null)
		{
			webSocket.close(1000, "Plugin shutting down");
			webSocket = null;
		}
		connected = false;
		pendingMessage = null;
	}

	boolean isConnected()
	{
		return connected;
	}

	void send(String message)
	{
		if (webSocket != null)
		{
			webSocket.send(message);
		}
	}

	String poll()
	{
		String msg = pendingMessage;
		pendingMessage = null;
		return msg;
	}
}
