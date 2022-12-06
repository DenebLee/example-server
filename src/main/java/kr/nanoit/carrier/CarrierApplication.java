package kr.nanoit.carrier;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CarrierApplication {

    public static void main(String[] args) throws IOException {
        Thread thread = new Thread(new CarrierAccess());
        thread.start();


        System.out.println("==========================================================================================================================================");
        log.info("  CARRIER SERVER START  ");
        System.out.println("==========================================================================================================================================");
    }
}