package info.rudicorpdev.krsfoto;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Rudi extends Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rudi = inflater.inflate(R.layout.rudi,container,false);
        return rudi;
    }
}
