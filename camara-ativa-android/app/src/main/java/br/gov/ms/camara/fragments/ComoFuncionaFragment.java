package br.gov.ms.camara.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rodolfoortale on 04/03/16.
 */
public class ComoFuncionaFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vInflater = inflater.inflate(br.gov.ms.camara.R.layout.fragment_como_funciona, container, false);

        return vInflater;
    }
}
