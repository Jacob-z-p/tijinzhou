"use client";

import { Button, Result } from "antd";
import { useRouter } from "next/navigation";

export default function Forbidden() {
  const router = useRouter();
  return (
    <Result
      status="403"
      title="没有权限"
      subTitle="只有管理员可以访问这个页面"
      extra={
        <Button type="primary" onClick={() => router.push("/")}>
          返回首页
        </Button>
      }
    />
  );
}
