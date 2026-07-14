package com.AppRH.AppRH.repository;

import com.AppRH.AppRH.models.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface VagaRepository extends CrudRepository<Vaga, Long> {

    Vaga findByCodigo(Long codigo);
    //List<Vaga> findByNome(String nome);

}
