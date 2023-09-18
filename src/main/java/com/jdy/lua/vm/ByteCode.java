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
    }

    /**
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
            this.a = dynamicLabel.getPc();
        }
    }

    /**
     * 加法指令
     * <p>
     * add a  b c
     * <p>
     * 将 寄存器b 和 寄存器 c 里面的内容 相加 放入到 a中
     */

    class ADD extends ThreeArgByteCode {
        public ADD(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     * 加法指令
     * <p>
     * add a  b c
     * <p>
     * 将 寄存器b 和 寄存器 c 里面的内容 相减 放入到 a中
     */
    class SUB extends ThreeArgByteCode {
        public SUB(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class MUL extends ThreeArgByteCode{
        public MUL(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class DIV extends ThreeArgByteCode{
        public DIV(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class MOD extends ThreeArgByteCode{
        public MOD(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class INTMOD extends ThreeArgByteCode{
        public INTMOD(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class POW extends ThreeArgByteCode{
        public POW(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITAND extends ThreeArgByteCode{
        public BITAND(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITOR extends ThreeArgByteCode{
        public BITOR(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITLEFTMOVE extends ThreeArgByteCode{
        public BITLEFTMOVE(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class BITRIGHTMOVE extends ThreeArgByteCode{
        public BITRIGHTMOVE(int a, int b, int c) {
            super(a, b, c);
        }
    }
    class CAT extends ThreeArgByteCode{
        public CAT(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     * 测试寄存器里面的值是否为真， 为真继续执行，否则跳到假出口
     */

    class TEST extends SingleArgByteCode {
        public TEST(int a) {
            super(a);
        }
    }

    class EQ extends TwoArgByteCode{
        public EQ(int a, int b) {
            super(a, b);
        }
    }

    class NE extends TwoArgByteCode {
        public NE(int a, int b) {
            super(a, b);
        }
    }
    class GE extends TwoArgByteCode{
        public GE(int a, int b) {
            super(a, b);
        }
    }
    class GT extends TwoArgByteCode{
        public GT(int a, int b) {
            super(a, b);
        }
    }

    class LT extends TwoArgByteCode{
        public LT(int a, int b) {
            super(a, b);
        }
    }
    class LE extends TwoArgByteCode{
        public LE(int a, int b) {
            super(a, b);
        }
    }



    /**
     *  变量下标b 加载到 寄存器 a里面
     */
    class LOADVAR extends TwoArgByteCode{
        public LOADVAR(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 加载父级函数中的变量 到寄存器 a 里面
     * 变量名存储在b里面
     */
    class LOADUPVAR extends TwoArgByteCode{
        public LOADUPVAR(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 常量下标b 加载到寄存器 a 里面
     */
    class LOADCONSTANT extends TwoArgByteCode{
        public LOADCONSTANT(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 全局变量下标b 加载到寄存器 a 里面
     */
    class LOADGLOBAL extends TwoArgByteCode{
        public LOADGLOBAL(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 寄存器下标b中的值 保存到 变量下标 a里面
     */
    class SAVEVAR extends TwoArgByteCode{
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
    }

    /**
     *
     * 寄存器下标b中的值， 加载到父级变量VAL里面去
     * 变量名存储在a里面
     */

    class SAVEUPVAL extends TwoArgByteCode{
        public SAVEUPVAL(int a, int b) {
            super(a, b);
        }
    }
    /**
     *
     * 调用寄存器a 里面的函数，
     * 参数范围 为
     *  a+1 -> b
     *
     *返回值存储在
     * a里面  （多返回值，包含长度）
     * 返回值长度存储在寄存器
     * a+1 里面
     */

    class CALL extends ThreeArgByteCode{
        public CALL(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     * 返回多个参数
     * 将 b -> c 寄存器里面的内容 存储到 a 寄存器里面
     */

    class RETURNMULTI extends ThreeArgByteCode{
        public RETURNMULTI(int a, int b, int c) {
            super(a, b, c);
        }
    }
    /**
     * 无参数返回
     */
    class RETURN extends NoArgByteCode{}

    /**
     * 将 寄存器A中的Table， 下标b 的内容读取到寄存器A里面
     */
    class GETTABLE extends TwoArgByteCode{
        public GETTABLE(int a, int b) {
            super(a, b);
        }
    }

    /**
     * 创建一个 table
     */
    class NEWTABLE extends NoArgByteCode{

    }

    /**
     * 将 b - > c 寄存器中的内容，
     * 添加到 a 寄存器对应的table中
     */
    class SETARRAY extends ThreeArgByteCode{
        public SETARRAY(int a, int b, int c) {
            super(a, b, c);
        }
    }
    /**
     * 将寄存器A，下标b的内容 设置为 c寄存器的内容
     */
    class SETTABLE extends ThreeArgByteCode{
        public SETTABLE(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     * 如果一个寄存器里面存放了多个值 （例如: 多返回值函数, ... ）
     *
     *  将 b 寄存器的多值 下标为 c 的值 存放在 a寄存器里面
     *
     */

    class GETMULTI extends ThreeArgByteCode{
        public GETMULTI(int a, int b, int c) {
            super(a, b, c);
        }
    }

}
