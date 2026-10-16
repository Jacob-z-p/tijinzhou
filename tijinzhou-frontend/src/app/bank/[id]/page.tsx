"use client";

import { getBank } from "@/api/questionBank";
import { listQuestionByPage } from "@/api/question";
import { pageTotal, type Question, type QuestionBank } from "@/types/api";
import { App, Card, Empty, Pagination, Spin, Tag, Typography } from "antd";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function BankDetailPage() {
  const { message } = App.useApp();
  const params = useParams<{ id: string }>();
  const id = params.id;
  const [bank, setBank] = useState<QuestionBank>();
  const [questions, setQuestions] = useState<Question[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) {
      return;
    }
    getBank(id)
      .then((res) => setBank(res.data))
      .catch((error: unknown) => {
        message.error(error instanceof Error ? error.message : "加载题库失败");
      });
  }, [id, message]);

  useEffect(() => {
    if (!id) {
      return;
    }
    let cancelled = false;
    listQuestionByPage({ questionBankId: id, current, pageSize: 10 })
      .then((res) => {
        if (cancelled) {
          return;
        }
        setQuestions(res.data?.records ?? []);
        setTotal(pageTotal(res.data));
      })
      .catch((error: unknown) => {
        if (!cancelled) {
          message.error(error instanceof Error ? error.message : "加载题目失败");
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [id, current, message]);

  return (
    <Spin spinning={loading && !bank}>
      <Card
        title={bank?.title || "题库"}
        cover={
          bank?.picture ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img alt="" src={bank.picture} style={{ maxHeight: 240, objectFit: "cover" }} />
          ) : undefined
        }
      >
        <Typography.Paragraph>{bank?.description || "暂无描述"}</Typography.Paragraph>
        {questions.length === 0 && !loading ? (
          <Empty description="这个题库还没有已发布的题目" />
        ) : (
          questions.map((item) => (
            <div key={item.id} style={{ padding: "12px 0", borderBottom: "1px solid #f0f0f0" }}>
              <Link href={`/question/${item.id}`}>{item.title}</Link>
              <div>
                {(item.tagList ?? []).map((tag) => (
                  <Tag key={tag}>{tag}</Tag>
                ))}
              </div>
            </div>
          ))
        )}
        <Pagination
          style={{ marginTop: 16, textAlign: "right" }}
          current={current}
          pageSize={10}
          total={total}
          hideOnSinglePage
          onChange={(page) => {
            setLoading(true);
            setCurrent(page);
          }}
        />
      </Card>
    </Spin>
  );
}
