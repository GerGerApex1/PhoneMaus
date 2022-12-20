#include <node_api.h>
#include <napi.h>
#include <Windows.h>
#include <iostream>
static Napi::Value moveMouse(const Napi::CallbackInfo& info) {
  // Napi::Env is the opaque data structure containing the environment in which the request is being run. 
  // We will need this env when we want to create any new objects inside of the node.js environment
  Napi::Env env = info.Env();

  if(info.Length() < 2) {
    Napi::TypeError::New(env, "2 Arguments need to be supplied").ThrowAsJavaScriptException();
    return env.Null();
  }
  if (!info[0].IsNumber() || !info[1].IsNumber()) {
    Napi::TypeError::New(env, "Either 2 arguments aren't a number.").ThrowAsJavaScriptException();
    return env.Null();
  }

  double x = info[0].As<Napi::Number>().DoubleValue();
  double y = info[1].As<Napi::Number>().DoubleValue();
  POINT p;
  GetCursorPos(&p);
  SetCursorPos(p.x + x, p.y + y);
  // Return a new javascript string that we copy-construct inside of the node.js environment
  return env.Null();
}

static Napi::Object Init(Napi::Env env, Napi::Object exports) {
  exports.Set(Napi::String::New(env, "moveMouse"),
              Napi::Function::New(env, moveMouse));
  return exports;
}

NODE_API_MODULE(hello, Init)