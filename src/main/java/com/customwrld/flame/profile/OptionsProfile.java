package com.customwrld.flame.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class OptionsProfile {

    @Getter
    @Setter
    private boolean publicChatEnabled = true;
    @Getter
    @Setter
    private boolean conversationsEnabled = true;
    @Getter
    @Setter
    private boolean soundsEnabled = true;

}