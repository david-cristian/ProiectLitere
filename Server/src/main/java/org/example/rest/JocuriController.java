package org.example.rest;

import org.example.Configuratie;
import org.example.IService;
import org.example.dto.JocIncercariDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class JocuriController {
    private IService service;

    public JocuriController(IService service) {
        this.service = service;
    }

    @GetMapping("/jocuri")
    public ResponseEntity<?> getJocuri(@RequestParam(value = "alias") String alias) {
        try {
            List<JocIncercariDTO> jocuri = service.getJocSiIncercari(alias);
            return new ResponseEntity<>(jocuri, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/incercari")
    public ResponseEntity<?> modificaIncercare(@RequestBody Configuratie configuratie) {
        try {
            Configuratie configuratieUpdated = service.modificaConfiguratie(configuratie.getId(), configuratie.getMultimi());
            return new ResponseEntity<>("Incercare adaugata cu cuvintele: " + configuratieUpdated.getMultimi(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
