package kz.kuat.kuat;

public class TransferObjects {
    private static TransferObjects instance;
    private int fieldNum;

    private ThingSpeakConnection thingSpeakConnection;

    public static TransferObjects getInstance() {
        if (instance == null) instance = new TransferObjects();
        return instance;
    }

    public static void setInstance(TransferObjects instance) {
        TransferObjects.instance = instance;
    }

    public ThingSpeakConnection getThingSpeakConnection() {
        return thingSpeakConnection;
    }

    public void setThingSpeakConnection(ThingSpeakConnection thingSpeakConnection) {
        if (this.thingSpeakConnection != null && this.thingSpeakConnection != thingSpeakConnection)
            this.thingSpeakConnection.stop();
        this.thingSpeakConnection = thingSpeakConnection;
    }

    public int getFieldNum() {
        return fieldNum;
    }

    public void setFieldNum(int fieldNum) {
        this.fieldNum = fieldNum;
    }
}
