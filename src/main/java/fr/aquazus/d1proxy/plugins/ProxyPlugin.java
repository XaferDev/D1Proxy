package fr.aquazus.d1proxy.plugins;

import fr.aquazus.d1proxy.Proxy;
import org.pf4j.ExtensionPoint;

@SuppressWarnings("WeakerAccess")
public interface ProxyPlugin extends ExtensionPoint {
    void main(Proxy proxy);
    String getPrettyName();
    String getVersion();
}
