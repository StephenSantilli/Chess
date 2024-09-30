# Chess by Stephen Santilli
Chess was my final project submission for my Data Structures and Algorithms class at Fox Chapel Area High School. It is a fully functional Chess game coded in Java with many advanced features. The full, animated GUI is created using JavaFX.

## Features
- Cross platform support with installers
- Play two player via pass-and-play
- Multiplayer games across devices over Local Area Network (LAN)
- Games against against every Chess engine, using the Universal Chess Interface (UCI) format
- All standard chess rules enforced, including: piece move paths, castling, promotion, en passant, etc.
- Automatic check, checkmate, stalemate, insufficent material, no possible checkmate, draw by reptition, etc. detection
- Full game move history
    - View old board positions by clicking on that move
- Timer
    - Auto-flipping
    - Automatic game end when time runs out
    - Time per side as well as adding time after each move
- Load games from PGN or FEN notations
- Save and export games to PGN or FEN notations
- Chess960 support, random position generator that follows Chess960 standard
- And more!


## Known Issues

### Mac Only - "Chess" is damaged and can't be opened. You should move it to the trash.
![Dialog displaying "'Chess' is damaged and can't be opened. You should move it to the Trash."](./DamagedScreenshot.png)

1. Open terminal
2. Enter `cd /Applications`
3. Enter `xattr -d com.apple.quarantine Chess.app`
4. Try to run the app
5. If it still won't run, open Settings and go to "Privacy and Security"
6. Find 
> "Chess" was blocked because it is not from an identified developer.

![Mac settings menu, on the "Privacy and Security" tab.](./SettingsScreenshot.png)

7. Then click "Open Anyway"

## Libraries, Software, and Concepts Used
- Java
- GUI & UX
    - JavaFX
- Networking
    - LAN
- Multiplayer gaming
- Build tools
    - Gradle
- Object-Oriented Programmning

