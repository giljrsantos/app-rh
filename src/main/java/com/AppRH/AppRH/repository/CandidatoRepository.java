package com.AppRH.AppRH.repository;

import com.AppRH.AppRH.models.Candidatos;
import com.AppRH.AppRH.models.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CandidatoRepository extends CrudRepository<Candidatos, String> {
    Iterable<Candidatos> findByVaga(Vaga vaga);

    Candidatos findByRg(String rg);

    Candidatos findById(Long id);

    List<Candidatos> findByNome(String nomeCandidato);
}
