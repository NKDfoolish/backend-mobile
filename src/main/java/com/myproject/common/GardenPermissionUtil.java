package com.myproject.common;

public class GardenPermissionUtil {

    public static boolean canView(GardenPermission p) {
        return p == GardenPermission.VIEW || p == GardenPermission.CONTROL;
    }

    public static boolean canControl(GardenPermission p) {
        return p == GardenPermission.CONTROL;
    }
}
