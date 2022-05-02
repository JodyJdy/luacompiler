import com.jdy.lua.lex.Lex;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lex.Token;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

import static com.jdy.lua.lparser.LParser.luaY_Parser;

public class Test {
    public static void main(String[] args) throws Exception {
      func4();
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
        lexState.setCurrTk(new Token());
        lexState.setReader(new FileInputStream(new File("src/test/b.lua")));
        Lex.next(lexState);
        while(lexState.getCurrent() != Lex.EOZ){
            Lex.llex(lexState,true);
//            System.out.println(lexState.getCurrTk());
        }

        System.out.println();
    }
    public static void func4() throws FileNotFoundException {
        luaY_Parser(null,null,"hello",'a');
    }
}
