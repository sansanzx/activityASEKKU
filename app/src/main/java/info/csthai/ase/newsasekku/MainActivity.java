package info.csthai.ase.newsasekku;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    ListView mListView;
    ArrayList<String> nameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView1);
        new DatabaseLoader().execute();
    }

    class DatabaseLoader extends AsyncTask<Void, Void, Void>{
        private String result;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("กำลังเชื่อมต่อฐานข้อมูล");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://www.csthai.info/jsshow.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                result = readInputStreamToString(connection);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            progressDialog.dismiss();
            try {
                JSONArray array = new JSONArray(result);
                for(int i=0;i<array.length();i++){
                    nameList.add(array.getJSONObject(i).getString("topic_news"));


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapterDir = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, nameList);
            mListView.setAdapter(adapterDir);
        }

        private String readInputStreamToString(HttpURLConnection connection) {
            String result = null;
            StringBuffer sb = new StringBuffer();
            InputStream is = null;

            try {
                is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
            }
            catch (Exception e) {
                Log.i("TAG", "Error reading InputStream");
                result = null;
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        Log.i("TAG", "Error closing InputStream");
                    }
                }
            }

            return result;
        }
    }
}