"use client";

import { addUser, deleteUser, listUserByPage, updateUser, type UserForm } from "@/api/user";
import { pageTotal, type LoginUser } from "@/types/api";
import { App, Button, Form, Input, Modal, Popconfirm, Select, Space, Table } from "antd";
import { useCallback, useEffect, useState } from "react";

const roleOptions = [
  { value: "user", label: "用户" },
  { value: "admin", label: "管理员" },
];

const statusOptions = [
  { value: 0, label: "正常" },
  { value: 1, label: "封禁" },
];

export default function AdminUserPage() {
  const { message } = App.useApp();
  const [form] = Form.useForm<UserForm>();
  const [records, setRecords] = useState<LoginUser[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [userAccount, setUserAccount] = useState("");
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<LoginUser>();
  const [reloadKey, setReloadKey] = useState(0);

  const reload = useCallback(() => {
    setLoading(true);
    setReloadKey((value) => value + 1);
  }, []);

  useEffect(() => {
    let cancelled = false;
    listUserByPage({ current, pageSize: 10, userAccount: userAccount || undefined })
      .then((res) => {
        if (cancelled) {
          return;
        }
        setRecords(res.data?.records ?? []);
        setTotal(pageTotal(res.data));
      })
      .catch((error: unknown) => {
        if (!cancelled) {
          message.error(error instanceof Error ? error.message : "加载用户失败");
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
  }, [current, message, reloadKey, userAccount]);

  function openCreate() {
    setEditing(undefined);
    form.resetFields();
    form.setFieldsValue({ userRole: "user", userStatus: 0 });
    setOpen(true);
  }

  function openEdit(record: LoginUser) {
    setEditing(record);
    form.setFieldsValue(record);
    setOpen(true);
  }

  async function onSubmit() {
    const values = await form.validateFields();
    try {
      if (editing) {
        await updateUser({ ...values, id: editing.id });
        message.success("已更新");
      } else {
        await addUser(values);
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
      await deleteUser(id);
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
          placeholder="按账号搜索"
          allowClear
          onSearch={(value) => {
            setLoading(true);
            setCurrent(1);
            setUserAccount(value.trim());
          }}
          style={{ width: 240 }}
        />
        <Button type="primary" onClick={openCreate}>
          新建用户
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
          { title: "账号", dataIndex: "userAccount" },
          { title: "昵称", dataIndex: "userName" },
          { title: "角色", dataIndex: "userRole", render: (value) => (value === "admin" ? "管理员" : "用户") },
          { title: "状态", dataIndex: "userStatus", render: (value) => (value === 1 ? "封禁" : "正常") },
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
        title={editing ? "编辑用户" : "新建用户"}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={onSubmit}
        destroyOnHidden={false}
        forceRender
      >
        <Form form={form} layout="vertical">
          <Form.Item label="账号" name="userAccount" rules={[{ required: true, message: "请填写账号" }]}>
            <Input />
          </Form.Item>
          <Form.Item label="昵称" name="userName">
            <Input />
          </Form.Item>
          <Form.Item label="头像地址" name="userAvatar">
            <Input />
          </Form.Item>
          <Form.Item label="简介" name="userProfile">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item label="角色" name="userRole">
            <Select options={roleOptions} />
          </Form.Item>
          <Form.Item label="状态" name="userStatus">
            <Select options={statusOptions} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
