/**
 * This class is part of the V.I.S.O.R app.
 * InstructionsByte class contains list of predetermined instructions which are used to identify the
 * message transmitted between the APP and the HUD.
 *
 * @version 1.0
 * @since 0/01/2024
 */

package com.matt.visor.app.hud;

public class InstructionsByte {

    /** Byte for error acknowledgment. */
    public static final byte ACK_ERROR = 0x00;


    /** Byte for ok acknowledgment. */
    public static final byte ACK_OK = 0x01;

    /** Instruction number identifying that received data are speed related. */
    public static final int SPEED_INSTRUCTION = 0x05;

    /** Instruction number identifying that received data are navigation. */
    public static final int NAVIGATION_INSTRUCTION = 0x06;

    
}
