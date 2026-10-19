# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Builds the D-day label shown next to a task in the task list. A completed task shows `CLEAR`; an active task with no due date shows nothing (`""`); an active task with a due date shows `D-N` (due in N days), `D-Day` (due today) or `D+N` (overdue by N days), where N counts calendar days between `today` and `dueDate`.

**Pre-conditions**
- `today` is the current local date, passed in by the caller.
- `dueDate` is either `null` (the task has no due date) or a valid `LocalDate`.

**Post-conditions**
- Success cases
  - Completed task with a due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-25`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel(dueDate, today, isCompleted)` is called / Then it returns `"CLEAR"`.
  - Completed task without a due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`.
  - Active task without a due date returns `""` (no label). `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `""`.
  - Active task due in the future returns `"D-N"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-24`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-5"`.
  - Active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-Day"`.
  - Active task past its due date returns `"D+N"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-14`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+5"`.
- Error cases
  - None. Every combination of `dueDate`, `today` and `isCompleted` is valid input. `[Unit]`
- Edge cases
  - One day before the due date returns `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-20`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`.
  - One day after the due date returns `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-18`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`.
  - Completed task due today still returns `"CLEAR"` (completion wins over `D-Day`). `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`.
  - Due date across a month end counts calendar days. `[Unit]`
    - Tip: Given `dueDate = 2026-11-01`, `today = 2026-10-31`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`.
  - Due date across a year end counts calendar days. `[Unit]`
    - Tip: Given `dueDate = 2027-01-01`, `today = 2026-12-31`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`.
  - Due date across a leap day counts the leap day. `[Unit]`
    - Tip: Given `dueDate = 2028-03-01`, `today = 2028-02-28`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-2"`.
  - Overdue across a month end counts calendar days. `[Unit]`
    - Tip: Given `dueDate = 2026-09-30`, `today = 2026-10-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`.
  - Due one day short of one month later: N is the total number of days. `[Unit]`
    - Tip: Given `dueDate = 2026-11-18`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-30"`.
  - Due exactly one month later: N is the total number of days, not `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-11-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-31"`.
  - Due one day past one month later: N is the total number of days. `[Unit]`
    - Tip: Given `dueDate = 2026-11-20`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-32"`.
  - Overdue by one day short of one month: N is the total number of days. `[Unit]`
    - Tip: Given `dueDate = 2026-09-20`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+29"`.
  - Overdue by exactly one month: N is the total number of days, not `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-09-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+30"`.
  - Overdue by one day past one month: N is the total number of days. `[Unit]`
    - Tip: Given `dueDate = 2026-09-18`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+31"`.

**Undefined behavior** (optional)
- `today` other than the real current date is not rejected; the function trusts the caller, so passing a wrong `today` gives a label relative to that date.
