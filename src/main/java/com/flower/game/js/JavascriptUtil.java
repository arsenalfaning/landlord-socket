package com.flower.game.js;

import com.flower.game.runtime.GameRuntime;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;

public class JavascriptUtil {
    private static final ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
    private static ScriptObjectMirror func;

    static {
        try {
            func = (ScriptObjectMirror) se.eval("function add(a, b){return game.add(a,b);}");
            Bindings bindings = se.createBindings();
            bindings.put("game", new GameRuntime());
            se.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public static Number test(int a, int b) throws ScriptException, NoSuchMethodException {
        if (se instanceof  Invocable) {
            Invocable invocable = (Invocable) se;
            return (Number) invocable.invokeFunction("add", a, b);
        }
        return 0;
    }
}
