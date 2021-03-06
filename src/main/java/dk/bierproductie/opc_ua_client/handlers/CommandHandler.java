package dk.bierproductie.opc_ua_client.handlers;

import dk.bierproductie.opc_ua_client.enums.Commands;
import dk.bierproductie.opc_ua_client.enums.node_enums.CommandNodes;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CommandHandler {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final OpcUaClient client;
    private static CommandHandler instance;

    public CommandHandler(OpcUaClient client) {
        this.client = client;
    }

    public void executeCommand() throws ExecutionException, InterruptedException {
        NodeId nodeId = CommandNodes.EXECUTE_MACHINE_COMMAND.nodeId;
        Variant variant = new Variant(true);
        client.writeValue(nodeId, DataValue.valueOnly(variant)).get();
    }

    public void setCommand(Commands command) throws ExecutionException, InterruptedException {
        NodeId nodeId = CommandNodes.SET_MACHINE_COMMAND.nodeId;
        Variant variant = new Variant(command.ordinal());
        client.writeValue(nodeId, DataValue.valueOnly(variant)).get();
        executeCommand();
        String message = String.format("Executed command: %s %d", command,command.ordinal());
        LOGGER.log(Level.INFO,message);
    }

    public static CommandHandler getInstance() {
        return instance;
    }

    public static void setInstance(OpcUaClient client) {
        CommandHandler.instance = new CommandHandler(client);
    }
}
