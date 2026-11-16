package Sprint1.demo.repository;

import Sprint1.demo.model.ApplicationSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ApplicationSummaryRepository extends JpaRepository<ApplicationSummary, Long> {

    @Query("""
            SELECT a FROM ApplicationSummary a
            WHERE a.email = :email
              AND (:status is null OR a.status = :status)
              AND (:state is null OR a.state = :state)
              AND (:keyword = '' 
                   OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(a.company) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:from is null OR a.appliedDate >= :from)
              AND (:to   is null OR a.appliedDate <= :to)
            ORDER BY a.state DESC
            """)
    List<ApplicationSummary> search(@Param("email") String email,
                                    @Param("status") String keyword,
                                    @Param("keyword") String status,
                                    @Param("from") LocalDate from,
                                    @Param("to") LocalDate to,
                                    @Param("state") String state);


    @Query("select a.status, a.state, count(a) from ApplicationSummary a " +
            "where a.email = :email group by a.status, a.state")
    List<Object[]> countByStatusAndState(@Param("email") String email);

    @Query("SELECT a FROM ApplicationSummary a WHERE a.id_application = :idApplication")
    Optional<ApplicationSummary> findByApplicationId(@Param("idApplication") Long idApplication);
}
