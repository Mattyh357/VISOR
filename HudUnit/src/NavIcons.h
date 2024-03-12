//
// Created by matty on 12-Mar-24.
//

#pragma once

#include "Images.h"

class NavIcon {
public:
    static Image* getImage(uint8_t index) {
        switch (index) {
            case 0:
                return &Icon_00_straight;
            case 1:
                return &Icon_01_merge;
            case 2:
                return &Icon_02_ferry_train;
            case 3:
                return &Icon_03_ferry;
            case 4:
                return &Icon_04_turn_slight_left;
            case 5:
                return &Icon_05_turn_left;
            case 6:
                return &Icon_06_turn_sharp_left;
            case 7:
                return &Icon_07_ramp_left;
            case 8:
                return &Icon_08_fork_left;
            case 9:
                return &Icon_09_uturn_left;
            case 10:
                return &Icon_10_roundabout_left;
            case 11:
                return &Icon_11_turn_slight_right;
            case 12:
                return &Icon_12_turn_right;
            case 13:
                return &Icon_13_turn_sharp_right;
            case 14:
                return &Icon_14_ramp_right;
            case 15:
                return &Icon_15_fork_right;
            case 16:
                return &Icon_16_uturn_right;
            case 17:
                return &Icon_17_roundabout_right;
            case 18:
                return &Icon_18_destination;

        }
    }
};
