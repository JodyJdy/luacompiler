import com.jdy.lua.lex.Lex;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lex.Token;
import com.jdy.lua.lex.TokenEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws Exception {
        func3();
    }
    public static void func1() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(new File("src/test/A.TXT")));
        while(scanner.hasNextLine()){
            String str = scanner.nextLine();
            String[] strs = str.split(" +|\t+");
            System.out.println("public static int "+ strs[1] +" = " + strs[2] +';' );
        }
    }

    public static void func2(){
        Formatter formatter = new Formatter();
        {}
        formatter.format("sldfjsd %d %c",1,'c');
        int i;
        System.out.println(formatter.toString());
    }
    public static void func3() throws FileNotFoundException {
        LexState lexState = new LexState();
        lexState.setT(new Token());
        lexState.setReader(new FileInputStream(new File("src/test/b.lua")));
        Lex.next(lexState);
        while(lexState.getCurrent() != Lex.EOZ){
            TokenEnum token = Lex.llex(lexState,lexState.getT());
            System.out.println(token);
        }

        System.out.println();
    }
}
