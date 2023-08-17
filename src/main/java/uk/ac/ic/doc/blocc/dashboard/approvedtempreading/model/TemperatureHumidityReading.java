package uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model;

import jakarta.persistence.Embeddable;

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
}
