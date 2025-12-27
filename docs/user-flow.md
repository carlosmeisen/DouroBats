# DouroBats User Flow

## Application Navigation Flow

This diagram shows the complete user journey from app launch through authentication and main navigation.

```mermaid
flowchart TD
    Start([App Launch]) --> Splash[Splash Screen]

    Splash --> AuthCheck{Authenticated?}

    AuthCheck -->|No| Login[Login Screen]
    AuthCheck -->|Yes| Home[Home Screen]

    Login -->|Success| Home
    Login -->|Cancel| Login

    Home --> BottomNav{Bottom Navigation}

    BottomNav -->|Home Tab| Home
    BottomNav -->|Training Tab| Training[Training Sessions Screen]
    BottomNav -->|Settings Tab| Settings[Settings Screen]

    Training --> ScheduleTraining[Schedule Training]
    ScheduleTraining --> Training

    Settings --> ViewDetails[View User Details]
    Settings --> Logout[Logout]

    Logout --> Login

    style Start fill:#4caf50
    style Splash fill:#00bcd4
    style Home fill:#2196f3
    style Training fill:#ff9800
    style Settings fill:#9c27b0
    style Login fill:#f44336
    style ScheduleTraining fill:#ffc107
```

## Key Navigation Points

### Entry Flow
1. User opens app
2. Splash screen displays
3. Authentication check:
   - **Authenticated** → Navigate to Home Screen
   - **Not Authenticated** → Navigate to Login Screen

### Main Navigation (Bottom Navigation Bar)
- **Home Screen**: Main dashboard
- **Training Sessions Screen**: View and manage training sessions
  - Can schedule new training from here
- **Settings Screen**: User profile and preferences
  - View user details
  - Logout option

### Authentication Flow
- Login required for first-time users or after logout
- Successful login redirects to Home Screen
- Logout returns user to Login Screen
