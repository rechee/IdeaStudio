package hu.ideastudio.richard.ideastudio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String m_Text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(Gravity.LEFT);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_login) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_adventures) {
            try {
                init();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_redeem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.redeem_dialog_title);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton(R.string.redeem_dialog_btn_redeem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Toast.makeText(MainActivity.this, m_Text, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton(R.string.redeem_dialog_btn_dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

        } else if (id == R.id.nav_bug_report) {
            Intent i = new Intent(this, StringSelectorActivity.class);
            i.putExtra("AdventureId", 1);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException {
        DbConnectionJob dcj = new DbConnectionJob(getApplicationContext(), output -> {
        });
        String[] queryToAdd = new String[]{"SELECT * FROM isa_users"};
        dcj.execute(queryToAdd);
    }
}

class DbConnectionJob extends AsyncTask {

    private Context context;
    Connection conn = null;
    public AsyncResponse delegate = null;
    Handler handler;
    String entry = "";

    public interface AsyncResponse {
        void processFinish(String output);
        //Log.d("","");

    }

    public DbConnectionJob(Context context, AsyncResponse delegate) {
        this.delegate = delegate;
        this.context = context;
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


        /*
        String url = String.valueOf(R.string.sql_connection_url);
        String username = String.valueOf(R.string.sql_connection_username);
        String password = String.valueOf(R.string.sql_connection_password);
        */

        /*
        String url = R.string.sql_connection_url + "";
        String username = R.string.sql_connection_username + "";
        String password = R.string.sql_connection_password + "";
        */

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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

        for (int i = 0; i < queryArray.length; i++) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(queryArray[i]);
                while (rs.next()) {
                    entry = rs.getString("passwordHash");
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
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

}