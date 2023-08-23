package uk.ac.ic.doc.blocc.dashboard.transaction.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovalTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.SensorChaincodeTransaction;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SensorChaincodeTransactionRepositoryTest {

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
  private SensorChaincodeTransactionRepository repository;

  @Test
  public void findAllByContainerNumReturnsInOrderOfReadingTimestamp() {
    // Given
    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123",
        1,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(25, 0.3F, 105L));

    SensorChaincodeTransaction tx2 = new SensorChaincodeTransaction(
        "tx456",
        3,
        "Container5MSP",
        103L,
        new TemperatureHumidityReading(30, 0.2F, 110L));

    SensorChaincodeTransaction tx3 = new SensorChaincodeTransaction(
        "tx1299",
        1,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(22, 0.3F, 100L));

    SensorChaincodeTransaction tx4 = new SensorChaincodeTransaction(
        "tx789",
        1,
        "Container5MSP",
        101L,
        new TemperatureHumidityReading(28, 0.25F, 102L));

    SensorChaincodeTransaction tx5 = new SensorChaincodeTransaction(
        "tx900",
        1,
        "Container5MSP",
        102L,
        new TemperatureHumidityReading(29, 0.26F, 108L));

    ApprovalTransaction approval1 = new ApprovalTransaction(
        "app1", 1, "Container6MSP", 101L, tx1);
    ApprovalTransaction approval2 = new ApprovalTransaction(
        "app2", 1, "Container7MSP", 102L, tx1);

    entityManager.persist(tx1);
    entityManager.persist(tx2);
    entityManager.persist(tx3);
    entityManager.persist(tx4);
    entityManager.persist(tx5);
    entityManager.persist(approval1);
    entityManager.persist(approval2);
    entityManager.flush();

    // When
    List<SensorChaincodeTransaction> found = repository.findAllByContainerNum(1);

    // Then
    assertThat(found).hasSize(4).containsExactly(tx3, tx4, tx1, tx5);
    assertThat(found).doesNotContain(tx2);
  }

}
