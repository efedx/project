package com.terminal.repositories;

import com.terminal.entities.Terminal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {

    @Query("SELECT t FROM Department d JOIN d.terminalList t WHERE t.isActive = true AND t.terminalName = :terminalName")
    List<Terminal> findByActiveTerminalsAndTerminalNameList(@Param("terminalName") String terminalName);

    @Query("SELECT t FROM Department d JOIN d.terminalList t WHERE t.isActive = true")
    List<Terminal> findAllByActiveTerminalsList(Pageable pageable);
}
