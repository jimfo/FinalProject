import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import com.udacity.gradle.builditbigger.EndpointsAsyncTask;
import com.udacity.gradle.builditbigger.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class JokeResultNotNullOrEmpty extends InstrumentationTestCase implements EndpointsAsyncTask.PostExecuteListener {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Context mContext;
    private final CountDownLatch signal = new CountDownLatch(1);

    @Before
    public void setUp() throws Exception {
        super.setUp();

        mContext = new MockContext();

        assertNotNull(mContext);
    }

    @Test
    public void jokeResultTest() {

        new EndpointsAsyncTask().execute(3);

    }


    @Override
    public void onPostExecute(String result) {

        signal.countDown();
        assertEquals("What do you call someone with no nose? Nobody knows.", result);
    }
}
