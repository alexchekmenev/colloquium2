package ru.ifmo.md.colloquium2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class VoteActivity extends ActionBarActivity {
    private CandidatesDataSource datasource;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        context = this;
        datasource = new CandidatesDataSource(this);
        datasource.open();

        //Log.i("DB", "Intent: "+getIntent().getExtras().getInt(""));

        final int id = getIntent().getExtras().getInt("_id");
        String name = getIntent().getExtras().getString("name");
        TextView e = (TextView)findViewById(R.id.textCandidateName);
        e.setText(name);

        Button b = (Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Log.i("DB", "Vote OnClick");
                if (id != -1) {
                    datasource.voteForCandidate(id);
                    Log.i("DB", "Vote success");
                    ((VoteActivity)context).finish();
                }
            }
        });
    }


    /*public void onClick(View view) {
        Candidate candidate = null;
        switch (view.getId()) {
            case R.id.button:
                EditText e = (EditText)findViewById(R.id.editText);
                String name = e.getText().toString();
                // save the new comment to the database
                candidate = datasource.voteForCandidate(name);
                Log.i("DB", "Candidate:id=" + candidate.getId());
                break;
        }
    }*/

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
