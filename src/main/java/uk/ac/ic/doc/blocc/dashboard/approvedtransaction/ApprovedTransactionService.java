package uk.ac.ic.doc.blocc.dashboard.approvedtransaction;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTempReading;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTransaction;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.CompositeKey;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

@Service
public class ApprovedTransactionService {

  private final ApprovedTransactionRepository repository;

  private static final Logger logger =
      LoggerFactory.getLogger(ApprovedTransactionService.class);

  @Autowired
  public ApprovedTransactionService(ApprovedTransactionRepository repository) {
    this.repository = repository;
  }

  public List<ApprovedTempReading> getApprovedTempReadings(int containerNum) {
    List<ApprovedTransaction> allApprovedTransactions =
        repository.findAllByContainerNum(containerNum);

    return allApprovedTransactions.stream().map(
        tx -> new ApprovedTempReading(tx.getReading().getTimestamp(),
            tx.getReading().getTemperature(),
            tx.getApprovals(), tx.getTxId())).toList();

  }

  public void addTempReading(String txId, int containerNum, float temperature,
                             float relativeHumidity,
                             long timestamp) {
    if (repository.findById(new CompositeKey(txId, containerNum)).isPresent()) {
      throw new IllegalArgumentException(String.format("Transaction %s exists", txId));
    }

    repository.save(new ApprovedTransaction(txId, containerNum,
        new TemperatureHumidityReading(temperature, relativeHumidity, timestamp)));
  }

  public void addTempReading(String txId, int containerNum, TemperatureHumidityReading reading) {
    if (repository.findById(new CompositeKey(txId, containerNum)).isPresent()) {
      throw new IllegalArgumentException(
          String.format("Transaction %s for container %d exists", txId, containerNum));
    }

    repository.save(new ApprovedTransaction(txId, containerNum, reading));
  }

  @Transactional
  public void approveTransaction(String txId, int containerNum, String approvingMspId) {
    logger.info("{} is approving Transaction {} for container {}", approvingMspId, txId,
        containerNum);
    Optional<ApprovedTransaction> possibleTx =
        repository.findById(new CompositeKey(txId, containerNum));
    if (possibleTx.isEmpty()) {
      String msg =
          String.format("Transaction %s for container %d is not found", txId, containerNum);
      logger.error(msg);
      throw new IllegalArgumentException(msg);
    }
    
    ApprovedTransaction approvedTransaction = possibleTx.get();
    approvedTransaction.approve(approvingMspId);

    repository.save(approvedTransaction);
  }
}
