package uk.ac.ic.doc.blocc.dashboard.approvedtempreading;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model.ApprovedTransaction;

@Repository
public interface ApprovedTransactionRepository extends JpaRepository<ApprovedTransaction, String> {

  @Query("SELECT tx FROM ApprovedTransaction tx WHERE tx.containerNum = ?1")
  List<ApprovedTransaction> findAllByContainerNum(int containerNum);
}
