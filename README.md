## Installation
#### Project dependencies:
  - This project use the Gson library from Google to read and write data in the JSON
  format.
  - Gson can be installed through Intellij by going through
    File -> Project Structure -> modules -> Dependencies -> '+' -> libraries -> From Maven
    and type in com.google.code.gson:gson:2.8.7

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
available ID that can be assigned. We could change how IDs are formed and
incremented without modifying any of the code in the Managers themselves. The
code can be reused or the strategy class can be replaced with a different
strategy without changing anything in the managers

#### Dependency Injection
`IdManager`, all managers

The IdManagers are passed into the managers from WordGameSystem using
dependency injection

#### Factory
`GameBuilderFactory`, `GameMatchFactory`, `TemplateEditorFactory`, `TemplateFactory`

The Factories construct the appropriate use-case class based on the provided
input. Each type of template has its own implementation of the Template, Game,
GameMatch objects, but the controllers all contain instances of the superclass
and treat them the same. The factory calls the correct constructors for
QuizGame, HangmanGame, HangmanGameMatch etc. and returns object instances of
the superclass which can be treated the same by the rest of the program.

## List of features
#### Mandatory features
- Add an extra template: Each game genre corresponds to a type of template. The templates contain a series of true/false
 options that dictates how a game is built and how it is played. The system currently support two game genres:
 Quiz and Hangman. With respect to each game genre, templates can be created by admins using the GUI.

- New user type: Temporary User. This user type can be selected on Signup and can be used only for a period of time. After
that, attempting to log in will result in an error message: "account is expired".

- Change at least one template from phase 1: Changes to any of the template attribute from QuizTemplate will change how
a game with that template is played. An example is changing Exact answer quiz template to Multiple choice.
- Allow admin user to set any of the user's creation access level from public to private and vice versa.
- Admin users can see all creations and delete them. The deletes are soft-deletes, which can be recovered.
- Admin can ban / suspend users for a given number of days

#### Optional features
- Password strength system. Password must be sufficiently long, contain both uppercase, lowercase, and special characters.
- We also created the 'forgot password' password feature. A user needs to provide his username and the email address with
which he used to register in order to recover password. If he succeeds, a temporary password is created and is sent through
"email" (a txt file). The temporary lasts for 24 hours or until the system shuts down.
- Users can modify/delete creations, or set them to public/private/friends only.
- Graphical User Interface: using HTML/CSS/JavaScript. This is a functional application which can be deployed on the
web platform.

#### Extra features
- Game-playing uses web-socket connection, which is faster and is more resource-saving than HTTP request/responses. It
also allows server to send data to client without client making a request. This allows online-multiplayer games.

Online multiplayer games can be tested by having two accounts on two different tabs.

## Special behaviours
- There is no "trial" option at the sign up page. This is because by vising the
  site, we automatically initialize a trial user. This trial user is saved
  until the end of the session, meaning that closing and reopening the tab
  would log you in as a new trial user.
- For all users except for trial users, you can log out by visiting the "My
  account" tab on the top right corner after signing in, and clicking on the
  "Log out" button. For all users including the trial users, you are logged out
  when you close the tab in your browser (this is the only way to log out as a
  trial user).
- When a trial user signs up for a temporary or normal user, we call
  `UserManager.promoteTrialUser` instead of `UserManager.createUser`, which
  preserves all of the trial user's data. This means that if you create some
  games as a trial user before signing up, you will still own all your games
  after you signed up
- The front-end is merely a gui. Due to the timing and the expectation of the
  project, we did not implement any security checks from the client. This means
  that you are discouraged from entering links to your browser manually. For
  example, a trial user does not have access to the "edit template" page, and
  the way we hide that from a trial user is to never display the button that
  redirects the them to the "edit template" page. While you can just type up
  `http://localhost:8080/pages/edit-template.html` in your browser, you should
  expect weird behaviours from the client if you do. However, the java
  backend is implemented with security checks
- As an extension to the previous point, you are also discouraged from force
  exiting the page by switching your URL. The only action that the client takes
  when you exit the site is to send a request to the server to log you out. If
  you are in the middle of building a game or a template and would like to
  cancel, you must use the reset button we created for you, rather than just
  force exiting the page.
- The multiplayer mode starts by having a user creating a match. Once the match
  is created, it is displayed on the "Join a match" page for other users. If
  there are still spaces available for the match, other users can join the
  match (you can test this by opening a separate tab). The user who created the
  match has a green "Start" button that the rest of the users don't have. When
  the match creator presses the start button, the match is removed from the
  "Join a match" page so new players can't join, and the match will start for
  all users in the match. Questions are asked one at a time so you can't answer
  the next question until all players have answered the current one
- It's okay to leave an ongoing match by closing the tab, but don't do this if
  you are a trial user who created the match, because you won't be able to log
  back in as the same trial user, and you'll access as the match creator so no
  one can start the game

## A Response to the Phase 1 Feedback
We really appreciate the time from the teaching team to give us helpful feedbacks on how to improve upon Phase 1.

All of the bugs addressed in the feedback was taken care of. We also implemented
a RESTful API, making the program a web-based application. In addition, we
tried to eliminate a good number of exceptions. However, since the program is
quite large and many new features were getting added, we felt that it was
necessary to keep the number of exceptions to better distinguish the errors
during development. We also felt that this would be appropriate since
exceptions are just empty classes that don't depend on anything. They have
little to no impact on the maintenance and the architecture of the program.
That being said, we still merged some exceptions that were similar to each
other, such as `InvalidGameIDException`, `InvalidUserIDException`, and
`InvalidIDException`... etc.

Also, as mentioned in Special Behaviours, we didn't have many _security_
measures on the client side since they are not the focus of the project, but we
hope we can receive more advices on how to tackle these sort of problems and
how to improve in general, to hopefully someday have a program that is good
enough for actual deployment.
