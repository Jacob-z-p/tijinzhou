"use client";

import { listBankByPage } from "@/api/questionBank";
import { pageTotal, type QuestionBank } from "@/types/api";
import { App, Card, Col, Empty, Pagination, Row, Spin } from "antd";
import Link from "next/link";
import { useEffect, useState } from "react";

export default function BanksPage() {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [records, setRecords] = useState<QuestionBank[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);

  useEffect(() => {
    let cancelled = false;
    listBankByPage({ current, pageSize: 12 })
      .then((res) => {
        if (cancelled) {
          return;
        }
        setRecords(res.data?.records ?? []);
        setTotal(pageTotal(res.data));
      })
      .catch((error: unknown) => {
        if (!cancelled) {
          message.error(error instanceof Error ? error.message : "加载题库失败");
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
  }, [current, message]);

  return (
    <Spin spinning={loading}>
      {records.length === 0 && !loading ? (
        <Empty description="还没有题库" />
      ) : (
        <Row gutter={[16, 16]}>
          {records.map((bank) => (
            <Col key={bank.id} xs={24} sm={12} md={8}>
              <Link href={`/bank/${bank.id}`}>
                <Card hoverable title={bank.title} cover={
                  bank.picture ? (
                    // 题库图片地址由后台填写，域名不固定，不能走 next/image 的远程白名单
                    // eslint-disable-next-line @next/next/no-img-element
                    <img alt="" src={bank.picture} style={{ height: 160, objectFit: "cover" }} />
                  ) : undefined
                }>
                  <Card.Meta description={bank.description || "暂无描述"} />
                </Card>
              </Link>
            </Col>
          ))}
        </Row>
      )}
      <Pagination
        style={{ marginTop: 24, textAlign: "right" }}
        current={current}
        pageSize={12}
        total={total}
        onChange={(page) => {
          setLoading(true);
          setCurrent(page);
        }}
        hideOnSinglePage
      />
    </Spin>
  );
}
