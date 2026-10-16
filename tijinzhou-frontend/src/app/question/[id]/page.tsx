"use client";

import { getQuestion } from "@/api/question";
import MarkdownView from "@/components/MarkdownView";
import type { Question } from "@/types/api";
import { App, Card, Space, Tag, Typography } from "antd";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function QuestionDetailPage() {
  const { message } = App.useApp();
  const params = useParams<{ id: string }>();
  const [question, setQuestion] = useState<Question>();

  useEffect(() => {
    if (!params.id) {
      return;
    }
    getQuestion(params.id)
      .then((res) => setQuestion(res.data))
      .catch((error: unknown) => {
        message.error(error instanceof Error ? error.message : "加载题目失败");
      });
  }, [params.id, message]);

  return (
    <Card title={question?.title || "题目"} loading={!question}>
      <Space wrap style={{ marginBottom: 16 }}>
        {(question?.tagList ?? []).map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </Space>
      {(question?.questionBankList ?? []).length > 0 && (
        <Typography.Paragraph>
          所属题库：
          {(question?.questionBankList ?? []).map((bank) => (
            <Link key={bank.id} href={`/bank/${bank.id}`} style={{ marginRight: 12 }}>
              {bank.title}
            </Link>
          ))}
        </Typography.Paragraph>
      )}
      <MarkdownView content={question?.content} />
    </Card>
  );
}
