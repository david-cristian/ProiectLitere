package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class StartServer {
    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        IRepositoryJucator jucatorRepo = new RepositoryJucator(sessionFactory);
        IRepositoryJoc jocRepo = new RepositoryJoc(sessionFactory);
        IRepositoryConfiguratie configRepo = new RepositoryConfiguratie(sessionFactory);
        IRepositoryIncercare mutareRepo = new RepositoryIncercare(sessionFactory);

        IService serverService = new Service(jucatorRepo, jocRepo, configRepo, mutareRepo);

        int port = 5555;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server pornit pe portul " + port);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client conectat: " + client.getInetAddress());

                ClientWorker worker = new ClientWorker(serverService, client);
                new Thread(worker).start();
            }
        } catch (IOException e) {
            System.out.println("Eroare server: " + e.getMessage());
        }
    }
}