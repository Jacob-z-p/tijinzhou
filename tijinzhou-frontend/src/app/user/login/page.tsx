"use client";

import { login } from "@/api/user";
import { setLoginUser } from "@/stores/user";
import { App, Button, Card, Form, Input } from "antd";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { Suspense, useState } from "react";
import { useDispatch } from "react-redux";

function redirectTarget(raw: string | null) {
  if (!raw) {
    return "/";
  }
  if (raw.startsWith("/") && !raw.startsWith("//")) {
    return raw;
  }
  try {
    const url = new URL(raw);
    if (url.origin === window.location.origin) {
      return `${url.pathname}${url.search}`;
    }
  } catch {
    return "/";
  }
  return "/";
}

function LoginForm() {
  const { message } = App.useApp();
  const router = useRouter();
  const searchParams = useSearchParams();
  const dispatch = useDispatch();
  const [submitting, setSubmitting] = useState(false);

  async function onFinish(values: { userAccount: string; userPassword: string }) {
    setSubmitting(true);
    try {
      const res = await login(values);
      dispatch(setLoginUser(res.data));
      message.success("登录成功");
      router.push(redirectTarget(searchParams.get("redirect")));
    } catch (error) {
      message.error(error instanceof Error ? error.message : "登录失败");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Card title="登录" style={{ maxWidth: 420, margin: "40px auto" }}>
      <Form layout="vertical" onFinish={onFinish}>
        <Form.Item label="账号" name="userAccount" rules={[{ required: true, min: 4, message: "账号至少 4 位" }]}>
          <Input />
        </Form.Item>
        <Form.Item label="密码" name="userPassword" rules={[{ required: true, min: 8, message: "密码至少 8 位" }]}>
          <Input.Password />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={submitting} block>
          登录
        </Button>
      </Form>
      <div style={{ marginTop: 16 }}>
        没有账号？<Link href="/user/register">去注册</Link>
      </div>
    </Card>
  );
}

export default function LoginPage() {
  return (
    <Suspense>
      <LoginForm />
    </Suspense>
  );
}
