package com.jdy.lua.lparser;

import com.jdy.lua.lex.Lex;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lex.Reserved;
import com.jdy.lua.lex.Token;
import com.jdy.lua.lobjects.*;
import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lopcodes.OpCode;
import com.jdy.lua.lstate.LuaState;
import com.jdy.lua.lstring.LString;

import java.rmi.dgc.Lease;
import java.util.List;

import static com.jdy.lua.lcodes.LCodes.*;
import static   com.jdy.lua.lparser.ParserConstants.*;
import static com.jdy.lua.lex.Lex.*;
import static com.jdy.lua.lex.Reserved.*;
import static com.jdy.lua.lparser.ExpKind.*;
@SuppressWarnings("all")
public class LParser {

    /**
     * 检查token 是否是c，如果是 读取下一个token
     * @param ls
     * @param c
     * @return
     */
    static boolean testNext (LexState ls, int c) {
        if (ls.getCurrTokenNum() == c) {
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
    static void check (LexState ls, int c) {
        if (ls.getCurrTokenNum() != c)
            System.err.println("expecd:"+luaXToken2Str(ls,c));;
    }
    static void check (LexState ls, Reserved c) {
        check(ls,c.getT());
    }

    /**
     * 期望是 waht
     * line 是 where
     * @param ls
     * @param what
     * @param who
     * @param where
     */
    public static void checkMatch(LexState ls, int what, int who, int where){
        if(!testNext(ls,what)){
            System.out.println("错误");
        }
    }

    /**
     * 获取一个 TK_NAME
     */
    public static TString strCheckName(LexState ls){
        check(ls,TK_NAME);
        String str = ls.getT().getS();
        luaX_Next(ls);
        return new TString(str);
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
    public static void codeStringExp(ExpDesc e, TString s){
        e.setF(NO_JUMP);
        e.setT(NO_JUMP);
        e.setK(VKSTR);
        e.setStrval(s.getContents());
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
    public static int regsisterLocalVar(LexState ls,FuncState fs,TString varName){
        Proto proto = fs.getProto();
        //注册前，本地变量的数量
        int oldSize = proto.getSizelocvars();
        LocalVar localVar = new LocalVar();
        localVar.setName(varName);
        localVar.setStartpc(fs.pc);
        proto.addLocalVar(localVar);
        return fs.ndebugvars++;
    }
    /**
     * 创建一个新的 本地 变量 返回在函数中的索引
     */
    public static int newLocalVar(LexState ls,TString name){
        LuaState l = ls.getL();
        FuncState fs = ls.getFs();
        DynData dyd = ls.getDyd();
        Vardesc vardesc = dyd.getVarDesc(dyd.n++);
        vardesc.setKind(VDKREG);
        vardesc.setName(name.getContents());
        return dyd.n - 1 - fs.firstlocal;
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
            var.pidx = regsisterLocalVar(ls,fs,new TString(var.name));
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
    public static int searchUpValue(FuncState fs,TString name){
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
    public static int newUpValue(FuncState fs,TString name,ExpDesc v){
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
    public static int searchVar(FuncState fs,TString n,ExpDesc e){
        int i;
        for(i=fs.nactvar - 1;i >=0;i--){
            Vardesc v = getLocalVarDesc(fs,i);
            if(v.getName().equals(n.getContents())){
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
    public static void singleVarAux(FuncState fs,TString n, ExpDesc e,int base){
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
        TString varname = strCheckName(ls);
        FuncState  fs = ls.getFs();
        singleVarAux(fs, varname, var, 1);
        if (var.getK() == VVOID) {  /* global name? */
            ExpDesc key = new ExpDesc();
            //这个意思是指，全局变量存在一个table里面。 需要通过 env[varname]访问
            // envn本身也是一个全局变量 env[env] = env; 表也存储了自己
            singleVarAux(fs, new TString(ls.getEnvn()), var, 1);  /* get environment variable */
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
   public static void solveGoto(LexState ls,int g,LableDesc label){
      int i;
      LabelList gotolist = ls.getDyd().getGt();
      //需要解决的goto
      LableDesc gt = gotolist.getArr().get(g);
      luaK_patchList(ls.getFs(),gt.getPc(),label.pc);
      //移除该条goto，从 pending list
       ls.getDyd().getArr().remove(g);
       gotolist.n--;
   }
    /**
     * 查找一个 活跃的label
      */
    public static LableDesc findLabel(LexState ls, TString name){
        int i;
        DynData dyd = ls.getDyd();
        FuncState fs = ls.getFs();
        //在当前函数里面查找
        for(i = fs.getFirstlabel(); i <dyd.getLabel().getN();i++){
            LableDesc lb = dyd.getLabel().getArr().get(i);
            if(lb.name.equals(name.getContents())){
                return lb;
            }
        }
        return null;
    }
    /**
     * 添加一个 新的 label/goto 到相关的 list
     */
    public static int newLabelEntry(LexState ls,LabelList l,TString name,int line,int pc){
        int n = l.getN();
        LableDesc desc = new LableDesc();
        desc.name = name.getContents();
        desc.line=line;
        desc.nactvar = ls.getFs().getNactvar();
        desc.close = false;
        desc.pc = pc;
        l.n++;
        l.getArr().add(desc);
        return n;
    }
    public static int newGotoEntry(LexState ls,TString name,int line,int pc){
        return newLabelEntry(ls,ls.getDyd().getGt(),name,line,pc);
    }
    /**
     * 解决 之前的jump，检查新的label是否有匹配的goto， solve他们，return true表示有goto 需要
     * close upvalues
     */
    public static boolean solveGotos(LexState ls,LableDesc lb){
        LabelList gl = ls.getDyd().getGt();
        //获取当前 block的第一个goto
        int i =ls.getFs().getBlockCnt().getFirstgoto();
        boolean needClose = false;
        while(i < gl.getN()){
            LableDesc desc = gl.getArr().get(i);
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
    public static boolean createLabbel(LexState ls, TString name,int line,boolean last){
        FuncState fs = ls.getFs();
        LabelList ll = ls.getDyd().getLabel();
        int l  =newLabelEntry(ls,ll,name,line,luaK_GetLabel(fs));
        LableDesc desc = ll.getArr().get(l);
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
         for(i = bl.getFirstgoto();i < gl.getN();i++){
             LableDesc gt = gl.getArr().get(i);
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
        bl.firstgoto=fs.lexState.getDyd().getGt().getN();
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
          if(bl.firstgoto < ls.getDyd().getGt().getN()){
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
        fs.firstlocal = ls.getDyd().getN();
        fs.firstlabel = ls.getDyd().getLabel().n;
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

    }
    /**
     * 表达式
     */

    public static void expr(LexState ls,ExpDesc v){

    }

    /**
     * 检查当前token 是不是 block 的 follow set
     * ’until' 关闭 了语法层面上的block， 但是没有关闭 scope，是分开处理的
     * 如果不是 block 的follow set， 代表可能要处理一个新的block1
     */
    public static boolean blockFollow(LexState ls,boolean withUntil){
        switch (Reserved.getReserved(ls.getCurrTokenNum())){
            case TK_ELSE: case TK_ELSEIF:
            case TK_END: case TK_EOS:
                return true;
            case TK_UNTIL:
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
            if(ls.getCurrTokenNum() == TK_RETURN.getT()){
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
        if(ls.getCurrTokenNum() == TK_NAME.getT()){
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
        switch (Reserved.getReserved(ls.getCurrTokenNum())){
            case TK_NAME:
                if(luaX_lookahead(ls) != '=' ){
                    listField(ls,cc);
                } else{
                    recField(ls,cc);
                }
                break;
            default:
                //单字符token
                if('[' == ls.getCurrTokenNum()){
                    recField(ls,cc);
                } else{
                    listField(ls,cc);
                }
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
            if(ls.getCurrTokenNum() =='}'){
                break;
            }
            //将field存如寄存器
            closeListField(fs,cc);
            //读取一个field
            field(ls,cc);
        }while(testNext(ls,',') || testNext(ls,';'));

        checkMatch(ls,'}','{',line);
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
     * 处理参数列表
     * parList =  [   {NAME ','} (NAME | '...')]
     */















}
