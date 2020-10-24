package com.starhacks.team2.covidwellnesstracker.ui.home.objects;

import java.util.ArrayList;
import java.util.List;

public class Results {
    public Global Global;
    public List<Countries> Countries;

    public com.starhacks.team2.covidwellnesstracker.ui.home.objects.Global getGlobal() {
        return Global;
    }

    public void setGlobal(com.starhacks.team2.covidwellnesstracker.ui.home.objects.Global global) {
        Global = global;
    }

    public List<com.starhacks.team2.covidwellnesstracker.ui.home.objects.Countries> getCountries() {
        return Countries;
    }

    public void setCountries(List<com.starhacks.team2.covidwellnesstracker.ui.home.objects.Countries> countries) {
        Countries = countries;
    }
}
