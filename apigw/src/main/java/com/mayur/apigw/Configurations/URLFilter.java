package com.mayur.apigw.Configurations;


import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.*;
import java.util.Hashtable;
import java.util.List;

@Slf4j
@Component
public class URLFilter implements GatewayFilter, Ordered {

    @Autowired
    private DiscoveryClient client;

    private DoubleJumpConsistentHash doubleJumpConsistentHash = new DoubleJumpConsistentHash();

    @Override
    public int getOrder() {
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String instanceURL = "";
        synchronized (doubleJumpConsistentHash) {
            List<ServiceInstance> instances = client.getInstances("hello-server");
            doubleJumpConsistentHash.update(instances);
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() + exchange.getRequest().getRemoteAddress().getPort();
            do {
                int cur = Hashing.consistentHash(ip.hashCode(), instances.size());
                log.info("{}",cur);
                instanceURL = doubleJumpConsistentHash.get(cur);
                try {
                    URL pingUrl = new URL(instanceURL);
                    new Socket(pingUrl.getHost(), pingUrl.getPort()).close();
                    break;
                } catch (ConnectException connectException) {
                    doubleJumpConsistentHash.remove(instanceURL);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.info(instanceURL + " removed");
            } while (true);
        }
        try {
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, new URI(instanceURL));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return chain.filter(exchange);

    }
}
