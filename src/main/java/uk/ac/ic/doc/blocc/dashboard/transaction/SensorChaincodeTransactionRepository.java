package uk.ac.ic.doc.blocc.dashboard.transaction;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.SensorChaincodeTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.CompositeKey;

@Repository
public interface SensorChaincodeTransactionRepository
    extends JpaRepository<SensorChaincodeTransaction, CompositeKey> {

  @Query("SELECT tx FROM SensorChaincodeTransaction tx WHERE tx.key.containerNum = ?1 ORDER BY tx.reading.timestamp")
  List<SensorChaincodeTransaction> findAllByContainerNum(int containerNum);
}
