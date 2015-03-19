package transport.channel;

import java.util.Map;

public interface ChannelConfig {

    static final String TCP_OR_UDP = "tcpOrUdp";

    static final String LOG = "log";

    void setOptions(Map<String, Object> options);

    void setOption(String key, Object value);

    Object getOption(String key, Object defaultValue);

}
