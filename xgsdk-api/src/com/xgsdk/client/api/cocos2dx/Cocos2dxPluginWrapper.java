/****************************************************************************
Copyright (c) 2012-2013 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/

package com.xgsdk.client.api.cocos2dx;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Field;

class Cocos2dxPluginWrapper {

    protected static Context sContext = null;
    protected static GLSurfaceView sGLSurfaceView = null;
    protected static Handler sMainThreadHandler = null;

    public static void init(Context context) {
        sContext = context;
        if (null == sMainThreadHandler) {
            sMainThreadHandler = new Handler();
        }
    }

    public static void setGLSurfaceView(GLSurfaceView value) {
        sGLSurfaceView = value;
    }

    protected static Object initPlugin(String classFullName) {
        Log.i("cocos2dx", "class name : ----" + classFullName + "----");
        Class<?> c = null;
        try {
            String fullName = classFullName.replace('/', '.');
            c = Class.forName(fullName);
        } catch (ClassNotFoundException e) {
            Log.e("", "Class " + classFullName + " not found.");
            e.printStackTrace();
            return null;
        }

        try {
            Context ctx = getContext();
            if (ctx != null) {
                Object o = c.getDeclaredConstructor(Context.class).newInstance(
                        ctx);
                return o;
            } else {
                Log.e("cocos2dx", "Plugin " + classFullName
                        + " wasn't initialized.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static int getPluginType(Object obj) {
        int nRet = -1;
        try {
            Field filedID = obj.getClass().getField("PluginType");
            Integer nObj = (Integer) filedID.get(obj);
            nRet = nObj.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nRet;
    }

    public static Context getContext() {
        return sContext;
    }

    public static void runOnGLThread(Runnable r) {
        if (null != sGLSurfaceView) {
            sGLSurfaceView.queueEvent(r);
        } else {
            Log.i("cocos2dx", "runOnGLThread sGLSurfaceView is null");
            r.run();
        }
    }

    public static void runOnMainThread(Runnable r) {
        if (null == sMainThreadHandler)
            return;
        sMainThreadHandler.post(r);
    }
}
