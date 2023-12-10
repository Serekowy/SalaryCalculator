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

        System.out.println("Wypłata netto wynosi: " + salaryNet);
    }

    public static BigDecimal calcSalary(BigDecimal salary, Map<String, Double> taxes) {

        double pensionContr = taxes.get("pensionContr");
        double disabilityContr = taxes.get("disabilityContr");
        double sicknessContr = taxes.get("sicknessContr");
        double healthInsurance = taxes.get("healthInsurance");
        double incomeTax = taxes.get("incomeTax");
        double incomeTax1 = taxes.get("incomeTax1");
        double incomeTax2 = taxes.get("incomeTax2");

        BigDecimal pensionAmount = calcPercent(salary, pensionContr);
        BigDecimal disabilityAmount = calcPercent(salary, disabilityContr);
        BigDecimal sicknessAmount = calcPercent(salary, sicknessContr);
        BigDecimal socialInsAmount = BigDecimal.valueOf(0);
        BigDecimal healthInsBase;
        BigDecimal healthInsAmount;
        BigDecimal incomeCost;
        BigDecimal tax;
        BigDecimal salaryNet;

        MathContext mc = new MathContext(salary.precision());

        socialInsAmount = socialInsAmount.add(pensionAmount).add(disabilityAmount).add(sicknessAmount);
        healthInsBase = salary.subtract(socialInsAmount);
        healthInsAmount = calcPercent(healthInsBase, healthInsurance);
        incomeCost = healthInsBase.subtract(BigDecimal.valueOf(incomeTax));
        incomeCost = incomeCost.round(mc);
        tax = incomeCost.multiply(BigDecimal.valueOf(incomeTax1)).divide(BigDecimal.valueOf(100), 0,RoundingMode.HALF_DOWN);
        tax = tax.subtract(BigDecimal.valueOf(incomeTax2));
        salaryNet = salary.subtract(socialInsAmount.add(healthInsAmount).add(tax));

//        Debug
//        System.out.println(socialInsAmount + " " + " " + healthInsBase + " " + healthInsAmount + " " + incomeCost + " " + tax + " " + salaryNet);

        return salaryNet;
    }

    public static BigDecimal calcPercent (BigDecimal mainNum, double percent) {
        return mainNum.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}