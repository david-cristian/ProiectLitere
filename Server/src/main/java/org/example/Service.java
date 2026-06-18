package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dto.ClasamentDTO;
import org.example.dto.JocIncercariDTO;
import org.example.dto.RezultatIncercareDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements IService {
    private static final Logger logger=  LogManager.getLogger();
    private Map<String, IObserver> loggedPlayers = new ConcurrentHashMap<>();
    private final IRepositoryJucator repositoryJucator;
    private final IRepositoryJoc repositoryJoc;
    private final IRepositoryConfiguratie repositoryConfiguratie;
    private final IRepositoryIncercare repositoryIncercare;

    public Service(IRepositoryJucator repositoryJucator, IRepositoryJoc repositoryJoc, IRepositoryConfiguratie repositoryConfiguratie, IRepositoryIncercare repositoryIncercare) {
        this.repositoryJucator = repositoryJucator;
        this.repositoryJoc = repositoryJoc;
        this.repositoryConfiguratie = repositoryConfiguratie;
        this.repositoryIncercare = repositoryIncercare;
    }

    private void notifyPlayers() {
        List<Joc> jocuri = repositoryJoc.findAll();
        List<ClasamentDTO> clasament =  new ArrayList<>();
        for (Joc joc : jocuri) {
            clasament.add(new ClasamentDTO(joc.getJucator().getAlias(), joc.getPuncteJucator(), joc.getData()));
        }

        for (IObserver observer : loggedPlayers.values()) {
            try {
                observer.update(clasament);
            } catch (Exception e) {
                logger.error("Eroare observer:" + e.getMessage());
            }
        }
    }

    @Override
    public Joc incepeJoc(String alias, IObserver client) {
        Jucator jucator = repositoryJucator.findByAlias(alias).orElseThrow(() -> {
                logger.warn("Failed to find player");
                return new ServiceException("Nu am gasit jucatorul cu alias " + alias);
        });
        loggedPlayers.put(jucator.getAlias(), client);
        logger.info("User {} logged", jucator.getAlias());

        Configuratie configuratie = repositoryConfiguratie.creazaConfiguratie();

        Joc joc = new Joc(jucator, configuratie, 0, 0, LocalDateTime.now());
        repositoryJoc.save(joc);

        logger.info("Joc salvat in bd cu id {}", joc.getId());
        notifyPlayers();
        return joc;
    }

    @Override
    public RezultatIncercareDTO faIncercare(Long idJoc, String pereche) {
        Joc joc = repositoryJoc.findById(idJoc).orElseThrow(() -> new ServiceException("Nu am gasit jocul."));
        Incercare incercare = new Incercare(joc, "jucator", pereche);
        repositoryIncercare.save(incercare);

        joc.setNrIncercare(joc.getNrIncercare() + 1);

        String multimi = joc.getConfiguratie().getMultimi();
        String[] multimiParted = multimi.split(";");

        List<Incercare> incercariCurente = repositoryIncercare.getAllIncercariByJoc(joc.getId());
        List<String> dejaAlese = incercariCurente.stream()
                .filter(i -> i.getCine().equals("server")) // Asigură-te că getter-ul entității tale este getCine()
                .map(Incercare::getIncercare)
                .toList();

        List<String> valabile = new ArrayList<>(Arrays.asList(multimiParted));
        valabile.removeAll(dejaAlese);
        Random random = new Random();
        String multimeAleasaDeServer = valabile.get(random.nextInt(valabile.size()));

        repositoryIncercare.save(new Incercare(joc, "server", multimeAleasaDeServer));

        Integer numarJucator = Integer.parseInt(pereche.split(",")[1]);
        Integer numarServer = Integer.parseInt(multimeAleasaDeServer.split(",")[1]);

        if (numarJucator > numarServer) {
            joc.setPuncteJucator(joc.getPuncteJucator() + numarJucator + numarServer);
        } else if (numarJucator < numarServer) {
            joc.setPuncteJucator(joc.getPuncteJucator() - numarJucator);
        }
        repositoryJoc.update(joc);

        if (joc.getNrIncercare() == 4) {
            return new RezultatIncercareDTO(Status.Finalizat, multimeAleasaDeServer);
        }

        return new RezultatIncercareDTO(Status.Nefinalizat, multimeAleasaDeServer);
    }

    public void finalizeazaJoc() {
        try {
            notifyPlayers();
        } catch (Exception e) {
            logger.error("Eroare observer:" + e.getMessage());
        }
    }

    @Override
    public List<JocIncercariDTO> getJocSiIncercari(String alias) {
        List<Joc> jocuri = repositoryJoc.findByAlias(alias);
        List<JocIncercariDTO> jocincercari = new ArrayList<>();

        for (Joc joc : jocuri) {
            List<Incercare> incercari = repositoryIncercare.getAllIncercariByJoc(joc.getId());

            List<String> litereJucator = incercari.stream()
                    .filter(i -> i.getCine().equals("jucator"))
                    .map(i -> i.getIncercare().split(",")[0])
                    .toList();

            List<String> litereServer = incercari.stream()
                    .filter(i -> i.getCine().equals("server"))
                    .map(i -> i.getIncercare().split(",")[0])
                    .toList();

            if (!Collections.disjoint(litereJucator, litereServer)) {
                jocincercari.add(new JocIncercariDTO(alias, joc, incercari));
            }
        }
        return jocincercari;
    }

    @Override
    public Configuratie modificaConfiguratie(Long idConfiguratie, String perechiNoi) {
        Configuratie configuratie = new Configuratie(perechiNoi);
        configuratie.setId(idConfiguratie);
        return repositoryConfiguratie.update(configuratie);
    }

}
