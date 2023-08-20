package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeKeyTest {

  private CompositeKey compositeKey;

  @BeforeEach
  public void setUp() {
    compositeKey = new CompositeKey("sampleTxId", 123);
  }

  @Test
  public void setsAndGetsTxIdCorrectly() {
    compositeKey.setTxId("newTxId");
    assertEquals("newTxId", compositeKey.getTxId());
  }

  @Test
  public void setsAndGetsContainerNumCorrectly() {
    compositeKey.setContainerNum(456);
    assertEquals(456, compositeKey.getContainerNum());
  }

  @Test
  public void equalsReturnsTrueForSameObject() {
    assertEquals(compositeKey, compositeKey);
  }

  @Test
  public void equalsReturnsFalseForNullObject() {
    assertNotEquals(null, compositeKey);
  }

  @Test
  public void equalsReturnsTrueForSameValues() {
    CompositeKey anotherCompositeKey = new CompositeKey("sampleTxId", 123);
    assertEquals(compositeKey, anotherCompositeKey);
  }

  @Test
  public void equalsReturnsFalseForDifferentTxId() {
    CompositeKey anotherCompositeKey = new CompositeKey("differentTxId", 123);
    assertNotEquals(compositeKey, anotherCompositeKey);
  }

  @Test
  public void equalsReturnsFalseForDifferentContainerNum() {
    CompositeKey anotherCompositeKey = new CompositeKey("sampleTxId", 789);
    assertNotEquals(compositeKey, anotherCompositeKey);
  }

  @Test
  public void hashCodeIsConsistent() {
    int initialHashCode = compositeKey.hashCode();
    compositeKey.setTxId("newTxId");
    compositeKey.setContainerNum(456);
    int newHashCode = compositeKey.hashCode();

    assertNotEquals(initialHashCode, newHashCode);
  }
}