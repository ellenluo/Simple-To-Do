package com.ellenluo.minimaList;

/**
 * FeedbackFragment
 * Created by Ellen Luo
 * Fragment that allows users to submit feedback by email.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.RuntimeExecutionException;

public class FeedbackFragment extends Fragment {

    private Tracker tracker;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);

        if (container != null) {
            container.removeAllViews();
        }

        // find all fields
        final EditText etName = (EditText) v.findViewById(R.id.user_name);
        final EditText etMessage = (EditText) v.findViewById(R.id.message);
        final Spinner feedbackType = (Spinner) v.findViewById(R.id.feedback_type);
        Button btnSubmit = (Button) v.findViewById(R.id.send_feedback);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set up email
                String subject = "MinimaList - Feedback (" + feedbackType.getSelectedItem().toString() + ")";
                String body = etMessage.getText().toString() + "\n\n" + etName.getText().toString();

                // send email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                        "ellenyilan.luo@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            }
        });

        return v;
    }

}
