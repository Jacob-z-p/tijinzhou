"use client";

import { register } from "@/api/user";
import { App, Button, Card, Form, Input } from "antd";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function RegisterPage() {
  const { message } = App.useApp();
  const router = useRouter();
  const [submitting, setSubmitting] = useState(false);

  async function onFinish(values: { userAccount: string; userPassword: string; checkPassword: string }) {
    if (values.userPassword !== values.checkPassword) {
      message.error("两次输入的密码不一致");
      return;
    }
    setSubmitting(true);
    try {
      await register(values);
      message.success("注册成功，请登录");
      router.push("/user/login");
    } catch (error) {
      message.error(error instanceof Error ? error.message : "注册失败");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Card title="注册" style={{ maxWidth: 420, margin: "40px auto" }}>
      <Form layout="vertical" onFinish={onFinish}>
        <Form.Item label="账号" name="userAccount" rules={[{ required: true, min: 4, message: "账号至少 4 位" }]}>
          <Input />
        </Form.Item>
        <Form.Item label="密码" name="userPassword" rules={[{ required: true, min: 8, message: "密码至少 8 位" }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item
          label="确认密码"
          name="checkPassword"
          rules={[{ required: true, min: 8, message: "密码至少 8 位" }]}
        >
          <Input.Password />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={submitting} block>
          注册
        </Button>
      </Form>
      <div style={{ marginTop: 16 }}>
        已有账号？<Link href="/user/login">去登录</Link>
      </div>
    </Card>
  );
}
