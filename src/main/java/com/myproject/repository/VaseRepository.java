package com.myproject.repository;

import com.myproject.dto.response.AreaResponse;
import com.myproject.dto.response.VaseResponse;
import com.myproject.model.UserEntity;
import com.myproject.model.Vase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VaseRepository extends JpaRepository<Vase, Integer> {

    @Query(value = "select v from Vase v join v.area a join a.garden g " +
            "where lower(v.vaseName) like :keyword and g.user = :user")
    Page<Vase> searchByKeyword(String keyword, Pageable pageable, UserEntity user);

    Vase findByVaseName(String vaseName);

    @Query(value = "select v from Vase v join v.area a join a.garden g " +
            "where g.user = :user")
    Page<Vase> searchByUser(Pageable pageable, UserEntity user);

    @Query(value = "select v from Vase v join v.area a join a.garden g " +
            "where v.id = :id and g.user = :user")
    Optional<Vase> findByUser(Integer id, UserEntity user);

    @Query("""
    select v
    from Vase v
    join v.area a
    where a.id = :areaId
""")
    List<Vase> findByAreaId(Integer areaId);

}
