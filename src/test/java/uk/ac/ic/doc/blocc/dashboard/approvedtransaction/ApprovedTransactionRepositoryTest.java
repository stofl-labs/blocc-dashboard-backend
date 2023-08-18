package uk.ac.ic.doc.blocc.dashboard.approvedtransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTransaction;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApprovedTransactionRepositoryTest {

  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:latest");

  static {
    postgres.start();
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ApprovedTransactionRepository repository;

  @Test
  public void testFindAllByContainerNum() {
    // Given
    ApprovedTransaction tx1 = new ApprovedTransaction(
        "tx123",
        1, List.of("Container5MSP", "Container6MSP"),
        new TemperatureHumidityReading(25, 0.3F, 100L));

    ApprovedTransaction tx2 = new ApprovedTransaction(
        "tx456",
        3, List.of("Container5MSP", "Container6MSP", "Container7MSP"),
        new TemperatureHumidityReading(30, 0.2F, 103L));

    ApprovedTransaction tx3 = new ApprovedTransaction(
        "tx1299",
        1, List.of("Container5MSP"),
        new TemperatureHumidityReading(22, 0.3F, 100L));
    entityManager.persist(tx1);
    entityManager.persist(tx2);
    entityManager.persist(tx3);
    entityManager.flush();

    // When
    List<ApprovedTransaction> found = repository.findAllByContainerNum(1);

    // Then
    assertThat(found).hasSize(2).contains(tx1, tx3);
    assertThat(found).doesNotContain(tx2);
  }
}
