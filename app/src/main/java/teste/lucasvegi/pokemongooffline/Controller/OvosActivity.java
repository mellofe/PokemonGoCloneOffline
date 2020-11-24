package teste.lucasvegi.pokemongooffline.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import teste.lucasvegi.pokemongooffline.Model.ControladoraFachadaSingleton;
import teste.lucasvegi.pokemongooffline.Model.Ovo;
import teste.lucasvegi.pokemongooffline.R;
import teste.lucasvegi.pokemongooffline.Util.BancoDadosSingleton;
import teste.lucasvegi.pokemongooffline.View.AdapterOvos;

public class OvosActivity extends Activity implements AdapterView.OnItemClickListener, LocationListener {
    private List<Ovo> ovos;
    public LocationManager lm;
    public Criteria criteria;
    public String provider;

    public int TEMPO_REQUISICAO_LATLONG = 5000;
    public int DISTANCIA_MIN_METROS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ovos);

        TextView txtTotal = (TextView) findViewById(R.id.txtOvosTotal);
        int total = ControladoraFachadaSingleton.getInstance().getOvos().size();
        txtTotal.setText("Ovos: " + total + "/9");
        //Location Manager
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        //Testa se o aparelho tem GPS
        PackageManager packageManager = getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if(hasGPS){
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            Log.i("LOCATION", "Usando GPS");

        }else{
            Log.i("LOCATION", "Usando internet");
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        }


        Location inicial = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ovos = ControladoraFachadaSingleton.getInstance().getOvos();
        ListView listView = (ListView) findViewById(R.id.listaOvos);
        for(int i=0; i<ovos.size(); i++) {
            ovos.get(i).setLocalizacao(inicial);
        }
        AdapterOvos adapterOvos = new AdapterOvos(ovos, this);
        listView.setAdapter(adapterOvos);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        provider = lm.getBestProvider(criteria, true);

        if (provider == null) {
            Log.e("PROVEDOR", "Nenhum provedor encontrado");
        } else {
            Log.i("PROVEDOR", "Esta sendo utilizado o provedor " + provider);

            lm.requestLocationUpdates(provider, TEMPO_REQUISICAO_LATLONG, DISTANCIA_MIN_METROS, this);
        }

    }
    @Override
    protected void onDestroy() {
        lm.removeUpdates(this);
        Log.w("PROVEDOR", "Provedor " + provider + " parado!");
        super.onDestroy();
    }
    public void clickVoltar(View v){
        finish();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            for(int i=0; i<ovos.size(); i++) {
                if (location != ovos.get(i).getLocalizacao() && ovos.get(i).getIncubado() == 1) {
                    double distancia = location.distanceTo(ovos.get(i).getLocalizacao()) / 1000;
                    ovos.get(i).setKmAndado(ovos.get(i).getKmAndado() + distancia);
                    ovos.get(i).setLocalizacao(location);
                    //Toast.makeText(this, "Distancia Calculada: " + distancia +
                           // "\nKm Andado: " + ovos.get(i).getKmAndado(), Toast.LENGTH_LONG).show();
                   // AdapterOvos.refreshDrawableState(i);
                }
            }
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
