package hu.ideastudio.richard.ideastudio;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class StringSelectorActivity extends AppCompatActivity {
    Integer mAdventureId;
    private boolean returnValue = false;
    FloatingActionButton mfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdventureId = getIntent().getExtras().getInt("AdventureId");

        EditText edtString = (EditText) findViewById(R.id.edtTaskStringContent);
        mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = edtString.getText().toString();


                try {
                    init(content, view);
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public boolean init(String content, View view) throws IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException, ExecutionException, InterruptedException {

        TaskStringSelector tss = (TaskStringSelector) new TaskStringSelector(getApplicationContext(), content, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.equals(content)){
                    mfab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(),R.color.colorAccent, null)));
                    Snackbar.make(view, "Next Screen", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    mfab.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                    Snackbar.make(view, "Stuck in here, entered code doesn't match", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }).execute(queryBuilder());
        return returnValue;
    }

    private String queryBuilder() {
        String query = "Select StringToGuessH FROM `isa_gamePartPassword` WHERE `id` = (SELECT id FROM `isa_adventure` Where id = '1');";
        if (!Locale.getDefault().toString().equals("hu_HU")){
            query = query.replace("StringToGuessH", "StringToGuessE");
        }
        return query;
    }

}


class TaskStringSelector extends AsyncTask {

    private Context context;
    private Connection conn = null;
    private AsyncResponse asyncResponse = null;
    private Handler handler;
    private String content = "";
    private String entry = "";


    public TaskStringSelector(Context context, String content, AsyncResponse asyncResponse) {
        this.context = context;
        this.content = content;
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected Objects doInBackground(Object[] params) {
        String[] queryArray = Arrays.copyOf(params, params.length, String[].class);
        boolean useOnEmulator = false;
        String url;
        String username;
        String password;
        if (useOnEmulator) {
            url = "jdbc:mysql://10.0.2.2:3306/isa";
            username = "richard";
            password = "670820";
        } else {
            url = "jdbc:mysql://192.168.43.86:3306/isa";
            username = "richard";
            password = "670820";
        }

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, username, password);
            Log.d("Connected", "Connected");
        } catch (java.sql.SQLException e1) {
            e1.printStackTrace();
            Log.d("No connection created", "No connection created");
        }
        runQuerys(queryArray);
        return null;
    }

    private void runQuerys(String[] queryArray) {
        for (String aQueryArray : queryArray) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(aQueryArray);
                while (rs.next()) {

                    entry = rs.getString(1);
                }
                Handler handler = new Handler(context.getMainLooper());
                String finalEntry = entry;
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, finalEntry,Toast.LENGTH_LONG).show();
                    }
                });
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        asyncResponse.processFinish(entry);
        if (entry.equals(content)){
            Intent i = new Intent(context, ImageViewActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

        super.onPostExecute(o);
    }

}