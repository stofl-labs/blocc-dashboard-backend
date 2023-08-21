package uk.ac.ic.doc.blocc.dashboard.transaction;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovalTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovedTempReading;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.SensorChaincodeTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.CompositeKey;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

@Service
public class TransactionService {

  private final SensorChaincodeTransactionRepository sensorChaincodeTransactionRepository;
  private final ApprovalTransactionRepository approvalTransactionRepository;

  private static final Logger logger =
      LoggerFactory.getLogger(TransactionService.class);

  @Autowired
  public TransactionService(
      SensorChaincodeTransactionRepository sensorChaincodeTransactionRepository,
      ApprovalTransactionRepository approvalTransactionRepository) {
    this.sensorChaincodeTransactionRepository = sensorChaincodeTransactionRepository;
    this.approvalTransactionRepository = approvalTransactionRepository;
  }

  public List<ApprovedTempReading> getApprovedTempReadings(int containerNum) {
    List<SensorChaincodeTransaction> allSensorChaincodeTransactions =
        sensorChaincodeTransactionRepository.findAllByContainerNum(containerNum);

    return allSensorChaincodeTransactions.stream().map(
        tx -> new ApprovedTempReading(tx.getReading().getTimestamp(),
            tx.getReading().getTemperature(),
            tx.getApprovalCount(),
            tx.getTxId())).toList();

  }

  public void addSensorChaincodeTransaction(String txId, int containerNum, String creator,
      long createdTimestamp,
      TemperatureHumidityReading reading) {
    if (sensorChaincodeTransactionRepository.findById(new CompositeKey(txId, containerNum))
        .isPresent()) {
      throw new IllegalArgumentException(
          String.format("Transaction %s for container %d exists", txId, containerNum));
    }

    sensorChaincodeTransactionRepository.save(
        new SensorChaincodeTransaction(txId, containerNum, creator, createdTimestamp, reading));
  }

  @Transactional
  public void addApprovalTransaction(String txId, int containerNum, String approvingMspId,
      long createdTime, String approvedTxId) {
    logger.info("Transaction (txId={}, creator={}) is approving Transaction {} for container {}",
        txId, approvingMspId, approvedTxId, containerNum);

    Optional<ApprovalTransaction> possibleApprovalTx = approvalTransactionRepository.findById(
        new CompositeKey(txId, containerNum));

    if (possibleApprovalTx.isPresent()) {
      String msg =
          String.format("%s already exists", possibleApprovalTx.get());
      logger.error(msg);
      throw new IllegalArgumentException(msg);
    }

    Optional<SensorChaincodeTransaction> possibleSensorChaincodeTx =
        sensorChaincodeTransactionRepository.findById(
            new CompositeKey(approvedTxId, containerNum));

    if (possibleSensorChaincodeTx.isEmpty()) {
      String msg =
          String.format("Transaction %s for container %d is not found", approvedTxId,
              containerNum);
      logger.error(msg);
      throw new IllegalArgumentException(msg);
    }

    SensorChaincodeTransaction sensorChaincodeTransaction = possibleSensorChaincodeTx.get();
    ApprovalTransaction approvalTransaction = new ApprovalTransaction(txId, containerNum,
        approvingMspId, createdTime, sensorChaincodeTransaction);

    approvalTransactionRepository.save(approvalTransaction);
  }
}
