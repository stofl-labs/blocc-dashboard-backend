package uk.ac.ic.doc.blocc.dashboard.transaction;

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
import uk.ac.ic.doc.blocc.dashboard.transaction.model.Transaction;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {

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
  private TransactionRepository repository;

  @Test
  void findAllByContainerNum() {
    // Given
    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123",
        1,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(25, 0.3F, 100L));

    SensorChaincodeTransaction tx2 = new SensorChaincodeTransaction(
        "tx456",
        3,
        "Container5MSP",
        103L,
        new TemperatureHumidityReading(30, 0.2F, 103L));

    SensorChaincodeTransaction tx3 = new SensorChaincodeTransaction(
        "tx1299",
        1,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(22, 0.3F, 100L));

    ApprovalTransaction approval1 = new ApprovalTransaction(
        "app1", 1, "Container6MSP", 101L, tx1);
    ApprovalTransaction approval2 = new ApprovalTransaction(
        "app2", 1, "Container7MSP", 102L, tx1);
    ApprovalTransaction approval3 = new ApprovalTransaction(
        "app3", 3, "Container9MSP", 102L, tx2);
    ApprovalTransaction approval4 = new ApprovalTransaction(
        "app4", 1, "Container7MSP", 102L, tx3);

    entityManager.persist(tx1);
    entityManager.persist(tx2);
    entityManager.persist(tx3);
    entityManager.persist(approval1);
    entityManager.persist(approval2);
    entityManager.persist(approval3);
    entityManager.persist(approval4);
    entityManager.flush();

    // When
    List<Transaction> found = repository.findAllByContainerNum(1);

    // Then
    assertThat(found).hasSize(5).contains(tx1, tx3, approval1, approval2, approval4);
    assertThat(found).doesNotContain(tx2, approval3);
  }
}