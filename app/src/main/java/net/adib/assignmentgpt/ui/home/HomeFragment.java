package net.adib.assignmentgpt.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.adib.assignmentgpt.R;
import net.adib.assignmentgpt.databinding.FragmentHomeBinding;
import net.adib.assignmentgpt.ui.dashboard.DashboardFragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    class Position {
        double lat, lng;
    }

    class Marker {
        String location;
        Position position;
        String description;

    }

    class Response {
        public boolean success;
        public String message;
        public Marker[] markers;
    }

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment fragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        ExecutorService service = Executors.newSingleThreadExecutor();
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                service.execute(() -> {
                    try {
                        URL url = new URL("http://172.20.10.11/webadmin/markers.php");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        if (conn.getResponseCode() != 200) {
                            InputStream is = conn.getErrorStream();
                            throw new Exception("Something Wrong");
                        }
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 16);
                        StringBuilder json = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }
                        Gson gson = new GsonBuilder().create();
                        Response r = gson.fromJson(json.toString(), Response.class);
                        if (!r.success) throw new Exception(r.message);
                        for (Marker marker : r.markers) {
                            getActivity().runOnUiThread(()->{
                                MarkerOptions m = new MarkerOptions();
                                m.position(new LatLng(marker.position.lat, marker.position.lng));
                                m.title(marker.location);
                                m.snippet(marker.description);
                                map.addMarker(m);
                            });

                        }
                    } catch (Exception e) {
                        Log.e("ERROR", e.toString());

                    }
                });


            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}