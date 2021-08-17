## Installation

## Running instruction
0. Make sure you don't have anything running on port 8080 and 8000
1. In your command line/terminal, navigate to phase2/src/client
  Enter command `python -m http.server 8080` on windows
  Enter command `python -m http-server 8080` on mac

  If you are using python 2.x, run `python -m SimpleHTTPServer 8080`
  If you have node, you can use `npx http-server`
  If you don't have either, you can use `php -S localhost:8080` but it is slower

  Note: Although we need to use a third party program to run a server. Our
  project is not depended on this third party program. It is only to host the
  client files on your local computer so you can interact with the client

2. Run WordGameSystem.java and wait for the server to start
3. Open http://localhost:8080/ in your browser

## List of design patterns
#### Builder
`InteractiveBuilder`, `HangmanInteractiveBuilder`, `QuizGameInteractiveBuilder`

The InteractiveBuilders allow the user to construct game objects piece by
piece. Design questions are provided to the caller in order, and each design
input/specification is applied as it is read to construct the game. When
construction is done, the build() method sends the specified Game object to the
caller. This allows the user to specify the game objects in steps rather than
all at once, and avoids the use of a long constructor to fully define game
entities with many parameters.

#### Dependency Injection
`IdManager`, all managers

The IdManagers are passed into the managers from WordGameSystem using
dependency injection

#### Observer
`GameMatch`, `QuizGameMatch`, `HangmanMatch` `MatchOutputDispatcher`

The MatchOutputDispatcher is the observer and the GameMatch is observable. When
the game match is modified, it triggers code in the MatchOutputDispatcher to
fetch the latest output from the GameMatch and send it to the client presenter
to update the display. The GameMatch triggers code in the MatchOutputDispatcher
to run whenever it is changed but it is not dependent on the
MatchOutputDispatcher.

#### Strategy
`IdManager`, all managers

All managers user IdManager as a strategy to count IDs and get the next
available ID  that can be assigned. We could change how IDs are formed and
incremented without modifying any of the code in the Managers themselves. The
code can be reused or the strategy class can be replaced with a different
strategy without changing anything in the managers

#### Factory
`GameBuilderFactory`, `GameMatchFactory`, `TemplateEditorFactory`

The Factories construct the appropriate use-case class based on the provided
input. Each type of template has its own implementation of the Template, Game,
GameMatch objects, but the controllers all contain instances of the superclass
and treat them the same. The factory calls the correct constructors for
QuizGame, HangmanGame, HangmanGameMatch etc. and returns object instances of
the superclass which can be treated the same by the rest of the program.

## List of features
#### Mandatory features
- Add an extra template: Hangman
- New user type
- Change at least one template from phase 1: QuizGame, multiplechoice quiz game, scorecategories
- Allow admin user to set creation access level
- Admin users can see all creations and delete
- Admin can ban / suspend users

#### Optional features
- Password strength system
- Users can modify/Delete creations, public/private/friends only
- Graphical User Interface (counts as two)

#### Extra features
- Web UI and WebSocket connection
- Live multiplayer gameplay
