package com.caliente.express.util;

import com.caliente.express.storage.LocalStorage;

/**
 * Created by Home on 2/2/2559.
 */
public class PermissionUtil
{
    public static boolean currentUserCanEditTarget()
    {
        if (LocalStorage.getCurrentTargetUser() != null)
        {
            if (LocalStorage.getCurrentTargetUser().getId() != LocalStorage.getUserId())
            {
                return LocalStorage.getPermissionLevel().equals("Admin");
            }
        }
        return true;
    }
}
