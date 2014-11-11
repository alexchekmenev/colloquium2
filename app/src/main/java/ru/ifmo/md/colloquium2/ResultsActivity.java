package ru.ifmo.md.colloquium2;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class ResultsActivity extends ActionBarActivity {
    private CandidatesDataSource datasource;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        context = getApplicationContext();
        datasource = new CandidatesDataSource(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        datasource.open();

        final List<Candidate> values = datasource.getAllCandidates(true);
        for(int i = 0; i < values.size(); i++) {
            values.get(i).setStatus(true);
        }
        // use the SimpleCursorAdapter to show the elements in a ListView
        ArrayAdapter<Candidate> adapter = new ArrayAdapter<Candidate>(this,
                android.R.layout.simple_list_item_1, values);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.results, menu);
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
