# Android Contest 2025: Round 1 Submission
## Project: Profile Screen Modernization

### Overview

This submission presents a comprehensive overhaul of the user profile screen, based on provided design mockups. The focus is on implementing dynamic, scroll-driven animations and modernizing the UI through component refactoring and simplification.

### Demo Videos

| Demo 1 | Demo 2 |
|:-------------------------------:|:-------------------------------:|
| [![Demo 1](https://img.youtube.com/vi/B4vWxe-wZLg/maxresdefault.jpg)](https://www.youtube.com/watch?v=B4vWxe-wZLg) | [![Demo 2](https://img.youtube.com/vi/b4tACDOuDok/maxresdefault.jpg)](https://www.youtube.com/watch?v=b4tACDOuDok) |

### Key Enhancements

#### Visual & Animation Enhancements
*   **Scroll-Driven Header Animation:** A fluid droplet morphing effect has been implemented in the profile header, which animates in response to scroll gestures.
*   **Component Animations:** New animations have been integrated for the user avatar, gift icons, and background patterns to create a more dynamic interface.
*   **Dynamic Action Bar Blur:** The action bar now features a background blur effect. This was achieved by integrating the `ProfileActionBarView` with the `SizeNotifierFrameLayout` to leverage its performant, built-in blur implementation.

#### UI & Codebase Refinements
*   **Modernized Action Bar:** A new action bar with filled icons for primary user actions has replaced the previous floating action button, centralizing controls.
*   **Interface Simplification:** The user interface has been streamlined by removing the 'Notifications' row and disabling the secondary voice and video call buttons to enhance focus on core content.
*   **Code Quality Improvements:** The codebase was refactored by extracting magic numbers into named constants (`ProfileActivity`) and preparing new UI components for localization with English string resources.

### Detailed Commit Log

*   `feat(Profile)`: Implement new animations for avatar, gifts, and background patterns
*   `feat(Profile)`: Add scroll-driven droplet morph animation to header
*   `feat(Profile)`: Add new action buttons with corresponding filled icons
*   `refactor(ui)`: Hook ProfileActionBarView into SizeNotifierFrameLayout for blur
*   `refactor(Profile)`: Simplify UI by removing write button
*   `refactor(Profile)`: Simplify UI by disabling notification row
*   `refactor(Profile)`: Simplify UI by disabling action buttons
*   `refactor(ProfileActivity)`: Extract magic numbers for layout
*   `feat(profile)`: Localize action bar buttons with English strings
