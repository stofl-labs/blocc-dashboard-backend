package uk.ac.ic.doc.blocc.dashboard.forkstatus.model;

public class ForkStatusResponse {

  private final ForkStatus status;

  public ForkStatusResponse(ForkStatus forkStatus) {
    status = forkStatus;
  }

  public ForkStatus getStatus() {
    return status;
  }
}
