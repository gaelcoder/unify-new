package com.projeto.unify.repositories;

import com.projeto.unify.models.Funcionario;
import com.projeto.unify.models.Usuario;
import com.projeto.unify.models.Universidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    Optional<Funcionario> findByEmail(String email);
    Optional<Funcionario> findByCpf(String cpf);
    Optional<Funcionario> findByUsuario(Usuario usuario);
    Optional<Funcionario> findByUsuarioId(Long usuarioId);
    List<Funcionario> findByUniversidadeId(Long universidadeId);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.universidade LEFT JOIN FETCH f.usuario u LEFT JOIN FETCH u.universidade WHERE f.universidade.id = :universidadeId")
    List<Funcionario> findByUniversidadeIdWithDetails(@Param("universidadeId") Long universidadeId);
    
    long countByUniversidadeId(Long universidadeId);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.usuario u LEFT JOIN FETCH u.universidade WHERE f.id = :id AND f.universidade = :universidade")
    Optional<Funcionario> findByIdAndUniversidadeWithDetails(@Param("id") Long id, @Param("universidade") Universidade universidade);

    // Methods for conflict checking during updates
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.usuario u LEFT JOIN FETCH u.universidade")
    List<Funcionario> findAllWithDetails();
}
