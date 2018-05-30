package info.rudicorpdev.krsfoto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Rudi Corp DEV on 13/08/2017.
 */

public class Aplikasi extends Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rudi = inflater.inflate(R.layout.aplikasi,container,false);
        return rudi;
    }
}
