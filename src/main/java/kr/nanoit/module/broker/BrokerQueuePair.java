package kr.nanoit.module.broker;

public class BrokerQueuePair<I, O> {
    private I input;
    private O output;

    public BrokerQueuePair(I input, O output) {
        this.input = input;
        this.output = output;
    }

    public I getInput() {
        return input;
    }

    public O getOutput() {
        return output;
    }
}
