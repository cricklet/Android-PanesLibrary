Disclaimer: unfortunately, I don't do Android dev anymore, so I can't maintain this library.

[![Android PanesLibrary](http://i.imgur.com/y213zca.png)](https://www.youtube.com/watch?v=UA-lAGVXoLU&feature=youtu.be)

# PanesLibrary

This library makes it easy to make native Android apps with multi-pane tablet layouts. On the phone, the app appears as a conventional app with a sliding menu and a content pane where fragments are stacked on top of each other. On the tablet, the menu and all other fragments appear in dynamically added panes of varying sizes. 

## Example

ExampleActivity demonstrates all the necessary configuration for using PanesActivity.

ExampleFragment uses a layout with two TextViews and a Button. The TextViews hold the index of the fragment and the length of time it's been alive. This is used to demonstrate that fragments are correctly retained on activity restarts. The Button has a callback which creates a new fragment and adds it to the parent activity.

## Using PanesLibrary

Make sure you have the most up-to-date [support library](http://developer.android.com/training/basics/fragments/support-lib.html) and [ActionBarSherlock](http://actionbarsherlock.com/).

Have your activity extend PanesActivity. Then, add fragments to the activity using the following functions:

 * setMenuFragment(fragment): set the menu
 * addFragment(prevFragment, newFragment): adds newFragment after the prevFragment (clobbering any fragments that used to be after prevFragment)
 * clearFragments(): removes all the fragments except the menu fragment
 * getMenuFragment(): get the menu fragment
 * getTopFragment(): get the fragment on the top of the stack

You also need to provide a PaneSizer. This object allows you to programatically set the width of each pane based on the type of Fragment/View you want to place inside the pane.

You should also implement updateFragment(...). This is run whenever a fragment is added (even on activity restarts).
 
Fragments are added onto a stack where the 0th fragment is the menu. Here's an example of what this means:



A Fragment is a piece of an activity which enable more modular activity design. It will not be wrong if we say, a fragment is a kind of sub-activity.
Following are important points about fragment −

A fragment has its own layout and its own behaviour with its own life cycle callbacks.

You can add or remove fragments in an activity while the activity is running.

You can combine multiple fragments in a single activity to build a multi-pane UI.

A fragment can be used in multiple activities.

Fragment life cycle is closely related to the life cycle of its host activity which means when the activity is paused, all the fragments available in the activity will also be stopped.

A fragment can implement a behaviour that has no user interface component.

Fragments were added to the Android API in Honeycomb version of Android which API version 11.

You create fragments by extending Fragment class and You can insert a fragment into your activity layout by declaring the fragment in the activity's layout file, as a <fragment> element.

Prior to fragment introduction, we had a limitation because we can show only a single activity on the screen at one given point in time. So we were not able to divide device screen and control different parts separately. But with the introduction of fragment we got more flexibility and removed the limitation of having a single activity on the screen at a time. Now we can have a single activity but each activity can comprise of multiple fragments which will have their own layout, events and complete life cycle.

Following is a typical example of how two UI modules defined by fragments can be combined into one activity for a tablet design, but separated for a handset design.

Android Fragment
The application can embed two fragments in Activity A, when running on a tablet-sized device. However, on a handset-sized screen, there's not enough room for both fragments, so Activity A includes only the fragment for the list of articles, and when the user selects an article, it starts Activity B, which includes the second fragment to read the article.

Fragment Life Cycle
Android fragments have their own life cycle very similar to an android activity. This section briefs different stages of its life cycle.

Fragment
Fragment lifecycle
Here is the list of methods which you can to override in your fragment class −

onAttach()The fragment instance is associated with an activity instance.The fragment and the activity is not fully initialized. Typically you get in this method a reference to the activity which uses the fragment for further initialization work.

onCreate() The system calls this method when creating the fragment. You should initialize essential components of the fragment that you want to retain when the fragment is paused or stopped, then resumed.

onCreateView() The system calls this callback when it's time for the fragment to draw its user interface for the first time. To draw a UI for your fragment, you must return a View component from this method that is the root of your fragment's layout. You can return null if the fragment does not provide a UI.

onActivityCreated()The onActivityCreated() is called after the onCreateView() method when the host activity is created. Activity and fragment instance have been created as well as the view hierarchy of the activity. At this point, view can be accessed with the findViewById() method. example. In this method you can instantiate objects which require a Context object

onStart()The onStart() method is called once the fragment gets visible.

onResume()Fragment becomes active.

onPause() The system calls this method as the first indication that the user is leaving the fragment. This is usually where you should commit any changes that should be persisted beyond the current user session.

onStop()Fragment going to be stopped by calling onStop()

onDestroyView()Fragment view will destroy after call this method

onDestroy()onDestroy() called to do final clean up of the fragment's state but Not guaranteed to be called by the Android platform.

How to use Fragments?
This involves number of simple steps to create Fragments.

First of all decide how many fragments you want to use in an activity. For example let's we want to use two fragments to handle landscape and portrait modes of the device.

Next based on number of fragments, create classes which will extend the Fragment class. The Fragment class has above mentioned callback functions. You can override any of the functions based on your requirements.

Corresponding to each fragment, you will need to create layout files in XML file. These files will have layout for the defined fragments.

Finally modify activity file to define the actual logic of replacing fragments based on your requirement.

Types of Fragments
Basically fragments are divided as three stages as shown below.

Single frame fragments − Single frame fragments are using for hand hold devices like mobiles, here we can show only one fragment as a view.

List fragments − fragments having special list view is called as list fragment

Fragments transaction − Using with fragment transaction. we can move one fragment to another fragment.

```
> setMenuFragment(A)
stack: A

> addFragment(A, B)
stack: A, B

> addFragment (B, C)
stack: A, B, C

> addFragment (C, D)
stack: A, B, C, D

> addFragment(B, E)
stack: A, B, E

>clearFragments()
stack: A
```

Note: PanesActivity requires you to use fragments. If you don't use fragments, you can still use PanesLayout manually.

## PanesLayout (this controls the tablet layout)

The hierarchy of a PanesLayout looks like this:
```
|--------------------------------|
| PanesLayout                    |
| |----------------------------| |
| | PaneScrollView             | |
| | |------------------------| | |
| | | PaneView |-----------| | | |
| | |          | (content) | | | |
| | |          |-----------| | | |
| | |------------------------| | |
| |----------------------------| |
|--------------------------------|
```

A PanesLayout can hold any number of panes. Each pane is made up of a PaneScrollView (which allows the pane contents to slide left & right), a PaneView (which provides padding on the left of the content), and some content (i.e. fragment).

Variables associated with each pane:
 * type: the possible values of type are defined by the user. For example, in ExampleActivity, the only possible type is DEFAULT_PANE_TYPE. This variable is used to size the pane by the PaneSizer
 * focused: if true, then PanesLayout will never intercept touch events associated with this pane.
 * index: the index of this pane-- starting at 0, going to # of panes - 1.

To add/remove/get panes:
 * addPane(int type)
 * addPane(int type, boolean focused)
 * removePanes(int i): removes all panes after the ith index
 * getNumPanes(): the number of panes
 * getCurrentIndex(): the current top index
 * setIndex(int index): scroll to the pane associated with index.

Listeners/delegates:
 * PaneSizer: determines what the type/focus of a pane should be based on some associated object (i.e. Fragment or View)
 * OnIndexChangedListener: this is fired whenever the visible panes change.

```
Copyright 2013 Kenrick Rilee

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
