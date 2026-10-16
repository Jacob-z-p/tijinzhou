"use client";

import { getLoginUser } from "@/api/user";
import BasicLayout from "@/components/BasicLayout";
import { store, type AppDispatch } from "@/stores";
import { setLoginUser } from "@/stores/user";
import { App, ConfigProvider } from "antd";
import zhCN from "antd/locale/zh_CN";
import { useEffect } from "react";
import { Provider, useDispatch } from "react-redux";

function LoginUserLoader({ children }: { children: React.ReactNode }) {
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    getLoginUser()
      .then((res) => {
        dispatch(setLoginUser(res.code === 0 ? res.data : null));
      })
      .catch(() => {
        dispatch(setLoginUser(null));
      });
  }, [dispatch]);

  return <BasicLayout>{children}</BasicLayout>;
}

export default function Providers({ children }: { children: React.ReactNode }) {
  return (
    <ConfigProvider locale={zhCN}>
      <App>
        <Provider store={store}>
          <LoginUserLoader>{children}</LoginUserLoader>
        </Provider>
      </App>
    </ConfigProvider>
  );
}
