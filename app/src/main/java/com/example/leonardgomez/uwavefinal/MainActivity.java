package com.example.leonardgomez.uwavefinal;

import com.example.leonardgomez.uwavefinal.uwavechat.*;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import com.example.leonardgomez.uwavefinal.serviceforms.*;


public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
    private OggStreamPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        player = new OggStreamPlayer();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void playAsync(View view) {
        player.playAsync("https://live.uwave.fm:8443/listen-128.ogg");
    }

    public void stop(View view) {

        player.stop();
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent activity_home = new Intent(this, MainActivity.class);
            startActivity(activity_home);
        } else if (id == R.id.nav_chat) {
            Intent activity_chat = new Intent(this, Login.class);
            startActivity(activity_chat);
            Toast.makeText(getApplicationContext(),"Welcome to UWave's live chat!",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_schedule) {
            Intent activity_schedule = new Intent(this, Schedule.class);
            startActivity(activity_schedule);
        } else if (id == R.id.nav_psa_form) {
            Intent activity_psa_form = new Intent(this, PsaApplicationForm.class);
            startActivity(activity_psa_form);
        } else if (id == R.id.nav_radio_app_form) {
            Intent activity_radio_form = new Intent(this, RadioApplicationForm.class);
            startActivity(activity_radio_form);
        } else if(id == R.id.nav_about) {
            Intent activity_about_us = new Intent(this, AboutUs.class);
            startActivity(activity_about_us);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
