package uk.ac.ic.doc.blocc.dashboard.forkstatus;

import org.hyperledger.fabric.client.GatewayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ic.doc.blocc.dashboard.forkstatus.model.ForkStatusResponse;

@RestController
@CrossOrigin(origins = "*")
@Profile({"prod", "fork-status-controller-test"})
@RequestMapping(path = "/api/v1/forkStatus")
public class ForkStatusController {

  private final ForkStatusService service;

  @Autowired
  public ForkStatusController(ForkStatusService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<ForkStatusResponse> getForkStatus(@RequestParam int containerNum)
      throws GatewayException {
    return ResponseEntity.ok(new ForkStatusResponse(service.getForkStatus(containerNum)));
  }

}
