package com.customwrld.flame.profile.rank;

import com.customwrld.customlib.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RankUpdateType {

    CREATED("Rank has been Created!"),
    DELETED("Rank has been Deleted!"),
    PERMISSION_ADDED("Permission has been Added!"),
    PERMISSION_REMOVED("Permission has been Removed!"),
    INHERIT_ADDED("Inherit has been Added!"),
    INHERIT_REMOVED("Inherit has been Removed!"),
    COLOR_UPDATED("Color has been Updated!"),
    WEIGHT_UPDATED("Weight has been Updated!"),
    PREFIX_UPDATE("Prefix has been Updated!"),
    STAFF_UPDATED("Staff has been Updated!");

    private final String text;

    public String format(String rank) {
        return CC.translate(CC.AQUA + CC.BOLD + "CUSTOMWRLD " + CC.DARK_GRAY + " - " + CC.GRAY + "(" + rank + ") -> " + text);
    }

}
