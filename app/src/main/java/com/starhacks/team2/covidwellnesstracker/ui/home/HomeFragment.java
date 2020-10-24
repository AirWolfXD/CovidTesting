package com.starhacks.team2.covidwellnesstracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.starhacks.team2.covidwellnesstracker.R;
import com.starhacks.team2.covidwellnesstracker.ui.home.objects.Countries;
import com.starhacks.team2.covidwellnesstracker.ui.home.objects.Global;
import com.starhacks.team2.covidwellnesstracker.ui.home.objects.Results;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private static TextView totalPositive;
    private static TextView totalDeaths;
    private static TextView totalRecovered;
    private static TextView newRecovered;

    private Results results;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        totalPositive = root.findViewById(R.id.total_positive);
        totalDeaths = root.findViewById(R.id.total_deaths);
        totalRecovered = root.findViewById(R.id.total_recover);
        newRecovered = root.findViewById(R.id.new_recovered);

        ApiRequest.getGlobal(root.getContext(), results,
                (results) -> {
                    totalPositive.setText(Integer.toString(results.getGlobal().getTotalConfirmed()));
                    totalDeaths.setText(Integer.toString(results.getGlobal().getTotalDeaths()));
                    totalRecovered.setText(Integer.toString(results.getGlobal().getTotalRecovered()));
                    newRecovered.setText(Integer.toString(results.getGlobal().getNewRecovered()));
                },
                (error) -> {
                    String dummy = "not me";
                });
        return root;
    }
}