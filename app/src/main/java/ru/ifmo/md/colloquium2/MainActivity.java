package ru.ifmo.md.colloquium2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    private SharedPreferences prefs;
    private Context context;
    private Toast toast;
    private CandidatesDataSource datasource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        datasource = new CandidatesDataSource(this);
    }

    private void checkStatus() {
        TextView textView = (TextView)findViewById(R.id.textView);

        Boolean status = prefs.getBoolean("status", false);
        if (status) {
            textView.setText(R.string.poll_started);
        } else {
            textView.setText(R.string.poll_not_started);
        }
    }

    private void updateCandidates() {
        final List<Candidate> values = datasource.getAllCandidates(false);
        Boolean isPrivatePoll = prefs.getBoolean("private", true);
        if (!isPrivatePoll) {
            for(int i = 0; i < values.size(); i++) {
                values.get(i).setStatus(true);
            }
        }
        // use the SimpleCursorAdapter to show the elements in a ListView
        ArrayAdapter<Candidate> adapter = new ArrayAdapter<Candidate>(this,
                android.R.layout.simple_list_item_1, values);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Boolean status = prefs.getBoolean("status", false);
                if (status) {
                    Intent intent = new Intent(context, VoteActivity.class);
                    intent.putExtra("_id", values.get(i).getId());
                    intent.putExtra("name", values.get(i).getName());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, EditActivity.class);
                    intent.putExtra("_id", values.get(i).getId());
                    intent.putExtra("name", values.get(i).getName());
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        datasource.open();

        //checking status of poll
        checkStatus();
        updateCandidates();

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.is_private_poll);
        Boolean status = prefs.getBoolean("status", false);
        Boolean isPrivatePoll = prefs.getBoolean("private", true);
        if (menuItem != null) {
            menuItem.setChecked(isPrivatePoll);
        }
        if (status) {
            menu.findItem(R.id.action_stop).setEnabled(true);

            menu.findItem(R.id.candidates).setEnabled(false);
            menu.findItem(R.id.is_private_poll).setEnabled(false);
            menu.findItem(R.id.action_start).setEnabled(false);
            menu.findItem(R.id.action_clear).setEnabled(false);
            menu.findItem(R.id.action_results).setEnabled(false);
        } else {
            menu.findItem(R.id.action_stop).setEnabled(false);

            menu.findItem(R.id.candidates).setEnabled(true);
            menu.findItem(R.id.is_private_poll).setEnabled(true);
            menu.findItem(R.id.action_start).setEnabled(true);
            menu.findItem(R.id.action_clear).setEnabled(true);
            menu.findItem(R.id.action_results).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Boolean status = prefs.getBoolean("status", false);
        if (id == R.id.action_add_candidate) {
            if (status == false) {
                Intent intent = new Intent(this, AddCandidateActivity.class);
                startActivity(intent);
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, getString(R.string.error_poll_already_started), Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        } else if (id == R.id.action_clear_candidates) {
            if (status == false) {
                datasource.clearCandidates();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, getString(R.string.error_poll_already_started), Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        } else if (id == R.id.action_start) {
            if (status == false) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("status", true);
                editor.commit();
                checkStatus();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, getString(R.string.error_poll_already_started), Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        } else if (id == R.id.action_stop) {
            if (status == true) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("status", false);
                editor.commit();
                checkStatus();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, getString(R.string.error_poll_not_started), Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        } else if (id == R.id.action_clear) {
            datasource.clearResults();
            updateCandidates();
            return true;
        } else if (id == R.id.action_results) {
            if (status == false) {
                Intent intent = new Intent(this, ResultsActivity.class);
                startActivity(intent);
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, "Poll in progress... You can see results after it ends", Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        } else if (id == R.id.is_private_poll) {
            if (status == false) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("private", !item.isChecked());
                editor.commit();
                updateCandidates();
                item.setChecked(!item.isChecked());
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, "Poll in progress... You can set private status after it ends", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
