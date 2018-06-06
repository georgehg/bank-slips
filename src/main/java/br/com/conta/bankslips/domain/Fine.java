package br.com.conta.bankslips.domain;

import java.math.BigDecimal;

public class Fine {

    private final Integer elapsed;

    private final Penalty penalty;

    private Fine(Integer elapsed, Penalty penalty) {
        this.elapsed = elapsed;
        this.penalty = penalty;
    }

    public static Fine from(Integer elapsed) {
        return new Fine(elapsed, Penalty.getFine(elapsed));
    }

    public BigDecimal calculateFine(BigDecimal amount) {
        return amount.multiply(penalty.tax).multiply(BigDecimal.valueOf(elapsed, 0)).setScale(0);
    }

    private enum Penalty {
        UNTIL_10_DAYS(10, BigDecimal.valueOf(0.005)),
        DEFAULT(Integer.MAX_VALUE, BigDecimal.valueOf(0.01));

        private Integer elapsed;

        private BigDecimal tax;

        Penalty(Integer elapsed, BigDecimal tax) {
            this.elapsed = elapsed;
            this.tax = tax;
        }

        public static Penalty getFine(Integer elapsed) {
            for (Penalty testEnum : values()) {
                if (elapsed <= testEnum.elapsed) {
                    return testEnum;
                }
            }
            throw new IllegalArgumentException();
        }
    }

}
