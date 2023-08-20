package uk.ac.ic.doc.blocc.dashboard.fabric;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class BloccConnections implements DisposableBean {

  private final String mspId;

  // Path to user certificate.
  private final Path certPath;
  // Path to user private key directory.
  private final Path keyDirPath;
  // Path to peer tls certificate.
  private final Path tlsCertPath;

  // Gateway peer end point.
  private final String peerEndpoint;
  private final String overrideAuth;

  private final Gateway gateway;
  private final ManagedChannel grcpChannel;

  private final Map<Integer, Network> channels = new HashMap<>();

  /**
   * Establish gRPC connection to create a Gateway, which can be used to connect to the BLOCC
   * network. The following environmental variables need to be provided:
   *
   * <li>
   * {@code FABRIC_CONTAINER_NUM}: the BLOCC shipping container number
   * </li>
   * <li>
   * {@code FABRIC_ORG_PATH}: the path to the cryptographic files ({@code organizations/})
   * </li>
   * <li>
   * {@code FABRIC_ENV}: whether it is {@code "test"} or {@code "production"} environment, default
   * to {@code "test"}
   * </li>
   */
  public BloccConnections() {

    // TODO: remove defaults after containerisation
    int fabricContainerNum =
        Integer.parseInt(System.getenv().getOrDefault("FABRIC_CONTAINER_NUM", "5"));

    Path organizationsPath = Path.of(System.getenv()
        .getOrDefault("FABRIC_ORG_PATH", "/home/tonywu/blocc/blocc-test-network/organizations"));

    this.mspId = String.format("Container%dMSP", fabricContainerNum);
    String peerOrg = String.format("container%d.blocc.doc.ic.ac.uk", fabricContainerNum);
    Path userMspDir =
        Paths.get("peerOrganizations", peerOrg, "users", String.format("User1@%s", peerOrg), "msp");

    this.certPath =
        organizationsPath.resolve(
            userMspDir.resolve(
                Paths.get("signcerts", String.format("User1@%s-cert.pem", peerOrg))));

    this.keyDirPath = organizationsPath.resolve(userMspDir.resolve("keystore"));

    this.tlsCertPath =
        organizationsPath.resolve(
            Paths.get(
                "peerOrganizations",
                peerOrg,
                "peers",
                String.format("peer0.%s", peerOrg),
                "tls",
                "ca.crt"));

    boolean isTestEnv = System.getenv().getOrDefault("FABRIC_ENV", "test").equals("test");

    this.peerEndpoint =
        String.format(isTestEnv ? "localhost:%d051" : "blocc-container%d:7051", fabricContainerNum);
    this.overrideAuth = String.format("peer0.container%d.blocc.doc.ic.ac.uk", fabricContainerNum);

    // The gRPC client connection should be shared by all Gateway connections to
    // this endpoint.
    grcpChannel = newGrpcConnection();

    gateway =
        Gateway.newInstance()
            .identity(newIdentity())
            .signer(newSigner())
            .connection(grcpChannel)
            // Default timeouts for different gRPC calls
            .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
            .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
            .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
            .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES))
            .connect();
  }

  protected ManagedChannel newGrpcConnection() {
    ChannelCredentials credentials;
    try {
      credentials = TlsChannelCredentials.newBuilder().trustManager(tlsCertPath.toFile()).build();
    } catch (IOException e) {
      throw new RuntimeException("IO Error occurred when reading TLS certificate file", e);
    }
    return Grpc.newChannelBuilder(peerEndpoint, credentials)
        .overrideAuthority(overrideAuth)
        .build();
  }

  protected Identity newIdentity() {
    BufferedReader certReader;
    try {
      certReader = Files.newBufferedReader(certPath);
    } catch (IOException e) {
      throw new RuntimeException("IO Error occurred when reading certificate file", e);
    }
    X509Certificate certificate;
    try {
      certificate = Identities.readX509Certificate(certReader);
    } catch (IOException e) {
      throw new RuntimeException("IO Error occurred when reading X509 Certificate file", e);
    } catch (CertificateException e) {
      throw new RuntimeException(e);
    }

    return new X509Identity(mspId, certificate);
  }

  protected Signer newSigner() {
    PrivateKey privateKey;
    try {
      BufferedReader keyReader = Files.newBufferedReader(getPrivateKeyPath());
      privateKey = Identities.readPrivateKey(keyReader);
    } catch (IOException e) {
      throw new RuntimeException("IO Error occurred when reading private key file", e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }

    return Signers.newPrivateKeySigner(privateKey);
  }

  private Path getPrivateKeyPath() throws IOException {
    try (Stream<Path> keyFiles = Files.list(keyDirPath)) {
      return keyFiles.findFirst().orElseThrow();
    }
  }

  public void connectToChannel(int channelNum) {
    String channelName = String.format("channel%d", channelNum);
    if (channels.containsKey(channelNum)) {
      throw new IllegalArgumentException(String.format("%s already exists", channelName));
    }

    channels.put(channelNum, gateway.getNetwork(channelName));
  }

  public Network getChannel(int channelNum) {
    return channels.get(channelNum);
  }

  public void connectToChannels(Iterable<Integer> channelNums) {
    channelNums.forEach(this::connectToChannel);
  }

  @Override
  public void destroy() throws InterruptedException {
    gateway.close();
    grcpChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
  }
}
