package uk.ac.ic.doc.blocc.dashboard.approvedtransaction;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTempReading;

@RestController
@RequestMapping(path = "/api/v1/approvedTempReading")
public class ApprovedTransactionController {

  private final ApprovedTransactionService approvedTransactionService;

  @Autowired
  public ApprovedTransactionController(ApprovedTransactionService approvedTransactionService) {
    this.approvedTransactionService = approvedTransactionService;
  }

  @GetMapping("/all")
  public List<ApprovedTempReading> getApprovedTransactions(@RequestParam int containerNum) {
    return approvedTransactionService.getApprovedTempReadings(containerNum);
  }

}
