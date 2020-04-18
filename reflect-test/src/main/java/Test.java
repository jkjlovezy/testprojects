import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    private static String s = "java.math.BigInteger bi[] = { 123, 234, 345 }";
    private static Pattern p = Pattern.compile("^\\s*(\\S+)\\s*(\\w+)\\[\\].*\\{\\s*([^}]+)\\s*\\}");
    public static void main(String[] args) {
        Matcher m = p.matcher(s);
        System.out.println(m.groupCount());
        if(m.find()){
            System.out.println(m.group(0));
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }

    }
}
