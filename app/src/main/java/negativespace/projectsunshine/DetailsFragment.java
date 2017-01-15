package negativespace.projectsunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        TextView forecast = (TextView) rootView.findViewById(R.id.forecast);
        Intent recover = getActivity().getIntent();
        if(recover != null && recover.hasExtra(Intent.EXTRA_TEXT)) {
            String recovered = recover.getStringExtra(Intent.EXTRA_TEXT);
            forecast.setText(recovered);
        }

        return rootView;
    }

}
