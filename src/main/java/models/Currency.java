package models;

public class Currency {
    private int id;
    private String fullName;
    private String code;
    private String sign;

    public Currency(int id, String code, String fullName, String sign) {
        this.sign = sign;
        this.id = id;
        this.fullName = fullName;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCode() {
        return code;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", name='" + fullName + '\'' +
                ", code='" + code + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
