package uk.ac.ic.doc.blocc.dashboard.transaction;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovedTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.CompositeKey;

@Repository
public interface TransactionRepository
    extends JpaRepository<ApprovedTransaction, CompositeKey> {

  @Query("SELECT tx FROM ApprovedTransaction tx WHERE tx.key.containerNum = ?1")
  List<ApprovedTransaction> findAllByContainerNum(int containerNum);
}
