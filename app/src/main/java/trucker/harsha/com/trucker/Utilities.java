package trucker.harsha.com.trucker;

import android.location.Address;

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
}
