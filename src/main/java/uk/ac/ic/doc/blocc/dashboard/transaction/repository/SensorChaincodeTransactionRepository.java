package uk.ac.ic.doc.blocc.dashboard.transaction.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.SensorChaincodeTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.CompositeKey;

@Repository
public interface SensorChaincodeTransactionRepository
    extends JpaRepository<SensorChaincodeTransaction, CompositeKey>,
    JpaSpecificationExecutor<SensorChaincodeTransaction> {

  @Query("SELECT tx FROM SensorChaincodeTransaction tx WHERE tx.key.containerNum = ?1 ORDER BY tx.reading.timestamp")
  List<SensorChaincodeTransaction> findAllByContainerNum(int containerNum);

  @Query("SELECT tx FROM SensorChaincodeTransaction tx WHERE tx.key.containerNum = ?1 AND tx.createdTimestamp >= ?2 ORDER BY tx.reading.timestamp")
  List<SensorChaincodeTransaction> findAllSinceTimestampByContainerNum(int containerNum,
      Long sinceTimestamp);
}
