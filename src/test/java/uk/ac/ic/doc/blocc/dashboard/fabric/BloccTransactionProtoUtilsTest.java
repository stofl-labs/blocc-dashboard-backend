package uk.ac.ic.doc.blocc.dashboard.fabric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hyperledger.fabric.protos.common.Block;
import org.hyperledger.fabric.protos.common.Envelope;
import org.junit.jupiter.api.Test;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

class BloccTransactionProtoUtilsTest {

  // Load the blocks file
  Envelope bsccTx = Envelope.parseFrom(Block.parseFrom(
      Files.readAllBytes(Paths.get("src/test/resources/data/bscc.block"))).getData().getData(0));
  Envelope sensorChaincodeTx = Envelope.parseFrom(Block.parseFrom(
          Files.readAllBytes(Paths.get("src/test/resources/data/sensor_chaincode.block"))).getData()
      .getData(0));

  BloccTransactionProtoUtilsTest() throws IOException {
  }

  @Test
  void extractsTransactionIdFromBsccTransaction() throws Exception {
    String txId = BloccTransactionProtoUtils.extractTransactionId(bsccTx);
    assertEquals("a6fee3f8c3ce8eda06faa828acbc11f1a0f452d905e14b527f30fe1761e5f65f", txId);
  }

  @Test
  void extractsTransactionIdFromSensorChaincodeTransaction() throws Exception {
    String txId = BloccTransactionProtoUtils.extractTransactionId(sensorChaincodeTx);
    assertEquals("da8ed18298efc8ffcfd8eba7c56f868d34aa67482c9d55ae16825081ec9388d0", txId);
  }

  @Test
  void itExtractsApprovedTransactionIdFromBsccTransaction() throws Exception {
    String approvedTxId = BloccTransactionProtoUtils.extractApprovedTransactionId(bsccTx);
    assertEquals("5016290b2ec5484dee743a7b7b25f9a982cdd7bc2d4e5167ae29dddda702d962", approvedTxId);
  }

  @Test
  void itExtractsTemperatureHumidityReadingFromSensorChaincodeBlock() throws Exception {
    TemperatureHumidityReading reading = BloccTransactionProtoUtils.extractTemperatureHumidityReading(
        sensorChaincodeTx);
    assertEquals(new TemperatureHumidityReading(31.825525f, 0.30688095f, 1692469931), reading);
  }

  @Test
  void isBsccReturnsTrueWhenBsccTransactionGiven() throws Exception {
    assertTrue(BloccTransactionProtoUtils.isBscc(bsccTx));
  }

  @Test
  void isBsccReturnsFalseWhenSensorChaincodeTransactionGiven() throws Exception {
    assertFalse(BloccTransactionProtoUtils.isBscc(sensorChaincodeTx));
  }

  @Test
  void isSensorChaincodeReturnsFalseWhenBsccTransactionGiven() throws Exception {
    assertFalse(BloccTransactionProtoUtils.isSensorChaincode(bsccTx));
  }

  @Test
  void isSensorChaincodeReturnsTrueWhenSensorChaincodeTransactionGiven() throws Exception {
    assertTrue(BloccTransactionProtoUtils.isSensorChaincode(sensorChaincodeTx));
  }

  @Test
  void extractsApprovingMspIdFromBsccBlock() throws Exception {
    String mspId = BloccTransactionProtoUtils.extractApprovingMspId(bsccTx);
    assertEquals("Container6MSP", mspId);
  }

  @Test
  void extractCreatorMspId() throws InvalidProtocolBufferException {
    assertEquals("Container6MSP", BloccTransactionProtoUtils.extractCreatorMspId(bsccTx));
    assertEquals("Container5MSP",
        BloccTransactionProtoUtils.extractCreatorMspId(sensorChaincodeTx));
  }

}