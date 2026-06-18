package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.dto.ClasamentDTO;
import org.example.dto.IncercareRequestDTO;
import org.example.dto.JocIncercariDTO;
import org.example.dto.RezultatIncercareDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceProxy implements IService{
    private String host;
    private int port;
    private IObserver client;
    private BufferedReader input;
    private PrintWriter output;
    private Socket connection;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private BlockingQueue<Response> responses = new LinkedBlockingQueue<>();
    private volatile boolean finished;

    public ServiceProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void sendRequest(Request request) throws Exception {
        try {
            String requestJson = gson.toJson(request);
            output.println(requestJson);
            output.flush();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private Response readResponse() throws Exception {
        try{
            return responses.take();
        } catch (InterruptedException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Joc incepeJoc(String alias, IObserver client) {
        try {
            this.client = client;
            initializeConnection();

            sendRequest(new Request(RequestType.INCEPE, alias));
            Response response = readResponse();

            if (response.getType() == ResponseType.OK) {
                System.out.println(response.getData());
                return gson.fromJson(gson.toJson(response.getData()), Joc.class);
            } else {
                this.client = null;
                String eroare = (response.getData() != null) ?  response.getData().toString() : "Eroare la autentificare";
                throw new Exception(eroare);
            }
        } catch (Exception e) {
            closeConnection();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RezultatIncercareDTO faIncercare(Long idJoc, String pereche) {
        try {
            sendRequest(new Request(RequestType.INCERCARE, new IncercareRequestDTO(idJoc, pereche)));
            Response response = readResponse();
            if (response.getType() == ResponseType.OK) {
                return gson.fromJson(gson.toJson(response.getData()), RezultatIncercareDTO.class);
            } else {
                String eroare = (response.getData() != null) ? response.getData().toString() : "Eroare la autentificare!";
                throw new Exception(eroare);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void finalizeazaJoc() {
        try {
            sendRequest(new Request(RequestType.FINALIZEAZA_JOC, null));
            Response response = readResponse();
            if (response.getType() != ResponseType.OK) {
                String eroare = (response.getData() != null) ? response.getData().toString() : "Eroare la autentificare!";
                throw new Exception(eroare);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<JocIncercariDTO> getJocSiIncercari(String alias) {
        try {
            sendRequest(new Request(RequestType.GET_ALL_JOCURI_MUTARI, alias));
            Response response = readResponse();
            if (response.getType() == ResponseType.OK) {
                Type listType = new TypeToken<List<JocIncercariDTO>>(){}.getType();
                return gson.fromJson(gson.toJson(response.getData()), listType);
            } else throw new Exception(response.getData().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Configuratie modificaConfiguratie(Long idConfiguratie, String multime) {
        try {
            Configuratie configuratie = new Configuratie(multime);
            configuratie.setId(idConfiguratie);
            sendRequest(new Request(RequestType.MODIFICA_CONFIGURATIE, configuratie));
            Response response = readResponse();
            if (response.getType() == ResponseType.OK) {
                return gson.fromJson(gson.toJson(response.getData()), Configuratie.class);
            } else {
                String eroare = (response.getData() != null) ? response.getData().toString() : "Eroare la autentificare!";
                throw new Exception(eroare);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void initializeConnection() throws Exception {
        try {
            connection = new Socket(host, port);
            output = new PrintWriter(connection.getOutputStream());
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished = false;
            responses.clear();
            startReader();
        } catch (IOException e) {
            throw new Exception("Conexiune esuata! Verifica daca serverul e pornit!");
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            if(input != null) input.close();
            if(output != null) output.close();
            if (connection != null) connection.close();
            connection = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    String responseLine = input.readLine();
                    if (responseLine == null) {
                        if (!finished)
                        {
                            responses.put(new Response(ResponseType.ERROR, "Conexiunea s a inchis neasteptat."));
                        }
                        finished = true;
                        break;
                    }

                    Response res = gson.fromJson(responseLine, Response.class);

                    if (res.getType() == ResponseType.UPDATE) {
                        Type listType = new TypeToken<List<ClasamentDTO>>(){}.getType();
                        List<ClasamentDTO> list = gson.fromJson(gson.toJson(res.getData()), listType);
                        client.update(list);
                    } else {
                        responses.put(res);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!finished){
                        try {
                            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
                            responses.put(new Response(ResponseType.ERROR, msg));
                        } catch (InterruptedException ex) {}
                    }
                    finished = true;
                }
            }
        }
    }
}
