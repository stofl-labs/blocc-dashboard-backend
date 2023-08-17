package uk.ac.ic.doc.blocc.dashboard.approvedtempreading;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model.ApprovedTempReading;
import uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model.ApprovedTransaction;

@Service
public class ApprovedTempReadingService {

  private final ApprovedTransactionRepository repository;

  @Autowired
  public ApprovedTempReadingService(ApprovedTransactionRepository repository) {
    this.repository = repository;
  }

  public List<ApprovedTempReading> getApprovedTransactions(int containerNum) {
    List<ApprovedTransaction> allApprovedTransactions =
        repository.findAllByContainerNum(containerNum);

    return allApprovedTransactions.stream().map(
        tx -> new ApprovedTempReading(tx.getReading().getTimestamp(),
            tx.getReading().getTemperature(),
            tx.getApprovals(), tx.getTxId())).toList();

  }
}
