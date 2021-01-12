package pt.fcul.lasige.sonaar.util;

public class Counter {
    int number;

    public Counter(int number) {
        this.number = number;
    }

    public void inc(){
        number++;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
