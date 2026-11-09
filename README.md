# Multiplayer BlackJack (Java NIO + JavaFX)

A client-server BlackJack game built for the *Computer Networks* course at the Faculty of Natural Sciences and Mathematics, University of Banja Luka.

Players log in, create or join a game session, place bets and play rounds of BlackJack against the dealer, with all game state managed by a central server.

## Features

- **Non-blocking server** built on Java NIO (`ServerSocketChannel` + `Selector`) that handles all clients in a single event loop
- **Game sessions:** a player hosts a session and others join it (up to 3 players); each session runs in its own thread (`SesijaThread`)
- **Game logic on the server:** dealing, the dealer's hidden card, bets (INVEST), HIT / STAND and round results
- **JavaFX client GUI** with card images, each player's balance and bet, and the dealer's hand
- **Validation:** unique usernames and checks on bet amounts

## Project structure

```
src/
├── Server.java        # NIO server: accepts clients, manages sessions, routes messages
├── SesijaThread.java  # one game session: deck, dealer, players, bets, rounds
├── Client.java        # client network thread (SocketChannel) that keeps the game state
├── Aplikacija.java    # JavaFX GUI: login, session lobby, game table
├── Card.java          # card model
└── cards/             # card images
```

## Requirements

- JDK 17+
- JavaFX SDK (developed with JavaFX 20)

## Running

The server listens on `localhost:12345`.

1. Open the project in IntelliJ IDEA and add the JavaFX SDK `lib` folder as a library.
2. Run `Server` (its `main` method).
3. Run `Aplikacija` once for each player, with VM options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls
   ```
4. Log in, then one player clicks **Kreiraj sesiju** (create session) and the others click **Pridruzi se sesiji** (join session).

> The UI and code comments are in Serbian.
