package com.customwrld.flame.sync.discord;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SyncCode {
    private String code;
    private long generatedAt;
}
