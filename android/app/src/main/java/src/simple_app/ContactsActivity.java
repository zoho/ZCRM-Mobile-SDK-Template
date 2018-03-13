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

public class ContactsActivity extends ZCRMBaseActivity {

    public static ArrayAdapter<ZCRMRecord> adapter;
    public static ListView recordList;
    public static List records = new ArrayList();
    public static List storeList = new ArrayList();

    ProgressBar mProgress;
    SwipeRefreshLayout refreshLayout;
    TextView emptylist;
    TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_tab);
        getSupportActionBar().setTitle("Contacts");
        emptylist = (TextView) findViewById(R.id.textView32);
        emptylist.setText("");
        loading = (TextView) findViewById(R.id.loading);
        initiatePage();
    }

    public void initiatePage()
    {
        initiateList();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.modulerefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                clearList();
                ApiModeRunner runner = new ApiModeRunner();
                runner.execute();
            }
        });

        mProgress = (ProgressBar) findViewById(R.id.moduleprogress);
        mProgress.setVisibility(ProgressBar.VISIBLE);
        loading.setText("LOADING.. please wait."); //No I18N

        ApiModeRunner runner = new ApiModeRunner();
        runner.execute();
    }

    public void recordList() throws ZCRMException
    {
        ZCRMRecord zcrmRecord;
        Iterator itr = records.iterator();
        while (itr.hasNext())
        {
            zcrmRecord = (ZCRMRecord) itr.next();
            addRecordToList(zcrmRecord,new RecordListAdapter());
        }
        setPageRefreshingOff();
    }

    public void initiateList()
    {
        recordList = (ListView) findViewById(R.id.listView);
        recordList.setAdapter(adapter);
        records.clear();
        storeList.clear();

        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView adapterView, View view, final int position, long idy)
            {
                //load record's detail page here
            }
        });

    }

    public void addRecordToList(ZCRMRecord zcrmRecord, Object recordListHandler)
    {
        storeList.add(zcrmRecord);
        adapter = (ArrayAdapter<ZCRMRecord>) recordListHandler;
        recordList.setAdapter(adapter);
    }

    public void setPageRefreshingOff()
    {
        refreshLayout.setRefreshing(false);
        mProgress.setVisibility(ProgressBar.INVISIBLE);
        loading.setText("");

        if(records.isEmpty())
        {
            emptylist.setText("Seems you have nothing...");
        }
    }

    public void clearList()
    {
        records.clear();
        storeList.clear();
    }

    class RecordListAdapter extends ArrayAdapter<ZCRMRecord>
    {
        public RecordListAdapter()
        {
            super(getBaseApplicationContext(), R.layout.list_item, storeList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                view = inflater.inflate(R.layout.list_item, parent, false);
            }
            ZCRMRecord record = (ZCRMRecord) storeList.get(position);
            try
            {
                TextView name = (TextView) view.findViewById(R.id.textView4);
                String fullName = "";
                if(record.getFieldValue("First_Name") != null)
                {
                    fullName += record.getFieldValue("First_Name") + " ";
                }
                fullName += record.getFieldValue("Last_Name");
                name.setText(fullName);

                String email, mobile;
                if(record.getFieldValue("Email") == null)
                {
                    email = "No Email";
                }
                else
                {
                    email = record.getFieldValue("Email").toString();
                }
                if(record.getFieldValue("Mobile") == null)
                {
                    mobile = "No Mobile";
                }
                else
                {
                    mobile = record.getFieldValue("Mobile").toString();
                }
                TextView phone = view.findViewById(R.id.textView5);
                phone.setText(mobile);
                TextView emailView = view.findViewById(R.id.textView6);
                emailView.setText(email);
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
                ZCRMModule module = ZCRMRestClient.getInstance().getModuleInstance("Contacts");
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
