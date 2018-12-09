package com.android.agzakhanty.sprints.one.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.CommonTasks;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetFragment extends Fragment {


    public BottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CommonTasks.setUpTranslucentStatusBar(getActivity());
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);


    }

}
