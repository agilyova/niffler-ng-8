package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.time.Month;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Calendar {
  private final SelenideElement self = $(".MuiDateCalendar-root");
  private final SelenideElement nextMonthButton = self.$("button[aria-label='Next month']");
  private final SelenideElement previousMonthButton = self.$("button[aria-label='Previous month']");
  private final SelenideElement yearSelectButton = self.$(".MuiPickersCalendarHeader-switchViewButton");
  private final SelenideElement yearAndMonthLabel = self.$(".MuiPickersCalendarHeader-label");
  private final ElementsCollection yearsList = self.$$(".MuiPickersYear-root");
  private final ElementsCollection days = self.$$(".MuiPickersDay-root");

  public void selectDateInCalendar(LocalDate date) {
    int year = date.getYear();
    Month month = date.getMonth();
    int day = date.getDayOfMonth();

    selectYear(year);
    selectMonth(month);
    selectDay(day);
  }

  private void selectYear(int year) {
    if (getSelectedYear() == year) return;
    yearSelectButton.click();
    yearsList.findBy(text(String.valueOf(year))).scrollTo().click();
  }

  private void selectMonth(Month month) {
    Month selectedMonth = Month.valueOf(getSelectedMonth());
    while (!month.equals(selectedMonth)) {
      if (month.compareTo(selectedMonth) < 0) {
        previousMonthButton.click();
        selectedMonth = Month.valueOf(getSelectedMonth());
      }
      if (month.compareTo(selectedMonth) > 0) {
        nextMonthButton.click();
        selectedMonth = Month.valueOf(getSelectedMonth());
      }
    }
  }

  private void selectDay(int day) {
    days.findBy(text(String.valueOf(day))).click();
  }

  private String getSelectedMonth() {
    return yearAndMonthLabel.should(appear).getText().split(" ")[0].toUpperCase();
  }

  private int getSelectedYear() {
    return Integer.valueOf(yearAndMonthLabel.should(appear).getText().split(" ")[1]);
  }
}
