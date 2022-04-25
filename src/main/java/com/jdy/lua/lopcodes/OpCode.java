package com.jdy.lua.lopcodes;

/**
 * 定义字节码
 */
@SuppressWarnings("all")
public enum OpCode {


    /***----------------------------------------------------------------------
  name		args	description
------------------------------------------------------------------------*/
    OP_MOVE(0),/**	A B	R[A] := R[B]					*/
    OP_LOADI(1),/**	A sBx	R[A] := sBx					*/
    OP_LOADF(2),/**	A sBx	R[A] := (lua_Number)sBx				*/
    OP_LOADK(3),/**	A Bx	R[A] := K[Bx]					*/
    OP_LOADKX(4),/**	A	R[A] := K[extra arg]				*/
    OP_LOADFALSE(5),/**	A	R[A] := false					*/
    OP_LFALSESKIP(6),/**A	R[A] := false; pc++	(*)			*/
    OP_LOADTRUE(7),/**	A	R[A] := true					*/
    OP_LOADNIL(8),/**	A B	R[A], R[A+1], ..., R[A+B] := nil		*/
    OP_GETUPVAL(9),/**	A B	R[A] := UpValue[B]				*/
    OP_SETUPVAL(10),/**	A B	UpValue[B] := R[A]				*/

    OP_GETTABUP(11),/**	A B C	R[A] := UpValue[B][K[C]:string]			*/
    OP_GETTABLE(12),/**	A B C	R[A] := R[B][R[C]]				*/
    OP_GETI(13),/**	A B C	R[A] := R[B][C]					*/
    OP_GETFIELD(14),/**	A B C	R[A] := R[B][K[C]:string]			*/

    OP_SETTABUP(15),/**	A B C	UpValue[A][K[B]:string] := RK(C)		*/
    OP_SETTABLE(16),/**	A B C	R[A][R[B]] := RK(C)				*/
    OP_SETI(17),/**	A B C	R[A][B] := RK(C)				*/
    OP_SETFIELD(18),/**	A B C	R[A][K[B]:string] := RK(C)			*/

    OP_NEWTABLE(19),/**	A B C k	R[A] := {}					*/

    OP_SELF(20),/**	A B C	R[A+1] := R[B]; R[A] := R[B][RK(C):string]	*/

    OP_ADDI(21),/**	A B sC	R[A] := R[B] + sC				*/

    OP_ADDK(22),/**	A B C	R[A] := R[B] + K[C]:number			*/
    OP_SUBK(23),/**	A B C	R[A] := R[B] - K[C]:number			*/
    OP_MULK(24),/**	A B C	R[A] := R[B] * K[C]:number			*/
    OP_MODK(25),/**	A B C	R[A] := R[B] % K[C]:number			*/
    OP_POWK(26),/**	A B C	R[A] := R[B] ^ K[C]:number			*/
    OP_DIVK(27),/**	A B C	R[A] := R[B] / K[C]:number			*/
    OP_IDIVK(28),/**	A B C	R[A] := R[B] // K[C]:number			*/

    OP_BANDK(29),/**	A B C	R[A] := R[B] & K[C]:integer			*/
    OP_BORK(30),/**	A B C	R[A] := R[B] | K[C]:integer			*/
    OP_BXORK(31),/**	A B C	R[A] := R[B] ~ K[C]:integer			*/

    OP_SHRI(32),/**	A B sC	R[A] := R[B] >> sC				*/
    OP_SHLI(33),/**	A B sC	R[A] := sC << R[B]				*/

    OP_ADD(34),/**	A B C	R[A] := R[B] + R[C]				*/
    OP_SUB(35),/**	A B C	R[A] := R[B] - R[C]				*/
    OP_MUL(36),/**	A B C	R[A] := R[B] * R[C]				*/
    OP_MOD(37),/**	A B C	R[A] := R[B] % R[C]				*/
    OP_POW(38),/**	A B C	R[A] := R[B] ^ R[C]				*/
    OP_DIV(39),/**	A B C	R[A] := R[B] / R[C]				*/
    OP_IDIV(40),/**	A B C	R[A] := R[B] // R[C]				*/

    OP_BAND(41),/**	A B C	R[A] := R[B] & R[C]				*/
    OP_BOR(42),/**	A B C	R[A] := R[B] | R[C]				*/
    OP_BXOR(43),/**	A B C	R[A] := R[B] ~ R[C]				*/
    OP_SHL(44),/**	A B C	R[A] := R[B] << R[C]				*/
    OP_SHR(45),/**	A B C	R[A] := R[B] >> R[C]				*/

    OP_MMBIN(46),/**	A B C	call C metamethod over R[A] and R[B]	(*)	*/
    OP_MMBINI(47),/**	A sB C k	call C metamethod over R[A] and sB	*/
    OP_MMBINK(48),/**	A B C k		call C metamethod over R[A] and K[B]	*/

    OP_UNM(49),/**	A B	R[A] := -R[B]					*/
    OP_BNOT(50),/**	A B	R[A] := ~R[B]					*/
    OP_NOT(51),/**	A B	R[A] := not R[B]				*/
    OP_LEN(52),/**	A B	R[A] := #R[B] (length operator)			*/

    OP_CONCAT(53),/**	A B	R[A] := R[A].. ... ..R[A + B - 1]		*/

    OP_CLOSE(54),/**	A	close all upvalues >= R[A]			*/
    OP_TBC(55),/**	A	mark variable A "to be closed"			*/

    /**只有jmp是无条件跳转*/
    OP_JMP(56),/**	sJ	pc += sJ	 无条件跳转				*/

    /**下面的跳转语句都是 进行判断后决定是否跳转  也就是说，所有比较语句，已经包含了跳转在里面*/
    OP_EQ(57),/**	A B k	if ((R[A] == R[B]) ~= k) then pc++		*/
    OP_LT(58),/**	A B k	if ((R[A] <  R[B]) ~= k) then pc++		*/
    OP_LE(59),/**	A B k	if ((R[A] <= R[B]) ~= k) then pc++		*/

    OP_EQK(60),/**	A B k	if ((R[A] == K[B]) ~= k) then pc++		*/
    OP_EQI(61),/**	A sB k	if ((R[A] == sB) ~= k) then pc++		*/
    OP_LTI(62),/**	A sB k	if ((R[A] < sB) ~= k) then pc++			*/
    OP_LEI(63),/**	A sB k	if ((R[A] <= sB) ~= k) then pc++		*/
    OP_GTI(64),/**	A sB k	if ((R[A] > sB) ~= k) then pc++			*/
    OP_GEI(65),/**	A sB k	if ((R[A] >= sB) ~= k) then pc++		*/
    /**OP_TEST进行赋值，OP_TESTSET不进行赋值*/
    OP_TEST(66),/**	A k	if (not R[A] == k) then pc++			*/

    OP_TESTSET(67),/**	A B k	if (not R[B] == k) then pc++ else R[A] := R[B] (*) */

    OP_CALL(68),/**	A B C	R[A], ... ,R[A+C-2] := R[A](R[A+1], ... ,R[A+B-1]) */
    OP_TAILCALL(69),/**	A B C k	return R[A](R[A+1], ... ,R[A+B-1])		*/

    OP_RETURN(70),/**	A B C k	return R[A], ... ,R[A+B-2]	(see note)	*/
    OP_RETURN0(71),/**		return						*/
    OP_RETURN1(72),/**	A	return R[A]					*/

    OP_FORLOOP(73),/**	A Bx	update counters; if loop continues then pc-=Bx; */
    OP_FORPREP(74),/**	A Bx	<check values and prepare counters>;
                        if not to run then pc+=Bx+1;			*/

    OP_TFORPREP(75),/**	A Bx	create upvalue for R[A + 3]; pc+=Bx		*/
    OP_TFORCALL(76),/**	A C	R[A+4], ... ,R[A+3+C] := R[A](R[A+1], R[A+2]);	*/
    OP_TFORLOOP(77),/**	A Bx	if R[A+2] ~= nil then { R[A]=R[A+2]; pc -= Bx }	*/

    OP_SETLIST(78),/**	A B C k	R[A][C+i] := R[A+i], 1 <= i <= B		*/

    OP_CLOSURE(79),/**	A Bx	R[A] := closure(KPROTO[Bx])			*/

    OP_VARARG(80),/**	A C	R[A], R[A+1], ..., R[A+C-2] = vararg		*/

    OP_VARARGPREP(81),/**A	(adjust vararg parameters)			*/

    OP_EXTRAARG(82)/**	Ax	extra (larger) argument for previous opcode	*/
    ;

    private int code;
    OpCode(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }

    /**
     * 字节码指令的数量
     */
    public static int NUM_OPCODES = OP_EXTRAARG.getCode() + 1;

    public static OpCode getOpCode(int i){
        for(OpCode o : OpCode.values()){
            if(o.getCode() == i){
                return o;
            }
        }
        return null;
    }
}
