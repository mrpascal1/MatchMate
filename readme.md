# MatchMate 🧡

**MatchMate** is a modern Android matchmaking app designed for displaying user matches with smooth pagination, offline support, and seamless network handling. Built with **Kotlin**, **Hilt**, **Room**, **Retrofit**, and **Jetpack components**.

---

## 🚀 Features

- Display a list of match profiles with **RecyclerView** using **ConcatAdapter**.
- Infinite scrolling with **pagination** 🌀.
- Accept or decline matches with dynamic UI updates.
- Offline support: changes are saved locally using **Room** 🗃️.
- Network state monitoring: shows an offline banner when the device is disconnected 🌐.
- Clean architecture with **Repository**, **ViewModel**, and **UI state handling**.
- Dependency injection with **Hilt** 🧩.
- Image loading with **Glide** 📸.

---

## 🏗 Tech Stack

- **Language:** Kotlin
- **DI:** Hilt
- **Networking:** Retrofit
- **Local Storage:** Room
- **UI:** RecyclerView, ConcatAdapter, ViewBinding
- **Coroutines & Flow:** For asynchronous operations
- **Lifecycle-aware components** for UI updates
