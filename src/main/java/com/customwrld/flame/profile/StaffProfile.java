package com.customwrld.flame.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffProfile {

    private String twoFactorKey = "";
    private boolean receivingStaffMessages = true;
    private boolean twoFactor = false;
    private boolean locked = false;
    private boolean operator = false;

}
