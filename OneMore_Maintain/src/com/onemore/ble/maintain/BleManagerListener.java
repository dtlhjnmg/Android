package com.onemore.ble.maintain;

public interface BleManagerListener {

    /**
     * <b>This method is called on separate from Main thread.</b>
     */
    public void onConnected();

    /**
     * <b>This method is called on separate from Main thread.</b>
     */
    public void onDisconnected(String serviceAddress);

    /**
     * <b>This method is called on separate from Main thread.</b>
     */
    public void onServiceDiscovered();

    /**
     * <b>This method is called on separate from Main thread.</b>
     */
    public void onDataAvailable(String serviceAddress, String text, byte[] data);
}
