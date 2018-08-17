package com.crm.sample_contacts_app;

import android.graphics.Bitmap;

/**
 * Created by sarath-zuch594 on 16/08/18.
 */

public interface DataHandler {

    void setUserImage(Bitmap bitmap);

    void setUserName(String userName);

    void setOrganizationName(String organizationName);
}
