package com.crm.sample_contacts_app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zoho.crm.library.crud.ZCRMModule;
import com.zoho.crm.library.crud.ZCRMRecord;
import com.zoho.crm.library.exception.ZCRMException;
import com.zoho.crm.library.setup.restclient.ZCRMRestClient;
import com.zoho.crm.sdk.android.zcrmandroid.activity.ZCRMBaseActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sarath-zuch594 on 09/08/18.
 */

public class ListActivity extends ZCRMBaseActivity {

    public static ArrayAdapter<ZCRMRecord> adapter;
    public static ListView recordList;
    public static List records = new ArrayList();
    public static List storeList = new ArrayList();
    private static String moduleApiName;

    ProgressBar mProgress;
    SwipeRefreshLayout refreshLayout;
    TextView emptyList;
    TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_tab);

        moduleApiName = getIntent().getStringExtra("module");
        getSupportActionBar().setTitle(moduleApiName);

        emptyList = (TextView) findViewById(R.id.textView32);
        emptyList.setText("");
        loading = (TextView) findViewById(R.id.loading);
        initiatePage();
    }

    public void initiatePage() {
        initiateList();

        refreshLayout = findViewById(R.id.modulerefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearList();
                ApiModeRunner runner = new ApiModeRunner();
                runner.execute();
            }
        });

        mProgress = findViewById(R.id.moduleprogress);
        mProgress.setVisibility(ProgressBar.VISIBLE);
        loading.setText("LOADING.. please wait."); //No I18N

        ApiModeRunner runner = new ApiModeRunner();
        runner.execute();
    }

    public void recordList() throws ZCRMException {
        ZCRMRecord zcrmRecord;
        Iterator itr = records.iterator();
        while (itr.hasNext()) {
            zcrmRecord = (ZCRMRecord) itr.next();
            addRecordToList(zcrmRecord, new RecordListAdapter());
        }
        setPageRefreshingOff();
    }

    public void initiateList() {
        recordList = findViewById(R.id.listView);
        recordList.setAdapter(adapter);
        records.clear();
        storeList.clear();

        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView adapterView, View view, final int position, long idy) {
                //load record's detail page here
            }
        });

    }

    public void addRecordToList(ZCRMRecord zcrmRecord, Object recordListHandler) {
        storeList.add(zcrmRecord);
        adapter = (ArrayAdapter<ZCRMRecord>) recordListHandler;
        recordList.setAdapter(adapter);
    }

    public void setPageRefreshingOff() {
        refreshLayout.setRefreshing(false);
        mProgress.setVisibility(ProgressBar.INVISIBLE);
        loading.setText("");

        if (records.isEmpty()) {
            emptyList.setText("Seems you have nothing...");
        }
    }

    public void clearList() {
        records.clear();
        storeList.clear();
    }

    class RecordListAdapter extends ArrayAdapter<ZCRMRecord> {
        public RecordListAdapter() {
            super(getBaseApplicationContext(), R.layout.list_item, storeList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item, parent, false);
            }

            ZCRMRecord record = (ZCRMRecord) storeList.get(position);
            try {
                TextView primaryField = view.findViewById(R.id.textView4);
                TextView secondaryField = view.findViewById(R.id.textView5);
                TextView tertiaryField = view.findViewById(R.id.textView6);
                String primaryFieldValue = "";
                String secondaryFieldValue = "";
                String tertiaryFieldValue = "";

                if (moduleApiName.equals("Contacts")) {

                    if (record.getFieldValue("First_Name") != null) {
                        primaryFieldValue += record.getFieldValue("First_Name") + " ";
                    }
                    primaryFieldValue += record.getFieldValue("Last_Name");

                    if (record.getFieldValue("Email") == null) {
                        secondaryFieldValue = "No Email";
                    } else {
                        secondaryFieldValue = record.getFieldValue("Email").toString();
                    }
                    if (record.getFieldValue("Phone") == null) {
                        tertiaryFieldValue = "No Phone";
                    } else {
                        tertiaryFieldValue = record.getFieldValue("Phone").toString();
                    }

                } else if (moduleApiName.equals("Tasks")) {

                    primaryFieldValue = record.getFieldValue("Subject").toString();
                    secondaryFieldValue = record.getFieldValue("Status").toString();
                    tertiaryFieldValue = record.getFieldValue("Priority").toString();
                }


                primaryField.setText(primaryFieldValue);
                secondaryField.setText(secondaryFieldValue);
                tertiaryField.setText(tertiaryFieldValue);

            } catch (ZCRMException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

    class ApiModeRunner extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {
            try {
                ZCRMModule module = ZCRMRestClient.getInstance().getModuleInstance(moduleApiName);
                records = module.getRecords().getData();
                resp = "success";
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                recordList();
            } catch (ZCRMException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

}
