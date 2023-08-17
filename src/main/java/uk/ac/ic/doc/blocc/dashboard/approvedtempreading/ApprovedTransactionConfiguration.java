package uk.ac.ic.doc.blocc.dashboard.approvedtempreading;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model.ApprovedTransaction;
import uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model.TemperatureHumidityReading;

@Configuration
public class ApprovedTransactionConfiguration {
  @Bean
  CommandLineRunner commandLineRunner(ApprovedTransactionRepository repository) {
    return args -> {
      ApprovedTransaction tx1 = new ApprovedTransaction(
          "tx123",
          1, List.of("Container5MSP", "Container6MSP"),
          new TemperatureHumidityReading(25, 0.3F, 100L));

      ApprovedTransaction tx2 = new ApprovedTransaction(
          "tx456",
          3, List.of("Container5MSP", "Container6MSP", "Container7MSP"),
          new TemperatureHumidityReading(30, 0.2F, 103L));

      ApprovedTransaction tx3 = new ApprovedTransaction(
          "tx1299",
          1, List.of("Container5MSP"),
          new TemperatureHumidityReading(22, 0.3F, 100L));

      repository.saveAll(List.of(tx1, tx2, tx3));
    };
  }
}
