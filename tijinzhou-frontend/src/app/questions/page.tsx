"use client";

import { listQuestionByPage } from "@/api/question";
import { pageTotal, type Question } from "@/types/api";
import { App, Card, Empty, Input, Pagination, Spin, Tag } from "antd";
import Link from "next/link";
import { useEffect, useState } from "react";

export default function QuestionsPage() {
  const { message } = App.useApp();
  const [searchText, setSearchText] = useState("");
  const [keyword, setKeyword] = useState("");
  const [current, setCurrent] = useState(1);
  const [total, setTotal] = useState(0);
  const [records, setRecords] = useState<Question[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    listQuestionByPage({ searchText: keyword || undefined, current, pageSize: 10 })
      .then((res) => {
        if (cancelled) {
          return;
        }
        setRecords(res.data?.records ?? []);
        setTotal(pageTotal(res.data));
      })
      .catch((error: unknown) => {
        if (!cancelled) {
          message.error(error instanceof Error ? error.message : "搜索失败");
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
  }, [keyword, current, message]);

  return (
    <Card title="题目搜索">
      <Input.Search
        placeholder="搜索标题或内容"
        value={searchText}
        onChange={(event) => setSearchText(event.target.value)}
        onSearch={(value) => {
          setLoading(true);
          setCurrent(1);
          setKeyword(value.trim());
        }}
        allowClear
        style={{ marginBottom: 16, maxWidth: 480 }}
      />
      <Spin spinning={loading}>
        {records.length === 0 && !loading ? (
          <Empty description="没有找到题目" />
        ) : (
          records.map((item) => (
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
      </Spin>
      <Pagination
        style={{ marginTop: 16, textAlign: "right" }}
        current={current}
        pageSize={10}
        total={total}
        onChange={(page) => {
          setLoading(true);
          setCurrent(page);
        }}
      />
    </Card>
  );
}
