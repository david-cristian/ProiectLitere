package org.example;

import org.example.dto.ClasamentDTO;

import java.util.List;

public interface IObserver {
    void update(List<ClasamentDTO> clasament);
}
