package com.customwrld.flame.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class StaffProfile {

    @Getter
    @Setter
    private String authKey = "";
    @Getter
    @Setter
    private boolean staffMessagesEnabled = true;
    @Getter
    @Setter
    private boolean authEnabled = false;
    @Getter
    @Setter
    private boolean locked = false;
    @Getter
    @Setter
    private boolean operator = false;

}
