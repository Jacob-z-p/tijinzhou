"use client";

import { getSignCalendar, getSignDay, signIn } from "@/api/sign";
import SignYearHeatmap from "@/components/SignYearHeatmap";
import type { SignCalendar, SignDay } from "@/types/api";
import { App, Button, Calendar, Card, Drawer, Space, Typography } from "antd";
import type { CalendarProps } from "antd";
import dayjs, { type Dayjs } from "dayjs";
import Link from "next/link";
import { useCallback, useEffect, useMemo, useState } from "react";

const deletedTitle = "题目已删除";

export default function SignPage() {
  const { message } = App.useApp();
  const [panel, setPanel] = useState(() => dayjs());
  const [calendarData, setCalendarData] = useState<SignCalendar>();
  const [signing, setSigning] = useState(false);
  const [open, setOpen] = useState(false);
  const [dayDetail, setDayDetail] = useState<SignDay>();
  const [dayLoading, setDayLoading] = useState(false);
  const [pickedYear, setPickedYear] = useState<number>();
  const [heatReload, setHeatReload] = useState(0);
  const heatYear = pickedYear ?? (calendarData?.today ? Number(calendarData.today.slice(0, 4)) : dayjs().year());

  const loadCalendar = useCallback(
    (year: number, month: number) => {
      getSignCalendar(year, month)
        .then((res) => setCalendarData(res.data))
        .catch((error: unknown) => {
          message.error(error instanceof Error ? error.message : "加载签到日历失败");
        });
    },
    [message],
  );

  useEffect(() => {
    loadCalendar(panel.year(), panel.month() + 1);
  }, [loadCalendar, panel]);

  const dayMap = useMemo(() => {
    const map = new Map<string, SignCalendar["days"][number]>();
    for (const day of calendarData?.days ?? []) {
      map.set(day.date, day);
    }
    return map;
  }, [calendarData]);

  async function handleSign() {
    setSigning(true);
    try {
      await signIn();
      message.success("签到成功");
      loadCalendar(panel.year(), panel.month() + 1);
      setHeatReload((value) => value + 1);
    } catch (error) {
      message.error(error instanceof Error ? error.message : "签到失败");
    } finally {
      setSigning(false);
    }
  }

  const onPanelChange: CalendarProps<Dayjs>["onPanelChange"] = (date, mode) => {
    if (mode !== "month") {
      return;
    }
    setPanel(date);
  };

  function openDay(date: string) {
    setPanel(dayjs(date));
    setOpen(true);
    setDayDetail(undefined);
    setDayLoading(true);
    getSignDay(date)
      .then((res) => setDayDetail(res.data))
      .catch((error: unknown) => {
        message.error(error instanceof Error ? error.message : "加载当天记录失败");
      })
      .finally(() => setDayLoading(false));
  }

  const onSelect: CalendarProps<Dayjs>["onSelect"] = (date, info) => {
    if (info.source !== "date") {
      return;
    }
    openDay(date.format("YYYY-MM-DD"));
  };

  const cellRender: CalendarProps<Dayjs>["cellRender"] = (date, info) => {
    if (info.type !== "date") {
      return info.originNode;
    }
    const day = dayMap.get(date.format("YYYY-MM-DD"));
    return (
      <div>
        {info.originNode}
        {day?.signed && <div style={{ color: "#389e0d", fontSize: 12 }}>已签</div>}
        {!!day?.practiceCount && (
          <div style={{ color: "#1677ff", fontSize: 12 }}>刷 {day.practiceCount} 题</div>
        )}
      </div>
    );
  };

  return (
    <Card title="签到日历">
      <Space wrap style={{ marginBottom: 16 }}>
        <Button type="primary" disabled={calendarData?.signedToday} loading={signing} onClick={handleSign}>
          {calendarData?.signedToday ? "已签到" : "今日签到"}
        </Button>
        <Typography.Text>连续 {calendarData?.streak ?? 0} 天</Typography.Text>
        <Typography.Text>本月签到 {calendarData?.monthSignCount ?? 0} 天</Typography.Text>
      </Space>
      <SignYearHeatmap
        year={heatYear}
        reloadKey={heatReload}
        onYearChange={setPickedYear}
        onPickDay={openDay}
      />
      <Calendar value={panel} onPanelChange={onPanelChange} onSelect={onSelect} cellRender={cellRender} />
      <Drawer title={dayDetail?.date || "当天记录"} open={open} onClose={() => setOpen(false)} loading={dayLoading}>
        <Typography.Paragraph>{dayDetail?.signed ? "已签到" : "未签到"}</Typography.Paragraph>
        {(dayDetail?.questions ?? []).length === 0 ? (
          <Typography.Text type="secondary">当天没有刷题</Typography.Text>
        ) : (
          <ul style={{ paddingLeft: 18, margin: 0 }}>
            {(dayDetail?.questions ?? []).map((item) => (
              <li key={item.id} style={{ marginBottom: 8 }}>
                {item.title === deletedTitle ? (
                  item.title
                ) : (
                  <Link href={`/question/${item.id}`}>{item.title}</Link>
                )}
              </li>
            ))}
          </ul>
        )}
      </Drawer>
    </Card>
  );
}
