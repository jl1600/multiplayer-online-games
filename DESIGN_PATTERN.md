# List of design patterns
## Builder
`InteractiveBuilder`, `HangmanInteractiveBuilder`, `QuizGameInteractiveBuilder`

`InteractiveBuilder`s allow the user to construct game objects piece by piece.
Design questions are provided to the caller in order, and each design
input/specification is applied as it is read to construct the game. When
construction is done, the build() method sends the specified Game object to the
caller. This allows the user to specify the game objects in steps rather than
all at once, and avoids the use of a long constructor to fully define game
entities with many parameters.

## Observer
`GameMatch`, `QuizGameMatch`, `HangmanMatch` `MatchOutputDispatcher`

`MatchOutputDispatcher` is the observer and `GameMatch` is observable. When the
game match is modified, it triggers code in `MatchOutputDispatcher` to fetch the
latest output from `GameMatch` and send it to the client presenter to update the
display. `GameMatch` triggers code in `MatchOutputDispatcher` to run whenever it
is changed but it is not dependent on the `MatchOutputDispatcher`.

## Strategy
`IdManager`, all managers

All managers user `IdManager` as a strategy to count IDs and get the next
available ID that can be assigned. We could change how IDs are formed and
incremented without modifying any of the code in the Managers themselves. The
code can be reused or the strategy class can be replaced with a different
strategy without changing anything in the managers

## Dependency Injection
`IdManager`, all managers

`IdManager`s are passed into the managers from `WordGameSystem` using dependency
injection

## Factory
`GameBuilderFactory`, `GameMatchFactory`, `TemplateEditorFactory`, `TemplateFactory`

The Factories construct the appropriate use-case class based on the provided
input. Each type of template has its own implementation of the `Template`,
`Game`, `GameMatch` objects, but the controllers all contain instances of the
superclass and treat them the same. The factory calls the correct constructors
for `QuizGame`, `HangmanGame`, `HangmanGameMatch` etc. and returns object
instances of the superclass which can be treated the same by the rest of the
program.
