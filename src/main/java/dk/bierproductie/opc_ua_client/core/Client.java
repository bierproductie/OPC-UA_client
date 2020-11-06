package dk.bierproductie.opc_ua_client.core;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Client {

    private OpcUaClient client;

    public Client(String endpointURL) {
        try {
            List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(endpointURL).get();
            System.out.println("Connecting to Endpoint: " + endpoints.get(0));

            OpcUaClientConfigBuilder ocb = new OpcUaClientConfigBuilder();
            ocb.setEndpoint(endpoints.get(0));

            client = OpcUaClient.create(ocb.build());
            client.connect().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UaException e) {
            e.printStackTrace();
        }
    }

    public Client(String endpointURL, String username, String password) {
        try {
            List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(endpointURL).get();
            System.out.println("Connecting to Endpoint: " + endpoints.get(0));

            OpcUaClientConfigBuilder ocb = new OpcUaClientConfigBuilder().setIdentityProvider(new UsernameProvider(username,password));
            ocb.setEndpoint(endpoints.get(0));

            client = OpcUaClient.create(ocb.build());
            client.connect().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UaException e) {
            e.printStackTrace();
        }
    }

    public OpcUaClient getClient() {
        return client;
    }
}
