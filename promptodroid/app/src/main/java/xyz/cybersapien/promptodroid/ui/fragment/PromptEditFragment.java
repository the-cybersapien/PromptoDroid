package xyz.cybersapien.promptodroid.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;
import xyz.cybersapien.promptodroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromptEditFragment extends Fragment {

    private static final String LOG_TAG = PromptEditFragment.class.getSimpleName();

    public PromptEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_prompt_edit, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

}
