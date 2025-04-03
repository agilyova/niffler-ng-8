package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.GhApiClient;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;

public class IssueExtension implements ExecutionCondition {
  GhApiClient client = new GhApiClient();

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {

    return AnnotationSupport.findAnnotation(
      context.getRequiredTestMethod(),
      DisabledByIssue.class
    ).or(() -> AnnotationSupport.findAnnotation(
        context.getRequiredTestClass(),
        DisabledByIssue.class,
        SearchOption.INCLUDE_ENCLOSING_CLASSES
      )
    ).map(
      byIssue -> "open".equals(client.issueState(byIssue.value()))
        ? ConditionEvaluationResult.disabled("Disabled by Issue " + byIssue.value())
        : ConditionEvaluationResult.enabled("Issue closed")
    ).orElse(
      ConditionEvaluationResult.enabled("Annotation @DisabledByIssue not found")
    );
  }
}
