package connection;

import static ch.qos.logback.core.util.OptionHelper.isEmpty;

public class ConnectionValidator {

    public static String validateConnection(ConnectionInstance c) {
        StringBuilder sb = new StringBuilder();
        validateEmptyFields(sb, c);
        validateParallelConnections(sb, c.getMaxParallelConnection());
        return sb.toString();
    }

    private static StringBuilder validateEmptyFields(StringBuilder sb, ConnectionInstance c) {
        sb.append(isEmpty(c.getQuery()) ? "Query empty or null; " : "");
        sb.append(isEmpty(c.getUser()) ? "User empty or null; " : "");
        sb.append(isEmpty(c.getPwd()) ? "Password empty or null; " : "");
        sb.append(isEmpty(c.getPort()) ? "Port empty or null; " : "");
        sb.append(isEmpty(c.getHost()) ? "Host empty or null; " : "");
        return sb;
    }

    private static StringBuilder validateParallelConnections(StringBuilder sb, int mpc) {
        sb.append(mpc < 1 || mpc > 64 ? "MaxParallelConnection must be between 1 and 64; " : "");
        return sb;
    }
}
