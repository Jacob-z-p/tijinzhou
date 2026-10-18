"use client";

import { logout } from "@/api/user";
import type { RootState } from "@/stores";
import { setLoginUser } from "@/stores/user";
import { App, Button, Layout, Space } from "antd";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useDispatch, useSelector } from "react-redux";

const links = [
  { href: "/banks", label: "题库" },
  { href: "/questions", label: "搜题" },
];

const adminLinks = [
  { href: "/admin/user", label: "用户管理" },
  { href: "/admin/bank", label: "题库管理" },
  { href: "/admin/question", label: "题目管理" },
];

function navStyle(active: boolean): React.CSSProperties {
  return {
    color: active ? "#1677ff" : "inherit",
    fontWeight: active ? 600 : 400,
  };
}

export default function BasicLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const dispatch = useDispatch();
  const { message } = App.useApp();
  const loginUser = useSelector((state: RootState) => state.user.loginUser);
  const isAdmin = loginUser?.userRole === "admin";

  async function handleLogout() {
    try {
      await logout();
      dispatch(setLoginUser(null));
      message.success("已退出");
      router.push("/");
    } catch (error) {
      message.error(error instanceof Error ? error.message : "退出失败");
    }
  }

  return (
    <Layout style={{ minHeight: "100vh", background: "#f5f5f5" }}>
      <Layout.Header
        style={{
          display: "flex",
          alignItems: "center",
          gap: 20,
          background: "#fff",
          padding: "0 24px",
          flexWrap: "wrap",
          height: "auto",
          lineHeight: "64px",
        }}
      >
        <Link href="/" style={{ fontSize: 18, fontWeight: 700 }}>
          题津舟
        </Link>
        <Space size="middle" wrap>
          {links.map((item) => (
            <Link key={item.href} href={item.href} style={navStyle(pathname.startsWith(item.href))}>
              {item.label}
            </Link>
          ))}
          {loginUser && (
            <Link href="/user/sign" style={navStyle(pathname.startsWith("/user/sign"))}>
              签到
            </Link>
          )}
          {isAdmin &&
            adminLinks.map((item) => (
              <Link key={item.href} href={item.href} style={navStyle(pathname.startsWith(item.href))}>
                {item.label}
              </Link>
            ))}
        </Space>
        <div style={{ marginLeft: "auto" }}>
          {loginUser ? (
            <Space>
              <span>{loginUser.userName || loginUser.userAccount}</span>
              <Button type="link" onClick={handleLogout}>
                退出
              </Button>
            </Space>
          ) : (
            <Space>
              <Link href="/user/login">登录</Link>
              <Link href="/user/register">注册</Link>
            </Space>
          )}
        </div>
      </Layout.Header>
      <Layout.Content style={{ padding: 24 }}>{children}</Layout.Content>
    </Layout>
  );
}
