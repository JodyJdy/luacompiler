package com.jdy.lua.lparser;

import com.jdy.lua.lex.Lex;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lex.Reserved;
import com.jdy.lua.lobjects.*;
import com.jdy.lua.lstate.LuaState;

import java.util.List;

import static com.jdy.lua.lcodes.LCodes.luaK_Indexed;
import static   com.jdy.lua.lparser.ParserConstants.*;
import static com.jdy.lua.lcodes.LCodes.NO_JUMP;
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



}
