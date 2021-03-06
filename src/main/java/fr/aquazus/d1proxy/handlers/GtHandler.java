package fr.aquazus.d1proxy.handlers;

import fr.aquazus.d1proxy.Proxy;
import fr.aquazus.d1proxy.network.ProxyClient;
import lombok.extern.slf4j.Slf4j;
import simplenet.packet.Packet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class GtHandler implements PacketHandler {

    private final Proxy proxy;
    private ExecutorService scheduler;

    public GtHandler(Proxy proxy) {
        this.proxy = proxy;
        this.scheduler = Executors.newCachedThreadPool();
    }

    @Override
    public boolean shouldForward(ProxyClient proxyClient, String packet, PacketDestination destination) {
        if (destination != PacketDestination.CLIENT || proxyClient.getGroupLeader() == 0) {
            return true;
        }
        String[] extraData = packet.substring(2).split("\\|");
        int id = Integer.parseInt(extraData[0]);
        ProxyClient client = proxy.getClientByCharacterId(id);
        if (client != null && client.isAutoJoinEnabled() && client.getCharacterId() != proxyClient.getCharacterId() && client.getGroupLeader() == proxyClient.getGroupLeader() && client.getIp().equals(proxyClient.getIp())) {
            String joinPacket = "GA903" + id + ";" + id;
            scheduler.execute(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
                    Packet.builder().putBytes(joinPacket.getBytes()).putByte(10).putByte(0).writeAndFlush(proxyClient.getServer());
                } catch (Exception ex) {
                    log.error("An error occurred while executing a game action", ex);
                }
            });
        }
        return true;
    }
}
