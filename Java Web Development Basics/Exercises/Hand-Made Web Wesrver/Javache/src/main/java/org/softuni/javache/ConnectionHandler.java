package org.softuni.javache;

import org.softuni.javache.api.RequestHandler;
import org.softuni.javache.utility.InputStreamCachingService;
import org.softuni.javache.utility.LoggingService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;

    private InputStream clientSocketInputStream;

    private OutputStream clientSocketOutputStream;

    private InputStreamCachingService inputStreamCachingService;

    private LoggingService loggingService;

    private Set<RequestHandler> requestHandlers;

    public ConnectionHandler(Socket clientSocket, Set<RequestHandler> requestHandlers, InputStreamCachingService inputStreamCachingService, LoggingService loggingService) {
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
        this.inputStreamCachingService = inputStreamCachingService;
        this.loggingService = loggingService;
    }

    private void initializeConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.clientSocketInputStream = this.clientSocket.getInputStream();
            this.clientSocketOutputStream = this.clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void processClientConnection() throws IOException {
        for (RequestHandler requestHandler : this.requestHandlers) {
            requestHandler.handleRequest(this.inputStreamCachingService.getOrCacheInputStream(this.clientSocketInputStream), this.clientSocketOutputStream);

            if(requestHandler.hasIntercepted()) break;
        }
    }

    @Override
    public void run() {
        try {
            this.processClientConnection();
            this.clientSocketInputStream.close();
            this.clientSocketOutputStream.close();
            this.clientSocket.close();
            this.inputStreamCachingService.evictCache();
        } catch (IOException e) {
            this.loggingService.error(e.getMessage());
        }
    }
}






