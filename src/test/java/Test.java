import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lex.Lex;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lex.Token;
import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lparser2.FunctionInfo;
import com.jdy.lua.lparser2.expr.SubExpr;
import com.jdy.lua.lparser2.statement.BlockStatement;
import com.jdy.lua.lparser2.statement.LocalStatement;
import com.jdy.lua.lstate.LuaState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

import static com.jdy.lua.lparser.LParser.luaY_Parser;
import static com.jdy.lua.lparser2.LParser.block;

public class Test {
    public static void main(String[] args) throws Exception {
        func5();
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

  public static void func5() throws FileNotFoundException{
        LexState lexState = new LexState();
        lexState.setCurrTk(new Token());
        lexState.setEnvn("_ENV");
        lexState.setL(new LuaState());
        lexState.setReader(new FileInputStream(new File("src/test/b.lua")));
        Lex.luaX_Next(lexState);
        BlockStatement b = block(lexState);
      FunctionInfo fi = new FunctionInfo();
      InstructionGenerator generator = new InstructionGenerator(fi);
      System.out.println();
//      SubExpr expr = (SubExpr)((LocalStatement)b.getStatList().getStatements().get(0)).getExprList().getExprList().get(0);
//      expr.generate(generator,0,0);
//      for(Instruction c : fi.getInstructions()){
//          System.out.println(c);
//      }
//      System.out.println();

    }
}
