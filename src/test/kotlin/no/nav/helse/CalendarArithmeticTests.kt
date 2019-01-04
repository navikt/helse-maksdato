package no.nav.helse

import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import java.time.*

object CalendarArithmeticTest: Spek({

   val weekdayTestcases = mapOf(
      Pair(LocalDate.of(2015, 11, 26), 2) to LocalDate.of(2015, 11, 30),
      Pair(LocalDate.of(2019, 1, 4), 3) to LocalDate.of(2019, 1, 9)
   )

   describe("general date arithmetic") {

      given("a specific day") {

         on("mon - fri") {
            it("is not weekend") {
               (7..11)
               .map { LocalDate.of(2019, 1, it) }
               .forEach { isWeekend(it).shouldBeFalse() }
            }
         }

         on("saturday and sunday") {
            it("is the weekend") {
               val saturday = LocalDate.of(2019, 1, 5)
               val sunday = LocalDate.of(2019, 1, 6)
               isWeekend(saturday).shouldBeTrue()
               isWeekend(sunday).shouldBeTrue()
            }
         }

         on ("any day of the week") {
            it("is calculates a date x amount of weekdays into the future") {
               weekdayTestcases.forEach {
                  val (start, nrOfWeekdays) = it.key
                  nWeekDaysFrom(nrOfWeekdays, start) `should equal` it.value
               }
            }
         }

      }

   }

})
