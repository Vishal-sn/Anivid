package com.example

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.VideoProjectViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun testVideoProjectViewModelInitialization() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = VideoProjectViewModel(application)
    assertNotNull(viewModel)
  }

  @Test
  fun testMainActivityLaunch() {
    ActivityScenario.launch(MainActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        assertNotNull(activity)
      }
    }
  }
}
