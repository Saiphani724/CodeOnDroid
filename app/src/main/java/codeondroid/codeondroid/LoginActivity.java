package codeondroid.codeondroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import codeondroid.codeondroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

//    SQLiteDatabase db;
    Button b;
    ProgressBar prgs;
    int someError=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
        b = (Button)findViewById(R.id.signin);
        prgs = (ProgressBar) findViewById(R.id.prgsBarLogin);

        TextView frgt = (TextView) findViewById(R.id.forgotpassword);

        TextView signUp_text = findViewById(R.id.signUp_text);
//        db = openOrCreateDatabase("CodeEditorDB", MODE_PRIVATE, null);
        signUp_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_HIGH);
            }

            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_1");

            notification
                    .setSmallIcon(R.drawable.ic_settings_ethernet_black_24dp)
                    .setContentTitle("Logged In!")
                    .setContentText("Welcome! User is aldready logged in.")
                    .setNumber(3); // this shows a number in the notification dots

            NotificationManager notificationManager =
                    (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(1, notification.build());
            startActivity(new Intent(getApplicationContext(), Navigationclass.class));
            finish();
        }

        TextView apname=(TextView)findViewById(R.id.openAbout);

        apname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),About.class);
                startActivity(i);
            }
        });

        final EditText emailET = findViewById(R.id.email);
        final EditText pwordET = findViewById(R.id.pword);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailET.getText().toString().trim();
                String pword = pwordET.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    emailET.setError("Enter Registered Email Id");
                    return;
                }
                if(TextUtils.isEmpty(pword))
                {
                    pwordET.setError("Enter Password");
                    return;
                }
                if(pword.length()<6)
                {
                    pwordET.setError("Firebase authentication\n requires password of length greater than 6");
                    return;
                }
                prgs.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email,pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            prgs.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Valid Credentials",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Navigationclass.class));
                            finish();
                        }
                        else
                        {
                            prgs.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Error!\n"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        frgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] someerror = {0};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setTitle("Reset password");
                alertDialog.setMessage("Are you sure you want reset your account password?");
                final EditText input2 = new EditText(getApplicationContext());
                input2.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                input2.setHint("Enter registered Email ID");
                alertDialog.setView(input2);
//                final int[] num = {0};
                alertDialog.setIcon(R.drawable.sendemail);

                alertDialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(input2.getText().toString()))
                        {
                            Toast.makeText(LoginActivity.this,"Email-Id not Entered",Toast.LENGTH_SHORT).show();
//                            input2.setError("Enter Email ID");
                        }
                        else
                        {
                            prgs.setVisibility(View.VISIBLE);
                            FirebaseAuth.getInstance().sendPasswordResetEmail(input2.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                prgs.setVisibility(View.INVISIBLE);
                                                AlertDialog.Builder next = new AlertDialog.Builder(LoginActivity.this);
                                                next.setTitle("Pasword RESET");
                                                next.setMessage("Password RESET initiated\nCheck your email for further instructions");
                                                next.setIcon(R.drawable.resetcomplete);
                                                next.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(getApplicationContext(), "Check Registered Email!",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                next.show();
                                            }
                                            else
                                            {
                                                prgs.setVisibility(View.INVISIBLE);
                                                AlertDialog.Builder next = new AlertDialog.Builder(LoginActivity.this);
                                                next.setTitle("Pasword RESET ERROR");
                                                next.setMessage("Entered Email Id is not Registered in Database");
                                                next.setIcon(R.drawable.emailnotregistered);
                                                next.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(getApplicationContext(), "Check Entered Email!",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                next.show();
                                            }
                                        }

                                    });
                        }
                    }

                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginActivity.this, "Try Logging in again :)", Toast.LENGTH_SHORT).show();
                        // dialog.cancel();
                    }
                });
                final AlertDialog dialog = alertDialog.create();
                dialog.show();
                if (TextUtils.isEmpty(input2.getText().toString())) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
//                input2.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
//                        return false;
//                    }
//                });

                input2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().length()==0){
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                        else {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        });


    }











    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }




//    public void verify(View v ){
//        EditText uname = findViewById(R.id.email);
//        EditText pword = findViewById(R.id.pword);
//
//
//        Cursor c = db.rawQuery(String.format("SELECT * FROM users WHERE username = '%s' and password='%s' ",uname.getText(),pword.getText()),null);
//
//
//        if(c.getCount() == 1)
//        {
//            StringBuffer buffer = new StringBuffer();
//            while (c.moveToNext())
//            {
//                String favLang = c.getString(3);
//                SharedPreferences sf=getSharedPreferences("myfile", Context.MODE_PRIVATE);
//                SharedPreferences.Editor edit=sf.edit();
//                edit.clear(); // remove existing entries
//                edit.putString("favLang",favLang);
//                edit.putString("uname" , c.getString(0) );
//                edit.putString("email" , c.getString(2) );
//                edit.commit();
//            }
//            Intent i = new Intent(getApplicationContext(), Navigationclass.class);
//            startActivity(i);
////            Toast.makeText(LoginActivity.this,"Login Successfull",Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Toast.makeText(LoginActivity.this,"Enter a valid username and password",Toast.LENGTH_SHORT).show();
//        }
//    }



}


