package net.adib.assignmentgpt.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.adib.assignmentgpt.LatestNewsPage;
import net.adib.assignmentgpt.R;
import net.adib.assignmentgpt.databinding.FragmentDashboardBinding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {

    class News{
        public String location, description, imgurl;
    }
    class Response{
        public boolean success;
        public String message;
        public News[] news;
    }

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LinearLayout newsList=root.findViewById(R.id.news);
        ExecutorService service= Executors.newSingleThreadExecutor();
        service.execute(()-> {
            try {
                Log.e("Information","Here");
                URL url = new URL("http://172.20.10.11/webadmin/news.php");
                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                if(conn.getResponseCode()!=200){
                    InputStream is = conn.getErrorStream();
                    throw new Exception("Something Wrong");
                }
                BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()),16);
                StringBuilder json= new StringBuilder();
                String line;
                while((line=reader.readLine())!=null){
                    json.append(line);
                }
                Gson gson = new GsonBuilder().create();
                Response r = gson.fromJson(json.toString(), Response.class);
                if(!r.success)throw new Exception(r.message);
                for(News news:r.news){
                    View view = getLayoutInflater().inflate(R.layout.single_news, null);
                    ((TextView)view.findViewById(R.id.newsTitle)).setText(news.location);
                    ((ImageView)view.findViewById(R.id.imgNews)).setImageBitmap(getImageFromURL(news.imgurl));
                    ((TextView)view.findViewById(R.id.descNews)).setText(news.description);
                    getActivity().runOnUiThread(()->newsList.addView(view));

                }
            }catch (Exception e){
                Log.e ("ERROR", e.toString());

            }
        });

        return root;
    }
    static void copy(BufferedInputStream in, BufferedOutputStream out) throws Exception {
        int bytesRead, BUFFERED_SIZE=16;byte[] bytes=new byte[BUFFERED_SIZE];
        while ((bytesRead=in.read(bytes))!=-1)out.write(bytes, 0, bytesRead);
    }
    static Bitmap getImageFromURL(String url){
        try {
            HttpURLConnection conn= (HttpURLConnection) new URL(url).openConnection();
            BufferedInputStream bi= new BufferedInputStream(conn.getInputStream());

            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            BufferedOutputStream bo = new BufferedOutputStream(baos);
            copy (bi, bo);
            bo.flush();
            byte[] bytes = baos.toByteArray();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, new BitmapFactory.Options());
        }catch(Exception e){
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}