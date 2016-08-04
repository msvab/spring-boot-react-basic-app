package net.svab.project.domain.validation;

import net.svab.project.domain.Price;
import net.svab.project.domain.validation.SinglePricePerCurrencyCheck.SinglePricePerCurrencyValidator;

import javax.money.CurrencyUnit;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SinglePricePerCurrencyValidator.class)
@Documented
public @interface SinglePricePerCurrencyCheck {

    String message() default "error.product.prices.single-per-currency";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };

    final class SinglePricePerCurrencyValidator implements ConstraintValidator<SinglePricePerCurrencyCheck, List<Price>> {

        @Override public void initialize(SinglePricePerCurrencyCheck constraintAnnotation) { }

        @Override public boolean isValid(List<Price> prices, ConstraintValidatorContext context) {
            Map<CurrencyUnit, List<Price>> priceMap = prices.stream().collect(Collectors.groupingBy(Price::getCurrency));
            return priceMap.values().stream().noneMatch(pricesPerCurrency -> pricesPerCurrency.size() > 1);
        }
    }
}
