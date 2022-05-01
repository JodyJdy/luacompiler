package com.jdy.lua.lparser;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes.UnOpr;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lex.TokenEnum;
import com.jdy.lua.lobjects.Closure;
import com.jdy.lua.lobjects.LocalVar;
import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lobjects.UpvalDesc;
import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lopcodes.Instructions;
import com.jdy.lua.lopcodes.OpCode;
import com.jdy.lua.lstate.LuaState;
import com.jdy.lua.lstring.LString;

import java.util.List;

import static com.jdy.lua.lcodes.BinOpr.*;
import static com.jdy.lua.lcodes.LCodes.*;
import static com.jdy.lua.lex.Lex.*;
import static com.jdy.lua.lex.TokenEnum.*;
import static com.jdy.lua.lopcodes.OpCode.*;
import static com.jdy.lua.lparser.ExpKind.*;
import static com.jdy.lua.lparser.ParserConstants.*;
@SuppressWarnings("all")
public class LParser {

    /**
     * 检查token 是否是c，如果是 读取下一个token
     * @param ls
     * @param c
     * @return
     */
    static boolean testNext (LexState ls, TokenEnum c) {
        if (ls.getCurTokenEnum() == c) {
            luaX_Next(ls);
            return true;
        }
        return false;
    }

    /**
     * 检查 token 是不是 c，如果不是报错
     * @param ls
     * @param c
     */
    static void check (LexState ls,TokenEnum c) {
        if (ls.getCurTokenEnum() != c)
            System.err.println("expecd:"+luaXToken2Str(ls,c));;
    }

    /**
     * 期望是 waht
     * line 是 where
     * @param ls
     * @param what
     * @param who
     * @param where
     */
    public static void checkMatch(LexState ls, TokenEnum what, TokenEnum who, int where){
        if(!testNext(ls,what)){
            System.out.println("错误");
        }
    }

    /**
     * 获取一个 TK_NAME
     */
    public static String strCheckName(LexState ls){
        check(ls,NAME);
        String str = ls.getT().getS();
        luaX_Next(ls);
        return str;
    }
    /**
     * 初始化 exp
     */
    public static void initExp(ExpDesc e,ExpKind k,int i){
        e.setK(k);
        e.setInfo(i);
        e.setT(NO_JUMP);
        e.setF(NO_JUMP);
    }
    /**
     * 初始化 exp，字符串作为内容
     */
    public static void codeStringExp(ExpDesc e, String  s){
        e.setF(NO_JUMP);
        e.setT(NO_JUMP);
        e.setK(VKSTR);
        e.setStrval(s);
    }
    /**
     * 初始化exp Name 作为内容
     */
    public static void codeNameExp(LexState ls,ExpDesc e){
        codeStringExp(e,strCheckName(ls));
    }
    /**
     * 注册一个本地变量到 Proto上面
     */
    public static int regsisterLocalVar(LexState ls,FuncState fs,String varName){
        Proto proto = fs.getProto();
        //注册前，本地变量的数量
        int oldSize = proto.getLocaVarSize();
        LocalVar localVar = new LocalVar();
        localVar.setName(varName);
        localVar.setStartpc(fs.pc);
        proto.addLocalVar(localVar);
        return fs.ndebugvars++;
    }
    /**
     * 创建一个新的 本地 变量 返回在函数中的索引
     */
    public static int newLocalVar(LexState ls,String  name){
        LuaState l = ls.getL();
        FuncState fs = ls.getFs();
        DynData dyd = ls.getDyd();
        Vardesc vardesc = new Vardesc();
        vardesc.setKind(VDKREG);
        vardesc.setName(name);
        dyd.addVarDesc(vardesc);
        return dyd.getActiveLocVarSize() - 1 - fs.firstlocal;
    }
    /**
     * 获取 Vardesc
     */
    public static Vardesc getLocalVarDesc(FuncState fs,int vidx){
        LexState ls = fs.getLexState();
        DynData dynData = ls.getDyd();
        return dynData.getVarDesc(fs.firstlocal + vidx);
    }
    /**
     *返回的值是 寄存器栈中变量的数量。
     *  lua是这样去分配栈的。 假设有以下代码
     *
     *  local a = 1;  栈中内容: [a]
     *  local b = 1;  栈中内容: [a,b]
     *  a=a+b*2;      栈中内容: [a,b, 运算的临时结果]
     *  local c =1;   栈中内容: [a,b,c]
     *
     *  每当一个表达式执行时，会占用寄存器， 表达式执行完后，寄存器就会释放掉，接着有变量声明时，存放变量。
     *  这样就保证了， 变量/常量 等是连续的存放在栈里面的。 当下面的代码，从尾部遍历到第一个 存放寄存器里面的
     *  变量/常量 时，就可用结束了， 下标+1 就是 变量的数量了
     *
     *
     *  因此 freeReg 会去判断下，释放的是不是变量所在的寄存器，如果是不做操作
     */
    public static int regLevel(FuncState fs,int nvar){
        while(nvar-->0){
            Vardesc vardesc = getLocalVarDesc(fs,nvar);
            //如果不是编译时常量，说明是存放在寄存器里面的
            if(vardesc.kind != RDKCTC){
                //返回寄存器索引加1
                return vardesc.ridx + 1;
            }
        }
        return 0;
    }

    /**
     *  返回 寄存器栈中 变量的数量， 同时也是存放新变量的寄存器下标
     */
    public static int luaY_nVarsStack(FuncState fs){
        return regLevel(fs,fs.nactvar);
    }
    /**
     * 获取  vidx下标的变量的  debug信息
     */
    public static LocalVar localDebugInfo(FuncState fs,int vidx){
        Vardesc vd = getLocalVarDesc(fs,vidx);
        if(vd.getKind() == RDKCTC){
            //无debug信息
            return null;
        }
        int idx = vd.getRidx();
        return fs.proto.getLocalVar(idx);
    }
    /**
     * 创建一个表示变量 vidx 的表达式
     */
    public static void initVar(FuncState fs,ExpDesc e,int vidx){
        e.setF(NO_JUMP);
        e.setT(NO_JUMP);
        e.setK(VLOCAL);
        e.setVidx(vidx);
        //设置寄存器索引
        e.setRidx(getLocalVarDesc(fs,vidx).ridx);
    }
    /**
     * 如果变量是 只读的，尝试赋值，会抛error
     */
    public static void checkReadOnly(LexState ls, ExpDesc e){
        FuncState fs = ls.getFs();
        String varName = null;
        switch (e.getK()){
            case VCONST:
                DynData dyd = ls.getDyd();
                varName=dyd.getVarDesc(e.getInfo()).getName();break;
            case VLOCAL:
                Vardesc vardesc = getLocalVarDesc(fs,e.getVidx());
                //不是一个 regular 变量
                if(vardesc.getKind() != VDKREG){
                    varName =vardesc.getName();
                }
                break;
            default:
                break;
        }

        if(varName != null){
            System.err.println("尝试赋值给常量");
        }
    }

    /**
     * 开启 前 'nvars' 个创建的变量的 作用域
     *  创建好变量后， 会添加到 DynData上面， 但此时还未注册到Proto上面去，也没有指定寄存器位置
     */

    public static void adjustLocalVars(LexState ls,int nvars){
        FuncState fs = ls.getFs();
        //变量数目
        int regLevel = luaY_nVarsStack(fs);
        int i;
        for(i=0;i<nvars;i++){
            int vidx = fs.nactvar++;
            //从 DynDdata里面取出变量描述
            Vardesc var = getLocalVarDesc(fs,vidx);
            //初始化寄存器索引和 Proto 中的VarDesc的索引。
            // 变量在 Proto中的索引 和 在 寄存器中的索引 不一定一致，因为Proto有多层.
            var.ridx = regLevel++;
            var.pidx = regsisterLocalVar(ls,fs,var.name);
        }
    }
    /**
     * 关闭  'toLevel' 级之后的变量的作用域
     *  将toLevel 变量的索引即可
     *  关闭 toLevel - > fs.nactvar 之间的变量
     */
    public static void removeVars(FuncState fs,int toLevel){
        DynData dynData = fs.getLexState().getDyd();
        // fs.nactvar - toLevel 代表 关闭的变量的数量
        dynData.removeNumVarDesc(fs.nactvar - toLevel);
        //打印debug信息，同时调整 nactvar的值
        while(fs.nactvar > toLevel){
            LocalVar var = localDebugInfo(fs,--fs.nactvar);
            if(var != null){
                var.setEndpc(fs.pc);
            }
        }
    }
    /**
     * 在函数 fs中 serarch UPValues ， 按照给定的名称 name
     */
    public static int searchUpValue(FuncState fs,String name){
        int i;
        List<UpvalDesc> vals = fs.getProto().getUpvalues();
        for(i=0;i<fs.nups;i++){
            if(vals.get(i).getName().equals(name)){
                return i;
            }
        }
        //没找到
        return -1;
    }
    /**
     * 新增一个 未初始化的upvalue
     */
    public static  UpvalDesc allocUpValue(FuncState fs){
        Proto proto =  fs.getProto();
        UpvalDesc desc = new UpvalDesc();
        proto.getUpvalues().add(desc);
        fs.nups++;
        return desc;
    }

    /**
     * 新建一个 UpValue，并设置初始值
     */
    public static int newUpValue(FuncState fs,String name,ExpDesc v){
        UpvalDesc desc = allocUpValue(fs);
        // 既然出现了 UpValue，说明函数一定不只一层
        FuncState prev = fs.getPrev();
        if(v.getK() == VLOCAL){
            desc.setInstack(true);
            //记录desc 在 outer 函数中寄存器地址
            desc.setIdx(v.getIdx());
            desc.setKind(getLocalVarDesc(prev,v.idx).getKind());
        } else{
            desc.setInstack(false);
            desc.setKind(prev.proto.getUpValDesc(v.info).getKind());
        }
        desc.setName(name);
        //返回UpValDesc的下标
        return fs.nups-1;
    }
    /**
     * 按照名称在函数 fs 查找一个  active local variable,如果找到了初始化表达式
     */
    public static int searchVar(FuncState fs,String  n,ExpDesc e){
        int i;
        for(i=fs.nactvar - 1;i >=0;i--){
            Vardesc v = getLocalVarDesc(fs,i);
            if(v.getName().equals(n)){
                //编译时常量
                if(v.getKind() == RDKCTC){
                    initExp(e,VCONST,fs.firstlocal + i);
                } else{
                    // real var
                    initVar(fs,e,i);
                }
            }
        }
        return -1;
    }

    /**
     * 标记给定 level的 变量 定义在的 block
     *  标记这些block，表示有upval
     */
    public static void markBlockUpval(FuncState fs,int level){
        BlockCnt cn = fs.getBlockCnt();
        while(cn.nactvar > level){
            cn = cn.previous;
        }
        cn.setUpval(true);
        fs.setNeedclose(true);
    }

    /**
     * 标记 当前 Block 有一个 to be closed 变量
     */
    public static void markToBeClosed(FuncState fs){
        BlockCnt cnt =fs.getBlockCnt();
        cnt.setUpval(true);
        cnt.setInsidetbc(true);
        fs.setNeedclose(true);

    }
    /**
     * 使用名称 n  查找变量； 如果是一个 upValue 添加到所有中间的函数。 如果是全局变量，设置
     * var 为 void类型作为标记
     *  考虑以下场景， val 在 fun1定义  func3 使用， 在func3中是一个 unval， 但是也应该添加到func中。
     *  fun1 {
     *      int val;
     *
     *      func2{
     *
     *          func3{
     *              print(val);
     *          }
     *      }
     *
     *  }
     */
    public static void singleVarAux(FuncState fs,String  n, ExpDesc e,int base){
        //如果fs == null,说明是全局变量
        if(fs == null){
            initExp(e,VVOID,0);
            return;
        }
        //在当前层级查询
        int v = searchVar(fs,n,e);
        //找到
        if(v >= 0){
            if(v == VLOCAL.kind && base == 0){
                //当作upval使用
                markBlockUpval(fs,e.getVidx());
            }
        } else{
            //尝试在 Upvalues中找
            int idx = searchUpValue(fs,n);
            if(idx < 0){
                //没找到,查找上层
                singleVarAux(fs.prev,n,e,0);
                if(e.getK() == VLOCAL || e.getK() == VUPVAL){
                    idx =newUpValue(fs,n,e);
                } else{
                    //当前层级什么都不做
                    return;
                }
                initExp(e,VUPVAL,idx);
            }
        }
    }

    /**
     * 根据变量 名 查找变量
     */
    static void singelVar (LexState ls, ExpDesc var) {
        String varname = strCheckName(ls);
        FuncState  fs = ls.getFs();
        singleVarAux(fs, varname, var, 1);
        if (var.getK() == VVOID) {  /* global name? */
            ExpDesc key = new ExpDesc();
            //这个意思是指，全局变量存在一个table里面。 需要通过 env[varname]访问
            // envn本身也是一个全局变量 env[env] = env; 表也存储了自己
            singleVarAux(fs, ls.getEnvn(), var, 1);  /* get environment variable */
            codeStringExp(key, varname);  /* key is variable name */
            luaK_Indexed(fs, var, key);  /* env[varname] */
        }
    }


    /**
     * 调整表达式返回值的数量
     * 有这种场景：
     * loca a,b,c,d = 1,2
     * 那么 c,d的值为nil
     */
    public static void adjustAssign(LexState ls,int nvars,int nexps,ExpDesc e){
       FuncState fs = ls.getFs();
        //说明需要额外的值
        int need = nvars - nexps;
        //最后一个表达式有多个返回值
        if(hasMultiRet(e)){
            int extra = need + 1;
            if(extra < 0){
                extra = 0;
            }
            luaK_setReturns(fs,e,extra);
        }else{
           if(e.getK() != VVOID){
               luaK_exp2nextReg(fs,e);
               // 变量数据多余表达式数据，填nil
               if(need > 0){
                  luaK_Nil(ls.getFs(),fs.getFreereg(),need);
               }
           }
        }
        if(need > 0 ){
            //申请寄存器
            luaK_reserveRegs(fs,need);
        }else {
            fs.freereg+= need;
        }
    }
    /**
     * 处理 索引为g 的goto指令，跳转到label，并且将其移除掉
     */
   public static void solveGoto(LexState ls, int g, LabelDesc label){
      int i;
      LabelList gotolist = ls.getDyd().getGt();
      //需要解决的goto
      LabelDesc gt = gotolist.getArr().get(g);
      luaK_patchList(ls.getFs(),gt.getPc(),label.pc);
      //移除该条goto，从 pending list
       ls.getDyd().getArr().remove(g);
   }
    /**
     * 查找一个 活跃的label
      */
    public static LabelDesc findLabel(LexState ls, String  name){
        int i;
        DynData dyd = ls.getDyd();
        FuncState fs = ls.getFs();
        //在当前函数里面查找
        for(i = fs.getFirstlabel(); i <dyd.getLabel().getSize();i++){
            LabelDesc lb = dyd.getLabel().getArr().get(i);
            if(lb.name.equals(name)){
                return lb;
            }
        }
        return null;
    }
    /**
     * 添加一个 新的 label/goto 到相关的 list
     */
    public static int newLabelEntry(LexState ls,LabelList l,String name,int line,int pc){
        LabelDesc desc = new LabelDesc();
        desc.name = name;
        desc.line=line;
        desc.nactvar = ls.getFs().getNactvar();
        desc.close = false;
        desc.pc = pc;
        l.getArr().add(desc);
        return l.getSize();
    }
    public static int newGotoEntry(LexState ls,String name,int line,int pc){
        return newLabelEntry(ls,ls.getDyd().getGt(),name,line,pc);
    }
    /**
     * 解决 之前的jump，检查新的label是否有匹配的goto， solve他们，return true表示有goto 需要
     * close upvalues
     */
    public static boolean solveGotos(LexState ls, LabelDesc lb){
        LabelList gl = ls.getDyd().getGt();
        //获取当前 block的第一个goto
        int i =ls.getFs().getBlockCnt().getFirstgoto();
        boolean needClose = false;
        while(i < gl.getSize()){
            LabelDesc desc = gl.getArr().get(i);
            if(desc.getName().equals(lb.getName())) {
                needClose = needClose || desc.isClose();
                solveGoto(ls,i,lb);
            }else{
                i++;
            }
        }
       return needClose;
    }
    /**
     * 创建一个新的label，行号为line， last用来表示 label是否是 block里面最后一个 无操作数的 statement。
     * 解决所有的 pending goto 到 这个新的label。 如果需要 close upvalues，就添加一个close instruction
     */
    public static boolean createLabbel(LexState ls, String name,int line,boolean last){
        FuncState fs = ls.getFs();
        LabelList ll = ls.getDyd().getLabel();
        int l  =newLabelEntry(ls,ll,name,line,luaK_GetLabel(fs));
        LabelDesc desc = ll.getArr().get(l);
        // blockcnt.nactvar表示 block外部活跃的 变量
        if(last){
            desc.nactvar = fs.blockCnt.nactvar;
        }
        if(solveGotos(ls,desc)){
            //need close,函数close掉，需要关闭无用的 closure
            luaK_codeABC(fs, OpCode.OP_CLOSE,luaY_nVarsStack(fs),0,0);
            return true;
        }
        return false;
    }

    /**
     *调整 pending goto 到 block的外部
     */
     public static void moveGotosOut(FuncState fs,BlockCnt bl){
         int i;
         LabelList gl = fs.getLexState().getDyd().getGt();
         // 修正 pending gotos 到 当前的block
         for(i = bl.getFirstgoto();i < gl.getSize();i++){
             LabelDesc gt = gl.getArr().get(i);
             //如果 label处的活跃变量大于block外部的， 可能需要执行close
             if(regLevel(fs,gt.getNactvar()) > regLevel(fs,bl.getNactvar())){
                 gt.close = gt.close || bl.upval;
             }
             gt.nactvar = bl.nactvar;
         }
     }
    /**
     * 进入 block 需要做的操作
     */
    public static void enterBlock(FuncState fs,BlockCnt bl,boolean inLoop){
        //记录block是否在循环里面
        bl.isloop = inLoop;
        bl.nactvar = fs.nactvar;
        bl.firstgoto=fs.lexState.getDyd().getGt().getSize();
        bl.upval = false;
        bl.insidetbc = (fs.blockCnt != null && fs.blockCnt.insidetbc);
        //block是一个链表
        bl.previous = fs.blockCnt;
        fs.blockCnt = bl;

    }
    /**
     * 离开block需要进行的操作
     */
    public static void leaveBlock(FuncState fs){

        BlockCnt bl = fs.blockCnt;
        LexState ls = fs.getLexState();
        boolean hasClose = false;
        int stkLevel = regLevel(fs,bl.nactvar);
        if(bl.isloop){
            hasClose = createLabbel(ls, LString.newStr(ls.getL(),"break"),0,false);
        }
       if(!hasClose && bl.previous != null && bl.upval){
           luaK_codeABC(fs,OpCode.OP_CLOSE,stkLevel,0,0);
       }
       //还原当前的block
      fs.blockCnt = bl.previous;
       //移除block里面定义的变量
      removeVars(fs,bl.nactvar);
      fs.freereg = stkLevel;
      //处理goto
      if(bl.previous !=null){
         moveGotosOut(fs,bl);
      } else{
          if(bl.firstgoto < ls.getDyd().getGt().getSize()){
              System.err.println("pending goto  in outer block");
          }
      }
    }


    /**
     * 添加一个 prototype
      */
    public static Proto addPrototype(LexState ls){
        Proto clp;
        LuaState l = ls.getL();
        FuncState fs = ls.getFs();
        Proto f = fs.getProto();
        clp = Proto.newProto(l);
        f.getProtoList().add(clp);
        fs.np++;
        return clp;
    }
    /**
     * 在 父函数里面生成指令创建 新的闭包
      */
    public static void codeClosure(LexState ls,ExpDesc e){
        FuncState fs = ls.getFs().getPrev();
        initExp(e,VRELOC,luaK_codeABx(fs,OpCode.OP_CLOSE,0,fs.np-1));
        luaK_exp2anyreg(fs,e);
    }
    /**
     * 开启函数
     */
    public static void openFunc(LexState ls,FuncState fs,BlockCnt bl){
        Proto f = fs.getProto();
        fs.prev = ls.getFs();
        fs.lexState = ls;
        ls.setFs(fs);
        fs.pc = 0;
        fs.previousline = f.getLinedefined();
        DynData dyd = ls.getDyd();
        fs.firstlocal = dyd.getActiveLocVarSize();
        fs.firstlabel = dyd.getLabel().getSize();
        f.setSource(ls.getSource());
        //前两个寄存器总是有效的
        f.setMaxstacksize(2);
        enterBlock(fs,bl,false);

    }
    /**
     * 关闭 func
     */
    public static void closeFunc(LexState ls){
        LuaState l = ls.getL();
        FuncState fs = ls.getFs();
        Proto f =fs.getProto();
        //最后一条生成一条return
        luaK_Ret(fs,luaY_nVarsStack(fs),0);
        leaveBlock(fs);
        luaK_Finish(fs);
        //重新设置 lex 的 funstate
        ls.setFs(fs.prev);
    }
    /**
     * 上个表达式是否有多个返回值
     */
    public static boolean hasMultiRet(ExpDesc e){
        return e.getK() == VCALL || e.getK() == VVARARG;
    }

    /**
     * 语法规则
     *
     *  {} 里面的内容表示可选， [] 里面的内容表示会出现0或者多次
     */
    /**
     * 语句
     */
    public static void statement(LexState ls){
        int line = ls.getLinenumber();
        switch (ls.getCurTokenEnum()){

            case IF: {  /* stat -> ifstat */
                ifStat(ls, line);
                break;
            }
            case WHILE: {  /* stat -> whilestat */
                whileStat(ls, line);
                break;
            }
            case DO: {  /* stat -> DO block END */
                luaX_Next(ls);  /* skip DO */
                block(ls);
                checkMatch(ls, END, DO, line);
                break;
            }
            case FOR: {  /* stat -> forstat */
                forStat(ls, line);
                break;
            }
            case REPEAT: {  /* stat -> repeatstat */
                repeatStat(ls, line);
                break;
            }
            case FUNCTION: {  /* stat -> funcstat */
                funcStat(ls, line);
                break;
            }
            case LOCAL: {  /* stat -> localstat */
                luaX_Next(ls);  /* skip LOCAL */
                if (testNext(ls, FUNCTION))  /* local function? */
                    localFunc(ls);
                else
                    localStat(ls);
                break;
            }
            case DOU_COLON: {  /* stat -> label */
                luaX_Next(ls);  /* skip double colon */
                labelStat(ls, strCheckName(ls), line);
                break;
            }
            case RETURN: {  /* stat -> retstat */
                luaX_Next(ls);  /* skip RETURN */
                retStat(ls);
                break;
            }
            case BREAK: {  /* stat -> breakstat */
                breakStat(ls);
                break;
            }
            case GOTO: {  /* stat -> 'goto' NAME */
                luaX_Next(ls);  /* skip 'goto' */
                gotoStat(ls);
                break;
            }
            case SEMICON:{
                luaX_Next(ls);
                break;
            }
            default:
                exprStat(ls);
                break;
        }
        //调整后寄存器数量
        ls.getFs().freereg = luaY_nVarsStack(ls.getFs());
    }
    /**
     * 表达式
     */

    public static void expr(LexState ls,ExpDesc v){
        subExpr(ls,v,0);
    }

    /**
     * 检查当前token 是不是 block 的 follow set
     * ’until' 关闭 了语法层面上的block， 但是没有关闭 scope，是分开处理的
     * 如果不是 block 的follow set， 代表可能要处理一个新的block1
     */
    public static boolean blockFollow(LexState ls,boolean withUntil){
        switch (ls.getCurTokenEnum()){
            case ELSE: case ELSEIF:
            case END: case EOF:
                return true;
            case UNTIL:
                return withUntil;
            default:
                return false;

        }
    }
    /**
     * 语句列表
     *    statlist -> {   stat [ ';' ]        }
      */
    public static void statList(LexState ls){
        while(!blockFollow(ls,true)){
            if(ls.getCurTokenEnum() == RETURN){
                statement(ls);
                //return作为最后一个语句
                return;
            }
        }
    }
    /**
     * field 都是表相关的
     * fieldsel ->  ['.'| ':'] NAME
     *
     * e代表 table
     * NAME是 表的索引
     */
    public static void fieldSel(LexState ls, ExpDesc e){
        FuncState fs = ls.getFs();
        ExpDesc key = new ExpDesc();
        luaK_exp2anyreg(fs,e);
        //跳过 . / :
        luaX_Next(ls);
        codeNameExp(ls,e);
        luaK_Indexed(fs,e,key);
    }

    /**
     * 表的索引
     *  index -> '[' expr ']'
     */
    public static void yIndex(LexState ls, ExpDesc v) {
        //跳过 '['
        luaX_Next(ls);
        expr(ls, v);
        luaK_exp2val(ls.getFs(),v);
        check_next1(ls,']');
    }
    /**
     * 初始化表时，字段赋值的处理
     *
     * a={ b=1 , ['b']=1 }
     * recField ->  (NAME |  '[' EXP ']' )= EXP */
    public static void recField(LexState ls,TableConstructor cons){
        FuncState fs = ls.getFs();
        int reg = ls.getFs().freereg;
        ExpDesc tab = new ExpDesc(),key = new ExpDesc(), val = new ExpDesc();
        if(ls.getCurTokenEnum() == NAME){
            codeNameExp(ls,key);
        } else{
            yIndex(ls,key);
        }
        cons.nh++;
        check_next1(ls,'=');
        tab = cons.getT();
        luaK_Indexed(fs,tab,key);
        expr(ls,val);
        luaK_storevar(fs,tab,val);
        //还原寄存器
        fs.freereg = reg;
    }

    /**
     * 结束 列表字段的处理 将 字段内容存放到寄存器里面去，
     */
    public static void closeListField(FuncState fs, TableConstructor cons){
        //表没有初始化的数据
        if(cons.getV().getK() ==VVOID){
            return;
        }
        //将表达式的内容存放到寄存器里面
        luaK_exp2nextReg(fs,cons.getV());
        cons.getV().setK(VVOID);
        if(cons.getToStore() == FIELDS_PER_FLUSH){
            luaK_setList(fs,cons.getT().getInfo(),cons.getNa(),cons.getToStore());
            //存储元素
            cons.na+=cons.toStore;
            cons.toStore = 0;
        }
    }
    /***
     * 表初始化时最后一个字段的添加
     */
    public static void lastListField(FuncState fs,TableConstructor cc){
        if(cc.toStore == 0){
            return;
        }
        if(hasMultiRet(cc.getV())){
            luaK_setReturns(fs,cc.getV(),LUA_MULTRET);
            luaK_setList(fs,cc.getT().getInfo(),cc.na,LUA_MULTRET);
            //最后一个表达式返回元素数量未知，没有进行技术
            cc.na--;

        } else{
            if(cc.getV().getK() == VVOID){
                luaK_exp2nextReg(fs,cc.getV());
            }
            luaK_setList(fs,cc.getT().getInfo(),cc.na,cc.toStore);
        }
        //记录已经存储的item数量
        cc.na+=cc.toStore;
    }

    /**
     * a={1,2,3,['a']=1, b=2}  1,2,3就是 listfield， ['a']=1 ,b=2是 recfield
     *
     * listfield -> exp
     * @param cc
     */
    public static void listField(LexState ls,TableConstructor cc){
        expr(ls,cc.getV());
        cc.toStore++;
    }
    /**
     * field
     *
     * field->  listfield | recField
     */
    public static void field(LexState ls, TableConstructor cc){

        switch (ls.getCurTokenEnum()){
            case NAME:
                if(luaX_lookahead(ls) != ASSIGN){
                    listField(ls,cc);
                } else{
                    recField(ls,cc);
                }
                break;
            case MID_LEFT:
                recField(ls,cc);
                break;
            default:
                listField(ls,cc);
        }
    }

    /**
     * 表的 构造
     * seq作为元素的分割符
     * constructor -> '{' [ field { sep field } [sep] ] '}'
     *      sep -> ',' | ';'
     */
    public static void constructor(LexState ls,ExpDesc t){
        FuncState fs = ls.getFs();
        int line = ls.getLinenumber();
        int pc = luaK_codeABC(fs,OpCode.OP_NEWTABLE,0,0,0);
        TableConstructor cc = new TableConstructor();
        //额外参数的空间
        luaK_code(fs,new Instruction(0));
        cc.na=cc.nh=cc.toStore = 0;
        cc.t=t;
        initExp(t,VNONRELOC,fs.freereg);
        //申请一个寄存器
        luaK_reserveRegs(fs,1);
        //还没有进行value的读取，先进行初始化
        initExp(cc.getV(),VVOID,0);
        //表的开头符号
        check_next1(ls,'{');
        do{
           //读取到表的末尾
            if(ls.getCurTokenEnum() == BIG_RIGHT){
                break;
            }
            //将field存如寄存器
            closeListField(fs,cc);
            //读取一个field
            field(ls,cc);
        }while(testNext(ls,COMMA) || testNext(ls,SEMICON));

        checkMatch(ls,BIG_RIGHT,BIG_LEFT,line);
        lastListField(fs,cc);
        //设置表的尺寸
        luaK_setTableSize(fs,pc,t.info,cc.na,cc.nh);
    }

    /**
     * 设置 可变参数
     */
    public static void setVararg(FuncState fs,int nParams){
        fs.getProto().setVararg(true);
        luaK_codeABC(fs,OpCode.OP_VARARGPREP,nParams,0,0);
    }

    /**
     * 处理参数列表， parameter 是函数定义的参数
     * parList =  [   {NAME ','} (NAME | '...')]
     */

    public static  void parList(LexState ls){
        FuncState fs = ls.getFs();
        Proto f = fs.getProto();
        int nparams = 0;
        boolean isvarArg = false;
        if(ls.getCurTokenEnum() != SMALL_RIGHT){
            do{
                switch (ls.getCurTokenEnum()){
                    case NAME:
                        newLocalVar(ls,strCheckName(ls));
                        nparams++;
                    case VARARG:
                        luaX_Next(ls);
                        isvarArg = true;
                        break;
                    default:
                        System.err.println("name or ...  expected");
                }

            }while (!isvarArg && testNext(ls,COMMA));
        }
        //调整本地变量，所有的函数参数都被当成一个local变量
        adjustLocalVars(ls,nparams);
        f.setNumparams(fs.nactvar);
        if(isvarArg){
            setVararg(fs,f.getNumparams());
        }
    }

    /**
     * 解析 函数体
     * method 代表是对象的方法
     *
     * body ->  '(' parlist ')' block END
     */
    public static void body(LexState ls,ExpDesc e, boolean isMethod,int line){
        //遇到一个新的函数
        FuncState newFs = new FuncState();
        BlockCnt bl = new BlockCnt();
        newFs.setProto(addPrototype(ls));
        //设置函数所在的行
        newFs.getProto().setLinedefined(line);
        openFunc(ls,newFs,bl);
        check_next1(ls,'(');
        //如果是method，会加上self作为method的参数
        if(isMethod){
            newLocalVar(ls,"self");
        }
        parList(ls);
        check_next1(ls,')');
        statList(ls);
        //设置最后一行的位置
        newFs.getProto().setLastlinedefined(ls.getLinenumber());
        checkMatch(ls,END,FUNCTION,line);
        codeClosure(ls,e);
        closeFunc(ls);
    }

    /**
     * 表达式列表
     * explist ->  expr { ',' expr }
     */
    public static int expList(LexState ls,ExpDesc v){
        //表达式的数量
        int n= 1;
        expr(ls,v);
        while(testNext(ls,COMMA)){
           luaK_exp2nextReg(ls.getFs(),v);
           expr(ls,v);
           n++;
        }
        return n;
    }


    /**
     * args是函数调用的参数
     * funcargs ->  '(' [explit] ')'
     * funcargs -> "xxxx"     lua支持  print "hello" 这种参数
     * funcargs -> constructor  函数名 加 表构造函数
     */
    public static void funcArgs(LexState ls,ExpDesc f,int line){

       FuncState fs = ls.getFs();
       ExpDesc args = new ExpDesc();
       int base = 0, nparams=0;

       switch (ls.getCurTokenEnum()){
           case SMALL_LEFT:
               luaX_Next(ls);
               //args 为空
               if(ls.getCurTokenEnum() == SMALL_RIGHT){
                   args.k = VVOID;
               } else{
                   expList(ls,args);
               }
               checkMatch(ls,SMALL_RIGHT,SMALL_LEFT,line);
               break;
           case BIG_LEFT:
               constructor(ls,args);
               break;
           case STRING:
               codeStringExp(args,ls.getT().getS());
               luaX_Next(ls);
               break;
           default:
               System.err.println("缺少函数参数");

       }
       base = f.getInfo();
       if(hasMultiRet(args)){
           nparams = LUA_MULTRET;
       } else{
           if(args.getK() != VVOID){
               luaK_exp2nextReg(fs,args);
           }
           nparams = fs.freereg-(base+1);
       }
       initExp(f,VCALL,luaK_codeABC(fs,OpCode.OP_CALL,base,nparams+1,2));
       luaK_fixline(fs,line);
       //移除函数 和参数占用的寄存器
        fs.freereg = base + 1;
    }
    /**
     * 表达式解析
     */
    /**
     * primaryexp -> NAME | '(' expr ')'
     * @param ls
     * @param v
     */
    public static void primaryExp(LexState ls,ExpDesc v){

        TokenEnum tkNum = ls.getCurTokenEnum();
        if(tkNum ==SMALL_LEFT){
            int line = ls.getLinenumber();
            luaX_Next(ls);
            expr(ls,v);
            checkMatch(ls,SMALL_RIGHT,SMALL_LEFT,line);
            luaK_dischargeVars(ls.getFs(),v);
        } else if(tkNum == NAME){
            singelVar(ls,v);
        }else{
            System.err.println("unexpected synbol");
        }
    }
    /**
     * 后缀表达式
     * suffixedExp -> primaryexp { '.' NAME | '[' exp ']' | ':' NAME funcargs | funcargs}
     */

    public static void suffixedExp(LexState ls, ExpDesc v){
        FuncState fs = ls.getFs();
        int line = ls.getLinenumber();
        primaryExp(ls,v);
        for(;;){
          switch (ls.getCurTokenEnum()){
              case DOT:
                  fieldSel(ls,v);
                  break;
              case MID_LEFT: {
                  ExpDesc key = new ExpDesc();
                  luaK_exp2anyregup(fs, v);
                  yIndex(ls, key);
                  luaK_Indexed(fs, v, key);
                  break;
              }
              case COLON: {
                  ExpDesc key = new ExpDesc();
                  luaX_Next(ls);
                  codeNameExp(ls,key);
                  luaK_self(fs,v,key);
                  funcArgs(ls,v,line);
                  break;
              }
              case SMALL_LEFT: case BIG_LEFT:
                  luaK_exp2nextReg(fs,v);
                  funcArgs(ls,v,line);
                  break;
              case STRING:
                  luaK_exp2nextReg(fs,v);
                  funcArgs(ls,v,line);
                  break;

              default:
                  break;
          }
        }
    }

    /**
     *
     * simpleexp -> FLT | INT | STRING | NIL | TRUE | FALSE | ... | constructor | FUNCTION body
     *  | suffixedexp
     */
    public static void simpleExp(LexState ls,ExpDesc v){
        switch (ls.getCurTokenEnum()){
            case BIG_LEFT:
                constructor(ls, v);
                return;
            case FLOAT:
                initExp(v,VKFLT,0);
                v.setNval(ls.getT().getR());
                break;
            case INT:
                initExp(v,VKINT,0);
                v.setIval(ls.getT().getI());
                break;
            case STRING: {
                codeStringExp(v,ls.getT().getS());
                break;
            }
            case NIL: {
                initExp(v, VNIL, 0);
                break;
            }
            case TRUE: {
                initExp(v, VTRUE, 0);
                break;
            }
            case FALSE: {
                initExp(v, VFALSE, 0);
                break;
            }
            case VARARG: {  /* vararg */
                FuncState fs = ls.getFs();
                initExp(v, VVARARG, luaK_codeABC(fs, OP_VARARG, 0, 0, 1));
                break;
            }

            case FUNCTION: {
                luaX_Next(ls);
                body(ls, v, false , ls.getLinenumber());
                return;
            }
            default: {
                suffixedExp(ls, v);
                return;
            }

        }
        luaX_Next(ls);
    }

    public static UnOpr getUnopr(TokenEnum op){
        switch (op){
            case SUB:return UnOpr.OPR_MINUS;
            case BITXOR: return UnOpr.OPR_BNOT;
            case LEN: return UnOpr.OPR_LEN;
            case NOT:return UnOpr.OPR_NOT;
            default:
                return UnOpr.OPR_NOUNOPR;
        }
    }

    public static BinOpr getBinopr(TokenEnum op){
        switch (op){
            case ADD:return BinOpr.OPR_ADD;
            case SUB: return OPR_SUB;
            case MUL: return OPR_MUL;
            case MOD: return OPR_MOD;
            case POW: return OPR_POW;
            case DIV: return OPR_DIV;
            case BITAND: return OPR_BAND;
            case BITOR: return OPR_BOR;
            case BITXOR: return OPR_BXOR;
            case LT: return OPR_LT;
            case GT: return OPR_GT;
            case IDIV: return OPR_IDIV;
            case LSHIFT: return OPR_SHL;
            case RSHIFT: return OPR_SHR;
            case CAT: return OPR_CONCAT;
            case NE: return OPR_NE;
            case EQ: return OPR_EQ;
            case LE: return OPR_LE;
            case GE: return OPR_GE;
            case AND: return OPR_AND;
            case OR: return OPR_OR;
            default:
                return OPR_NOBINOPR;
        }
    }

    /**
     *  2元运算符优先表。  没有使用递归的形式，隐含出运算符优先级，使用优先级表的方式
     *
     *  数值表示优先级高底。 如果 数组的第一个元素和第二个元素值不一样 表面该运算符
     *  是左结合或者右结合的
     */
    public static int[][] priority ={
            {10, 10}, {10, 10},           /* '+' '-' */
            {11, 11}, {11, 11},           /* '*' '%' */
            {14, 13},                  /* '^' (right associative) */
            {11, 11}, {11, 11},           /* '/' '//' */
            {6, 6}, {4, 4}, {5, 5},   /* '&' '|' '~' */
            {7, 7}, {7, 7},           /* '<<' '>>' */
            {9, 8},                   /* '..' (right associative) */
            {3, 3}, {3, 3}, {3, 3},   /* ==, <, <= */
            {3, 3}, {3, 3}, {3, 3},   /* ~=, >, >= */
            {2, 2}, {1, 1}            /* and, or */
    };
    /**
     * 单运算符的优先级
     */
    public static int UNARY_PRIORITY = 12;

    /**
     * 子表达式
     *
     * subexpr ->  (simpleexp | unop subexpr) { binopr subexpr  }
     *  binop 是优先级比limit要高的操作符， 遇到优先级高的操作符就继续读取
     */
    public static BinOpr subExpr(LexState ls,ExpDesc v,int limit){
        BinOpr op;
        UnOpr uop;
        uop = getUnopr(ls.getCurTokenEnum());
        if(uop != UnOpr.OPR_NOUNOPR){
            int line = ls.getLinenumber();
            luaX_Next(ls);
            subExpr(ls,v,UNARY_PRIORITY);
            luaK_Prefix(ls.getFs(),uop,v,line);
        } else{
            simpleExp(ls,v);
        }
        op = getBinopr(ls.getCurTokenEnum());
        while(op != OPR_NOBINOPR && priority[op.getOp()][0] > limit){
            ExpDesc v2 = new ExpDesc();
            BinOpr nextOp;
            int line = ls.getLinenumber();
            luaX_Next(ls);
            luaK_Infix(ls.getFs(),op,v);
            //读取优先级更高的子表达式
            nextOp = subExpr(ls,v2,priority[op.getOp()][1]);
            luaK_posFix(ls.getFs(),op,v,v2,line);
            op = nextOp;
        }
        return op;
    }

    /**
     * 语句的 语法规则
     *
     *
     */

    /**
     * block -> statlist
     * @param ls
     */
    public static void block(LexState ls){
        FuncState fs = ls.getFs();
        BlockCnt bl = new BlockCnt();
        enterBlock(fs,bl,false);
        statList(ls);
        leaveBlock(fs);
    }
    public static boolean isIndexed(ExpKind e){
        return e.kind>=VINDEXED.kind && e.kind <= VINDEXSTR.kind;
    }
    /**
     * 检查冲突
     *
     * 赋值表达式的结构是：
     *
     *  a,b,c,d = ....的， 左侧可以有多个操作数
     *
     *  对于
     *
     *    a[xx],a = x,y
     *
     *    对a赋值后，a的值为y， a[xx] 的a应该使用赋值后的，
     *
     *    还有
     *      a[b],b=x,y
     *      的情况，也需要对b进行一次拷贝
     *
     *      VINDEXED的 索引用寄存器下标表示，赋值过程中会有
     *        a[b],b=x,y的情况
     *
     *      VINDEXUP，VINDEXDI,VINDEXDSTR 的索引 要么是常量数组，要么就是值本身。
     *      在赋值过程中不会发生改变。因为不可能有
     *         a['123'],'123' =x,y 的情况
     *
     *
     *
     */
    public static void checkConflict(LexState ls,LeftAssignVars lh,ExpDesc v){
        FuncState fs = ls.getFs();
        //如果需要拷贝值，放在extra寄存器里面
        int extra = fs.freereg;
        boolean conflict = false;
        for(;lh != null;lh =lh.prev){
            //前几个表达式是给表赋值
            if(isIndexed(lh.v.getK())){
                //table是一个 upvalue，其索引使用的是 "常量"，
                if(lh.v.getK() == VINDEXUP){
                    //tt代表之前赋值用到的table
                    // tt == v.getinfo 表示之前用的table 和 现在 v中的变量
                    //是同一个，有冲突。
                    if(v.getK() == VUPVAL && lh.v.getTt() == v.getInfo()){
                        // 表示lh中使用的table 是 当前被赋值的变量
                        conflict = true;
                        //使用拷贝的内容 去赋值
                        lh.v.setK(VINDEXSTR);
                        lh.v.setTt(extra);
                    }
                } else{
                    //table 放在寄存器里面
                    // a[b],a=x,y
                    if(v.getK() == VLOCAL && lh.v.getTt() == v.getIdx()){
                        //表示lh中使用的table是当前被赋值的变量
                        conflict =true;
                        lh.v.setTt(extra);
                    }
                    // a[b],b=x,y的情形，需要实现拷贝b
                    //VINDEXED比较特殊，索引可以是寄存器下标
                    //而寄存器里面的值是会发生改变的
                    //如果有冲突，需要拷贝一份副本
                    if(lh.v.getK()==VINDEXED && v.getK() == VLOCAL
                        && lh.v.getIdx() == v.getIdx()){
                        conflict = true;
                        lh.v.setIdx(extra);
                    }
                }
            }
        }

        if(conflict){
            //拷贝 upvalue 或者local,到extra的位置
            if(v.getK() == VLOCAL){
                luaK_codeABC(fs,OP_MOVE,extra,v.getRidx(),0);
            }else{
                luaK_codeABC(fs,OP_GETUPVAL,extra,v.getInfo(),0);
            }

            luaK_reserveRegs(fs,1);
        }
    }


    /**
     *
     * 解析有多个赋值变量的语句
     *
     * 第一个赋值的内容，已经在外部被解析过了
     *
     * assignment -> suffixedexp restassign
     *
     * restassin -> ',' suffixedexp restassign | '=' explist
     */
    public static void restAssign(LexState ls,LeftAssignVars lh,int nvars){
        ExpDesc e = new ExpDesc();
        //只读的变量不能赋值
        checkReadOnly(ls,lh.v);
        if(testNext(ls,COMMA)){
            //a,b,c,d=x,x,x 说名还有左侧的赋值变量还没有读完
            LeftAssignVars nv = new LeftAssignVars();
            nv.prev = lh;
            suffixedExp(ls,nv.v);
            //检查冲突
            if(!isIndexed(nv.v.getK())){
                checkConflict(ls,lh,nv.v);
            }
            restAssign(ls,nv,nvars+1);
        } else{
            // 读取到了 =， 接着处理等于号后面的多个表达式
            check_next1(ls,'=');
            //返回表达式的数量
            int nextps = expList(ls,e);
            if(nextps != nvars){
                adjustAssign(ls,nvars,nextps,e);
            } else{
                luaK_setOneRet(ls.getFs(),e);
                luaK_storevar(ls.getFs(),lh.v,e);
                return;
            }

        }

        initExp(e,VNONRELOC,ls.getFs().freereg-1);
        luaK_storevar(ls.getFs(),lh.v,e);
    }

    /**
     * 条件跳转
     * cond -> exp
     */
    public static int cond(LexState ls){
        ExpDesc v = new ExpDesc();
        expr(ls,v);
        if(v.k == null){
            v.k = VFALSE;
        }
        luaK_goIfTrue(ls.getFs(),v);
        return v.getF();
    }
    /**
     * goto语句
     */
    public static void gotoStat(LexState ls){
        FuncState fs = ls.getFs();
        int line = ls.getLinenumber();
        String name = strCheckName(ls);
        LabelDesc lb = findLabel(ls,name);
        if(lb == null){
            //label还没有定义，直接jump，等label被定义后再处理
            newGotoEntry(ls,name,line,luaK_Jump(fs));
        } else{
            //label是存在的,向后跳
            int lbLevel = regLevel(fs,lb.nactvar);
            //跳到的位置，变量少于现在的，如果有upval，需要进行关闭
            /**
             * 如下面的指令所示，跳到label xx时，要关闭upval类型的变量 a,b
             * label xx
             * upval a
             * upval b
             * jump xx
             */
            if(luaY_nVarsStack(fs) > lbLevel){
                luaK_codeABC(fs,OP_CLOSE,lbLevel,0,0);
            }
            //创建一个jump，跳到label
            luaK_patchList(fs,luaK_Jump(fs),lb.pc);
        }
    }
    /**
     * break
     */
    public static void breakStat(LexState ls){
        int line = ls.getLinenumber();
        luaX_Next(ls);
        newGotoEntry(ls,LString.newStr(ls.getL(),"break"),line,luaK_Jump(ls.getFs()));
    }

    /**
     * 检查label是不是已经被定义过了
     */
    public static void checkRepeated(LexState ls,String name){
        LabelDesc lb = findLabel(ls,name);
        if(lb != null){
            System.err.println("label is already defined");
        }
    }
    /**
     * 定义label
     * label ->  '::' NMAE '::"
     */
    public static void labelStat(LexState ls,String name,int line){
        checkNext(ls,DOU_COLON);
        while(ls.getCurrent() == ';'||ls.getCurTokenEnum() == DOU_COLON){
            statement(ls);
        }
        checkRepeated(ls,name);
        createLabbel(ls,name,line,blockFollow(ls,false));

    }
    /**
     * while
     *
     * whilestat -> WHILE cond DO block END
     */
    public static void whileStat(LexState ls,int line){
        FuncState fs = ls.getFs();
        int whileInit;
        int condExit;
        BlockCnt bl = new BlockCnt();
        luaX_Next(ls);
        whileInit =luaK_GetLabel(fs);
        condExit = cond(ls);
        enterBlock(fs,bl,true);
        checkNext(ls,DO);
        block(ls);
        luaK_JumpTo(fs,whileInit);
        checkMatch(ls,END,WHILE,line);
        leaveBlock(fs);
        luaK_patchToHere(fs,condExit);
    }
    /**
     * repeat -> REPEAT block  UNTIL cond
     */
    public static void repeatStat(LexState ls, int line){
        int condExit;
        FuncState fs = ls.getFs();
        int repeatInit = luaK_GetLabel(fs);
        BlockCnt bl1=new BlockCnt(),bl2 =new BlockCnt();
        enterBlock(fs,bl1,true);
        enterBlock(fs,bl2,false);
        luaX_Next(ls);
        statList(ls);
        checkMatch(ls,UNTIL,REPEAT,line);
        condExit = cond(ls);
        leaveBlock(fs);
        if(bl2.upval){
            int exit = luaK_Jump(fs);
            luaK_patchToHere(fs,condExit);
            luaK_codeABC(fs,OP_CLOSE,regLevel(fs,bl2.nactvar),0,0);
            condExit = luaK_Jump(fs);
            luaK_patchToHere(fs,exit);
        }
        luaK_patchList(fs,condExit,repeatInit);
        leaveBlock(fs);
    }
    /**
     * 读取一个 表达式，将结果放到下个 stack slot
     */
    public static void exp1(LexState ls){
        ExpDesc e = new ExpDesc();
        expr(ls,e);
        luaK_exp2nextReg(ls.getFs(),e);
    }
    /**
     * 修正jump指令的跳转位置
     */
    public static void fixForJump(FuncState fs,int pc,int dest,boolean back){
        Instruction jmp = fs.getProto().getInstruction(pc);
        int offset = dest - (pc+1);
        if(back){
            offset = -offset;
        }
        Instructions.setArgsBx(jmp,offset);
    }

    /**
     * for循环
     * forBody -> DO block
     */
    //整数for循环
    public static OpCode[] forpreg = {OP_FORPREP,OP_TFORPREP};
    //泛型 for循环
    public static OpCode[] forloop = {OP_FORLOOP,OP_TFORLOOP};

    public static void forBody(LexState ls,int base,int line,int nvars,int isgen){
        BlockCnt bl = new BlockCnt();
        FuncState fs = ls.getFs();
        int prep,endfor;
        checkNext(ls,DO);
        prep = luaK_codeABx(fs,forpreg[isgen],base,0);
        enterBlock(fs,bl,false);
        adjustLocalVars(ls,nvars);
        luaK_reserveRegs(fs,nvars);
        block(ls);
        leaveBlock(fs);
        fixForJump(fs,prep,luaK_GetLabel(fs),false);
        if(isgen == 1){
            luaK_codeABC(fs,OP_TFORCALL,base,0,nvars);
            luaK_fixline(fs,line);
        }
        endfor = luaK_codeABC(fs,forloop[isgen],base,0,nvars);
        fixForJump(fs,endfor,prep+1,true);
        luaK_fixline(fs,line);
    }

    /**
     * fornum -> NAME = exp,exp[,exp] forbody
     * @param ls
     * @param varname
     * @param line
     */
    public static void fornum(LexState ls, String  varname,int line){
        FuncState fs = ls.getFs();
        int base = fs.getFreereg();
        newLocalVar(ls,"(for state)");
        newLocalVar(ls,"(for state)");
        newLocalVar(ls,"(for state)");
        newLocalVar(ls,varname);
        checkNext(ls,ASSIGN);
        //循环初始值
        exp1(ls);
        checkNext(ls,COMMA);
        exp1(ls);
        if(testNext(ls,COMMA)){
            exp1(ls);
        } else{
            //默认步长1
            luaK_int(fs,fs.freereg,1);
            luaK_reserveRegs(fs,1);
        }
        //3个控制循环的变量
        adjustLocalVars(ls,3);
        forBody(ls,base,line,1,0);

    }
    /**
     * 泛型for循环
     * forlist -> NAME {, NAME} IN explsit forbody
     */
    public static void forList(LexState ls,String indexName){
        FuncState fs = ls.getFs();
        ExpDesc e = new ExpDesc();
        int nvars = 5;  /* gen, state, control, toclose, 'indexname' */
        int line;
        int base = fs.getFreereg();
        /* create control variables */
        newLocalVar(ls,"(for state)");
        newLocalVar(ls,"(for state)");
        newLocalVar(ls,"(for state)");
        newLocalVar(ls,"(for state)");
        newLocalVar(ls, indexName);
        while (testNext(ls, COMMA)) {
            newLocalVar(ls, strCheckName(ls));
            nvars++;
        }
        checkNext(ls, IN);
        line = ls.getLinenumber();
        adjustAssign(ls, 4, expList(ls,e), e);
        adjustLocalVars(ls, 4);  /* control variables */
        markToBeClosed(fs);  /* last control var. must be closed */
        luaK_checkStack(fs, 3);  /* extra space to call generator */
        forBody(ls, base, line, nvars - 4, 1);
    }
    /**
     * for语句
     * forstat -> FOR (fornum | forlist) END
     */
    public static void forStat(LexState ls,int line){
        FuncState fs = ls.getFs();
        String varname;
        BlockCnt bl = new BlockCnt();
        enterBlock(fs,bl,true);
        luaX_Next(ls);
        varname = strCheckName(ls);
        switch (ls.getCurTokenEnum()){
            case ASSIGN:fornum(ls,varname,line);break;
            case COMMA:forList(ls,varname);break;
            case IN:
                forList(ls,varname);break;
            default:
                System.err.println("语法错误");

        }
        checkMatch(ls,END,FOR,line);
        leaveBlock(fs);
    }

    /**
     * test_then_block -> [IF| ELSEIF] cond THEN block
     * @param ls
     * @param excapeList
     * @return
     */
    public static int testThenBlock(LexState ls, ExpDesc escapeList){
        BlockCnt bl = new BlockCnt();
        FuncState fs = ls.getFs();
        ExpDesc v = new ExpDesc();
        //如果condition是错的，用于跳过 then后面的内容
        int jumpFalse = 0;
        luaX_Next(ls);
        expr(ls,v);
        checkNext(ls,THEN);
        // if xx then break
        if(ls.getCurTokenEnum() == BREAK){
            int line = ls.getLinenumber();
            luaK_goIfFalse(fs,v);
            luaX_Next(ls);
            enterBlock(fs,bl,false);
            newGotoEntry(ls,LString.newStr(ls.getL(),"break"),line,v.t);
            while(testNext(ls,SEMICON));
            if(blockFollow(ls,false)){
                leaveBlock(fs);
                return jumpFalse;
            } else{
                //if conditon is false, skip then
                jumpFalse = luaK_Jump(fs);
            }
        } else{
            luaK_goIfTrue(fs,v);
            enterBlock(fs,bl,false);
            jumpFalse = v.getF();
        }
        //then part
        statList(ls);
        leaveBlock(fs);
        if(ls.getCurTokenEnum() == ELSE ||ls.getCurTokenEnum() == ELSEIF){
            luaK_Concat(fs,escapeList,luaK_Jump(fs),true);
        }

        luaK_patchToHere(fs,jumpFalse);


        return 0;
    }

    /**
     * if语句
     * /* ifstat -> IF cond THEN block {ELSEIF cond THEN block} [ELSE block] END
     * @param ls
     * @param line
     */
    public static void ifStat(LexState ls,int line){
        FuncState fs = ls.getFs();

        //仅仅用来存储 一个 整数
        ExpDesc escapeList = new ExpDesc();
        escapeList.setT(NO_JUMP);
        testThenBlock(ls,escapeList);
        while(ls.getCurTokenEnum() == ELSEIF){
            testThenBlock(ls,escapeList);
        }
        if(testNext(ls,ELSE)){
            block(ls);
        }
        checkMatch(ls,END,IF,line);
        luaK_patchToHere(fs,escapeList.getT());
    }
    public static void localFunc(LexState ls){
        ExpDesc b = new ExpDesc();
        FuncState fs = ls.getFs();
        int fvar = fs.getNactvar();
        newLocalVar(ls,strCheckName(ls));
        //函数名作为一个参数
        adjustLocalVars(ls,1);
        body(ls,b,false,ls.getLinenumber());
        localDebugInfo(fs,fvar).setStartpc(fs.pc);

    }
    /**
     * 获取一个 local的属性 例如 const, close
     * ATTRIB -> ['<' Name '>']
     */
    public static int getLocalAttribute(LexState ls){
        if(testNext(ls,LT)){
            String attr = strCheckName(ls);
            checkNext(ls,GT);
            if("const".equals(attr)){
                return RDKCONST;
            } else if("close".equals(attr)){
                return RDKTOCLOSE;
            } else{
                System.err.println("unknow attribute");
            }

        }

        return VDKREG;
    }

    public static void checkToClose(FuncState fs,int level){
        if(level !=1){
            markToBeClosed(fs);
            luaK_codeABC(fs,OP_TBC,regLevel(fs,level),0,0);
        }
    }

    /**
     * stat -> LOCAL NAME ATTRIB { ',' NAME ATTRIB } ['=' explist]
     * @param ls
     */
    public static void localStat(LexState ls){
        FuncState fs = ls.getFs();
        // to-be-closed 变量的下标
        int toclose = -1;
        //最后一个变量
        Vardesc var;
        int vidx,kind,nvars=0,nextexps;
        ExpDesc e  = new ExpDesc();
        do{
            vidx = newLocalVar(ls,strCheckName(ls));
            kind = getLocalAttribute(ls);
            getLocalVarDesc(fs,vidx).kind =kind;
            if(kind == RDKTOCLOSE){
                if(toclose != -1){
                    System.err.println("error");
                }
                toclose = fs.nactvar +nvars;
            }
            nvars++;
        } while(testNext(ls,COMMA));
        if(testNext(ls,ASSIGN)){
            nextexps = expList(ls,e);
        } else{
            e.setK(VVOID);
            nextexps = 0;
        }
        //获取最后一个变量
        var = getLocalVarDesc(fs,vidx);
        if(nvars == nextexps && var.getKind() == RDKCONST && luaK_Exp2Const(fs,e,var.getK())){
            var.kind = RDKCTC;
            adjustLocalVars(ls,nvars -1 );
            fs.nactvar++;
        } else{
            adjustAssign(ls,nvars,nextexps,e);
            adjustLocalVars(ls,nvars);
        }
        checkToClose(fs,toclose);
    }

    /**
     *
     *  /* funcname -> NAME {fieldsel} [':' NAME]
     */
    public static boolean funcname(LexState ls,ExpDesc v){
        boolean isMethod =false;
        singelVar(ls,v);
        while(ls.getCurTokenEnum() ==DOT){
            fieldSel(ls,v);
        }
        if(ls.getCurTokenEnum() == COLON){
            isMethod = true;
            fieldSel(ls,v);
        }

        return isMethod;
    }

    /**
     * funcstat -> FUNCTION  funname body
     */
    public static void funcStat(LexState ls, int line){
        boolean isMethod;
        ExpDesc v = new ExpDesc(),b = new ExpDesc();
        luaX_Next(ls);
        isMethod = funcname(ls,v);
        body(ls,b,isMethod,line);
        checkReadOnly(ls,v);
        luaK_storevar(ls.getFs(),v,b);
        luaK_fixline(ls.getFs(),line);
    }

    /** stat -> func | assignment */
    public static void exprStat(LexState ls){
        FuncState fs = ls.getFs();
        LeftAssignVars v = new LeftAssignVars();
        suffixedExp(ls,v.v);
        //赋值表达式
        if(ls.getCurTokenEnum() == ASSIGN || ls.getCurTokenEnum() == COMMA){
            v.prev = null;
            restAssign(ls,v,1);

        } else{
            Instruction ins = getInstruction(fs,v.v);
            Instructions.setArgC(ins,1);

        }
    }
    /** stat -> RETURN [explist] [';'] */
    public static void retStat(LexState ls){
       FuncState fs = ls.getFs();
       ExpDesc e = new ExpDesc();
       //返回值数量
       int nret;
       //返回值存放的第一个 slot
       int first = luaY_nVarsStack(fs);
       if(blockFollow(ls,true) || ls.getCurTokenEnum() ==SEMICON){
           nret = 0;
       } else{
           nret = expList(ls,e);
           if(hasMultiRet(e)){
               luaK_setReturns(fs,e,LUA_MULTRET);
               if(e.getK() == VCALL && nret == 1 && !fs.getBlockCnt().insidetbc){
                   Instructions.setOpCode(getInstruction(fs,e),OP_TAILCALL);
               }
               nret = LUA_MULTRET;
           } else{
               //只有一个返回值
               if(nret == 1){
                    first = luaK_exp2anyreg(fs,e);
               } else{
                   luaK_exp2nextReg(fs,e);
               }
           }
       }
       luaK_Ret(fs,first,nret);
       testNext(ls,SEMICON);
    }

    /**
     * main func
     */
    public static void mainfunc(LexState ls,FuncState fs){
        BlockCnt bl = new BlockCnt();
        UpvalDesc env;
        openFunc(ls,fs,bl);
        setVararg(fs,0);
        env = allocUpValue(fs);
        env.setInstack(true);
        env.setIdx(0);
        env.setKind(VDKREG);
        env.setName(ls.getEnvn());
        // read first token
        luaX_Next(ls);
        statList(ls);
        check(ls,EOF);
        closeFunc(ls);

    }

    public Closure luaY_Parser(LuaState l,DynData dyd,String name,int firstchar){


        return null;
    }


}
