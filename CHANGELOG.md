# Updates

## 2.1.6
* Added collection mode to all widgets (thanks @engd)!

## 2.1.5
* Issue #5 fixed (thanks @goodengineer)! - Addressed a crash running the sample app on numerous phones

## 2.1.4
* Updated to use version 2.1.7 of imoji-android-sdk 

## 2.1.3
* Made it possible to add any image to the editor through content uris @endg

## 2.1.2
* Use RenderingOptions instead of imageFormat and displayStickerBorders

## 2.1.1
* Refactored some more classes
  * SearchResultAdapter.ImojiImageLoader -> SearchResultAdapter.ImageLoader
  * SearchResultAdapter.ImojiImageLoaderCallback -> SearchResultAdapter.ImageLoaderCallback
* Updated contributer list

## 2.1.0
* Big additions to the Android UI SDK! We now offer new Search Widgets that allow users to plugin full Imoji Grids with search capabilities into their apps. 
* Refactoring packages for consistency
  * ImojiCreateService and ImojiEditorActivity moved from io.imoji.sdk.ui => io.imoji.sdk.editor
  * Move search widgets to the grid package* Refactored 

## 2.0.6
* Updated to use version 2.1.5 of imoji-android-sdk 

## 2.0.5
* Fixes a crash on construction of ImojiCreateService

## 2.0.4
* Updated to use version 2.1.3 of imoji-android-sdk 

## 2.0.3
* Updated to use version 2.0.3 of imoji-android-sdk 

## 2.0.2
* Fix reference to the old pacakge in fragment_tag_imoji
* Use the IntentService reference for context instance in ImojiCreateService 

## 2.0.1
* Move to io.imoji namespace

##2.0.0
* Updated build to match version 2.0.0 of imoji-android-sdk

#0.1.10
- Use smaller JPG assets for Imoji Editor Tips for a smaller footprint

#0.1.9
- Fix dependency issue with Imoji SDK

#0.1.8
- Fixes a bug in which a bordered image was being uploaded to the server

#0.1.7
- Added new option to allow for an outlined bitmap to be returned immediately while letting the
  Imoji object registration happen in the background
- Clear bitmaps from cache that are not used after the editor

#0.1.6
- Properly handle Activity lifecycle events for the GLSurfaceView

#0.1.5
- Added ability to skip tagging screen which will effectively make a private imoji
- Restructured code to use retained fragments during the creation process

#0.1.4
- Fixed undo button misbehavior in the editor
- Updated graphics lib
- Added a simple transition animation to dots in the hints

#0.1.3
- Removed unused assets
- Optimized asset usage 

#0.1.2
- Fixed flickering when transitioning into the tagging phase
- Fixed bug that outlined whole image when you went back from tagging screen 

#0.1.1
- Updated hint layouts to fit properly in portrait. No landscape support yet.
- Removed imojigraphics project dependency

#0.1.0
- Set SDK Version to 22
- Added hints
- More styling of the editor based on themes
