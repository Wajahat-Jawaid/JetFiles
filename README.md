# JetFiles

An illustration of how you can employ Jetpack Compose to access media storage in Android. The app is based on <a href="https://developer.android.com/topic/architecture?gclsrc=ds">Android developers architecture</a> developed with href="https://developer.android.com/jetpack/compose">Jetpack Compose</a>.

https://user-images.githubusercontent.com/13690429/200324386-14ecd5f0-09c8-4ebe-9941-e3dd40258392.mp4

<br />

<h2> <img src="https://cdn-icons-png.flaticon.com/128/675/675579.png"
  width="26"
  height="26"
  style="float:left;">
Architecture</h2>

<ul>
  <li><a href="https://kotlinlang.org/">Kotlin</a> - First class and official programming language for Android development.</li>
  <li><a href="https://kotlinlang.org/">Jetpack Compose</a> - Android’s latest recommended, modern toolkit for building native UI in a declarative way.</li>
  <li><a href="https://kotlinlang.org/docs/coroutines-overview.html">Coroutines</a> - For asynchronous and non-blocking programming.</li>
  <li><a href="https://developer.android.com/kotlin/flow">Flow</a> - A cold asynchronous data stream that sequentially emits values and completes normally or with an exception.</li>
  <li><a href="https://developer.android.com/topic/libraries/architecture/livedata">LiveData</a> - Lifecycler-aware, observable data holder objects that notifies their observers.</li>
  <li><a href="https://developer.android.com/topic/libraries/architecture/viewmodel">ViewModel</a> - Business logic or screen level state holder class.</li>
  <li><a href="https://github.com/material-components/material-components-android">Material Components</a> - Modular and customizable Material Design UI components for Android.</li>
  <li><a href="https://developer.android.com/jetpack/compose/navigation">Jetpack Compose Navigation</a> - Navigate between the composables.</li>
  <li><a href="https://developer.android.com/training/testing/espresso">Espresso</a> - Helps you to write concise, beautiful, and reliable Android UI tests.</li>
  <li><a href="https://robolectric.org/">Roboelectric</a> - A black box testing, making the tests more effective for refactoring and allowing the tests to focus on the behavior of the application instead of the implementation.</li>
</ul>

<br />

<h2> <img src="https://cdn-icons-png.flaticon.com/512/756/756940.png"
  width="26"
  height="26"
  style="float:left;">
Package Structure</h2>

<ul>
  <li><code>data</code> Classes to manage data communication across different components</li>
    <ul>
      <li><code>file</code> Repositories to fetch the files</li>
    </ul>
    <li><code>model</code> Contains the data models</li>
    <li><code>ui</code> Classes dealing with the presentation including Composables, Navigation, ViewModels etc.</li>
      <ul>
        <li><code>component</code> Composable components used across the screens</li>
        <li><code>home</code> Home screen composables to display the list of files</li>
        <li><code>theme</code> App themes</li>
        <li><code>utils</code> Utility classes specifically for the views</li>
      </ul>
    <li><code>utils</code> Utility classes</li>
</ul>
