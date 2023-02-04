package net.adib.assignmentgpt.ui.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.adib.assignmentgpt.R;
import net.adib.assignmentgpt.databinding.FragmentAboutBinding;
import net.adib.assignmentgpt.databinding.FragmentHomeBinding;
import net.adib.assignmentgpt.ui.home.HomeViewModel;


public class AboutUsFragment extends Fragment {

   FragmentAboutBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                                ViewGroup container, Bundle savedInstanceState) {
        AboutUsViewModel homeViewModel =
                new ViewModelProvider(this).get(AboutUsViewModel.class);

        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.findViewById(R.id.textViewLink).setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/adibOthman/CrashNews"));
            startActivity(intent);
        });

        return root;
    }

    


}