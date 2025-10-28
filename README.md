 # Travel Notes - Android Application
​
 ## Overview
​
 Travel Notes is a personal travel application for Android. It allows users to create, manage, and browse their travel memories, each with a title, description, date, image, location, and rating. 
 The app supports multiple users, ensuring that each user's data is kept private.
 Although originally designed for documenting travel experiences, the app's flexible structure makes it equally suitable for recording and showing information about various types of places, 
 such as local garages, hair salons, restaurants, or any other points of interest within a single city.

​
 ## Features
​
 - **User Authentication**: Simple and effective login/sign-up system. If a user doesn't exist, an account is automatically created.
 - **CRUD Operations**: Full Create, Read, Update, and Delete functionality for travel notes.
 - **Rich Travel Notes**: Each note can include:
     - A title and description.
     - An image from the device gallery.
     - A specific date.
     - A rating from 1 to 10.
     - A geographical location set on a map.
     - A list of comments from the user.
 - **Interactive Map View**: View all your travel notes pinned on a single Google Map for a geographical overview of your journeys.
 - **List Filtering and Sorting**: A powerful popup menu allows users to:
     - Sort notes by date (ascending/descending).
     - Sort notes by rating (ascending/descending).
     - Filter to show only the Top 5 or Lowest 5 rated places.
 - **Dynamic UI**: The UI adapts based on the user's state (logged in vs. logged out) and actions (filtering, item selection), hiding and showing UI elements like the "Add" button and contextual menus accordingly.
​
 ## Architecture
​
 This application is built following the **Model-View-Presenter (MVP)** architectural pattern to ensure a clean separation of concerns, making the codebase scalable and easy to maintain.
​
 - **Model**: The data layer is handled by a `JSONStore` which acts as a local data source, persisting all user and place data to a single JSON file on the device. `PlaceStore` and `UserStore` interfaces define the contract for data operations.
 - **View**: The views are implemented as Android `Activity` classes (e.g., `ListView`, `PlaceView`, `ActionView`). They are responsible for displaying data and relaying user actions to the presenter. They are designed to be as "dumb" as possible.
 - **Presenter**: The presenters (e.g., `ListPresenter`, `PlacePresenter`) contain all the business logic. They react to user input from the View, interact with the Model, and tell the View what to display.
​
 ## Technical Details
​
 - **Language**: 100% [Kotlin](https://kotlinlang.org/).
 - **Minimum Android Version:** Android 11 (API Level 30)
 - **Target Android Version:** Android 14 (API Level 34)
 - **UI**: Built with modern Android UI components, including `RecyclerView`, `CardView`, `ConstraintLayout`, and Google Material Components like `FloatingActionButton` and `AppBarLayout`.
 - **Data Persistence**: Data is serialized to and from a local `travel_notes_.json` file using the [Gson](https://github.com/google/gson) library.
 - **Image Loading**: Images are loaded efficiently using the [Picasso](https://square.github.io/picasso/) library.
 - **Logging**: [Timber](https://github.com/ajalt/timberkt) is used for clear and organised logging.
 - **Maps**: [Google Maps Platform](https://developers.google.com/maps/documentation/android-sdk) is used for all location and mapping features.
​
 ## How to Run
​
 1.  Clone the repository.
 2.  Open the project in Android Studio.
 3.  Obtain a Google Maps API key from the Google Cloud Console.
 4.  Add your API key to the `app/src/main/res/values/keys.xml` file.
 5.  Build and run the application on an Android device or emulator.
