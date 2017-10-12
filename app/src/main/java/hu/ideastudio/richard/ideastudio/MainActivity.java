package hu.ideastudio.richard.ideastudio;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String m_Text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            try {
                init();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException | InstantiationException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //drawer.openDrawer(Gravity.START);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    private void inflateListView(List<String> adventureName, List<String> adventurePrice, List<String> adventureShortDescription, List<Integer> adventureid, List<String> adventureIamgePath) {
        ListView lv = (ListView) findViewById(R.id.lvAdventures);
        final List<Adventure> AdventureList = new ArrayList<Adventure>();
        for (int i = 0; i < adventureid.size(); i++) {
            if (Integer.parseInt(adventurePrice.get(i)) != 0) {
                if (Locale.getDefault().toString().equals("hu_HU")){
                    Toast.makeText(getApplicationContext(), adventureIamgePath.get(i), Toast.LENGTH_SHORT).show();
                    AdventureList.add(new Adventure(adventureName.get(i), adventurePrice.get(i) + " Forint", adventureShortDescription.get(i), adventureIamgePath.get(i)));
                }else {
                    AdventureList.add(new Adventure(adventureName.get(i), adventurePrice.get(i) + " HUF", adventureShortDescription.get(i), adventureIamgePath.get(i)));
                }
            } else {
                AdventureList.add(new Adventure(adventureName.get(i), getResources().getString(R.string.AdventurePriceFree), adventureShortDescription.get(i), null));
            }
        }
        lv.setAdapter(new AdventureListViewAdapter(MainActivity.this, AdventureList, " "));
        lv.setDivider(new ColorDrawable(Color.TRANSPARENT));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(MainActivity.this, AdventureDetailsActivity.class);
                i.putExtra("AdventureId", adventureid.get(position).toString());
                startActivity(i);
            }
        });
    }

    @Deprecated
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
            } catch (IllegalAccessException | InstantiationException | SQLException | ClassNotFoundException | InterruptedException | ExecutionException e) {
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
            startActivity(new Intent(this, SimpleMapsActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException, ExecutionException, InterruptedException {
        GetAdventureListTask getAdventureListTask = (GetAdventureListTask) new GetAdventureListTask(getApplicationContext(), new MainActivityAdventureListIterface() {
            @Override
            public void AdventureListFillFinished(List<String> AdventureName, List<String> AdventurePrice, List<String> AdventureShortDescription, List<Integer> Adventureid, List<String> AdventureImagePath) {
                Toast.makeText(getApplicationContext(), AdventureImagePath.get(0), Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ideastudio", AdventureImagePath.get(0));
                clipboard.setPrimaryClip(clip);
                inflateListView(AdventureName, AdventurePrice, AdventureShortDescription, Adventureid, AdventureImagePath);
            }
        }).execute(queryBuilder()).get();
        //dcj.execute(queryBuilder()).get();
    }

    private String queryBuilder() {
        StringBuilder sb = new StringBuilder();
        if (Locale.getDefault().toString().equals("hu_HU")){
            sb.append("Select id, nameH, Price, shortDescriptionH, imagePath FROM isa_adventure");
        } else {
            sb.append("Select id, nameE, Price, shortDescriptionE, imagePath FROM `isa_adventure` ");
        }
        return sb.toString();
    }
}

class GetAdventureListTask extends AsyncTask {

    private Context context;
    private Connection conn = null;
    private MainActivityAdventureListIterface delegate = null;
    Handler handler;
    private List<String> AdventureNames;
    private List<String> AdventurePrices;
    private List<String> AdventureShortDescription;
    private List<Integer> AdventureId;
    private List<String> AdventureImagePath;

    public interface AsyncResponse {
        void processFinish(String output);
        //Log.d("","");

    }

    public GetAdventureListTask(Context context, MainActivityAdventureListIterface delegate) {
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
            url = "jdbc:mysql://192.168.1.65:3306/isa";
            username = "richard";
            password = "670820";
        }
        Log.d("stuffs", url);

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

        for (int i = 0; i < queryArray.length; i++) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(queryArray[i]);
                AdventureNames = new ArrayList<String>();
                AdventurePrices = new ArrayList<String>();
                AdventureShortDescription = new ArrayList<String>();
                AdventureId = new ArrayList<Integer>();
                AdventureImagePath = new ArrayList<String>();
                while (rs.next()) {
                    AdventureId.add(Integer.parseInt(rs.getString(1)));
                    AdventureNames.add(rs.getString(2));
                    AdventurePrices.add(rs.getString(3));
                    AdventureShortDescription.add(rs.getString(4));
                    AdventureImagePath.add(rs.getString(5));
                }
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, AdventureShortDescription.get(0) + " " + AdventureShortDescription.get(1), Toast.LENGTH_LONG).show();
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
        delegate.AdventureListFillFinished(AdventureNames, AdventurePrices, AdventureShortDescription, AdventureId, AdventureImagePath);
    }
}