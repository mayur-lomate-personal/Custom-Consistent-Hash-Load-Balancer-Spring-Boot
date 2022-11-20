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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Component
public class URLFilter implements GatewayFilter, Ordered {

    @Autowired
    private DiscoveryClient client;

    @Override
    public int getOrder() {
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<ServiceInstance> instances = client.getInstances("hello-server");
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() + exchange.getRequest().getRemoteAddress().getPort();
        int cur = Hashing.consistentHash(ip.hashCode(), instances.size());
        log.info("{}",cur);
        String newUrl = "http://" + client.getInstances("hello-server").get(cur).getHost() + ":" + client.getInstances("hello-server").get(cur).getPort()+"/hello";
        log.info(newUrl);
        try {
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, new URI(newUrl));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return chain.filter(exchange);

    }
}
