import { get, post } from "@/libs/request";
import type { PageData, Question } from "@/types/api";

export type QuestionQuery = {
  current?: number;
  pageSize?: number;
  searchText?: string;
  questionBankId?: string;
};

export type QuestionForm = {
  id?: string;
  title?: string;
  content?: string;
  tags?: string[];
  answer?: string;
  status?: number;
};

export function listQuestionByPage(body: QuestionQuery) {
  return post<PageData<Question>>("/api/question/list/page/vo", body);
}

export function listQuestionByPageAdmin(body: QuestionQuery) {
  return post<PageData<Question>>("/api/question/list/page", body);
}

export function getQuestion(id: string) {
  return get<Question>("/api/question/get/vo", { id });
}

export function addQuestion(body: QuestionForm) {
  return post<string>("/api/question/add", body);
}

export function updateQuestion(body: QuestionForm) {
  return post<boolean>("/api/question/update", body);
}

export function deleteQuestion(id: string) {
  return post<boolean>("/api/question/delete", { id });
}
