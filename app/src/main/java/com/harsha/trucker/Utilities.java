package com.harsha.trucker;

import android.location.Address;
import android.widget.Toast;

/**
 * Created by Admin on 11/11/2017.
 */

public class Utilities {

    public static String refineAddress(Address address){
        String result = "";
        if(address.getAddressLine(0) != null) {
            result += address.getAddressLine(0) + ", ";
        }
        if(address.getAddressLine(1) != null) {
            result += address.getAddressLine(1) + ", ";
        }
        if(address.getAddressLine(2) != null) {
            // result += address.getAddressLine(2);
        }

        return result;
    }

    public static void  myToast(String message)
    {
    }
}
