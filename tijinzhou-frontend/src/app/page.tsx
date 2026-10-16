"use client";

import { Button, Card, Space, Typography } from "antd";
import Link from "next/link";

export default function HomePage() {
  return (
    <Card>
      <Typography.Title level={2}>题津舟</Typography.Title>
      <Typography.Paragraph>从题库开始刷题，或者直接搜索题目。</Typography.Paragraph>
      <Space>
        <Link href="/banks">
          <Button type="primary">题库列表</Button>
        </Link>
        <Link href="/questions">
          <Button>题目搜索</Button>
        </Link>
      </Space>
    </Card>
  );
}
