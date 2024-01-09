import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class CalcSalary {
    private static final String PENSION = "pensionContr";
    private static final String DISABILITY = "disabilityContr";
    private static final String SICKNESS = "sicknessContr";
    private static final String HEALTH = "healthInsurance";
    private static final String INCOME1 = "incomeTax";
    private static final String INCOME2 = "incomeTax1";
    private static final String INCOME3 = "incomeTax2";
    private static final int PERCENT_DIVIDER = 100;

    public static void main(String[] args) {
        BigDecimal salaryGross = new BigDecimal(args[0]);

        Map<String, Double> taxes = new HashMap<>(createTaxesMap(args));

        BigDecimal salaryNet = calcSalary(salaryGross, taxes);

        System.out.println("Wypłata netto wynosi: " + salaryNet + " zł");
    }

    public static BigDecimal calcSalary(BigDecimal salary, Map<String, Double> taxes) {
        double incomeTax = taxes.get(INCOME1);

        MathContext mc = new MathContext(salary.precision());

        BigDecimal socialInsAmount = calcSocialIns(salary,  taxes);
        BigDecimal healthInsBase = calcInsBase(salary, socialInsAmount);
        BigDecimal healthInsAmount = calcHealthIns(salary, taxes.get(HEALTH), socialInsAmount);
        BigDecimal incomeCost = calcIncome(incomeTax, healthInsBase).round(mc);
        BigDecimal tax = calcTax(incomeCost, taxes);

        return salary.subtract(socialInsAmount.add(healthInsAmount).add(tax));
    }

    public static BigDecimal calcPercent(BigDecimal mainNum, double percent) {
        int decimalPlaces = 2;
        return mainNum.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(PERCENT_DIVIDER), decimalPlaces, RoundingMode.HALF_UP);
    }

    public static Map<String, Double> createTaxesMap(String[] args) {
        Map<String, Double> taxes = new HashMap<>();

        taxes.put(PENSION, Double.valueOf(args[1]));
        taxes.put(DISABILITY, Double.valueOf(args[2]));
        taxes.put(SICKNESS, Double.valueOf(args[3]));
        taxes.put(HEALTH, Double.valueOf(args[4]));
        taxes.put(INCOME1, Double.valueOf(args[5]));
        taxes.put(INCOME2, Double.valueOf(args[6]));
        taxes.put(INCOME3, Double.valueOf(args[7]));

        return taxes;
    }

    public static BigDecimal calcHealthIns(BigDecimal salary, double healthInsurance, BigDecimal insuranceAmount) {
        return calcPercent(calcInsBase(salary, insuranceAmount), healthInsurance);
    }

    public static BigDecimal calcInsBase(BigDecimal salary, BigDecimal socialInsAmount) {
        return salary.subtract(socialInsAmount);
    }

    public static BigDecimal calcSocialIns(BigDecimal salary, Map<String, Double> taxes) {
        BigDecimal pensionAmount = calcPercent(salary, taxes.get(PENSION));
        BigDecimal disabilityAmount = calcPercent(salary, taxes.get(DISABILITY));
        BigDecimal sicknessAmount = calcPercent(salary, taxes.get(SICKNESS));

        BigDecimal socialInsAmount = BigDecimal.valueOf(0);

        return socialInsAmount.add(pensionAmount).add(disabilityAmount).add(sicknessAmount);

    }

    public static BigDecimal calcIncome(double incomeTax, BigDecimal healthInsBase) {
        return healthInsBase.subtract(BigDecimal.valueOf(incomeTax));
    }

    public static BigDecimal calcTax(BigDecimal incomeCost, Map<String, Double> taxes) {
        BigDecimal tax;
        int decimalPlaces = 0;

        double incomeTax1 = taxes.get(INCOME2);
        double incomeTax2 = taxes.get(INCOME3);

        tax = incomeCost.multiply(BigDecimal.valueOf(incomeTax1)).divide(BigDecimal.valueOf(PERCENT_DIVIDER), decimalPlaces, RoundingMode.HALF_DOWN);
        tax = tax.subtract(BigDecimal.valueOf(incomeTax2));

        return tax;
    }
}