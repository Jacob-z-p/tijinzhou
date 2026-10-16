import { get, post } from "@/libs/request";
import type { PageData, QuestionBank } from "@/types/api";

export type BankQuery = {
  current?: number;
  pageSize?: number;
  searchText?: string;
  title?: string;
};

export type BankForm = {
  id?: string;
  title?: string;
  description?: string;
  picture?: string;
};

export function listBankByPage(body: BankQuery) {
  return post<PageData<QuestionBank>>("/api/question_bank/list/page/vo", body);
}

export function getBank(id: string) {
  return get<QuestionBank>("/api/question_bank/get/vo", { id });
}

export function addBank(body: BankForm) {
  return post<string>("/api/question_bank/add", body);
}

export function updateBank(body: BankForm) {
  return post<boolean>("/api/question_bank/update", body);
}

export function deleteBank(id: string) {
  return post<boolean>("/api/question_bank/delete", { id });
}
