"use client";

import { listBankByPage } from "@/api/questionBank";
import { addQuestion, deleteQuestion, listQuestionByPageAdmin, updateQuestion, type QuestionForm } from "@/api/question";
import { syncQuestionBanks } from "@/api/questionBankQuestion";
import { pageTotal, type Question, type QuestionBank } from "@/types/api";
import { App, Button, Form, Input, Modal, Popconfirm, Select, Space, Table, Tag } from "antd";
import { useCallback, useEffect, useState } from "react";

type QuestionEditor = QuestionForm & { bankIds?: string[] };

const statusOptions = [
  { value: 0, label: "草稿" },
  { value: 1, label: "已发布" },
];

export default function AdminQuestionPage() {
  const { message } = App.useApp();
  const [form] = Form.useForm<QuestionEditor>();
  const [records, setRecords] = useState<Question[]>([]);
  const [banks, setBanks] = useState<QuestionBank[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [searchText, setSearchText] = useState("");
  const [questionBankId, setQuestionBankId] = useState<string>();
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Question>();
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let cancelled = false;
    listBankByPage({ current: 1, pageSize: 100 })
      .then((res) => {
        if (!cancelled) {
          setBanks(res.data?.records ?? []);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setBanks([]);
        }
      });
    return () => {
      cancelled = true;
    };
  }, []);

  const reload = useCallback(() => {
    setLoading(true);
    setReloadKey((value) => value + 1);
  }, []);

  useEffect(() => {
    let cancelled = false;
    listQuestionByPageAdmin({
      current,
      pageSize: 10,
      searchText: searchText || undefined,
      questionBankId,
    })
      .then((res) => {
        if (cancelled) {
          return;
        }
        setRecords(res.data?.records ?? []);
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
  }, [current, message, questionBankId, reloadKey, searchText]);

  function openCreate() {
    setEditing(undefined);
    form.resetFields();
    form.setFieldsValue({ status: 1, tags: [], bankIds: [] });
    setOpen(true);
  }

  function openEdit(record: Question) {
    setEditing(record);
    form.setFieldsValue({
      title: record.title,
      content: record.content,
      answer: record.answer,
      status: record.status,
      tags: record.tagList ?? [],
      bankIds: (record.questionBankList ?? []).map((bank) => String(bank.id)),
    });
    setOpen(true);
  }

  async function onSubmit() {
    const values = await form.validateFields();
    const payload: QuestionForm = {
      title: values.title,
      content: values.content,
      answer: values.answer,
      status: values.status,
      tags: values.tags ?? [],
    };
    try {
      let questionId = editing?.id;
      if (editing) {
        await updateQuestion({ ...payload, id: editing.id });
      } else {
        const created = await addQuestion(payload);
        questionId = String(created.data);
        await updateQuestion({ ...payload, id: questionId });
      }
      if (questionId) {
        await syncQuestionBanks(questionId, values.bankIds ?? []);
      }
      message.success("已保存");
      setOpen(false);
      reload();
    } catch (error) {
      message.error(error instanceof Error ? error.message : "保存失败");
    }
  }

  async function onDelete(id: string) {
    try {
      await deleteQuestion(id);
      message.success("已删除");
      reload();
    } catch (error) {
      message.error(error instanceof Error ? error.message : "删除失败");
    }
  }

  const bankOptions = banks.map((bank) => ({ value: String(bank.id), label: bank.title }));

  return (
    <div>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="搜索标题或内容"
          allowClear
          onSearch={(value) => {
            setLoading(true);
            setCurrent(1);
            setSearchText(value.trim());
          }}
          style={{ width: 240 }}
        />
        <Select
          allowClear
          placeholder="按题库筛选"
          options={bankOptions}
          style={{ width: 220 }}
          value={questionBankId}
          onChange={(value) => {
            setLoading(true);
            setCurrent(1);
            setQuestionBankId(value);
          }}
        />
        <Button type="primary" onClick={openCreate}>
          新建题目
        </Button>
      </Space>
      <Table
        rowKey="id"
        loading={loading}
        dataSource={records}
        pagination={{
          current,
          pageSize: 10,
          total,
          onChange: (page) => {
            setLoading(true);
            setCurrent(page);
          },
        }}
        columns={[
          { title: "标题", dataIndex: "title" },
          {
            title: "标签",
            dataIndex: "tagList",
            render: (tags?: string[]) => (tags ?? []).map((tag) => <Tag key={tag}>{tag}</Tag>),
          },
          { title: "状态", dataIndex: "status", render: (value) => (value === 1 ? "已发布" : "草稿") },
          {
            title: "题库",
            dataIndex: "questionBankList",
            render: (list?: QuestionBank[]) => (list ?? []).map((bank) => bank.title).join("、"),
          },
          {
            title: "操作",
            render: (_, record) => (
              <Space>
                <Button type="link" onClick={() => openEdit(record)}>
                  编辑
                </Button>
                <Popconfirm title="确认删除？" onConfirm={() => onDelete(record.id)}>
                  <Button type="link" danger>
                    删除
                  </Button>
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />
      <Modal
        title={editing ? "编辑题目" : "新建题目"}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={onSubmit}
        width={720}
        destroyOnHidden={false}
        forceRender
      >
        <Form form={form} layout="vertical">
          <Form.Item label="标题" name="title" rules={[{ required: true, message: "请填写标题" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="内容" name="content">
            <Input.TextArea rows={8} placeholder="支持 Markdown" />
          </Form.Item>
          <Form.Item label="推荐答案" name="answer">
            <Input.TextArea rows={6} />
          </Form.Item>
          <Form.Item label="标签" name="tags">
            <Select mode="tags" placeholder="输入后回车" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={statusOptions} />
          </Form.Item>
          <Form.Item label="所属题库" name="bankIds">
            <Select mode="multiple" options={bankOptions} placeholder="选择要挂上的题库" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
