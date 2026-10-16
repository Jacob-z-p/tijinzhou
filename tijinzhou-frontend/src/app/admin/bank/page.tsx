"use client";

import { addBank, deleteBank, listBankByPage, updateBank, type BankForm } from "@/api/questionBank";
import { pageTotal, type QuestionBank } from "@/types/api";
import { App, Button, Form, Input, Modal, Popconfirm, Space, Table } from "antd";
import { useCallback, useEffect, useState } from "react";

export default function AdminBankPage() {
  const { message } = App.useApp();
  const [form] = Form.useForm<BankForm>();
  const [records, setRecords] = useState<QuestionBank[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [title, setTitle] = useState("");
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<QuestionBank>();
  const [reloadKey, setReloadKey] = useState(0);

  const reload = useCallback(() => {
    setLoading(true);
    setReloadKey((value) => value + 1);
  }, []);

  useEffect(() => {
    let cancelled = false;
    listBankByPage({ current, pageSize: 10, title: title || undefined })
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
  }, [current, message, reloadKey, title]);

  function openCreate() {
    setEditing(undefined);
    form.resetFields();
    setOpen(true);
  }

  function openEdit(record: QuestionBank) {
    setEditing(record);
    form.setFieldsValue(record);
    setOpen(true);
  }

  async function onSubmit() {
    const values = await form.validateFields();
    try {
      if (editing) {
        await updateBank({ ...values, id: editing.id });
        message.success("已更新");
      } else {
        await addBank(values);
        message.success("已创建");
      }
      setOpen(false);
      reload();
    } catch (error) {
      message.error(error instanceof Error ? error.message : "保存失败");
    }
  }

  async function onDelete(id: string) {
    try {
      await deleteBank(id);
      message.success("已删除");
      reload();
    } catch (error) {
      message.error(error instanceof Error ? error.message : "删除失败");
    }
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="按标题搜索"
          allowClear
          onSearch={(value) => {
            setLoading(true);
            setCurrent(1);
            setTitle(value.trim());
          }}
          style={{ width: 240 }}
        />
        <Button type="primary" onClick={openCreate}>
          新建题库
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
          { title: "描述", dataIndex: "description", ellipsis: true },
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
      <Modal title={editing ? "编辑题库" : "新建题库"} open={open} onCancel={() => setOpen(false)} onOk={onSubmit}
        forceRender
      >
        <Form form={form} layout="vertical">
          <Form.Item label="标题" name="title" rules={[{ required: true, message: "请填写标题" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="描述" name="description">
            <Input.TextArea rows={4} />
          </Form.Item>
          <Form.Item label="图片地址" name="picture">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
