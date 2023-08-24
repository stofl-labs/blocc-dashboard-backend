package uk.ac.ic.doc.blocc.dashboard.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovalTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovedTempReading;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.CompositeKey;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.SensorChaincodeTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.Transaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.repository.ApprovalTransactionRepository;
import uk.ac.ic.doc.blocc.dashboard.transaction.repository.SensorChaincodeTransactionRepository;
import uk.ac.ic.doc.blocc.dashboard.transaction.repository.TransactionRepository;

public class TransactionServiceTest {

  @InjectMocks
  private TransactionService transactionService;

  @Mock
  private SensorChaincodeTransactionRepository sensorChaincodeTransactionRepository;

  @Mock
  private ApprovalTransactionRepository approvalTransactionRepository;
  @Mock
  private TransactionRepository transactionRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getsApprovedTempReadings() {
    int containerNum = 1;
    List<SensorChaincodeTransaction> mockData = Arrays.asList(
        new SensorChaincodeTransaction(
            "tx123",
            1, "creator", 100L,
            new TemperatureHumidityReading(25, 0.3F, 100L)),

        new SensorChaincodeTransaction(
            "tx1299",
            1, "creator", 100L,
            new TemperatureHumidityReading(22, 0.3F, 100L))
    );

    when(sensorChaincodeTransactionRepository.findAllByContainerNum(containerNum)).thenReturn(
        mockData);

    List<ApprovedTempReading> result =
        transactionService.getApprovedTempReadings(containerNum);

    assertEquals(2, result.size());

    // Expected ApprovedTempReadings based on the mock data
    ApprovedTempReading expectedFirstReading = new ApprovedTempReading(100L, 25, 0, "tx123");
    ApprovedTempReading expectedSecondReading = new ApprovedTempReading(100L, 22, 0, "tx1299");

    // Assertions
    assertEquals(expectedFirstReading, result.get(0));
    assertEquals(expectedSecondReading, result.get(1));
  }

  @Test
  public void addsSensorChaincodeTransaction() {
    TemperatureHumidityReading reading = new TemperatureHumidityReading(30, 0.9F, 300L);
    transactionService.addSensorChaincodeTransaction("tx6789", 1, "creator", 300L, reading);

    ArgumentCaptor<SensorChaincodeTransaction> captor = ArgumentCaptor.forClass(
        SensorChaincodeTransaction.class);
    verify(sensorChaincodeTransactionRepository, times(1)).save(captor.capture());

    SensorChaincodeTransaction capturedTransaction = captor.getValue();
    assertEquals("tx6789", capturedTransaction.getTxId());
    assertEquals(1, capturedTransaction.getContainerNum());
    assertEquals(30, capturedTransaction.getReading().getTemperature());
    assertEquals(0.9F, capturedTransaction.getReading().getRelativeHumidity());
    assertEquals(300L, capturedTransaction.getReading().getTimestamp());
  }

  @Test
  public void throwsExceptionWhenAddingExistingSensorChaincodeTransaction() {
    String txId = "tx6789";
    int containerNum = 1;
    TemperatureHumidityReading reading = new TemperatureHumidityReading(30, 0.9F, 300L);

    when(sensorChaincodeTransactionRepository.findById(
        new CompositeKey(txId, containerNum))).thenReturn(
        Optional.of(new SensorChaincodeTransaction(txId, containerNum, "creator", 300L)));

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> transactionService.addSensorChaincodeTransaction(txId, containerNum, "creator", 300L,
            reading)
    );

    assertEquals(String.format("Transaction %s for container %d exists", txId, containerNum),
        exception.getMessage());
  }

  @Test
  public void addsApprovalTransaction() {
    String txId = "12345";
    int containerNum = 1;
    String approvingMspId = "Container5MSP";
    long createdTime = System.currentTimeMillis();
    String approvedTxId = "67890";

    SensorChaincodeTransaction mockTransaction = new SensorChaincodeTransaction(approvedTxId,
        containerNum, "creator", createdTime, new TemperatureHumidityReading());

    when(sensorChaincodeTransactionRepository.findById(
        new CompositeKey(approvedTxId, containerNum))).thenReturn(Optional.of(mockTransaction));

    transactionService.addApprovalTransaction(txId, containerNum, approvingMspId, createdTime,
        approvedTxId);

    ArgumentCaptor<ApprovalTransaction> captor = ArgumentCaptor.forClass(ApprovalTransaction.class);
    verify(approvalTransactionRepository, times(1)).save(captor.capture());

    ApprovalTransaction capturedApproval = captor.getValue();
    assertEquals(txId, capturedApproval.getTxId());
    assertEquals(containerNum, capturedApproval.getContainerNum());
    assertEquals(approvingMspId, capturedApproval.getCreator());
  }

  @Test
  public void throwsExceptionWhenApprovingNonExistentSensorChaincodeTransaction() {
    String txId = "12345";
    int containerNum = 1;
    String approvingMspId = "Container5MSP";
    long createdTime = System.currentTimeMillis();
    String approvedTxId = "67890";

    when(sensorChaincodeTransactionRepository.findById(
        new CompositeKey(approvingMspId, containerNum))).thenReturn(Optional.empty());

    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> transactionService.addApprovalTransaction(txId, containerNum, approvingMspId,
            createdTime, approvedTxId));

    assertEquals(
        String.format("Transaction %s for container %d is not found", approvedTxId, containerNum),
        exception.getMessage());
  }

  @Test
  public void throwsExceptionWhenSameApprovalTransactionApprovesSensorTransactionAgain() {
    String txId = "12345";
    int containerNum = 1;
    String approvingMspId = "Container5MSP";
    long readingCreatedTime = 0L;
    long approvalCreatedTime = 1L;
    String approvedTxId = "67890";

    SensorChaincodeTransaction sensorChaincodeTransaction = new SensorChaincodeTransaction(
        approvedTxId,
        containerNum, "creator", readingCreatedTime, new TemperatureHumidityReading());

    ApprovalTransaction approvalTransaction = new ApprovalTransaction(txId, containerNum,
        approvingMspId, approvalCreatedTime, sensorChaincodeTransaction);

    when(approvalTransactionRepository.findById(new CompositeKey(txId, containerNum))).thenReturn(
        Optional.of(approvalTransaction));

    when(sensorChaincodeTransactionRepository.findById(
        new CompositeKey(approvedTxId, containerNum))).thenReturn(
        Optional.of(sensorChaincodeTransaction));

    // Attempting to approve again with the same ApprovalTransaction should throw an exception
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> transactionService.addApprovalTransaction(txId, containerNum, approvingMspId,
            approvalCreatedTime, approvedTxId));

    assertEquals(
        String.format("%s already exists", approvalTransaction),
        exception.getMessage());
  }

  @Test
  public void getsTransactionsByContainerNum() {
    int containerNum = 1;

    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123",
        1, "creator", 100L,
        new TemperatureHumidityReading(25, 0.3F, 100L));
    SensorChaincodeTransaction tx2 = new SensorChaincodeTransaction(
        "tx1299",
        1, "creator", 100L,
        new TemperatureHumidityReading(22, 0.3F, 100L));
    ApprovalTransaction approval1 = new ApprovalTransaction(
        "tx222",
        1, "approvingMSP", 101L, tx1);
    ApprovalTransaction approval2 = new ApprovalTransaction(
        "tx223",
        1, "approvingMSP", 101L, tx2);

    List<Transaction> mockData = Arrays.asList(
        tx1,
        tx2,
        approval1,
        approval2
    );

    when(transactionRepository.findAllByContainerNum(containerNum)).thenReturn(mockData);

    List<Transaction> result = transactionService.getTransactions(containerNum);

    assertEquals(4, result.size());

    // Assertions
    assertTrue(result.contains(tx1));
    assertTrue(result.contains(tx2));
    assertTrue(result.contains(approval1));
    assertTrue(result.contains(approval2));
  }

  @Test
  public void getsTransactionsReturnsALlExistingTransactions() {

    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123",
        1, "creator", 100L,
        new TemperatureHumidityReading(25, 0.3F, 100L));
    SensorChaincodeTransaction tx2 = new SensorChaincodeTransaction(
        "tx1299",
        2, "creator", 100L,
        new TemperatureHumidityReading(22, 0.3F, 100L));
    SensorChaincodeTransaction tx3 = new SensorChaincodeTransaction(
        "tx123",
        3, "creator", 100L,
        new TemperatureHumidityReading(25, 0.3F, 100L));
    ApprovalTransaction approval1 = new ApprovalTransaction(
        "tx222",
        1, "approvingMSP", 101L, tx1);
    ApprovalTransaction approval2 = new ApprovalTransaction(
        "tx223",
        2, "approvingMSP", 101L, tx2);

    List<Transaction> mockData = Arrays.asList(
        tx1,
        tx2,
        tx3,
        approval1,
        approval2
    );

    when(transactionRepository.findAll()).thenReturn(mockData);

    List<Transaction> result = transactionService.getTransactions();

    assertEquals(5, result.size());

    // Assertions
    assertTrue(result.contains(tx1));
    assertTrue(result.contains(tx2));
    assertTrue(result.contains(tx3));
    assertTrue(result.contains(approval1));
    assertTrue(result.contains(approval2));
  }

  @Test
  public void getsApprovedTempReadingsSinceTimestamp() {
    int containerNum = 1;
    long sinceTimestamp = 101L;

    // Sample data from repository
    List<SensorChaincodeTransaction> mockSensorTransactions = Arrays.asList(
        new SensorChaincodeTransaction("tx124", 1, "creator", 102L,
            new TemperatureHumidityReading(25, 0.3F, 102L)),
        new SensorChaincodeTransaction("tx1300", 1, "creator", 103L,
            new TemperatureHumidityReading(22, 0.3F, 103L))
    );

    // Mocking the repository call
    when(sensorChaincodeTransactionRepository.findAllSinceTimestampByContainerNum(containerNum,
        sinceTimestamp))
        .thenReturn(mockSensorTransactions);

    // Expected data after conversion
    List<ApprovedTempReading> expectedReadings = Arrays.asList(
        new ApprovedTempReading(102L, 25, 0, "tx124"),
        new ApprovedTempReading(103L, 22, 0, "tx1300")
    );

    // Invoking the service method
    List<ApprovedTempReading> result = transactionService.getApprovedTempReadingsSince(containerNum,
        sinceTimestamp);

    // Assertions
    assertEquals(expectedReadings.size(), result.size());
    assertTrue(result.containsAll(expectedReadings));
  }

  @Test
  public void getsSensorChaincodeTransactionsFiltersApprovalsByWindow() {
    // Given
    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123", 1, "Container5MSP", 100L, new TemperatureHumidityReading(25, 0.3F, 105L));
    SensorChaincodeTransaction tx2 = new SensorChaincodeTransaction(
        "tx456", 1, "Container5MSP", 103L, new TemperatureHumidityReading(30, 0.2F, 110L));

    ApprovalTransaction approval1 = new ApprovalTransaction("app1", 1, "Container6MSP", 102L, tx1);
    ApprovalTransaction approval2 = new ApprovalTransaction("app2", 1, "Container7MSP", 108L,
        tx1); // this will be filtered out
    tx1.setApprovals(Arrays.asList(approval1, approval2));

    when(sensorChaincodeTransactionRepository.findAll(anySensorTransactionSpecification()))
        .thenReturn(Arrays.asList(tx1, tx2));

    // When
    List<SensorChaincodeTransaction> transactions = transactionService.getSensorChaincodeTransactions(
        1, 100L, 110L, 3L);

    // Then
    assertEquals(2, transactions.size());
    SensorChaincodeTransaction fetchedTx1 = transactions.get(0);
    assertEquals(1, fetchedTx1.getApprovals().size());
    assertEquals(approval1, fetchedTx1.getApprovals().get(0));
  }

  @Test
  public void getsSensorChaincodeTransactionsFiltersApprovalsWithoutWindow() {
    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123", 1, "Container5MSP", 100L, null
    );

    ApprovalTransaction approval1 = new ApprovalTransaction(
        "app1", 1, "Container6MSP", 101L, tx1
    );

    ApprovalTransaction approval2 = new ApprovalTransaction(
        "app2", 1, "Container7MSP", 105L, tx1
    );

    tx1.setApprovals(Arrays.asList(approval1, approval2));

    when(sensorChaincodeTransactionRepository.findAll(anySensorTransactionSpecification()))
        .thenReturn(List.of(tx1));

    // The actual call
    List<SensorChaincodeTransaction> result = transactionService.getSensorChaincodeTransactions(1,
        null, null, null);

    // Verifying that the tx1 was returned with both its approvals
    assertEquals(1, result.size());
    assertEquals(tx1.getTxId(), result.get(0).getTxId());
    assertEquals(2, result.get(0).getApprovals().size());
  }


  private Specification<SensorChaincodeTransaction> anySensorTransactionSpecification() {
    return any();
  }


}