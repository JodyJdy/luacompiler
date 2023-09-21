package com.jdy.lua.vm;

/**
 * 字节码设计
 *
 * @author jdy
 * @title: ByteCode
 * @description:
 * @data 2023/9/18 11:09
 */
public interface ByteCode {




    /**
     * 无参 指令
     */
    abstract class NoArgByteCode implements ByteCode {
    }

    /**
     * 1个参数的指令
     */
    abstract class SingleArgByteCode implements ByteCode {
        protected int a;

        public SingleArgByteCode(int a) {
            this.a = a;
        }
    }

    /**
     * 两个参数的指令
     */
    abstract class TwoArgByteCode implements ByteCode {
        protected int a;
        protected int b;

        public TwoArgByteCode(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "a = "+ a+ ","+"b="+b;
        }
    }

    /**
     * 三个参数的指令
     */
    abstract class ThreeArgByteCode implements ByteCode {
        protected int a;
        protected int b;
        protected int c;

        public ThreeArgByteCode(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
        @Override
        public String toString() {
            return this.getClass().getName() + "a = "+ a+ ","+"b="+b+"c="+c;
        }
    }

    /**
     *
     * 跳转指令
     * jmp a
     * a 表示 跳转到的 pc 位置
     */
    class JMP extends SingleArgByteCode {
        private DynamicLabel dynamicLabel;

        public JMP(DynamicLabel dynamicLabel) {
            super(0);
            this.dynamicLabel = dynamicLabel;
        }

        public JMP(int a) {
            super(a);
        }
        /**
         * 应用label的值，找到具体的跳转位置
         */
        public void applyLabel(){
            if (dynamicLabel != null) {
                this.a = dynamicLabel.getPc();
            }
        }

        @Override
        public String toString() {
            return "JMP{" +
                    "pc=" + a +
                    '}';
        }
    }

    /**
     * 用于计算的
     * 字节码
     */
    abstract class Calculate extends ThreeArgByteCode{
        public Calculate(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     * 加法指令
     * <p>
     * add a  b c
     * <p>
     * 将 寄存器b 和 寄存器 c 里面的内容 相加 放入到 a中
     */

    class ADD extends Calculate {
        public ADD(int a, int b, int c) {
            super(a, b, c);
        }

        @Override
        public String toString() {
            return "ADD{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }
    }

    /**
     * 加法指令
     * <p>
     * add a  b c
     * <p>
     * 将 寄存器b 和 寄存器 c 里面的内容 相减 放入到 a中
     */
    class SUB extends Calculate {
        public SUB(int a, int b, int c) {
            super(a, b, c);
        }

        @Override
        public String toString() {
            return "SUB{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }
    }
    class MUL extends Calculate{
        @Override
        public String toString() {
            return "MUL{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public MUL(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class DIV extends Calculate{
        @Override
        public String toString() {
            return "DIV{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public DIV(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class MOD extends Calculate{
        @Override
        public String toString() {
            return "MOD{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public MOD(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class INTMOD extends Calculate{
        @Override
        public String toString() {
            return "INTMOD{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public INTMOD(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class POW extends Calculate{
        @Override
        public String toString() {
            return "POW{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public POW(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITAND extends Calculate{
        @Override
        public String toString() {
            return "BITAND{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public BITAND(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITOR extends Calculate{
        @Override
        public String toString() {
            return "BITOR{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public BITOR(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITLEFTMOVE extends Calculate{
        @Override
        public String toString() {
            return "BITLEFTMOVE{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public BITLEFTMOVE(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITRIGHTMOVE extends Calculate{
        public BITRIGHTMOVE(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class CAT extends Calculate{
        @Override
        public String toString() {
            return "CAT{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public CAT(int a, int b, int c) {
            super(a, b, c);
        }
    }

    class AND extends Calculate{
        public AND(int a, int b, int c) {
            super(a, b, c);
        }

        @Override
        public String toString() {
            return "AND{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }
    }
    class OR extends Calculate{
        @Override
        public String toString() {
            return "OR{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public OR(int a, int b, int c) {
            super(a, b, c);
        }
    }
    /**
     * 测试寄存器里面的值是否为真， 为真继续执行，否则跳到假出口
     */

    class TEST extends SingleArgByteCode {
        @Override
        public String toString() {
            return "TEST{" +
                    "a=" + a +
                    '}';
        }

        public TEST(int a) {
            super(a);
        }
    }

    class Compare extends TwoArgByteCode{
        public Compare(int a, int b) {
            super(a, b);
        }
    }

    class EQ extends Compare{
        @Override
        public String toString() {
            return "EQ{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public EQ(int a, int b) {
            super(a, b);
        }
    }

    class NE extends Compare {
        @Override
        public String toString() {
            return "NE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public NE(int a, int b) {
            super(a, b);
        }
    }
    class GE extends Compare{
        @Override
        public String toString() {
            return "GE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public GE(int a, int b) {
            super(a, b);
        }
    }
    class GT extends Compare{
        @Override
        public String toString() {
            return "GT{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public GT(int a, int b) {
            super(a, b);
        }
    }

    class LT extends Compare{
        @Override
        public String toString() {
            return "LT{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LT(int a, int b) {
            super(a, b);
        }
    }
    class LE extends Compare{
        @Override
        public String toString() {
            return "LE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LE(int a, int b) {
            super(a, b);
        }
    }



    /**
     *  变量下标b 加载到 寄存器 a里面
     */
    class LOADVAR extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LOADVAR{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LOADVAR(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 加载父级函数中的变量 到寄存器 a 里面
     * 变量名存储在b里面
     */
    class LOADUPVAR extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LOADUPVAR{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LOADUPVAR(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 将变量类型a: 本地变量，upval，global var
     * 变量下标 b
     * 对应的值设置为 nil
     */
    class SAVENIL extends TwoArgByteCode{
        public static int LOCAL_VAR = 0;
        public static int UPVAL = 1;
        public static int GLOBAL = 2;
        @Override
        public String toString() {
            String type = a == 0 ? "localVar" : (a == 1) ? "UpVal" : " globalVar";
            return "SAVENIL{" +
                    type +
                    ", b=" + b +
                    '}';
        }

        public SAVENIL(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 将函数下标b 加载到寄存器a里面
     */
    class LOADFUNC extends TwoArgByteCode{
        public LOADFUNC(int a, int b) {
            super(a, b);
        }
        @Override
        public String toString() {
            return "LOADFUNC{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }

    /**
     * 常量下标b 加载到寄存器 a 里面
     */
    class LOADCONSTANT extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LOADCONSTANT{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LOADCONSTANT(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 全局变量下标b 加载到寄存器 a 里面
     */
    class LOADGLOBAL extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LOADGLOBAL{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LOADGLOBAL(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 寄存器下标b中的值 保存到 变量下标 a里面
     */
    class SAVEVAR extends TwoArgByteCode{


        @Override
        public String toString() {
            return "SAVEVAR{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public SAVEVAR(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 寄存器下标b中的值  保存到全局变量下标 a 里面
     */

    class SAVEGLOBAL extends  TwoArgByteCode{


        public SAVEGLOBAL(int a, int b) {
            super(a, b);
        }

        @Override
        public String toString() {
            return "SAVEGLOBAL{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }

    /**
     *
     * 寄存器下标b中的值， 加载到父级变量VAL里面去
     * 变量名存储在a里面
     */

    class SAVEUPVAL extends TwoArgByteCode{
        @Override
        public String toString() {
            return "SAVEUPVAL{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public SAVEUPVAL(int a, int b) {
            super(a, b);
        }
    }
    /**
     *
     *
     * 调用寄存器a 里面的函数，
     * 参数范围 为
     *  b -> c
     *  如果c为-1，那么是 b 到栈顶部
     *  如果 b = a 说明是 a:b()这种调用
     *
     *  返回值保存在 以 a 开始的寄存器中
     *
     *
     *  d 表示 返回值数量， 如果d = -1 表示全部返回
     */

    class CALL extends ThreeArgByteCode{
        int d;
        @Override
        public String toString() {
            return "CALL{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    ", d=" + d +
                    '}';
        }

        public CALL(int a, int b, int c, int d) {
            super(a, b, c);
            this.d = d;
        }

        public CALL(int a, int b, int c) {
            super(a, b,c);
        }
    }
    /**
     * 处理 ... 参数
     * 将...的参数的 b个元素  以寄存器a开始，放置
     * 如果 b 为 -1， 表示全部放置
     */
    class VARARGS extends TwoArgByteCode{
        @Override
        public String toString() {
            return "VARARGS{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public VARARGS(int a, int b) {
            super(a, b);
        }
    }



    /**
     * 返回多个参数
     * 将 a -> b 寄存器里面的内容 存储到 a 寄存器里面
     *
     * b == -1 表示到 栈顶的全返回
     */

    class RETURNMULTI extends TwoArgByteCode{
        @Override
        public String toString() {
            return "RETURNMULTI{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
        public RETURNMULTI(int a, int b) {
            super(a, b);
        }
    }
    /**
     * 无参数返回
     */
    class RETURN extends NoArgByteCode{
        @Override
        public String toString() {
            return "RETURN{}";
        }
    }

    /**
     * 获取 table的对象方法
     *
     * a:b
     * 结果是
     *   方法 表
     *   方便函数调用
     */
    class GETTABLEMETHOD extends TwoArgByteCode{

        @Override
        public String toString() {
            return "GETTABLEMETHOD{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public GETTABLEMETHOD(int a, int b) {
            super(a, b);
        }
    }
    /**
     *
     * 将 寄存器A中的Table， 下标b 的内容读取到寄存器A里面
     */
    class GETTABLE extends TwoArgByteCode{

        @Override
        public String toString() {
            return "GETTABLE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public GETTABLE(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 创建一个 table
     */
    class NEWTABLE extends SingleArgByteCode{
        @Override
        public String toString() {
            return "NEWTABLE{" +
                    "a=" + a +
                    '}';
        }

        public NEWTABLE(int a) {
            super(a);
        }
    }

    /**
     *
     * 将寄存器A，下标b的内容 设置为 nil
     */
    class SETTABLENIL extends TwoArgByteCode{

        @Override
        public String toString() {
            return "SETTABLENIL{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public SETTABLENIL(int a, int b) {
            super(a, b);
        }
    }
    /**
     *
     * 将寄存器A，下标b的内容 设置为 c寄存器的内容
     */
    class SETTABLE extends ThreeArgByteCode{
        @Override
        public String toString() {
            return "SETTABLE{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public SETTABLE(int a, int b, int c) {
            super(a, b, c);
        }

    }

    /**
     * 数值循环
     *
     * 处理从 a, a + 1, a + 2  的三个寄存器
     *
     * a 存放初始值
     * a+1 存放 终值
     * a+2 存放 步长
     *
     * 如果 步长 > 0
     * 判断 初始值 是否 小于等于 终值， 是继续
     * 如果步长 < 0
     * 判断初始值是否 大于等于终值， 是继续
     */
    class NUMBERFOR extends SingleArgByteCode{
        @Override
        public String toString() {
            return "NUMBERFOR{" +
                    "a= " + a
                    + "}";
        }

        public NUMBERFOR(int a) {
            super(a);
        }
    }

    /**
     *执行初始值的累加步长的操作
     *
     * a 是初始值所在的寄存器
     */
    class ENDNUMBERFOR extends SingleArgByteCode{
        @Override
        public String toString() {
            return "ENDNUMBERFOR{" +
                    "a=" + a +
                    '}';
        }

        public ENDNUMBERFOR(int a) {
            super(a);
        }
    }

    /**
     *
     * 参数范围
     * a->b
     *
     * 表达式范围
     * c -> d
     *
     * 如果可以继续循环 pc+1

     */

    class GENERICFOR extends ThreeArgByteCode{
        int d;
        public GENERICFOR(int a, int b, int c,int d) {
            super(a, b, c);
            this.d = d;
        }

        @Override
        public String toString() {
            return "GENERICFOR{" +
                    ", a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    ",  d=" + d +
                    '}';
        }
    }


    /**
     * 计算a寄存器里面内容的长度，放在a寄存器里面
     */
    class LENGTH extends SingleArgByteCode{
        @Override
        public String toString() {
            return "LENGTH{" +
                    "a=" + a +
                    '}';
        }
        public LENGTH(int a) {
            super(a);
        }
    }

    /**
     *对a寄存器里面的内容进行 not 运行 结果存放在 a里面
     */
    class NOT extends SingleArgByteCode{

        @Override
        public String toString() {
            return "NOT{" +
                    "a=" + a +
                    '}';
        }

        public NOT(int a) {
            super(a);
        }
    }

    /**
       进行 ~ 运算
     */
    class BITREVERSE extends SingleArgByteCode{
        @Override
        public String toString() {
            return "BITREVERSE{" +
                    "a=" + a +
                    '}';
        }

        public BITREVERSE(int a) {
            super(a);
        }
    }

    /**
     * 对a寄存器进行   - 运算
     */
    class SINGLESUB extends SingleArgByteCode{
        public SINGLESUB(int a) {
            super(a);
        }

        @Override
        public String toString() {
            return "SINGLESUB{" +
                    "a=" + a +
                    '}';
        }
    }


    /**
     * 将常量 b 作为模块加载到全局变量 a中
     */
    class LOADGLOBALMODULE extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LOADMODULE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LOADGLOBALMODULE(int a, int b) {
            super(a, b);
        }
    }
    /**
     *  将常量 b 作为模块加载到寄存器 a中
     */
    class LOADMODULE extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LOADMODULE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LOADMODULE(int a, int b) {
            super(a, b);
        }
    }


}
