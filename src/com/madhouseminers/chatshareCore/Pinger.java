package com.madhouseminers.chatshareCore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Pinger extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean endLoop = false;
    private Websocket ws;

    public Pinger(Websocket ws) {
        super("Pinger");
        this.ws = ws;
    }

    public void run() {
        while (!endLoop) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.info("Sleep interrupted, no more PINGs");
                return;
            }
            ws.sendMessage(new Message(MessageType.PING));
        }
    }

    public void end() {
        LOGGER.info("Stopping ChatShare ping");
        endLoop = true;
        this.interrupt();
    }
}
