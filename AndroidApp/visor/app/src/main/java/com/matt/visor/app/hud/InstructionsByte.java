package com.matt.visor.app.hud;

public class InstructionsByte {

    public static final byte ACK_ERROR = 0x00;
    public static final byte ACK_OK = 0x01;

    public static final byte SPEED_INSTRUCTION = 0x05;
    public static final byte NAVIGATION_IMG_INSTRUCTION = 0x06;


    public static final byte REQUEST_BOOT_DATA = 0x10;
    public static final byte RESTART_BOOTING = 0x11;
    public static final byte IMG_FILE_INSTRUCTION = 0x12;
    public static final byte CONFIG_INSTRUCTION = 0x13;
    public static final byte ALL_BOOT_DATA_SENT = 0x15;
    public static final byte BOOT_DATA_PROCESSED = 0x16;



}
