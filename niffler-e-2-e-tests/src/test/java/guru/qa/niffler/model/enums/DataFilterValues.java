package guru.qa.niffler.model.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DataFilterValues {
  TODAY("Today"), WEEK("last week"), MONTH("Last month");
  public final String text;
}
