package com.github.liosha2007.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liosha on 12.11.13.
 */
public abstract class BaseFragment extends Fragment {
    protected Context context = null;
    protected View view = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutId) {
        this.context = getActivity();
        return (this.view = inflater.inflate(layoutId, container, false));
    }

    public <T extends View> T view(int id) {
        return (T) view.findViewById(id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    public void onFragmentFocused() {
    }
}
