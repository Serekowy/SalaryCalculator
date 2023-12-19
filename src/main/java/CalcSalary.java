import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class CalcSalary {

    public static void main(String[] args) {
        BigDecimal salaryGross = new BigDecimal(args[0]);
        Map<String, Double> taxes = new HashMap<>();

        taxes.put("pensionContr", Double.valueOf(args[1]));
        taxes.put("disabilityContr", Double.valueOf(args[2]));
        taxes.put("sicknessContr", Double.valueOf(args[3]));
        taxes.put("healthInsurance", Double.valueOf(args[4]));
        taxes.put("incomeTax", Double.valueOf(args[5]));
        taxes.put("incomeTax1", Double.valueOf(args[6]));
        taxes.put("incomeTax2", Double.valueOf(args[7]));

        BigDecimal salaryNet = calcSalary(salaryGross, taxes);

        System.out.println("Wypłata netto wynosi: " + salaryNet + " zł");
    }

    public static BigDecimal calcSalary(BigDecimal salary, Map<String, Double> taxes) {
        double incomeTax = taxes.get(INCOME1);

        MathContext mc = new MathContext(salary.precision());

        BigDecimal healthInsBase = calcInsurance(salary, taxes, "base");
        BigDecimal socialInsAmount = calcInsurance(salary, taxes, "insurance");
        BigDecimal healthInsAmount = calcInsurance(salary, taxes, "");
        BigDecimal incomeCost = calcIncome(incomeTax, healthInsBase).round(mc);
        BigDecimal tax = calcTax(incomeCost, taxes);

//        Debug
//        System.out.println(socialInsAmount + " " + " " + healthInsBase + " " + healthInsAmount + " " + incomeCost + " " + tax + " " + salaryNet);

        return salaryNet;
    }

    public static BigDecimal calcPercent(BigDecimal mainNum, double percent) {
        return mainNum.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
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

    public static BigDecimal calcInsurance(BigDecimal salary, Map<String, Double> taxes, String whatReturn) {
        double healthInsurance = taxes.get(HEALTH);

        BigDecimal pensionAmount = calcPercent(salary, taxes.get(PENSION));
        BigDecimal disabilityAmount = calcPercent(salary, taxes.get(DISABILITY));
        BigDecimal sicknessAmount = calcPercent(salary, taxes.get(SICKNESS));

        BigDecimal socialInsAmount = BigDecimal.valueOf(0);
        BigDecimal healthInsAmount;
        BigDecimal healthInsBase;

        socialInsAmount = socialInsAmount.add(pensionAmount).add(disabilityAmount).add(sicknessAmount);
        healthInsBase = salary.subtract(socialInsAmount);
        healthInsAmount = calcPercent(healthInsBase, healthInsurance);

        return switch (whatReturn) {
            case "base" -> healthInsBase;
            case "insurance" -> socialInsAmount;
            default -> healthInsAmount;
        };
    }

    public static BigDecimal calcIncome(double incomeTax, BigDecimal healthInsBase) {
        BigDecimal incomeCost;

        incomeCost = healthInsBase.subtract(BigDecimal.valueOf(incomeTax));

        return incomeCost;
    }

    public static BigDecimal calcTax(BigDecimal incomeCost, Map<String, Double> taxes) {
        BigDecimal tax;

        double incomeTax1 = taxes.get(INCOME2);
        double incomeTax2 = taxes.get(INCOME3);

        tax = incomeCost.multiply(BigDecimal.valueOf(incomeTax1)).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
        tax = tax.subtract(BigDecimal.valueOf(incomeTax2));

        return tax;
    }
}