package uk.ac.ic.doc.blocc.dashboard.fabric.model;

import jakarta.persistence.Embeddable;
import java.time.Instant;

@Embeddable
public class TemperatureHumidityReading {

  private float temperature;
  private float relativeHumidity;
  private long timestamp;

  public TemperatureHumidityReading() {

  }

  public TemperatureHumidityReading(float temperature, float relativeHumidity, long timestamp) {
    this.temperature = temperature;
    this.relativeHumidity = relativeHumidity;
    this.timestamp = timestamp;
  }

  public void setTemperature(float temperature) {
    this.temperature = temperature;
  }

  public void setRelativeHumidity(float relativeHumidity) {
    this.relativeHumidity = relativeHumidity;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public float getRelativeHumidity() {
    return relativeHumidity;
  }

  public float getTemperature() {
    return temperature;
  }

  @Override
  public String toString() {
    return String.format("Temperature=%f, Relative Humidity=%f, Time=%s", temperature,
        relativeHumidity,
        Instant.ofEpochSecond(timestamp).toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TemperatureHumidityReading that = (TemperatureHumidityReading) o;

    if (Float.compare(that.temperature, temperature) != 0) {
      return false;
    }
    if (Float.compare(that.relativeHumidity, relativeHumidity) != 0) {
      return false;
    }
    return timestamp == that.timestamp;
  }

  @Override
  public int hashCode() {
    int result = (temperature != 0.0f ? Float.floatToIntBits(temperature) : 0);
    result = 31 * result + (relativeHumidity != 0.0f ? Float.floatToIntBits(relativeHumidity) : 0);
    result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
    return result;
  }

}
