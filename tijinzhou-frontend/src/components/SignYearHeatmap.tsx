"use client";

import { getSignYear } from "@/api/sign";
import type { SignYearDay } from "@/types/api";
import { LeftOutlined, RightOutlined } from "@ant-design/icons";
import { App, Button, Space, Tooltip, Typography } from "antd";
import dayjs from "dayjs";
import { useEffect, useMemo, useState } from "react";

const signedColor = "#389e0d";
const unsignedColor = "#ebedf0";
const cellSize = 26;
const gap = 4;
const weekdays = ["日", "一", "二", "三", "四", "五", "六"];

type Cell = SignYearDay | null;

function buildWeeks(days: SignYearDay[]): Cell[][] {
  if (days.length === 0) {
    return [];
  }
  const cells: Cell[] = Array.from({ length: dayjs(days[0].date).day() }, () => null);
  cells.push(...days);
  while (cells.length % 7 !== 0) {
    cells.push(null);
  }
  const weeks: Cell[][] = [];
  for (let index = 0; index < cells.length; index += 7) {
    weeks.push(cells.slice(index, index + 7));
  }
  return weeks;
}

function monthLabel(week: Cell[]) {
  const firstOfMonth = week.find((item) => item && dayjs(item.date).date() === 1);
  return firstOfMonth ? `${dayjs(firstOfMonth.date).month() + 1}月` : "";
}

export default function SignYearHeatmap({
  year,
  reloadKey,
  onYearChange,
  onPickDay,
}: {
  year: number;
  reloadKey: number;
  onYearChange: (year: number) => void;
  onPickDay: (date: string) => void;
}) {
  const { message } = App.useApp();
  const [days, setDays] = useState<SignYearDay[]>([]);

  useEffect(() => {
    let ignore = false;
    getSignYear(year)
      .then((res) => {
        if (!ignore) {
          setDays(res.data.days ?? []);
        }
      })
      .catch((error: unknown) => {
        if (!ignore) {
          message.error(error instanceof Error ? error.message : "加载签到热力图失败");
        }
      });
    return () => {
      ignore = true;
    };
  }, [year, reloadKey, message]);

  const weeks = useMemo(() => buildWeeks(days), [days]);

  return (
    <div style={{ marginBottom: 24 }}>
      <Space style={{ marginBottom: 12 }}>
        <Button
          icon={<LeftOutlined />}
          disabled={year <= 1}
          onClick={() => onYearChange(year - 1)}
          aria-label="上一年"
        />
        <Typography.Text strong>{year} 年</Typography.Text>
        <Button
          icon={<RightOutlined />}
          disabled={year >= 9999}
          onClick={() => onYearChange(year + 1)}
          aria-label="下一年"
        />
      </Space>
      <div style={{ display: "flex", gap: 8, maxWidth: "100%", overflowX: "auto", overflowY: "hidden" }}>
        <div style={{ position: "sticky", left: 0, zIndex: 1, background: "#fff" }}>
          <div style={{ height: 16 }} />
          <div style={{ display: "grid", gridTemplateRows: `repeat(7, ${cellSize}px)`, gap }}>
            {weekdays.map((label) => (
              <div key={label} style={{ fontSize: 12, lineHeight: `${cellSize}px` }}>
                {label}
              </div>
            ))}
          </div>
        </div>
        <div>
          <div style={{ position: "relative", height: 16, width: weeks.length * (cellSize + gap) }}>
            {weeks.map((week, index) => {
              const label = monthLabel(week);
              if (!label) {
                return null;
              }
              return (
                <span
                  key={index}
                  style={{
                    position: "absolute",
                    left: index * (cellSize + gap),
                    fontSize: 12,
                    lineHeight: "16px",
                    whiteSpace: "nowrap",
                  }}
                >
                  {label}
                </span>
              );
            })}
          </div>
          <div
            style={{
              display: "grid",
              gridTemplateRows: `repeat(7, ${cellSize}px)`,
              gridAutoFlow: "column",
              gap,
            }}
          >
            {weeks.flatMap((week, weekIndex) =>
              week.map((item, dayIndex) => {
                if (!item) {
                  return <div key={`${weekIndex}-${dayIndex}`} />;
                }
                const label = `${item.date} ${item.signed ? "已签到" : "未签到"}`;
                return (
                  <Tooltip key={item.date} title={label}>
                    <button
                      type="button"
                      aria-label={label}
                      onClick={() => onPickDay(item.date)}
                      style={{
                        width: cellSize,
                        height: cellSize,
                        padding: 0,
                        border: 0,
                        borderRadius: 2,
                        background: item.signed ? signedColor : unsignedColor,
                        cursor: "pointer",
                      }}
                    />
                  </Tooltip>
                );
              }),
            )}
          </div>
        </div>
      </div>
      <Space style={{ marginTop: 8 }} size={16}>
        <Space size={4}>
          <span style={{ display: "inline-block", width: cellSize, height: cellSize, borderRadius: 2, background: unsignedColor }} />
          <Typography.Text type="secondary">未签到</Typography.Text>
        </Space>
        <Space size={4}>
          <span style={{ display: "inline-block", width: cellSize, height: cellSize, borderRadius: 2, background: signedColor }} />
          <Typography.Text type="secondary">已签到</Typography.Text>
        </Space>
      </Space>
    </div>
  );
}
