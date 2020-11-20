package dk.bierproductie.opc_ua_client.handlers;

import dk.bierproductie.opc_ua_client.core.Batch;
import dk.bierproductie.opc_ua_client.core.DataCollector;
import dk.bierproductie.opc_ua_client.core.DataWriter;
import dk.bierproductie.opc_ua_client.enums.Commands;
import dk.bierproductie.opc_ua_client.enums.node_enums.AdminNodes;
import dk.bierproductie.opc_ua_client.enums.node_enums.CommandNodes;
import dk.bierproductie.opc_ua_client.enums.node_enums.StatusNodes;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BatchHandler {

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static BatchHandler instance;
    private static Batch currentBatch;
    private final CommandHandler commandHandler;
    private final DataWriter dataWriter;
    private final SubscriptionHandler subscriptionHandler;
    private final DataCollector dataCollector;

    public BatchHandler() {
        this.commandHandler = CommandHandler.getInstance();
        this.dataWriter = DataWriter.getInstance();
        this.subscriptionHandler = SubscriptionHandler.getInstance();
        this.dataCollector = DataCollector.getInstance();
    }

    public static void finishBatch() {
        currentBatch.setAmountProduced((int) DataCollector.getInstance().readData("produced", AdminNodes.PRODUCED_PRODUCTS.nodeId, false));
        currentBatch.setDefectiveProducts((int) DataCollector.getInstance().readData("defective", AdminNodes.DEFECTIVE_PRODUCTS.nodeId, false));
        currentBatch.setOee();
        String msg = currentBatch.toString();
        LOGGER.log(Level.INFO, msg);
    }

    public static Batch getCurrentBatch() {
        return currentBatch;
    }

    public static void setCurrentBatch(Batch currentBatch) {
        BatchHandler.currentBatch = currentBatch;
    }

    public static void setInstance() {
        BatchHandler.instance = new BatchHandler();
    }

    public static BatchHandler getInstance() {
        return instance;
    }

    public void startBatch(Batch batch) throws ExecutionException, InterruptedException {
        if (batch.isRunning() || dataCollector.readMachineState(false) != 4 && dataCollector.readMachineState(false) != 17) {
            LOGGER.log(Level.WARNING, "Another batch is already running");
        } else {
            commandHandler.setCommand(Commands.RESET);
            Thread.sleep(1000);
            setCurrentBatch(batch);
            currentBatch.setRunning(true);
            setupSubscriptions();
            dataWriter.writeData(CommandNodes.SET_NEXT_BATCH_ID.nodeId, currentBatch.getId());
            dataWriter.writeData(CommandNodes.SET_PRODUCT_ID_FOR_NEXT_BATCH.nodeId, currentBatch.getProductType());
            dataWriter.writeData(CommandNodes.SET_PRODUCT_AMOUNT_IN_NEXT_BATCH.nodeId, currentBatch.getAmountToProduce());
            dataWriter.writeData(CommandNodes.SET_MACHINE_SPEED.nodeId, currentBatch.getMachineSpeed());
            commandHandler.setCommand(Commands.START);
        }
    }

    public void setupSubscriptions() {
        subscriptionHandler.subscribe(StatusNodes.MACHINE_STATE.nodeId);
        subscriptionHandler.subscribe(StatusNodes.TEMPERATURE.nodeId);
        subscriptionHandler.subscribe(StatusNodes.HUMIDITY.nodeId, 1000);
        subscriptionHandler.subscribe(StatusNodes.VIBRATION.nodeId, 1000);  
    }

    public static Batch getCurrentBatch() {
        return currentBatch;
    }

    public static void setCurrentBatch(Batch currentBatch) {
        BatchHandler.currentBatch = currentBatch;
    }

    public static void setInstance() {
        BatchHandler.instance = new BatchHandler();
    }

    public static BatchHandler getInstance() {
        return instance;
    }
}
