package Sprint1.demo.repository;

import Sprint1.demo.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository
        extends JpaRepository<Company, Long> {
}