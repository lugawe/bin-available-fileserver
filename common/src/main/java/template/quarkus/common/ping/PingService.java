package template.quarkus.common.ping;

public interface PingService {

    record PingPackage(String ping) {}

    PingPackage ping(PingPackage ping);
}
