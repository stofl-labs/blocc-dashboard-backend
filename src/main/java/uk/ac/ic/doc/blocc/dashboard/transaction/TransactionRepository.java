package uk.ac.ic.doc.blocc.dashboard.transaction;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovalTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.CompositeKey;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.Transaction;

@Repository
public interface TransactionRepository
    extends JpaRepository<ApprovalTransaction, CompositeKey> {

  @Query("SELECT tx FROM Transaction tx WHERE tx.key.containerNum = ?1")
  List<Transaction> findAllByContainerNum(int containerNum);
}
