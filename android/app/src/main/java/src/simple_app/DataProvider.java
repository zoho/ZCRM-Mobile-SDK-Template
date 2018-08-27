package com.crm.sample_contacts_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.common.api.Api;
import com.zoho.crm.library.common.CommonUtil;
import com.zoho.crm.library.exception.ZCRMException;
import com.zoho.crm.library.setup.metadata.ZCRMOrganization;
import com.zoho.crm.library.setup.restclient.ZCRMRestClient;
import com.zoho.crm.library.setup.users.ZCRMUser;

import java.io.InputStream;


public class DataProvider {

    private static DataProvider instance = new DataProvider();
    private String userName;
    private Bitmap userImage;
    private String organisationName;
    private apiTypes api;

    private DataProvider(){

    }

    public static DataProvider getInstance() {
        return instance;
    }

    private  enum apiTypes {
        userName, organisationInformation, userImage
    }

    public void getUserImage(DataHandler handler) {
        if (userImage == null) {
            api = apiTypes.userImage;
            new APIRunner(handler).execute();
        } else {
            handler.setUserImage(userImage);
        }
    }

    public void getOrganisationName(DataHandler handler) {
        if (organisationName == null) {
            api = apiTypes.organisationInformation;
            new APIRunner(handler).execute();
        } else {
            handler.setOrganizationName(organisationName);
        }

    }

    public void getUserName(DataHandler handler) {
        if (userName == null) {
            api = apiTypes.userName;
            new APIRunner(handler).execute();
        } else {
            handler.setUserName(userName);
        }
    }

    private class APIRunner extends AsyncTask{

        private DataHandler dataHandler;

        public APIRunner(DataHandler dataHandler){
            super();
            this.dataHandler = dataHandler;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

          final ZCRMRestClient restClient = ZCRMRestClient.getInstance();
          try {
              if (api == apiTypes.userName) {

                  final ZCRMUser zcrmUser = (ZCRMUser) restClient.getCurrentUser().getData();
                  userName = zcrmUser.getFullName();
                  this.dataHandler.setUserName(userName);
              }  else if (api == apiTypes.userImage) {
                  final ZCRMUser zcrmUser = (ZCRMUser) restClient.getCurrentUser().getData();
                  InputStream is = zcrmUser.downloadProfilePic(CommonUtil.PhotoSize.original).getFileAsStream();
                  userImage = BitmapFactory.decodeStream(is, null, getBitMapOptions());
                  this.dataHandler.setUserImage(userImage);
              } else if (api == apiTypes.organisationInformation) {
                  organisationName = ((ZCRMOrganization) restClient.getOrganizationDetails().getData()).getCompanyName();
                  this.dataHandler.setOrganizationName(organisationName);
              }
          } catch (ZCRMException ze) {
              ze.printStackTrace();
          } catch (Exception e) {
              e.printStackTrace();
          }
            return null;
        }

        private BitmapFactory.Options getBitMapOptions() {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inSampleSize = 1;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return options;
        }
    }
}

