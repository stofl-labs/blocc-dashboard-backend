package uk.ac.ic.doc.blocc.dashboard.approvedtempreading;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model.ApprovedTempReading;

@RestController
@RequestMapping(path = "/api/v1/approvedTempReading")
public class ApprovedTempReadingController {

  private final ApprovedTempReadingService approvedTempReadingService;

  @Autowired
  public ApprovedTempReadingController(ApprovedTempReadingService approvedTempReadingService) {
    this.approvedTempReadingService = approvedTempReadingService;
  }

  @GetMapping("/all")
  public List<ApprovedTempReading> getApprovedTransactions(@RequestParam int containerNum) {
    return approvedTempReadingService.getApprovedTransactions(containerNum);
  }

}
