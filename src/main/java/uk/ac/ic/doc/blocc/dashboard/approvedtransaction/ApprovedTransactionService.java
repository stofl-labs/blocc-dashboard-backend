package uk.ac.ic.doc.blocc.dashboard.approvedtransaction;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTempReading;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTransaction;

@Service
public class ApprovedTransactionService {

  private final ApprovedTransactionRepository repository;

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
}
