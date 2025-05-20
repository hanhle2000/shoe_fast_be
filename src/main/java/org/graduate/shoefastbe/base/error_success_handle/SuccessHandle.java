package org.graduate.shoefastbe.base.error_success_handle;

public class SuccessHandle {
    public static SuccessResponse success(String successCode){
        String[] suCo = successCode.split("-");
        return new SuccessResponse(suCo[0], suCo[1]);
    }
}
