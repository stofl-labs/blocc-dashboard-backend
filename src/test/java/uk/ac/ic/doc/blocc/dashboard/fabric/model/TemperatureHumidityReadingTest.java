package uk.ac.ic.doc.blocc.dashboard.fabric.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TemperatureHumidityReadingTest {

  @Test
  public void initialisesWithConstructor() {
    TemperatureHumidityReading reading = new TemperatureHumidityReading(
        25.5f, 65.5f, 1629200000L);

    assertEquals(25.5f, reading.getTemperature());
    assertEquals(65.5f, reading.getRelativeHumidity());
    assertEquals(1629200000L, reading.getTimestamp());
  }

  @Test
  public void setsAndGetsTemperature() {
    TemperatureHumidityReading reading = new TemperatureHumidityReading();
    reading.setTemperature(26.5f);

    assertEquals(26.5f, reading.getTemperature());
  }

  @Test
  public void setsAndGetsRelativeHumidity() {
    TemperatureHumidityReading reading = new TemperatureHumidityReading();
    reading.setRelativeHumidity(66.5f);

    assertEquals(66.5f, reading.getRelativeHumidity());
  }

  @Test
  public void setsAndGetsTimestamp() {
    TemperatureHumidityReading reading = new TemperatureHumidityReading();
    reading.setTimestamp(1629300000L);

    assertEquals(1629300000L, reading.getTimestamp());
  }

  @Test
  public void convertsToString() {
    TemperatureHumidityReading reading = new TemperatureHumidityReading(
        25.5f, 65.5f, 0L);

    String expected = "Temperature=25.500000, Relative Humidity=65.500000, Time=1970-01-01T00:00:00Z";
    assertEquals(expected, reading.toString());
  }

  @Test
  public void checksEqualityBasedOnFields() {
    TemperatureHumidityReading reading1 = new TemperatureHumidityReading(
        25.5f, 65.5f, 1629200000L);
    TemperatureHumidityReading reading2 = new TemperatureHumidityReading(
        25.5f, 65.5f, 1629200000L);
    TemperatureHumidityReading reading3 = new TemperatureHumidityReading(
        26.5f, 66.5f, 1629300000L);

    assertEquals(reading1, reading2);
    assertNotEquals(reading1, reading3);
  }

  @Test
  public void computesHashCodeBasedOnFields() {
    TemperatureHumidityReading reading1 = new TemperatureHumidityReading(
        25.5f, 65.5f, 1629200000L);
    TemperatureHumidityReading reading2 = new TemperatureHumidityReading(
        25.5f, 65.5f, 1629200000L);

    assertEquals(reading1.hashCode(), reading2.hashCode());
  }
}
