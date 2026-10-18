import { post } from "@/libs/request";

export function addPracticeRecord(questionId: string) {
  return post<boolean>("/api/practice_record/add", { questionId });
}
