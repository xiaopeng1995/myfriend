import java.util.Random;

/**
 * Created by xiaopeng on 2017/4/10.
 */
public class Test {
    private char[] getChar() {
        char[] passwordLit = new char[62];
        char fword = 'A';
        char mword = 'A';
        char bword = '0';
        for (int i = 0; i < 62; i++) {
            if (i < 26) {
                passwordLit[i] = fword;
                fword++;
            } else if (i < 52) {
                passwordLit[i] = mword;
                mword++;
            } else {
                passwordLit[i] = bword;
                bword++;
            }//方法的抽取，按功能
            //System.out.println(passwordLit[i]);
        }
        return passwordLit;
    }

    private String getRandom() {

        char[] r = getChar();
        Random rr = new Random();
        String pw="";
        for (int i = 0; i < 8; i++) {
            int num = rr.nextInt(62);
            pw += r[num];
        }
        return pw;
    }

    @org.junit.Test
    public void aa() {
        System.out.println(getRandom());
    }
}
