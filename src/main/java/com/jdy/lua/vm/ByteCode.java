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
    class Jmp extends SingleArgByteCode {
        private DynamicLabel dynamicLabel;

        public Jmp(DynamicLabel dynamicLabel) {
            super(0);
            this.dynamicLabel = dynamicLabel;
        }

        public Jmp(int a) {
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

    class Add extends Calculate {
        public Add(int a, int b, int c) {
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
    class Sub extends Calculate {
        public Sub(int a, int b, int c) {
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
    class Div extends Calculate{
        @Override
        public String toString() {
            return "DIV{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public Div(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class Mod extends Calculate{
        @Override
        public String toString() {
            return "MOD{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public Mod(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class IntMod extends Calculate{
        @Override
        public String toString() {
            return "INTMOD{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public IntMod(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class Pow extends Calculate{
        @Override
        public String toString() {
            return "POW{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public Pow(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BitAnd extends Calculate{
        @Override
        public String toString() {
            return "BITAND{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public BitAnd(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BitOr extends Calculate{
        @Override
        public String toString() {
            return "BITOR{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public BitOr(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BitLeftShift extends Calculate{
        @Override
        public String toString() {
            return "BitLeftShift{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public BitLeftShift(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BitRightShift extends Calculate{
        public BitRightShift(int a, int b, int c) {
            super(a, b, c);
        }

        @Override
        public String toString() {
            return "BitRightShift{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }
    }
    class Cat extends Calculate{
        @Override
        public String toString() {
            return "CAT{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public Cat(int a, int b, int c) {
            super(a, b, c);
        }
    }

    class And extends Calculate{
        public And(int a, int b, int c) {
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
    class Or extends Calculate{
        @Override
        public String toString() {
            return "OR{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public Or(int a, int b, int c) {
            super(a, b, c);
        }
    }
    /**
     * 测试寄存器里面的值是否为真， 为真继续执行，否则跳到假出口
     */

    class Test extends SingleArgByteCode {
        @Override
        public String toString() {
            return "TEST{" +
                    "a=" + a +
                    '}';
        }

        public Test(int a) {
            super(a);
        }
    }

    class Compare extends TwoArgByteCode{
        public Compare(int a, int b) {
            super(a, b);
        }
    }

    class Eq extends Compare{
        @Override
        public String toString() {
            return "EQ{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public Eq(int a, int b) {
            super(a, b);
        }
    }

    class Ne extends Compare {
        @Override
        public String toString() {
            return "NE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public Ne(int a, int b) {
            super(a, b);
        }
    }
    class Ge extends Compare{
        @Override
        public String toString() {
            return "GE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public Ge(int a, int b) {
            super(a, b);
        }
    }
    class Gt extends Compare{
        @Override
        public String toString() {
            return "GT{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public Gt(int a, int b) {
            super(a, b);
        }
    }

    class Lt extends Compare{
        @Override
        public String toString() {
            return "LT{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public Lt(int a, int b) {
            super(a, b);
        }
    }
    class Le extends Compare{
        @Override
        public String toString() {
            return "LE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public Le(int a, int b) {
            super(a, b);
        }
    }



    /**
     *  变量下标b 加载到 寄存器 a里面
     */
    class LoadVar extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LoadVar{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LoadVar(int a, int b) {
            super(a, b);
        }
    }

    /**
     *
     * 加载父级函数中的变量 到寄存器 a 里面
     * 变量下标存储在b里面
     */
    class LoadUpVar extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LoadUpVar{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LoadUpVar(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 将变量类型a: 本地变量，upval，global var
     * 变量下标 b
     * 对应的值设置为 nil
     */
    class SaveNil extends TwoArgByteCode{
        public static int LOCAL_VAR = 0;
        public static int UPVAL = 1;
        public static int GLOBAL = 2;
        @Override
        public String toString() {
            String type = a == 0 ? "localVar" : (a == 1) ? "UpVal" : " globalVar";
            return "SaveNil{" +
                    type +
                    ", b=" + b +
                    '}';
        }

        public SaveNil(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 将函数下标b 加载到寄存器a里面
     */
    class LoadFunc extends TwoArgByteCode{
        public LoadFunc(int a, int b) {
            super(a, b);
        }
        @Override
        public String toString() {
            return "LoadFunc{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }

    /**
     * 常量下标b 加载到寄存器 a 里面
     */
    class LoadConstant extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LoadConstant{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LoadConstant(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 全局变量下标b 加载到寄存器 a 里面
     */
    class LoadGlobal extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LoadGlobal{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LoadGlobal(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 寄存器下标b中的值 保存到 变量下标 a里面
     */
    class SaveVar extends TwoArgByteCode{


        @Override
        public String toString() {
            return "SaveVar{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public SaveVar(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 寄存器下标b中的值  保存到全局变量下标 a 里面
     */

    class SaveGlobal extends  TwoArgByteCode{


        public SaveGlobal(int a, int b) {
            super(a, b);
        }

        @Override
        public String toString() {
            return "SaveGlobal{" +
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

    class SaveUpval extends TwoArgByteCode{
        @Override
        public String toString() {
            return "SaveUpval{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public SaveUpval(int a, int b) {
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

    class Call extends ThreeArgByteCode{
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

        public Call(int a, int b, int c, int d) {
            super(a, b, c);
            this.d = d;
        }

    }
    /**
     * 处理 ... 参数
     * 将...的参数的 b个元素  以寄存器a开始，放置
     * 如果 b 为 -1， 表示全部放置
     */
    class VarArgs extends TwoArgByteCode{
        @Override
        public String toString() {
            return "VARARGS{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public VarArgs(int a, int b) {
            super(a, b);
        }
    }



    /**
     * 返回多个参数
     * 将 a -> b 寄存器里面的内容 存储到 a 寄存器里面
     *
     * b == -1 表示到 栈顶的全返回
     */

    class ReturnMulti extends TwoArgByteCode{
        @Override
        public String toString() {
            return "ReturnMulti{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
        public ReturnMulti(int a, int b) {
            super(a, b);
        }
    }
    /**
     * 无参数返回
     */
    class Return extends NoArgByteCode{
        @Override
        public String toString() {
            return "RETURN{}";
        }
    }

    /**
     * a:b()
     * 获取 table的对象方法
     *  寄存器 a 里面存放 表
     *  寄存器 b 里面存放 方法在表中的索引
     *  执行后:
     *    寄存器 a 里面存放 方法
     *    寄存器 b 里面存放  表
     */
    class GetTableMethod extends TwoArgByteCode{
        @Override
        public String toString() {
            return "GetTableMethod{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public GetTableMethod(int a, int b) {
            super(a, b);
        }
    }
    /**
     *
     * 将 寄存器A中的Table， 下标b 的内容读取到寄存器A里面
     */
    class GetTable extends TwoArgByteCode{

        @Override
        public String toString() {
            return "GETTABLE{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public GetTable(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 创建一个 table
     */
    class NewTable extends SingleArgByteCode{
        @Override
        public String toString() {
            return "NEWTABLE{" +
                    "a=" + a +
                    '}';
        }

        public NewTable(int a) {
            super(a);
        }
    }

    /**
     *
     * 将寄存器A，下标b的内容 设置为 nil
     */
    class SetTableNil extends TwoArgByteCode{

        @Override
        public String toString() {
            return "SETTABLENIL{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public SetTableNil(int a, int b) {
            super(a, b);
        }
    }
    /**
     *
     * 将寄存器A，下标b的内容 设置为 c寄存器的内容
     */
    class SetTable extends ThreeArgByteCode{
        @Override
        public String toString() {
            return "SETTABLE{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public SetTable(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     * 表a， 添加 寄存器 b -> c范围的数据
     * 如果c == -1 表示不确定数量
     */
    class SetTableArray extends ThreeArgByteCode{
        @Override
        public String toString() {
            return "SetTableArray{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public SetTableArray(int a, int b, int c) {
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
    class NumberFor extends SingleArgByteCode{
        @Override
        public String toString() {
            return "NumberFor{" +
                    "a= " + a
                    + "}";
        }

        public NumberFor(int a) {
            super(a);
        }
    }

    /**
     *执行初始值的累加步长的操作
     *
     * a 是初始值所在的寄存器
     */
    class EndNumberFor extends SingleArgByteCode{
        @Override
        public String toString() {
            return "EndNumberFor{" +
                    "a=" + a +
                    '}';
        }

        public EndNumberFor(int a) {
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

    class GenericFor extends ThreeArgByteCode{
        int d;
        public GenericFor(int a, int b, int c, int d) {
            super(a, b, c);
            this.d = d;
        }

        @Override
        public String toString() {
            return "GenericFor{" +
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
    class Length extends SingleArgByteCode{
        @Override
        public String toString() {
            return "LENGTH{" +
                    "a=" + a +
                    '}';
        }
        public Length(int a) {
            super(a);
        }
    }

    /**
     *对a寄存器里面的内容进行 not 运行 结果存放在 a里面
     */
    class Not extends SingleArgByteCode{

        @Override
        public String toString() {
            return "NOT{" +
                    "a=" + a +
                    '}';
        }

        public Not(int a) {
            super(a);
        }
    }

    /**
       进行 ~ 运算
     */
    class BitReverse extends SingleArgByteCode{
        @Override
        public String toString() {
            return "BITREVERSE{" +
                    "a=" + a +
                    '}';
        }

        public BitReverse(int a) {
            super(a);
        }
    }

    /**
     * 对a寄存器进行   - 运算
     */
    class Negative extends SingleArgByteCode{
        public Negative(int a) {
            super(a);
        }

        @Override
        public String toString() {
            return "Negative{" +
                    "a=" + a +
                    '}';
        }
    }


    /**
     * 将常量 b 作为模块加载到全局变量 a中
     */
    class LoadGlobalModule extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LoadGlobalModule{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LoadGlobalModule(int a, int b) {
            super(a, b);
        }
    }
    /**
     *  将常量 b 作为模块加载到寄存器 a中
     */
    class LoadModule extends TwoArgByteCode{
        @Override
        public String toString() {
            return "LoadModule{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }

        public LoadModule(int a, int b) {
            super(a, b);
        }
    }


}
