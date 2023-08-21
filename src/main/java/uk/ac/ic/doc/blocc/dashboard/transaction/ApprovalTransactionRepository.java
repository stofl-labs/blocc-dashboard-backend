package uk.ac.ic.doc.blocc.dashboard.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovalTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.CompositeKey;

@Repository
public interface ApprovalTransactionRepository
    extends JpaRepository<ApprovalTransaction, CompositeKey> {

}
