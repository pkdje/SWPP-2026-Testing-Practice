package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for [dueDateLabel], one per Tip in specs/DueDateUtils.spec.md.
 */
class DueDateUtilsTest {

    // Success cases

    @Test
    fun dueDateLabel_completedWithDueDate_returnsClear() {
        // GIVEN - A completed task due in the future
        val dueDate = LocalDate.of(2026, 10, 25)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is CLEAR
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_completedWithoutDueDate_returnsClear() {
        // GIVEN - A completed task with no due date
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(null, today, isCompleted = true)

        // THEN - The label is CLEAR
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmpty() {
        // GIVEN - An active task with no due date
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(null, today, isCompleted = false)

        // THEN - There is no label
        assertEquals("", label)
    }

    @Test
    fun dueDateLabel_activeDueInFiveDays_returnsDMinus5() {
        // GIVEN - An active task due in 5 days
        val dueDate = LocalDate.of(2026, 10, 24)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-5
        assertEquals("D-5", label)
    }

    @Test
    fun dueDateLabel_activeDueToday_returnsDDay() {
        // GIVEN - An active task due today
        val dueDate = LocalDate.of(2026, 10, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-Day
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_activeOverdueByFiveDays_returnsDPlus5() {
        // GIVEN - An active task overdue by 5 days
        val dueDate = LocalDate.of(2026, 10, 14)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+5
        assertEquals("D+5", label)
    }

    // Edge cases

    @Test
    fun dueDateLabel_activeDueTomorrow_returnsDMinus1() {
        // GIVEN - An active task due tomorrow
        val dueDate = LocalDate.of(2026, 10, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeDueYesterday_returnsDPlus1() {
        // GIVEN - An active task that was due yesterday
        val dueDate = LocalDate.of(2026, 10, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_completedDueToday_returnsClear() {
        // GIVEN - A completed task due today
        val dueDate = LocalDate.of(2026, 10, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - Completion wins over D-Day
        assertEquals("CLEAR", label)
    }

    @Test
    fun dueDateLabel_activeDueAcrossMonthEnd_returnsDMinus1() {
        // GIVEN - An active task due the day after a month end
        val dueDate = LocalDate.of(2026, 11, 1)
        val today = LocalDate.of(2026, 10, 31)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeDueAcrossYearEnd_returnsDMinus1() {
        // GIVEN - An active task due the day after a year end
        val dueDate = LocalDate.of(2027, 1, 1)
        val today = LocalDate.of(2026, 12, 31)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", label)
    }

    @Test
    fun dueDateLabel_activeDueAcrossLeapDay_returnsDMinus2() {
        // GIVEN - An active task due across 2028-02-29
        val dueDate = LocalDate.of(2028, 3, 1)
        val today = LocalDate.of(2028, 2, 28)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The leap day is counted
        assertEquals("D-2", label)
    }

    @Test
    fun dueDateLabel_activeOverdueAcrossMonthEnd_returnsDPlus1() {
        // GIVEN - An active task that was due on the last day of the previous month
        val dueDate = LocalDate.of(2026, 9, 30)
        val today = LocalDate.of(2026, 10, 1)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", label)
    }

    @Test
    fun dueDateLabel_activeDueOneDayShortOfOneMonth_returnsDMinus30() {
        // GIVEN - An active task due one day short of one month later
        val dueDate = LocalDate.of(2026, 11, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N is the total number of days
        assertEquals("D-30", label)
    }

    @Test
    fun dueDateLabel_activeDueExactlyOneMonthLater_returnsDMinus31() {
        // GIVEN - An active task due exactly one month later
        val dueDate = LocalDate.of(2026, 11, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N is the total number of days, not D-Day
        assertEquals("D-31", label)
    }

    @Test
    fun dueDateLabel_activeDueOneDayPastOneMonth_returnsDMinus32() {
        // GIVEN - An active task due one day past one month later
        val dueDate = LocalDate.of(2026, 11, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N is the total number of days
        assertEquals("D-32", label)
    }

    @Test
    fun dueDateLabel_activeOverdueOneDayShortOfOneMonth_returnsDPlus29() {
        // GIVEN - An active task overdue by one day short of one month
        val dueDate = LocalDate.of(2026, 9, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N is the total number of days
        assertEquals("D+29", label)
    }

    @Test
    fun dueDateLabel_activeOverdueExactlyOneMonth_returnsDPlus30() {
        // GIVEN - An active task overdue by exactly one month
        val dueDate = LocalDate.of(2026, 9, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N is the total number of days, not D-Day
        assertEquals("D+30", label)
    }

    @Test
    fun dueDateLabel_activeOverdueOneDayPastOneMonth_returnsDPlus31() {
        // GIVEN - An active task overdue by one day past one month
        val dueDate = LocalDate.of(2026, 9, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - The label is built
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - N is the total number of days
        assertEquals("D+31", label)
    }
}
