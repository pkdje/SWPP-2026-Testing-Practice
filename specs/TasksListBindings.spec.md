# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Shows the task's D-day label (`D-N`, `D-Day`, `D+N` or `CLEAR`, computed against today's date) in the task row's label `TextView`, and shows no label when the task has nothing to show. "No label" means the `TextView`'s text is `""`, so nothing is visible to the user. The label is updated whenever the row is re-bound, for example after the task is checked or unchecked.

**Pre-conditions**
- Called on the main thread with a `TextView` from the task row (`R.id.due_date_text`), as data binding does.
- "Today" is the device's current date (`LocalDate.now()`), so every due date below is written relative to `today = LocalDate.now()`.

**Post-conditions**
- Success cases
  - Active task due in the future: the label shows `"D-N"` and is visible. `[Integration]`
    - Tip: Given a `TextView` and `Task("Title", "Desc", false, "id1", today.plusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == "D-3"` and `textView.visibility == View.VISIBLE`.
  - Active task due today: the label shows `"D-Day"` and is visible. `[Integration]`
    - Tip: Given a `TextView` and `Task("Title", "Desc", false, "id1", today)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == "D-Day"` and `textView.visibility == View.VISIBLE`.
  - Active task past its due date: the label shows `"D+N"` and is visible. `[Integration]`
    - Tip: Given a `TextView` and `Task("Title", "Desc", false, "id1", today.minusDays(2))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == "D+2"` and `textView.visibility == View.VISIBLE`.
  - Completed task with a due date: the label shows `"CLEAR"` and is visible. `[Integration]`
    - Tip: Given a `TextView` and `Task("Title", "Desc", true, "id1", today.plusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == "CLEAR"` and `textView.visibility == View.VISIBLE`.
  - Completed task without a due date: the label shows `"CLEAR"` and is visible. `[Integration]`
    - Tip: Given a `TextView` and `Task("Title", "Desc", true, "id1", null)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == "CLEAR"` and `textView.visibility == View.VISIBLE`.
  - Active task without a due date: no label is shown (empty text). `[Integration]`
    - Tip: Given a new `TextView` and `Task("Title", "Desc", false, "id1", null)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == ""`.
  - `task` is `null` (binding not ready yet): no label is shown (empty text). `[Integration]`
    - Tip: Given a new `TextView` and `task = null` / When `setDueDateLabel(textView, null)` is called / Then `textView.text == ""`.
- Error cases
  - None. A `null` task is valid input and is handled as "no label". `[Integration]`
- Edge cases
  - Due tomorrow (one day before the `D-Day` boundary): the label shows `"D-1"`. `[Integration]`
    - Tip: Given a `TextView` and `Task("Title", "Desc", false, "id1", today.plusDays(1))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == "D-1"` and `textView.visibility == View.VISIBLE`.
  - Due yesterday (one day after the `D-Day` boundary): the label shows `"D+1"`. `[Integration]`
    - Tip: Given a `TextView` and `Task("Title", "Desc", false, "id1", today.minusDays(1))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text == "D+1"` and `textView.visibility == View.VISIBLE`.
  - Checking an active task with a due date re-binds the same row: the label changes from `"D-3"` to `"CLEAR"`. `[Integration]`
    - Tip: Given a `TextView` already bound to `Task("Title", "Desc", false, "id1", today.plusDays(3))` (shows `"D-3"`) / When `setDueDateLabel(textView, sameTaskWith isCompleted = true)` is called on the same `TextView` / Then `textView.text == "CLEAR"` and `textView.visibility == View.VISIBLE`.
  - Unchecking a completed task with a due date re-binds the same row: the label changes from `"CLEAR"` back to `"D-3"`. `[Integration]`
    - Tip: Given a `TextView` already bound to `Task("Title", "Desc", true, "id1", today.plusDays(3))` (shows `"CLEAR"`) / When `setDueDateLabel(textView, sameTaskWith isCompleted = false)` is called on the same `TextView` / Then `textView.text == "D-3"` and `textView.visibility == View.VISIBLE`.
  - Checking an active task without a due date re-binds the same row: the label changes from no label to `"CLEAR"`. `[Integration]`
    - Tip: Given a `TextView` already bound to `Task("Title", "Desc", false, "id1", null)` (shows no label, text `""`) / When `setDueDateLabel(textView, Task("Title", "Desc", true, "id1", null))` is called on the same `TextView` / Then `textView.text == "CLEAR"` and `textView.visibility == View.VISIBLE`.
  - Unchecking a completed task without a due date re-binds the same row: the `"CLEAR"` label is removed and no label is shown. `[Integration]`
    - Tip: Given a `TextView` already bound to `Task("Title", "Desc", true, "id1", null)` (shows `"CLEAR"`) / When `setDueDateLabel(textView, Task("Title", "Desc", false, "id1", null))` is called on the same `TextView` / Then `textView.text == ""`.

**Undefined behavior** (optional)
- A test that runs across midnight may see `today` change between building the task and the call; tests should build the due date from `LocalDate.now()` right before the call.
