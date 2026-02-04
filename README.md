# AirBlock 🛡️

**AirBlock** is a minimalist Android application designed to improve focus and digital wellbeing. It utilizes physical **NFC Tags** as secure keys to lock and unlock the device's "Focus Mode".

Unlike traditional apps that can be easily bypassed, AirBlock requires a physical action (scanning a tag) to change states, adding a layer of friction that helps break digital addiction loops.

---

## 🚀 Features

* **NFC Authentication:** Register any standard NFC tag (NTAG215 recommended) as your physical security key.
* **Focus Chronometer:** Tracks your focus sessions. The timer persists even if the app is closed or the phone reboots.
* **Secure Persistence:** Uses SharedPreferences to securely store tag signatures and lock states.
* **Modern UI:** Built entirely with **Jetpack Compose** using Material Design 3.
* **Smart State Recovery:** The app remembers if it was locked/unlocked upon restarting.

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Hardware Integration:** Android NFC Adapter API
* **Architecture:** MVVM (Model-View-ViewModel pattern)
* **Persistence:** SharedPreferences & Custom State Management

---

## 🏗️ Architecture

The app follows a unidirectional data flow architecture. The `MainActivity` acts as the hardware bridge, while `AirBlockState` manages the reactive UI state.

```mermaid
classDiagram
    class MainActivity {
        +onCreate()
        +onResume()
        +onPause()
        +onNewIntent(NFC)
        +bytesToHex(ByteArray)
        +enableNfcForegroundDispatch()
        -disableNfcForegroundDispatch()
    }

    class AirBlockState {
        +isLocked: Boolean
        +startTime: Long
        +hasTagRegistered: Boolean
        +timerActivated: Boolean
        +registeredTagId: String
    }

    class TagStorage {
        +saveTag()
        +getTag()
        +saveLockState()
        +saveStartTime()
    }

    class AirBlockHome {
        +AirBlockHomeScreen()
        +rememberStopwatch()
        +formatTime()
        +resetNfcTag()
    }

    %% Relationships
    MainActivity ..> AirBlockState : Updates (Lock/Unlock)
    MainActivity ..> TagStorage : Reads/Writes (Persistence)
    AirBlockHome ..> AirBlockState : Observes changes (UI)
    TagStorage ..> SharedPreferences : Android Disk
