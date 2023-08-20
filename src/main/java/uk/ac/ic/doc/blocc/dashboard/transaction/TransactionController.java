package uk.ac.ic.doc.blocc.dashboard.transaction;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovedTempReading;

@RestController
@RequestMapping(path = "/api/v1/transaction")
public class TransactionController {

  private final TransactionService transactionService;

  @Autowired
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @GetMapping("/approvedTempReadings")
  public List<ApprovedTempReading> getApprovedTransactions(@RequestParam int containerNum) {
    return transactionService.getApprovedTempReadings(containerNum);
  }

}
