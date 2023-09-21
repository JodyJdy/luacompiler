package com.jdy.lua.data;

import com.jdy.lua.executor.Checker;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.vm.FuncInfo;
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
            if (val instanceof Function) {
                Executor executor = new Executor((Function) val, new ArrayList<>());
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


    public void addVal(Value key, Value value) {
        if (key instanceof NumberValue) {
            addVal(String.valueOf(((NumberValue) key).getF()), value);
        } else if (key instanceof StringValue) {
            addVal(((StringValue) key).getVal(), value);
        } else {
            throw new RuntimeException("不支持的表索引类型");
        }
    }

    public void addVal(String key, Value value) {
        checkKeyExist(key);
        map.put(key, value);
    }


    public void checkKeyExist(String key) {
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    public void addVal(Value value) {
        String key = String.valueOf(map.size());
        map.put(key, value);
    }

    public Value get(float key) {
        // lua 下标从1开始
        return get(String.valueOf(key));
    }

    public Value get(int key) {
        // lua 下标从1开始
        return get(String.valueOf(key));
    }

    public Value get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        //从元表获取数据
        if (metatable != null && metatable.get(INDEX) != NilValue.NIL) {
            return getFromMetaTable(metatable.get(INDEX), key);
        }
        return NilValue.NIL;
    }

    private Value getFromMetaTable(Value meta, String key) {
        //从元表中返回数据
        if (meta instanceof Table) {
            return ((Table) meta).get(key);
        } else if (meta instanceof Function call) {
            Expr.Function body = call.getBody();
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

    public Value get(Value value) {
        if (value instanceof NumberValue) {
            return get(((NumberValue) value).getF());
        }
        if (value instanceof StringValue) {
            return get(((StringValue) value).getVal());
        }
        throw new RuntimeException("不支持索引的类型");
    }


    @Override
    public BoolValue eq(Value b) {
        if (b instanceof Table t) {
            if (metatable.hasKey(EQ)) {
                Value value = metatable.get(EQ);
                if (value instanceof Function fun) {
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
                if (value instanceof Function fun) {
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
                if (value instanceof Function fun) {
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
                if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
            if (value instanceof Function fun) {
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
