# Crane

A fully programatic, type-safe navigation solution for Android development, focused on multi-modular navigation.

## How it works

Crane is an engine that wires navigation based on `Route` instances to the underlying native android navigation sctucture. Currently supporting `Fragment` navigation, it uses `FragmentActivity`'s `supportFragmentManager` to properly handle backstack and fragment transitions, while providing an easy to use API surface that allows

- Push/Pop functionality through `Route` instances that can hold a `Fragment` parameters.
- Mechanism for a `Fragment` to return results down the stack.
- A `Route`-`Fragment` relationship that allows to easy multi-modular navigation.
- Capability to easily pop an specific sub-set of `Fragment` instances off the stack.
- Resilient structure that can survive configuration changes *and* process death.
  - Test this with `adb shell am kill <app-package>` command with app on background.

### Setting up your navigation

Here's an example of a fragment routed by Crane:

```kotlin
// i.e. ":navigation" module
@Parcelize
data class HomeRoute(val title: String) : Route

// i.e. ":features:home" module
class HomeFragment : Fragment() {
    private val params: HomeRoute by params()

    // ...
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById(R.id.title).text = params.title
    }
}
```

Crane does not know from scratch that `HomeRoute` routes to `HomeFragment`. For that, we need a `RouteMap` which is a typealias for simply a regular `Map` that will wire the classes. (*This can be a bit of a hassle as the project scales, for that reason a Router is being created.*)

```kotlin
// This would be placed in your bottom module, i.e. ":app" module
val routeMap: RouteMap = mapOf(
    HomeRoute::class to HomeFragment::class,
)
```

Now to set up your navigation you will need to create an activity that will hold everything for us

```kotlin
class NavRootActivity : AppCompatActivity {
    private lateinit var crane: Crane

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.R.id.content
        crane = Crane.Builder()
            .create(routeMap, this, android.R.id.content)
            .root(HomeRoute("Our App Title")) // Must have
            // Tell Crane to check for state to restore
            .restoreSavedState(savedInstanceState)
            .build()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Allow Crane to save what it need to
        crane.saveInstanceState(outState)
    }

    override fun onBackPressed() {
        // Delegate back press to Crane
        if (!crane.pop()) super.onBackPressed()
    }
}
```

You will also need to make the same `Crane` instance available across your app, so that fragments can use that to navigate to other fragments. Ideally this should be handled by your project's dependency inversion solution, but a singleton accessor is planned.

- A root activity with a `FragmentManager`.
- A fragment that will represent your navigation home.
- A `Route` to that fragment.
- A Route map, that tells `Crane` which fragment a `Route` leads to.
  - *Soon to come: A router that can create that automatically.*
- Something to hold the single `Crane` instance and make it available to the rest of the project.

### Navigation Affinity

Closing a sub-set of views in a stack at once is a common requirement in android projects. While regular solutions can be tricky, Crane leverages `FragmentManager` under the affinity abstraction. Basically what it means is that once a fragment is pushed with an affinity, every fragment that is pushed after it belongs to the same affinity, until a new affinity is set. When we wish to pop a sub-set of fragments from the stack, we're speaking of *popping an affinity*. This will make `Crane` resume to the fragment previous to the one that set the popped affinity. The root fragment/route will *always* have an affinity associated with it *regardless of wether it was set to do it or not*, popping this affinity will close the application.

Take for example "post new picture" feature which consists of:

    Gallery -> Edit Image -> Caption & Confirm

After we confirm our action of posting a new picture, we don't want to go back to either "Gallery" (or maybe we want?) nor "Edit Image" (this indeed). So we can define that "Gallery" is what would set our affinity here.

```kotlin
// GalleryRoute.kt
@Parcelize
class GalleryRoute : AffinityRoute

// Any fragment
crane.push(GalleryRoute()) // Crane will know to set an Affinity

// ConfirmPicturePostFragment.kt
class ConfirmPicturePostFragment() : Fragment {

    // ...
    fun finish(postUrl: String) {
        crane.popAffinity()
        // We could also...
        crane.push(PostSuccessFeedbackRoute(postUrl))
    }
}
```

In this situation, we decided to add a little feedback, and as we move to "Success Feedback" we've already closed the previously active affinity, and things will work just fine.

### Returning results

Crane also offers a mechanism to pass results to anyone in the stack, *even pass ahead, but please don't (or do?)*. It's as simple as it is to navigate, the only requirement is that the result should be `Parcelable`.

```kotlin
// ProfileRegistrationResult.kt
@Parcelize
data class ProfileRegistrationResult(val success: Boolean) : Parcelable

// Any fragment (possibly "ConfirmProfileRegistrationFragment")
crane.pushResult(ProfileRegistrationResult(true))

// Any other fragment
val result = crane.fetchResult<ProfileRegistrationResult>()
```

*This current implemenation comes with a limitation: Only one result of each type is allowed at a time, and it gets erased once it's fetched. This will probably change in the future, likely along with an Api change*.

## License

    Copyright 2021 Gabriel Freitas Vasconcelos

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
