package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dto.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

public class ClientWorker implements Runnable, IObserver {
    private IService service;
    private Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private volatile boolean connected;
    private static final Logger logger = LogManager.getLogger();

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public ClientWorker(IService service, Socket connection) {
        this.service = service;
        this.connection = connection;

        try {
            output = new PrintWriter(connection.getOutputStream());
            output.flush();
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            connected = true;
        } catch (IOException e) {
            logger.error("Eroare conexiune: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                String requestLine = input.readLine();
                if (requestLine == null) {
                    connected = false;
                    break;
                }
                Request request = gson.fromJson(requestLine, Request.class);
                Response response = handleRequest(request);

                if (response != null)
                    sendResponse(response);
            } catch (Exception e) {
                connected = false;
                logger.error("Eroare worker: " + e.getMessage());
            }
        }

        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Eroare conexiune: " + e.getMessage());
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;

        try {
            switch (request.getType()) {
                case INCEPE -> {
                    String alias = gson.fromJson(gson.toJson(request.getData()), String.class);
                    Joc jocNou = service.incepeJoc(alias, this);
                    System.out.println("Am trecut prin Worker");
                    System.out.println(jocNou.getData());
                    return new Response(ResponseType.OK, jocNou);
                }
                case INCERCARE -> {
                    IncercareRequestDTO mutareRequest = gson.fromJson(gson.toJson(request.getData()), IncercareRequestDTO.class);
                    RezultatIncercareDTO rezultatMutare = service.faIncercare(mutareRequest.getIdJoc(), mutareRequest.getPereche());
                    return new Response(ResponseType.OK, rezultatMutare);
                }
                case FINALIZEAZA_JOC -> {
                    service.finalizeazaJoc();
                    return new Response(ResponseType.OK, "Joc castigat/abandonat cu succes!");
                }
                case GET_ALL_JOCURI_MUTARI -> {
                    String alias = gson.fromJson(gson.toJson(request.getData()), String.class);
                    List<JocIncercariDTO> jocuriSiMutari = service.getJocSiIncercari(alias);
                    return new Response(ResponseType.OK, jocuriSiMutari);
                }
                case MODIFICA_CONFIGURATIE -> {
                    Configuratie configRequest = gson.fromJson(gson.toJson(request.getData()), Configuratie.class);
                    return new Response(ResponseType.OK, configRequest);
                }
            }
        } catch (Exception e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        }
        return response;
    }

    @Override
    public void update(List<ClasamentDTO> clasament) {
        try {
            sendResponse(new Response(ResponseType.UPDATE, clasament));
        } catch (Exception e) {
            logger.error("Eroare response update: " + e.getMessage());
        }
    }

    private synchronized void sendResponse(Response response) {
        String responseLine = gson.toJson(response);
        output.println(responseLine);
        System.out.println("AM trecut prin sendResponse");
        output.flush();
    }
}
