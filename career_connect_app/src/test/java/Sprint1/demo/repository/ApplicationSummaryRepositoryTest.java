package Sprint1.demo.repository;
import Sprint1.demo.repository.ApplicationSummaryRepository;
import Sprint1.demo.model.ApplicationSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ApplicationSummaryRepositoryTest {

    @Autowired
    private ApplicationSummaryRepository repository;

    // Helper: builds a valid row. Adapt the setters to your entity.
    private ApplicationSummary summary(String email, String status, String state) {
        ApplicationSummary s = new ApplicationSummary();
        s.setEmail(email);
        s.setStatus(status);
        s.setState(state);
        s.setTitle("Developer");
        s.setCompany("Acme");
        s.setAppliedDate(LocalDate.of(2026, 11, 1));
        return s;
    }

    @Test
    void countByStatusAndState_groupsApplicationsOfOneUser() {
        // Given
        repository.save(summary("a@test.com", "Pending", "Active"));
        repository.save(summary("a@test.com", "Pending", "Active"));
        repository.save(summary("a@test.com", "Accepted", "Active"));
        repository.save(summary("a@test.com", "Rejected", "Closed"));
        repository.save(summary("other@test.com", "Pending", "Active")); // must be ignored

        // When
        List<Object[]> rows = repository.countByStatusAndState("a@test.com");

        // Then
        List<String> asText = rows.stream()
                .map(r -> r[0] + "|" + r[1] + "|" + r[2])
                .toList();

        assertThat(asText).containsExactlyInAnyOrder(
                "Pending|Active|2",
                "Accepted|Active|1",
                "Rejected|Closed|1"
        );
    }

    @Test
    void search_filtersByEmailAndState() {
        // Given
        repository.save(summary("a@test.com", "Pending", "Active"));
        repository.save(summary("a@test.com", "Rejected", "Closed"));
        repository.save(summary("other@test.com", "Pending", "Active"));

        // When
        List<ApplicationSummary> result =
                repository.search("a@test.com", null, "", null, null, "Active");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("Pending");
    }

}
