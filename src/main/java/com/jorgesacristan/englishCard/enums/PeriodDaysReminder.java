package com.jorgesacristan.englishCard.enums;

public enum PeriodDaysReminder {
    DAY1(1),DAY3(2),DAY7(3),DAY14(4),DAY30(5),DAY90(6);

    Integer days;

    PeriodDaysReminder(Integer days) {
        this.days = days;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
