import android.os.AsyncTask;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.MainActivity;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AsyncTaskTest extends InstrumentationTestCase {

    // Solution gleaned from SO
    // https://stackoverflow.com/questions/2321829/android-asynctask-testing-with-android-test-framework/3802487#3802487
    // Billy Brackeen

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void retrieveJokeTest() {

        final CountDownLatch signal = new CountDownLatch(1);

        final AsyncTask<Integer, Void, String> myTask = new AsyncTask<Integer, Void, String>() {

            MyApi myApiService = null;

            @Override
            protected String doInBackground(Integer... params) {
                if (myApiService == null) {  // Only do this once
                    MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null
                    )
                            .setRootUrl("http://192.168.1.5:8080/_ah/api/")
                            .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                @Override
                                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) {
                                    abstractGoogleClientRequest.setDisableGZipContent(true);
                                }
                            });
                    // end options for devappserver

                    myApiService = builder.build();
                }

                Integer index = params[0];

                try {
                    return myApiService.tellJoke(index).execute().getData();
                }
                catch (IOException e) {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                signal.countDown();
                assertEquals("What do you call someone with no nose? Nobody knows.", result);
            }
        };

        try {
            runTestOnUiThread(new Runnable() {

                @Override
                public void run() {
                    myTask.execute(3);
                }
            });
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        try {
            signal.await(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}