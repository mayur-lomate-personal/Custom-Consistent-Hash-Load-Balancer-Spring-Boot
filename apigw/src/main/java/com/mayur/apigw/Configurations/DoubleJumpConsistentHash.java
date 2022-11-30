package com.mayur.apigw.Configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

@Slf4j
public class DoubleJumpConsistentHash {

    private Hashtable<Integer, Integer> serversPos = new Hashtable<>();
    private Vector<String> servers = new Vector<>();

    public synchronized void update(List<ServiceInstance> instances) {
        for (ServiceInstance instance : instances) {
            String url = "http://" + instance.getHost() + ":" + instance.getPort()+"/hello";
            if (!serversPos.containsKey(url.hashCode())) {
                servers.add(url);
                serversPos.put(url.hashCode(), servers.size()-1);
            }
        }
        log.info(servers.toString());
    }

    public void remove(String url) {
        //changed Position
        serversPos.put(servers.get(servers.size()-1).hashCode(), serversPos.get(url.hashCode()));
        //changing value
        servers.set(serversPos.get(url.hashCode()), servers.get(servers.size()-1));
        //removing the instance from map
        serversPos.remove(url.hashCode());
        //shrinking array by 1
        servers.remove(servers.size()-1);
        log.info(serversPos.toString());
        log.info(servers.toString());
    }

    public int getSize() {
        return servers.size();
    }

    public String get(int index) {
        return servers.get(index);
    }
}
