"use client";

import Forbidden from "@/components/Forbidden";
import type { RootState } from "@/stores";
import { Spin } from "antd";
import { useSelector } from "react-redux";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const { loginUser, loaded } = useSelector((state: RootState) => state.user);
  if (!loaded) {
    return <Spin style={{ display: "block", margin: "80px auto" }} />;
  }
  if (loginUser?.userRole !== "admin") {
    return <Forbidden />;
  }
  return children;
}
