package org.koreait.survey.diabetes.constants;

public enum SmokingHistory {
    NO_INFO(0), // 정보 없음
    CURRENT(1), // 현재 흡연중
    EVER(3),    // 항상 흡연중
    FORMER(4),  // 과거 흡연했지만 지금은 X
    NEVER(5),   // 흡연을 한적이 없음
    NOT_CURRENT(6); // 현재는 아니지만 흡연 예정

    private  final int num;

    SmokingHistory(int num){
        this.num = num;
    }

    public int getNum(){
        return num;
    }
}
