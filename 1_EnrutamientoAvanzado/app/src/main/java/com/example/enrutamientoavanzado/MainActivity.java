package com.example.enrutamientoavanzado;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.enrutamientoavanzado.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEEPLINK", "onCreate, savedInstanceState=" + savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });

        // Solo en el primer arranque (no al rotar la pantalla)
        if (savedInstanceState == null) {
            manejarDeepLink(getIntent());
        }
    }

    // La app ya estaba abierta y llega un deep link nuevo
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("DEEPLINK", "onNewIntent");
        setIntent(intent);
        manejarDeepLink(intent);
    }

    private void manejarDeepLink(Intent intent) {
        if (intent == null || !Intent.ACTION_VIEW.equals(intent.getAction())) return;

        Uri uri = intent.getData();
        Log.d("DEEPLINK", "Intent recibido: " + uri);
        if (uri == null || !"notas".equals(uri.getScheme()) || !"detalle".equals(uri.getHost())) return;

        String titulo = uri.getLastPathSegment();
        if (titulo == null) return;

        // Se navega después de que el NavHost esté listo
        binding.getRoot().post(() -> {
            try {
                NavController navController =
                        Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                Log.d("DEEPLINK", "Destino actual: " + navController.getCurrentDestination());
                if (navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() == R.id.SecondFragment) {
                    Log.d("DEEPLINK", "Ya estaba en la pantalla 2");
                    return;
                }
                Bundle args = new Bundle();
                args.putString("tituloNota", titulo);
                navController.navigate(R.id.SecondFragment, args);
                Log.d("DEEPLINK", "navigate() ejecutado con titulo=" + titulo);
            } catch (Exception e) {
                Log.e("DEEPLINK", "Fallo al navegar", e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}