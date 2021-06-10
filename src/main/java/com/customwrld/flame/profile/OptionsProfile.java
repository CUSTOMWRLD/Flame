package com.customwrld.flame.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionsProfile {

    private boolean receivingFriendRequests = true;
    private boolean receivingPublicChat = true;
    private boolean receivingConversations = true;
    private boolean receivingMessageSounds = true;

}