Contributing
============

Thank you for your interest in contributing to Core! We appreciate your 
effort, but to make sure that the inclusion of your patch is a smooth process, we
ask that you make note of the following guidelines.

## Workflow

* We use Git, with a typical [feature branch workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow)
* Trivial changes and emergency fixes can be merged straight to the master branch
* Any significant change requires a PR, and code review by at least one other developer.
  This applies indiscriminately to all developers. Everyone should have their
  code reviewed, and anyone can review anyone else's code.
* Once a change has been merged to master, it should be deployed ASAP so that
  problems can be found.
  Deploying several old changes at once just makes it harder to trace bugs to
  their source.
* Without automated tests, we rely heavily on user reports and Sentry alerts to
  discover regressions.
  Developers should be around for at least a few hours after their change is
  deployed, in case something breaks.

## Coding guidelines

* General
  * **Follow the [Oracle coding conventions](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf).**
    We can't stress this enough; if your code has notable issues, it may delay
    the process significantly.
  * **Use your judgment always.** Any rule can be broken with a good reason.
    Don't follow a rule without understanding its purpose.
  * Write code for efficiency above all else.
  * Make sure your code is very readable. Always think about how
    another developer would work with your code.
  * **Avoid repetitive code.** Factor out the repetition, if there is a
    reasonable way to do so.
* Formatting
  * **Wrap code to a 120 column limit.** We do this to make side by side diffs
    and other such tasks easier. Ignore this guideline if it makes the code
    too unreadable.
  * **Use only spaces for indentation.** Our indents are 4-spaces long, and tabs
    are unacceptable.
  * You can use concise formats in places where it helps readability (e.g.
    single-line getters).
* Documentation
  * **Write complete Javadocs.** Do so only for public methods, and make sure
    that your `@param` and `@return` fields are not just blank.
  * Try to write code that is obvious enough so that it doesn't need to be
    explained with comments.
  * In places where a reader might be confused or miss something important, use
    comments to fill them in.
  * Don't put redundant or obvious things in comments or javadocs.
  * Ensure your IDE is not inserting any generated comments.
  * **Don't tag classes with @author.** Some legacy classes may have this tag,
    but we are phasing it out.
* Nulls
  * Strongly prefer `java.util.Optional` over `null`, generally speaking.
  * Use empty collections as nil values for collections.
  * **Use `@Nullable` wherever nulls are allowed.** Place it before the type, if
    possible.
  * **Don't use `@Nonnull`.** Assume anything (in our own code) without
    `@Nullable` is never null.
  * Use `Preconditions.checkNotNull` or `Validate.notNull` on constructor
    arguments for manually created objects.
* Structure
  * **Design classes to do [one thing only](https://en.wikipedia.org/wiki/Single_responsibility_principle).**
    If a class provides multiple services, break them down into seperate public
    interfaces and keep the class private.
  * Use `final` fields and create immutable data types, wherever possible.
  * Don't create unnecessary getters and setters, only what is actually needed.
  * No mutable static fields, collections, or any other static state (there are
    a few exceptions, such as caches and `ThreadLocal`s).
  * Getters don't have to start with `get`, but they can if you think it's
    important.
* Exceptions
  * **Detect errors as early as possible.** Ideally at server startup. This
    applies to both user errors and internal assertions.
  * Only catch specific exceptions that you are expecting and can handle
    thoroughly. Don't hide exceptions that other handlers need to know about.
  * Avoid catching common exceptions like `IllegalArgumentException`, because
    it's hard to be certain where they come from. If you need to catch them,
    keep the code inside the `try` block as small as possible.
  * Don't catch all exceptions or try to handle internal errors for no
    particular reason. We have a nice framework for dealing with unhandled
    exceptions at the top-level.
* Concurrency
  * Never block the main thread on API calls, or any other I/O operation. Use
    `ListenableFuture`s and `FutureCallback`s to handle API results.
  * Don't use the Bukkit API, or any of our own APIs, from a background thread,
    unless it is explicitly allowed by the API.
    Use `MainThreadExecutor` or `SyncExecutor` to get back on the main thread
    from a background thread.
  * A Bungee server is entirely multi-threaded. Handlers for a specific event
    run in sequence, but seperate events and tasks can run concurrently.
    This is one of the reasons we avoid doing things in Bungee.

Checklist
---------

Ready to submit? Perform the checklist below:

1. Have all tabs been replaced into four spaces? Are indentations 4-space wide?
2. Have I written proper Javadocs for my public methods? Are the @param and
   @return fields actually filled out?
3. Have I `git rebase`d my pull request to the latest commit of the target
   branch?
4. Have I combined my commits into a reasonably small number (if not one)
   commit using `git rebase`?
5. Have I made my pull request too large? Pull requests should introduce
   small sets of changes at a time. Major changes should be discussed with
   the team prior to starting work.
6. Are my commit messages descriptive?

You should be aware of [`git rebase`](http://learn.github.com/p/rebasing.html).
It allows you to modify existing commit messages, and combine, break apart, or
adjust past changes.

Example
-------

This is **GOOD:**

```
if (var.func(param1, param2)) {
    // do things
}
```

This is **EXTREMELY BAD:**

```
if(var.func( param1, param2 ))
{
    // do things
}
```