package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Integration tests for [setDueDateLabel], one per Tip in specs/TasksListBindings.spec.md.
 * The adapter is called directly on a real [TextView], on the main thread as data binding does.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var textView: TextView

    @Before
    fun createTextView() {
        instrumentation.runOnMainSync {
            textView = TextView(instrumentation.targetContext)
        }
    }

    private fun bind(task: Task?) {
        instrumentation.runOnMainSync { setDueDateLabel(textView, task) }
    }

    // Success cases

    @Test
    fun setDueDateLabel_activeDueInThreeDays_showsDMinus3() {
        // GIVEN - An active task due in 3 days
        val task = Task("Title", "Desc", false, "id1", LocalDate.now().plusDays(3))

        // WHEN - The label is bound
        bind(task)

        // THEN - D-3 is shown
        assertEquals("D-3", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeDueToday_showsDDay() {
        // GIVEN - An active task due today
        val task = Task("Title", "Desc", false, "id1", LocalDate.now())

        // WHEN - The label is bound
        bind(task)

        // THEN - D-Day is shown
        assertEquals("D-Day", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeOverdueByTwoDays_showsDPlus2() {
        // GIVEN - An active task overdue by 2 days
        val task = Task("Title", "Desc", false, "id1", LocalDate.now().minusDays(2))

        // WHEN - The label is bound
        bind(task)

        // THEN - D+2 is shown
        assertEquals("D+2", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_completedWithDueDate_showsClear() {
        // GIVEN - A completed task with a due date
        val task = Task("Title", "Desc", true, "id1", LocalDate.now().plusDays(3))

        // WHEN - The label is bound
        bind(task)

        // THEN - CLEAR is shown
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_completedWithoutDueDate_showsClear() {
        // GIVEN - A completed task with no due date
        val task = Task("Title", "Desc", true, "id1", null)

        // WHEN - The label is bound
        bind(task)

        // THEN - CLEAR is shown
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeWithoutDueDate_showsNoLabel() {
        // GIVEN - An active task with no due date
        val task = Task("Title", "Desc", false, "id1", null)

        // WHEN - The label is bound
        bind(task)

        // THEN - No label is shown
        assertEquals("", textView.text.toString())
    }

    @Test
    fun setDueDateLabel_nullTask_showsNoLabel() {
        // GIVEN - No task bound yet

        // WHEN - The label is bound with a null task
        bind(null)

        // THEN - No label is shown
        assertEquals("", textView.text.toString())
    }

    // Edge cases

    @Test
    fun setDueDateLabel_activeDueTomorrow_showsDMinus1() {
        // GIVEN - An active task due tomorrow
        val task = Task("Title", "Desc", false, "id1", LocalDate.now().plusDays(1))

        // WHEN - The label is bound
        bind(task)

        // THEN - D-1 is shown
        assertEquals("D-1", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeDueYesterday_showsDPlus1() {
        // GIVEN - An active task that was due yesterday
        val task = Task("Title", "Desc", false, "id1", LocalDate.now().minusDays(1))

        // WHEN - The label is bound
        bind(task)

        // THEN - D+1 is shown
        assertEquals("D+1", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_checkTaskWithDueDate_changesDMinus3ToClear() {
        // GIVEN - A row bound to an active task due in 3 days
        val task = Task("Title", "Desc", false, "id1", LocalDate.now().plusDays(3))
        bind(task)

        // WHEN - The task is checked and the same row is re-bound
        bind(task.copy(isCompleted = true))

        // THEN - The label changes to CLEAR
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_uncheckTaskWithDueDate_changesClearToDMinus3() {
        // GIVEN - A row bound to a completed task due in 3 days
        val task = Task("Title", "Desc", true, "id1", LocalDate.now().plusDays(3))
        bind(task)

        // WHEN - The task is unchecked and the same row is re-bound
        bind(task.copy(isCompleted = false))

        // THEN - The label changes back to D-3
        assertEquals("D-3", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_checkTaskWithoutDueDate_changesNoLabelToClear() {
        // GIVEN - A row bound to an active task with no due date
        bind(Task("Title", "Desc", false, "id1", null))

        // WHEN - The task is checked and the same row is re-bound
        bind(Task("Title", "Desc", true, "id1", null))

        // THEN - The label changes to CLEAR
        assertEquals("CLEAR", textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    @Test
    fun setDueDateLabel_uncheckTaskWithoutDueDate_removesClear() {
        // GIVEN - A row bound to a completed task with no due date
        bind(Task("Title", "Desc", true, "id1", null))

        // WHEN - The task is unchecked and the same row is re-bound
        bind(Task("Title", "Desc", false, "id1", null))

        // THEN - The CLEAR label is removed and no label is shown
        assertEquals("", textView.text.toString())
    }
}
