package hu.ideastudio.richard.ideastudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Objects;

import static android.R.attr.id;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFirstname;
    EditText edtLastname;
    EditText edtEmail;
    EditText edtPassword;
    Button btnRegister;
    CheckBox cbNewsletter;
    String returnData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //returnData = getAvailableId();
        Log.d("returnData", "" + returnData);
        EditText email = (EditText) findViewById(R.id.editText_register_email);
        EditText password = (EditText) findViewById(R.id.editText_register_password);
        Intent i = getIntent();
        try {
            email.setText(i.getStringExtra("Username"));
            password.setText(i.getStringExtra("Password"));
        } catch (NullPointerException npe) {
            Log.d("", npe.getMessage());
        }


        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                String queryForRegister = queryBuilder(initializeLayoutComponents());
                Log.d("Register querz> ", queryForRegister);
                UserRegisterTask userRegisterTask = (UserRegisterTask) new UserRegisterTask(getApplicationContext(), new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                    }
                }).execute(queryForRegister);
            }
        });
    }

    private Object[] initializeLayoutComponents() {
        Log.d("Reached init", " method");
        edtFirstname = (EditText) findViewById(R.id.editTextFirstName);
        edtLastname = (EditText) findViewById(R.id.editTextLastName);
        edtEmail = (EditText) findViewById(R.id.editText_register_email);
        edtPassword = (EditText) findViewById(R.id.editText_register_password);
        cbNewsletter = (CheckBox) findViewById(R.id.checkboxNewsletter);
        return new Object[]{edtFirstname.getText().toString(), edtLastname.getText().toString(), edtEmail.getText().toString(), edtPassword.getText().toString(), cbNewsletter.isChecked()};
    }

    private String queryBuilder(Object[] criteria) {
        String[] registerDetails = Arrays.copyOf(criteria, criteria.length - 1, String[].class);
        boolean newsletter = (boolean) criteria[criteria.length - 1];
        String baseQuery = "INSERT INTO isa_users (id,firstName, lastName, emailAddress, passwordHash, newsletter)\n" +
                "SELECT MAX(ID) + 1, 'firstNameIdentifier', 'lastNameIdentifier', 'emailAddressIdentifier', 'passwordHashIdentifier', 'newsletterIdentifier' FROM isa_users;\n";
        baseQuery = baseQuery.replace("firstNameIdentifier", registerDetails[0]);
        baseQuery = baseQuery.replace("lastNameIdentifier", registerDetails[1]);
        baseQuery = baseQuery.replace("emailAddressIdentifier", registerDetails[2]);
        baseQuery = baseQuery.replace("passwordHashIdentifier", registerDetails[3]);
        if (newsletter) {
            baseQuery = baseQuery.replace("newsletterIdentifier", "1");
        } else {
            baseQuery = baseQuery.replace("newsletterIdentifier", "0");
        }
        Log.d("reached query builder", baseQuery);
        return "";
    }
}

class UserRegisterTask extends AsyncTask {

    private Context context;
    private Connection conn = null;
    private AsyncResponse asyncResponse = null;
    private String entry = "";


    UserRegisterTask(Context context, AsyncResponse asyncResponse) {
        this.context = context;
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected Objects doInBackground(Object[] params) {
        String[] queryArray = Arrays.copyOf(params, params.length, String[].class);
        Log.d("execQuery", queryArray[0]);
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, queryArray[0], Toast.LENGTH_SHORT).show();
            }
        });
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
                PreparedStatement pst = conn.prepareStatement(aQueryArray);
                pst.executeQuery();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        //asyncResponse.processFinish(entry);
        super.onPostExecute(o);
    }
}


