package cs.dartmouth.edu.cs165.mahesh.stressmeter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int EXIT_APPLICATION = 0x0001;    //mark for finishing the application
    private Fragment fragment;
    private MediaPlayer mediaplayer;
    private Vibrator vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationview = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(navigationview);

        //set pattern of vibrator
        vibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibration_pattern = {1000, 500, 1000, 500};
        vibration.vibrate(vibration_pattern, 2);

        //set sound and start
        try {
            mediaplayer = MediaPlayer.create(this, R.raw.tone);
            mediaplayer.setLooping(true);
            mediaplayer.start();
        } catch (Exception exc) {
            exc.printStackTrace();
            Toast.makeText(this, "MediaPlayer couldn't start.", Toast.LENGTH_LONG).show();
        }
        //set alarm
        PSMScheduler.setSchedule(this);
        fragment = new Grid();
        FragmentManager fragmentmanager = getSupportFragmentManager();
        fragmentmanager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    /*
        * Set up the drawer menu and clicker
        *
    */
    private void setupDrawerContent(NavigationView navigationview) {
        navigationview.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        getNavigationBar(menuItem);
                        return true;
                    }
                });
    }

    /*
     * When the activity stops, vibrate and sound stops.
     */
    @Override
    public void onDestroy() {
        PSMScheduler.setSchedule(this);
        super.onDestroy();
        endMediaPlayer();
    }

    /*
       *
       *  When a new activity is created then
       *  called after the resume activity
    */
    @Override
    protected void onResume() {
        int flag = getIntent().getIntExtra("flag", 0);
        if (flag == EXIT_APPLICATION) {
            endMediaPlayer();
            finish();
        }
        super.onResume();

    }

    /*
     * Method to stop playing media and stop vibration.
     */
    public void endMediaPlayer() {
        vibration.cancel();
        mediaplayer.stop();
    }


    /*
     * When back button is pressed stop mediaplayer
     * stop vibration and return to normal condition.
     */
    @Override
    public void onBackPressed() {
        endMediaPlayer();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    /*
        *
        *  Method to select items from navigation menu.
        *
    */
    public void getNavigationBar(MenuItem menuItem) {
        endMediaPlayer();
        Class loadclass;
        switch (menuItem.getItemId()) {
            case R.id.nav_result:
                loadclass = Graph.class;
                break;
            case R.id.nav_stress:
                loadclass = Grid.class;
                break;
            default:
                loadclass = Grid.class;
                break;
        }

        try {
            fragment = (Fragment) loadclass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load .class file, please check.", Toast.LENGTH_LONG).show();

        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentmanager = getSupportFragmentManager();
        fragmentmanager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        DrawerLayout drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerlayout.closeDrawer(GravityCompat.START);
        onBackPressed();
    }
}
