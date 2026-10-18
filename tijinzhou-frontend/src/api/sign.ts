import { get, post } from "@/libs/request";
import type { SignCalendar, SignDay, SignResult } from "@/types/api";

export function signIn() {
  return post<SignResult>("/api/sign/do");
}

export function getSignCalendar(year: number, month: number) {
  return get<SignCalendar>("/api/sign/calendar", {
    year: String(year),
    month: String(month),
  });
}

export function getSignDay(date: string) {
  return get<SignDay>("/api/sign/day", { date });
}
