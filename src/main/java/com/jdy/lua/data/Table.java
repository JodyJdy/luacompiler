package com.jdy.lua.data;

import com.jdy.lua.Lua;
import com.jdy.lua.executor.Checker;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.vm.RuntimeFunc;
import com.jdy.lua.vm.Vm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.jdy.lua.data.MetaTable.*;

/**
 * @author jdy
 * @title: Table
 * @description:
 * @data 2023/9/14 16:33
 */
public class Table implements CalculateValue {


    private final LinkedHashMap<String, Value> map = new LinkedHashMap<>();

    /**
     * 记录key 用于遍历
     */
    private final List<String> keys = new ArrayList<>();

    public void setMetatable(Table metatable) {
        this.metatable = metatable;
    }

    private Table metatable;


    @Override
    public String toString() {
        if (map.containsKey(TO_STRING)) {
            Value val = map.get(TO_STRING);
            if (val instanceof LuaFunction) {
                Executor executor = new Executor((LuaFunction) val, new ArrayList<>());
                return executor.execute().toString();
            } else if (val instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute().toString();
            }
        }
        return String.valueOf(map);
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.TABLE;
    }

    public Table getMetatable() {
        return metatable;
    }

    public void addVal(Value key, Value value, boolean useNewIndex) {
        if (key instanceof NumberValue) {
            addVal(key.toString(), value,useNewIndex);
        } else if (key instanceof StringValue) {
            addVal(((StringValue) key).getVal(), value,useNewIndex);
        } else {
            throw new RuntimeException("不支持的表索引类型");
        }
    }

    public void addVal(Value key, Value value) {
        addVal(key,value,true);
    }

    /**
     *检查元表重是否存在某个属性
     */
    public Boolean checkMetatableExist(String key) {
        return metatable != null && metatable.get(key) != NilValue.NIL;
    }

    public void addVal(String key, Value value,boolean useNewIndex) {
        if (useNewIndex && !keys.contains(key) && checkMetatableExist(NEW_INDEX)) {
            setToNewIndex(key,value);
        } else{
            checkKeyExist(key);
            map.put(key, value);
        }
    }

    public void addVal(String key, Value value) {
        addVal(key,value,true);
    }



    public void checkKeyExist(String key) {
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    public void addVal(Value value) {
        String key = String.valueOf(map.size() + 1);
        checkKeyExist(key);
        map.put(key, value);
    }

    public Value get(NumberValue numberValue,boolean useIndex) {
        // lua 下标从1开始
        return get(numberValue.toString(),useIndex);
    }

    public Value get(String key,boolean useIndex) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        //从元表获取数据
        if (useIndex && metatable != null && metatable.get(INDEX) != NilValue.NIL) {
            return getFromMetaTable(metatable.get(INDEX), key);
        }
        return NilValue.NIL;
    }
    public Value get(String key) {
        return get(key,true);
    }

    private void setToNewIndex(String key, Value value) {
        Value meta = metatable.get(NEW_INDEX);
        if (meta instanceof Table table) {
           table.addVal(key,value);
        } else if (meta instanceof LuaFunction call) {
            // function(table,key,value)
            Executor executor = new Executor(call,List.of(this,new StringValue(key),value));
            executor.execute();
        }
    }

    private Value getFromMetaTable(Value meta, String key) {
        //从元表中返回数据
        if (meta instanceof Table) {
            return ((Table) meta).get(key);
        } else if (meta instanceof LuaFunction call) {
            Expr.LuaFunctionBody body = call.getBody();
            //只支持无参函数调用， 有参的没有使用场景
            if (body.getParamNames().isEmpty()) {
                Executor executor = new Executor(call, new ArrayList<>());
                return executor.execute();
            }
        }  else if (meta instanceof RuntimeFunc runtimeFunc) {
            return new Vm(runtimeFunc).execute();
        }
        return NilValue.NIL;
    }

    public Value get(Value value,boolean useIndex) {
        if (value instanceof NumberValue numberValue) {
            return get(numberValue,useIndex);
        }
        if (value instanceof StringValue stringValue) {
            return get(stringValue.getVal(),useIndex);
        }
        throw new RuntimeException("不支持索引的类型");
    }
    public Value get(Value value) {
        return get(value, true);
    }


    @Override
    public BoolValue eq(Value b) {
        if (b instanceof Table t) {
            if (metatable.hasKey(EQ)) {
                Value value = metatable.get(EQ);
                if (value instanceof LuaFunction fun) {
                    Value val = new Executor(fun, List.of(this, b)).execute();
                    return Checker.checkBool(val);
                } else if (value instanceof RuntimeFunc runtimeFunc) {
                    return (BoolValue) new Vm(runtimeFunc).execute();
                }
            }
             return this.map.equals(t.map) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        if (b instanceof Table t) {
            if (metatable.hasKey(NE)) {
                Value value = metatable.get(NE);
                if (value instanceof LuaFunction fun) {
                    Value val = new Executor(fun, List.of(this, b)).execute();
                    return Checker.checkBool(val);
                } else if (value instanceof RuntimeFunc runtimeFunc) {
                    return (BoolValue) new Vm(runtimeFunc).execute();
                }
            }
            return this.map.equals(t.map) ? BoolValue.FALSE : BoolValue.TRUE;
        }
        return BoolValue.TRUE;
    }

    @Override
    public BoolValue lt(Value b) {
        if (b instanceof Table) {
            if (metatable.hasKey(LT)) {
                Value value = metatable.get(LT);
                if (value instanceof LuaFunction fun) {
                    Value val = new Executor(fun, List.of(this, b)).execute();
                    return Checker.checkBool(val);
                }  else if (value instanceof RuntimeFunc runtimeFunc) {
                    return (BoolValue) new Vm(runtimeFunc).execute();
                }
            }
        }
        throw new RuntimeException("无法比较数值");
    }

    @Override
    public BoolValue gt(Value b) {
        throw new RuntimeException("无法比较数值");
    }

    @Override
    public BoolValue le(Value b) {
        if (b instanceof Table) {
            if (metatable.hasKey(LE)) {
                Value value = metatable.get(LE);
                if (value instanceof LuaFunction fun) {
                    Value val = new Executor(fun, List.of(this, b)).execute();
                    return Checker.checkBool(val);
                }  else if (value instanceof RuntimeFunc runtimeFunc) {
                    return (BoolValue) new Vm(runtimeFunc).execute();
                }
            }
        }
        throw new RuntimeException("无法比较数值");
    }

    @Override
    public BoolValue ge(Value b) {
        throw new RuntimeException("无法比较数值");
    }


    /**
     * __add 函数格式是 xx(t1,t2)
     */
    @Override
    public Value add(Value b) {
        if (metatable != null && metatable.hasKey(ADD)) {
            Value value = metatable.get(ADD);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this, b)).execute();
            }  else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.add(b);
    }

    @Override
    public Value sub(Value b) {
        if (metatable != null && metatable.hasKey(SUB)) {
            Value value = metatable.get(SUB);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this, b)).execute();
            } else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.sub(b);
    }

    @Override
    public Value mul(Value b) {
        if (metatable != null && metatable.hasKey(MUL)) {
            Value value = metatable.get(MUL);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this, b)).execute();
            } else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.mul(b);
    }

    @Override
    public Value div(Value b) {
        if (metatable != null && metatable.hasKey(DIV)) {
            Value value = metatable.get(DIV);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this, b)).execute();
            } else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.div(b);
    }

    @Override
    public Value unm() {
        if (metatable != null && metatable.hasKey(UNM)) {
            Value value = metatable.get(UNM);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this)).execute();
            } else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.unm();
    }

    @Override
    public Value mod(Value b) {
        if (metatable != null && metatable.hasKey(MOD)) {
            Value value = metatable.get(MOD);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this, b)).execute();
            } else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.mod(b);
    }


    @Override
    public Value pow(Value b) {
        if (metatable != null && metatable.hasKey(POW)) {
            Value value = metatable.get(POW);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this, b)).execute();
            } else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.pow(b);
    }

    @Override
    public Value concat(Value b) {
        if (metatable != null && metatable.hasKey(CONCAT)) {
            Value value = metatable.get(CONCAT);
            if (value instanceof LuaFunction fun) {
                return new Executor(fun, List.of(this, b)).execute();
            }else if (value instanceof RuntimeFunc runtimeFunc) {
                return new Vm(runtimeFunc).execute();
            }
        }
        return CalculateValue.super.concat(b);
    }

    @Override
    public Value len() {
        return new NumberValue(map.size());
    }

    /**
     * 不包含元表
     *
     * @return
     */
    public boolean hasKey(String key) {
        return map.containsKey(key);
    }

    public String key(int i){
        return keys.get(i);
    }
    public List<String> keys(){
        return keys;
    }
}
